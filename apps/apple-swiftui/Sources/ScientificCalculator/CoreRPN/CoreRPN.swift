import Foundation

enum TVMRegister: String, CaseIterable, Sendable {
    case n = "N"
    case i = "I"
    case pv = "PV"
    case pmt = "PMT"
    case fv = "FV"
}

enum RPNPrefix: Sendable {
    case none
    case f
    case g
}

enum RPNKey: Sendable {
    case digit(Int)
    case decimalPoint
    case enter
    case chs
    case clx
    case swapXY
    case add
    case subtract
    case multiply
    case divide
    case financial(TVMRegister)
    case npv
    case irr
    case advanced(AdvancedFinancialKey)
    case prefixF
    case prefixG
    case clearPrefix
}

enum AdvancedFinancialKey: String, Sendable {
    case amort = "AMORT"
    case bond = "BOND"
    case depSL = "DEP-SL"
    case depDB = "DEP-DB"
    case depSYD = "DEP-SYD"
}

struct RPNStack: Sendable, Equatable {
    var x: Double = 0
    var y: Double = 0
    var z: Double = 0
    var t: Double = 0

    mutating func push(_ value: Double) {
        t = z
        z = y
        y = x
        x = value
    }

    mutating func dropToX(_ value: Double) {
        x = value
        y = z
        z = t
    }

    mutating func swapXY() {
        let tmp = x
        x = y
        y = tmp
    }
}

struct CoreRPNState: Sendable {
    var stack = RPNStack()
    var lastX: Double = 0
    var prefix: RPNPrefix = .none
    var isEnteringNumber: Bool = false
    var entryBuffer: String = "0"
    var beginMode: Bool = false
    var financialRegisters: [TVMRegister: Double] = [
        .n: 0, .i: 0, .pv: 0, .pmt: 0, .fv: 0
    ]
    var cashFlows: [Double] = []
    var lastAmortization: [FinancialEngine.AmortizationRow] = []

    var display: String {
        if isEnteringNumber {
            return entryBuffer
        }
        return formatValue(stack.x)
    }

    mutating func apply(_ key: RPNKey) {
        switch key {
        case .prefixF:
            prefix = .f
            return
        case .prefixG:
            prefix = .g
            return
        case .clearPrefix:
            prefix = .none
            return
        default:
            break
        }

        // Prefix is one-shot for non-shifted keys.
        switch key {
        case .financial, .npv, .irr, .advanced:
            break
        default:
            prefix = .none
        }

        switch key {
        case .digit(let digit):
            guard (0...9).contains(digit) else { return }
            appendDigit(digit)
        case .decimalPoint:
            appendDecimalPoint()
        case .enter:
            finalizeEntry()
            stack.push(stack.x)
            isEnteringNumber = false
            entryBuffer = formatValue(stack.x)
        case .chs:
            applyChs()
        case .clx:
            finalizeEntry()
            lastX = stack.x
            stack.x = 0
            isEnteringNumber = false
            entryBuffer = "0"
        case .swapXY:
            finalizeEntry()
            stack.swapXY()
            isEnteringNumber = false
            entryBuffer = formatValue(stack.x)
        case .add:
            applyBinary(+)
        case .subtract:
            applyBinary(-)
        case .multiply:
            applyBinary(*)
        case .divide:
            applyBinary { lhs, rhs in
                guard rhs != 0 else { return .nan }
                return lhs / rhs
            }
        case .financial(let register):
            applyFinancialKey(register)
        case .npv:
            applyNPVKey()
        case .irr:
            applyIRRKey()
        case .advanced(let op):
            applyAdvancedKey(op)
        case .prefixF, .prefixG, .clearPrefix:
            break
        }
    }

    mutating func loadX(_ value: Double) {
        stack.x = value
        entryBuffer = formatValue(value)
        isEnteringNumber = false
    }

    mutating func clearAll() {
        stack = RPNStack()
        lastX = 0
        prefix = .none
        isEnteringNumber = false
        entryBuffer = "0"
        beginMode = false
        financialRegisters = [.n: 0, .i: 0, .pv: 0, .pmt: 0, .fv: 0]
        cashFlows = []
        lastAmortization = []
    }

    func registerValue(_ register: TVMRegister) -> Double {
        financialRegisters[register, default: 0]
    }

    private mutating func appendDigit(_ digit: Int) {
        let token = String(digit)
        if !isEnteringNumber {
            isEnteringNumber = true
            entryBuffer = token
        } else if entryBuffer == "0" {
            entryBuffer = token
        } else if entryBuffer == "-0" {
            entryBuffer = "-\(token)"
        } else {
            entryBuffer += token
        }
        stack.x = Double(entryBuffer) ?? stack.x
    }

    private mutating func appendDecimalPoint() {
        if !isEnteringNumber {
            isEnteringNumber = true
            entryBuffer = "0."
        } else if !entryBuffer.contains(".") {
            entryBuffer += "."
        }
        stack.x = Double(entryBuffer) ?? stack.x
    }

    private mutating func applyChs() {
        if isEnteringNumber {
            if entryBuffer.hasPrefix("-") {
                entryBuffer.removeFirst()
            } else if entryBuffer != "0" && entryBuffer != "0." {
                entryBuffer = "-\(entryBuffer)"
            }
            stack.x = Double(entryBuffer) ?? stack.x
        } else {
            lastX = stack.x
            stack.x = -stack.x
            entryBuffer = formatValue(stack.x)
        }
    }

    private mutating func applyBinary(_ op: (Double, Double) -> Double) {
        finalizeEntry()
        let lhs = stack.y
        let rhs = stack.x
        lastX = rhs
        let result = op(lhs, rhs)
        stack.dropToX(result)
        isEnteringNumber = false
        entryBuffer = formatValue(stack.x)
    }

    private mutating func applyFinancialKey(_ register: TVMRegister) {
        finalizeEntry()
        defer { prefix = .none }

        switch prefix {
        case .none:
            loadX(financialRegisters[register, default: 0])
        case .f:
            financialRegisters[register] = stack.x
        case .g:
            solveTVM(for: register)
        }
    }

    private mutating func solveTVM(for target: TVMRegister) {
        let n = financialRegisters[.n, default: 0]
        let i = financialRegisters[.i, default: 0]
        let pv = financialRegisters[.pv, default: 0]
        let pmt = financialRegisters[.pmt, default: 0]
        let fv = financialRegisters[.fv, default: 0]

        let result: Double?
        switch target {
        case .n:
            result = FinancialEngine.numberOfPeriods(rate: i, pv: pv, pmt: pmt, fv: fv, beginMode: beginMode)
        case .i:
            result = FinancialEngine.interestRate(n: n, pv: pv, pmt: pmt, fv: fv, beginMode: beginMode)
        case .pv:
            result = FinancialEngine.presentValue(n: n, rate: i, fv: fv, pmt: pmt, beginMode: beginMode)
        case .pmt:
            result = FinancialEngine.payment(n: n, rate: i, pv: pv, fv: fv, beginMode: beginMode)
        case .fv:
            result = FinancialEngine.futureValue(n: n, rate: i, pv: pv, pmt: pmt, beginMode: beginMode)
        }

        guard let value = result else {
            loadX(.nan)
            return
        }

        financialRegisters[target] = value
        loadX(value)
    }

    private mutating func applyNPVKey() {
        finalizeEntry()
        defer { prefix = .none }

        switch prefix {
        case .f:
            cashFlows.append(stack.x)
            loadX(Double(cashFlows.count))
        case .g:
            cashFlows.removeAll()
            loadX(0)
        case .none:
            let rate = financialRegisters[.i, default: 0]
            let value = FinancialEngine.npv(rate: rate, cashFlows: cashFlows)
            loadX(value)
        }
    }

    private mutating func applyIRRKey() {
        finalizeEntry()
        defer { prefix = .none }

        switch prefix {
        case .f:
            if !cashFlows.isEmpty {
                cashFlows.removeLast()
            }
            loadX(Double(cashFlows.count))
        case .g:
            loadX(Double(cashFlows.count))
        case .none:
            if let value = FinancialEngine.irr(cashFlows: cashFlows) {
                loadX(value)
            } else {
                loadX(.nan)
            }
        }
    }

    private mutating func applyAdvancedKey(_ op: AdvancedFinancialKey) {
        finalizeEntry()
        defer { prefix = .none }

        // Advanced finance operations are mapped to g-shift.
        guard prefix == .g else { return }

        switch op {
        case .amort:
            let principal = abs(financialRegisters[.pv, default: 0])
            let rate = financialRegisters[.i, default: 0]
            let periods = max(1, Int(financialRegisters[.n, default: 0].rounded()))
            let schedule = FinancialEngine.amortizationSchedule(
                principal: principal,
                annualRate: rate,
                periods: periods
            )
            lastAmortization = schedule
            loadX(schedule.first?.payment ?? 0)
        case .bond:
            // Stack mapping: T=face, Z=couponRate, Y=yieldRate, X=periods(years)
            let face = stack.t
            let coupon = stack.z
            let yld = stack.y
            let periods = max(1, Int(stack.x.rounded()))
            let price = FinancialEngine.bondPrice(
                faceValue: face,
                couponRate: coupon,
                yieldRate: yld,
                periods: periods
            )
            loadX(price)
        case .depSL:
            // Stack mapping: Z=cost, Y=salvage, X=life
            let cost = stack.z
            let salvage = stack.y
            let life = max(1, Int(stack.x.rounded()))
            let dep = FinancialEngine.straightLineDepreciation(cost: cost, salvage: salvage, life: life)
            loadX(dep)
        case .depDB:
            // Stack mapping: T=cost, Z=salvage, Y=life, X=year
            let cost = stack.t
            let salvage = stack.z
            let life = max(1, Int(stack.y.rounded()))
            let year = max(1, Int(stack.x.rounded()))
            let dep = FinancialEngine.decliningBalanceDepreciation(
                cost: cost,
                salvage: salvage,
                life: life,
                year: min(year, life)
            )
            loadX(dep)
        case .depSYD:
            // Stack mapping: T=cost, Z=salvage, Y=life, X=year
            let cost = stack.t
            let salvage = stack.z
            let life = max(1, Int(stack.y.rounded()))
            let year = max(1, Int(stack.x.rounded()))
            let dep = FinancialEngine.sydDepreciation(
                cost: cost,
                salvage: salvage,
                life: life,
                year: min(year, life)
            )
            loadX(dep)
        }
    }

    private mutating func finalizeEntry() {
        guard isEnteringNumber else { return }
        stack.x = Double(entryBuffer) ?? stack.x
        isEnteringNumber = false
    }

    private func formatValue(_ value: Double) -> String {
        if value.isNaN { return "NaN" }
        if value.isInfinite { return value > 0 ? "inf" : "-inf" }
        if value == value.rounded(), abs(value) < 1e15 {
            return String(format: "%.0f", value)
        }
        return String(format: "%.10g", value)
    }
}
