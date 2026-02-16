import SwiftUI

// MARK: - Scientific Calculator View

struct ScientificCalculatorView: View {
    @State private var viewModel = ScientificViewModel()

    var body: some View {
        VStack(spacing: 12) {
            headerBar
            displaySection
            buttonGrid
        }
        .padding(16)
        .background(CatppuccinMocha.base)
    }

    // MARK: - Header

    private var headerBar: some View {
        HStack {
            Text("Calculadora Científica")
                .font(.system(size: 18, weight: .bold, design: .rounded))
                .foregroundStyle(CatppuccinMocha.text)

            Spacer()

            Button(viewModel.angleMode.rawValue) {
                viewModel.toggleAngleMode()
            }
            .font(.system(size: 13, weight: .bold, design: .monospaced))
            .foregroundStyle(CatppuccinMocha.base)
            .padding(.horizontal, 12)
            .padding(.vertical, 6)
            .background(
                Capsule().fill(CatppuccinMocha.teal)
            )

            if viewModel.memory != 0 {
                Text("M")
                    .font(.system(size: 13, weight: .bold, design: .monospaced))
                    .foregroundStyle(CatppuccinMocha.yellow)
                    .padding(.horizontal, 8)
                    .padding(.vertical, 6)
                    .background(
                        Capsule().fill(CatppuccinMocha.surface0)
                    )
            }
        }
    }

    // MARK: - Display

    private var displaySection: some View {
        VStack(alignment: .trailing, spacing: 4) {
            Text(viewModel.expression)
                .font(.system(size: 14, weight: .regular, design: .monospaced))
                .foregroundStyle(CatppuccinMocha.subtext0)
                .lineLimit(1)
                .frame(maxWidth: .infinity, alignment: .trailing)

            Text(viewModel.display)
                .displayStyle()
        }
    }

    // MARK: - Button Grid

    private var buttonGrid: some View {
        VStack(spacing: 8) {
            // Row 1: 2nd, memory, clear
            HStack(spacing: 8) {
                funcButton("2nd") { viewModel.toggleSecondFunction() }
                    .opacity(viewModel.isSecondFunction ? 1.0 : 0.6)
                funcButton("MC") { viewModel.memoryClear() }
                funcButton("MR") { viewModel.memoryRecall() }
                funcButton("M+") { viewModel.memoryAdd() }
                funcButton("M−") { viewModel.memorySubtract() }
            }

            // Row 2: Functions
            HStack(spacing: 8) {
                if viewModel.isSecondFunction {
                    funcButton("sin⁻¹") { viewModel.applyFunction("sin⁻¹") }
                    funcButton("cos⁻¹") { viewModel.applyFunction("cos⁻¹") }
                    funcButton("tan⁻¹") { viewModel.applyFunction("tan⁻¹") }
                } else {
                    funcButton("sin") { viewModel.applyFunction("sin") }
                    funcButton("cos") { viewModel.applyFunction("cos") }
                    funcButton("tan") { viewModel.applyFunction("tan") }
                }
                funcButton("π") { viewModel.insertConstant("π") }
                funcButton("e") { viewModel.insertConstant("e") }
            }

            // Row 3: More functions
            HStack(spacing: 8) {
                if viewModel.isSecondFunction {
                    funcButton("sinh") { viewModel.applyFunction("sinh") }
                    funcButton("cosh") { viewModel.applyFunction("cosh") }
                    funcButton("tanh") { viewModel.applyFunction("tanh") }
                } else {
                    funcButton("ln") { viewModel.applyFunction("ln") }
                    funcButton("log₁₀") { viewModel.applyFunction("log₁₀") }
                    funcButton("log₂") { viewModel.applyFunction("log₂") }
                }
                funcButton("x²") { viewModel.applyFunction("x²") }
                funcButton("√x") { viewModel.applyFunction("√x") }
            }

            // Row 4: Power functions
            HStack(spacing: 8) {
                if viewModel.isSecondFunction {
                    funcButton("eˣ") { viewModel.applyFunction("eˣ") }
                    funcButton("10ˣ") { viewModel.applyFunction("10ˣ") }
                } else {
                    funcButton("x³") { viewModel.applyFunction("x³") }
                    funcButton("³√x") { viewModel.applyFunction("³√x") }
                }
                funcButton("xʸ") { viewModel.inputOperator("^") }
                funcButton("x!") { viewModel.applyFunction("x!") }
                funcButton("1/x") { viewModel.applyFunction("1/x") }
            }

            // Row 5: Clear, parentheses, operators
            HStack(spacing: 8) {
                accentButton("AC", color: CatppuccinMocha.red) { viewModel.clear() }
                funcButton("⌫") { viewModel.backspace() }
                funcButton("%") { viewModel.applyFunction("%") }
                operatorButton("÷") { viewModel.inputOperator("÷") }
            }

            // Row 6-9: Number pad
            HStack(spacing: 8) {
                numberButton("7") { viewModel.inputDigit("7") }
                numberButton("8") { viewModel.inputDigit("8") }
                numberButton("9") { viewModel.inputDigit("9") }
                operatorButton("×") { viewModel.inputOperator("×") }
            }

            HStack(spacing: 8) {
                numberButton("4") { viewModel.inputDigit("4") }
                numberButton("5") { viewModel.inputDigit("5") }
                numberButton("6") { viewModel.inputDigit("6") }
                operatorButton("−") { viewModel.inputOperator("−") }
            }

            HStack(spacing: 8) {
                numberButton("1") { viewModel.inputDigit("1") }
                numberButton("2") { viewModel.inputDigit("2") }
                numberButton("3") { viewModel.inputDigit("3") }
                operatorButton("+") { viewModel.inputOperator("+") }
            }

            HStack(spacing: 8) {
                numberButton("±") { viewModel.toggleSign() }
                numberButton("0") { viewModel.inputDigit("0") }
                numberButton(".") { viewModel.inputDecimal() }
                accentButton("=", color: CatppuccinMocha.blue) { viewModel.calculate() }
            }
        }
    }

    // MARK: - Button Builders

    private func numberButton(_ title: String, action: @escaping () -> Void) -> some View {
        Button(title, action: action)
            .buttonStyle(NumberButtonStyle())
    }

    private func operatorButton(_ title: String, action: @escaping () -> Void) -> some View {
        Button(title, action: action)
            .buttonStyle(OperatorButtonStyle())
    }

    private func funcButton(_ title: String, action: @escaping () -> Void) -> some View {
        Button(title, action: action)
            .buttonStyle(FunctionButtonStyle())
    }

    private func accentButton(_ title: String, color: Color, action: @escaping () -> Void) -> some View {
        Button(title, action: action)
            .buttonStyle(AccentButtonStyle(color: color))
    }
}

#Preview {
    ScientificCalculatorView()
}
