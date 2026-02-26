import Foundation

// MARK: - Financial Engine (HP 12C-style)

struct FinancialEngine: Sendable {

    // MARK: - TVM (Time Value of Money)

    /// Calculate Future Value
    static func futureValue(n: Double, rate: Double, pv: Double, pmt: Double, beginMode: Bool = false) -> Double {
        let i = rate / 100.0
        if abs(i) < 1e-15 {
            return -(pv + pmt * n)
        }
        let factor = pow(1 + i, n)
        let annuityFactor = (factor - 1) / i
        let pmtAdjusted = beginMode ? pmt * (1 + i) : pmt
        return -(pv * factor + pmtAdjusted * annuityFactor)
    }

    /// Calculate Present Value
    static func presentValue(n: Double, rate: Double, fv: Double, pmt: Double, beginMode: Bool = false) -> Double {
        let i = rate / 100.0
        if abs(i) < 1e-15 {
            return -(fv + pmt * n)
        }
        let factor = pow(1 + i, n)
        let annuityFactor = (factor - 1) / i
        let pmtAdjusted = beginMode ? pmt * (1 + i) : pmt
        return -(fv + pmtAdjusted * annuityFactor) / factor
    }

    /// Calculate Payment
    static func payment(n: Double, rate: Double, pv: Double, fv: Double, beginMode: Bool = false) -> Double {
        let i = rate / 100.0
        if abs(i) < 1e-15 {
            return -(pv + fv) / n
        }
        let factor = pow(1 + i, n)
        let annuityFactor = (factor - 1) / i
        let divisor = beginMode ? annuityFactor * (1 + i) : annuityFactor
        return -(pv * factor + fv) / divisor
    }

    /// Calculate Number of Periods
    static func numberOfPeriods(rate: Double, pv: Double, pmt: Double, fv: Double, beginMode: Bool = false) -> Double? {
        let i = rate / 100.0
        if abs(i) < 1e-15 {
            guard pmt != 0 else { return nil }
            return -(pv + fv) / pmt
        }
        let pmtAdj = beginMode ? pmt * (1 + i) : pmt
        let numerator = pmtAdj - fv * i
        let denominator = pmtAdj + pv * i
        guard numerator / denominator > 0 else { return nil }
        return Foundation.log(numerator / denominator) / Foundation.log(1 + i)
    }

    /// Calculate Interest Rate using Newton-Raphson
    static func interestRate(n: Double, pv: Double, pmt: Double, fv: Double, beginMode: Bool = false) -> Double? {
        var guess = 0.1
        let maxIterations = 1000
        let tolerance = 1e-10

        for _ in 0..<maxIterations {
            let i = guess
            if abs(i) < 1e-15 {
                guess = 0.01
                continue
            }
            let factor = pow(1 + i, n)
            let annuityFactor = (factor - 1) / i
            let pmtAdj = beginMode ? pmt * (1 + i) : pmt

            let f = pv * factor + pmtAdj * annuityFactor + fv

            // Derivative
            let dFactor = n * pow(1 + i, n - 1)
            let dAnnuity = (dFactor * i - (factor - 1)) / (i * i)
            var df = pv * dFactor + pmtAdj * dAnnuity
            if beginMode {
                df += pmt * annuityFactor
            }

            guard abs(df) > 1e-20 else { return nil }

            let newGuess = i - f / df
            if abs(newGuess - guess) < tolerance {
                return newGuess * 100.0
            }
            guess = newGuess
        }
        return nil
    }

    // MARK: - NPV & IRR

    static func npv(rate: Double, cashFlows: [Double]) -> Double {
        let i = rate / 100.0
        var result = 0.0
        for (t, cf) in cashFlows.enumerated() {
            result += cf / pow(1 + i, Double(t))
        }
        return result
    }

    static func irr(cashFlows: [Double], guess: Double = 10.0) -> Double? {
        var rate = guess / 100.0
        let maxIterations = 1000
        let tolerance = 1e-10

        for _ in 0..<maxIterations {
            var f = 0.0
            var df = 0.0
            for (t, cf) in cashFlows.enumerated() {
                let dt = Double(t)
                let denom = pow(1 + rate, dt)
                f += cf / denom
                if t > 0 {
                    df -= dt * cf / pow(1 + rate, dt + 1)
                }
            }

            guard abs(df) > 1e-20 else { return nil }

            let newRate = rate - f / df
            if abs(newRate - rate) < tolerance {
                return newRate * 100.0
            }
            rate = newRate
        }
        return nil
    }

    // MARK: - Amortization

    struct AmortizationRow: Sendable {
        let period: Int
        let payment: Double
        let principal: Double
        let interest: Double
        let balance: Double
    }

    static func amortizationSchedule(
        principal: Double,
        annualRate: Double,
        periods: Int,
        periodsPerYear: Int = 12
    ) -> [AmortizationRow] {
        let i = annualRate / 100.0 / Double(periodsPerYear)
        guard abs(i) > 1e-15 else {
            let pmt = principal / Double(periods)
            return (1...periods).map { p in
                AmortizationRow(
                    period: p,
                    payment: pmt,
                    principal: pmt,
                    interest: 0,
                    balance: principal - pmt * Double(p)
                )
            }
        }

        let factor = pow(1 + i, Double(periods))
        let pmt = principal * (i * factor) / (factor - 1)
        var balance = principal
        var rows: [AmortizationRow] = []

        for p in 1...periods {
            let interestPart = balance * i
            let principalPart = pmt - interestPart
            balance -= principalPart
            rows.append(AmortizationRow(
                period: p,
                payment: pmt,
                principal: principalPart,
                interest: interestPart,
                balance: max(balance, 0)
            ))
        }
        return rows
    }

    // MARK: - Depreciation

    /// Straight-Line Depreciation
    static func straightLineDepreciation(cost: Double, salvage: Double, life: Int) -> Double {
        guard life > 0 else { return 0 }
        return (cost - salvage) / Double(life)
    }

    /// Declining Balance Depreciation for a given year
    static func decliningBalanceDepreciation(cost: Double, salvage: Double, life: Int, year: Int) -> Double {
        guard life > 0, year > 0, year <= life else { return 0 }
        let rate = 1.0 / Double(life)
        var bookValue = cost
        for y in 1...year {
            let depreciation = bookValue * rate
            bookValue -= depreciation
            if y == year {
                return depreciation
            }
        }
        return 0
    }

    /// Sum-of-Years-Digits Depreciation
    static func sydDepreciation(cost: Double, salvage: Double, life: Int, year: Int) -> Double {
        guard life > 0, year > 0, year <= life else { return 0 }
        let sumOfYears = Double(life * (life + 1)) / 2.0
        let remaining = Double(life - year + 1)
        return (cost - salvage) * remaining / sumOfYears
    }

    // MARK: - Percentage Calculations

    static func percentOf(_ value: Double, percent: Double) -> Double {
        value * percent / 100.0
    }

    static func percentChange(from oldValue: Double, to newValue: Double) -> Double? {
        guard oldValue != 0 else { return nil }
        return ((newValue - oldValue) / abs(oldValue)) * 100.0
    }

    // MARK: - Bond Pricing

    static func bondPrice(faceValue: Double, couponRate: Double, yieldRate: Double, periods: Int) -> Double {
        let c = faceValue * couponRate / 100.0 / 2.0
        let y = yieldRate / 100.0 / 2.0
        let n = periods * 2

        var price = 0.0
        for t in 1...n {
            price += c / pow(1 + y, Double(t))
        }
        price += faceValue / pow(1 + y, Double(n))
        return price
    }

    // MARK: - Formatting

    static func formatCurrency(_ value: Double) -> String {
        let formatter = NumberFormatter()
        formatter.numberStyle = .currency
        formatter.locale = Locale(identifier: "pt_BR")
        formatter.maximumFractionDigits = 2
        return formatter.string(from: NSNumber(value: value)) ?? String(format: "%.2f", value)
    }

    static func formatPercent(_ value: Double) -> String {
        String(format: "%.4f%%", value)
    }
}
