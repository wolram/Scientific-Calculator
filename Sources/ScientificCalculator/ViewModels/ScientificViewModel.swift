import SwiftUI

// MARK: - Scientific Calculator ViewModel

@MainActor
@Observable
final class ScientificViewModel {
    var display: String = "0"
    var expression: String = ""
    var angleMode: AngleMode = .degrees
    var memory: Double = 0
    var isSecondFunction: Bool = false
    var hasError: Bool = false

    private var currentValue: Double = 0
    private var pendingOperator: String?
    private var operandA: Double?
    private var shouldResetDisplay: Bool = true

    // MARK: - Input Handling

    func inputDigit(_ digit: String) {
        if hasError { clear() }
        if shouldResetDisplay {
            display = digit
            shouldResetDisplay = false
        } else {
            if display == "0" && digit != "." {
                display = digit
            } else {
                display += digit
            }
        }
    }

    func inputDecimal() {
        if hasError { clear() }
        if shouldResetDisplay {
            display = "0."
            shouldResetDisplay = false
        } else if !display.contains(".") {
            display += "."
        }
    }

    func inputOperator(_ op: String) {
        if hasError { return }
        calculatePending()
        operandA = Double(display) ?? 0
        pendingOperator = op
        shouldResetDisplay = true
        expression = "\(ScientificEngine.formatResult(operandA!)) \(op)"
    }

    func calculate() {
        guard let op = pendingOperator, let a = operandA else { return }
        let b = Double(display) ?? 0
        expression = "\(ScientificEngine.formatResult(a)) \(op) \(ScientificEngine.formatResult(b)) ="

        var result: Double?
        switch op {
        case "+": result = ScientificEngine.add(a, b)
        case "-", "−": result = ScientificEngine.subtract(a, b)
        case "×": result = ScientificEngine.multiply(a, b)
        case "÷":
            result = ScientificEngine.divide(a, b)
            if result == nil { showError("Divisão por zero"); return }
        case "^": result = ScientificEngine.power(a, b)
        case "ʸ√x":
            result = ScientificEngine.nthRoot(a, n: b)
            if result == nil { showError("Raiz inválida"); return }
        case "nPr":
            result = ScientificEngine.permutation(Int(a), Int(b))
            if result == nil { showError("nPr inválido"); return }
        case "nCr":
            result = ScientificEngine.combination(Int(a), Int(b))
            if result == nil { showError("nCr inválido"); return }
        default: result = b
        }

        if let r = result {
            display = ScientificEngine.formatResult(r)
            currentValue = r
        }
        pendingOperator = nil
        operandA = nil
        shouldResetDisplay = true
    }

    // MARK: - Unary Functions

    func applyFunction(_ function: String) {
        guard let value = Double(display) else { return }
        var result: Double?
        var errorMsg: String?

        switch function {
        case "sin":
            result = ScientificEngine.sin(value, mode: angleMode)
        case "cos":
            result = ScientificEngine.cos(value, mode: angleMode)
        case "tan":
            result = ScientificEngine.tan(value, mode: angleMode)
            if result == nil { errorMsg = "tan indefinida" }
        case "sin⁻¹", "asin":
            result = ScientificEngine.asin(value, mode: angleMode)
            if result == nil { errorMsg = "Requer [-1, 1]" }
        case "cos⁻¹", "acos":
            result = ScientificEngine.acos(value, mode: angleMode)
            if result == nil { errorMsg = "Requer [-1, 1]" }
        case "tan⁻¹", "atan":
            result = ScientificEngine.atan(value, mode: angleMode)
        case "sinh":
            result = ScientificEngine.sinh(value)
        case "cosh":
            result = ScientificEngine.cosh(value)
        case "tanh":
            result = ScientificEngine.tanh(value)
        case "sinh⁻¹":
            result = ScientificEngine.asinh(value)
        case "cosh⁻¹":
            result = ScientificEngine.acosh(value)
            if result == nil { errorMsg = "Requer valor ≥ 1" }
        case "tanh⁻¹":
            result = ScientificEngine.atanh(value)
            if result == nil { errorMsg = "Requer (-1, 1)" }
        case "ln":
            result = ScientificEngine.naturalLog(value)
            if result == nil { errorMsg = "Requer valor > 0" }
        case "log₁₀":
            result = ScientificEngine.log10(value)
            if result == nil { errorMsg = "Requer valor > 0" }
        case "log₂":
            result = ScientificEngine.log2(value)
            if result == nil { errorMsg = "Requer valor > 0" }
        case "eˣ":
            result = ScientificEngine.exp(value)
        case "10ˣ":
            result = ScientificEngine.tenToThe(value)
        case "x²":
            result = ScientificEngine.square(value)
        case "x³":
            result = ScientificEngine.cube(value)
        case "√x":
            result = ScientificEngine.squareRoot(value)
            if result == nil { errorMsg = "Requer valor ≥ 0" }
        case "³√x":
            result = ScientificEngine.cubeRoot(value)
        case "x!":
            result = ScientificEngine.factorial(Int(value))
            if result == nil { errorMsg = "Requer 0 ≤ n ≤ 170" }
        case "1/x":
            result = ScientificEngine.reciprocal(value)
            if result == nil { errorMsg = "Divisão por zero" }
        case "%":
            result = ScientificEngine.percentage(value)
        case "|x|":
            result = ScientificEngine.absolute(value)
        default:
            return
        }

        if let errorMsg {
            showError(errorMsg)
        } else if let result {
            expression = "\(function)(\(ScientificEngine.formatResult(value)))"
            display = ScientificEngine.formatResult(result)
            currentValue = result
            shouldResetDisplay = true
        }
    }

    // MARK: - Constants

    func insertConstant(_ name: String) {
        switch name {
        case "π": display = ScientificEngine.formatResult(.pi)
        case "e": display = ScientificEngine.formatResult(M_E)
        case "φ": display = ScientificEngine.formatResult(ScientificEngine.phi)
        default: return
        }
        shouldResetDisplay = true
    }

    // MARK: - Memory

    func memoryClear() { memory = 0 }
    func memoryRecall() {
        display = ScientificEngine.formatResult(memory)
        shouldResetDisplay = true
    }
    func memoryAdd() {
        memory += Double(display) ?? 0
    }
    func memorySubtract() {
        memory -= Double(display) ?? 0
    }

    // MARK: - Controls

    func clear() {
        display = "0"
        expression = ""
        currentValue = 0
        pendingOperator = nil
        operandA = nil
        shouldResetDisplay = true
        hasError = false
    }

    func toggleSign() {
        guard var value = Double(display) else { return }
        value = ScientificEngine.negate(value)
        display = ScientificEngine.formatResult(value)
    }

    func toggleAngleMode() {
        angleMode = angleMode == .degrees ? .radians : .degrees
    }

    func toggleSecondFunction() {
        isSecondFunction.toggle()
    }

    func backspace() {
        if hasError { clear(); return }
        if display.count > 1 {
            display.removeLast()
        } else {
            display = "0"
        }
    }

    // MARK: - Private

    private func calculatePending() {
        if pendingOperator != nil && operandA != nil {
            calculate()
        }
    }

    private func showError(_ msg: String) {
        display = "Erro"
        expression = msg
        hasError = true
        shouldResetDisplay = true
    }
}
