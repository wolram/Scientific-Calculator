import SwiftUI

// MARK: - Financial Calculator View (RPN-first)

struct FinancialCalculatorView: View {
    @State private var viewModel = FinancialViewModel()

    var body: some View {
        VStack(spacing: 0) {
            headerBar

            ScrollView {
                VStack(spacing: 14) {
                    stackPanel
                    registerPanel
                    keypad
                    logPanel
                }
                .padding(16)
            }
        }
        .background(CatppuccinMocha.base)
    }

    private var headerBar: some View {
        HStack {
            Text("Financeira RPN")
                .font(.system(size: 18, weight: .bold, design: .rounded))
                .foregroundStyle(CatppuccinMocha.text)

            Spacer()

            Button {
                viewModel.toggleBeginMode()
            } label: {
                Text(viewModel.rpnState.beginMode ? "BEGIN" : "END")
                    .font(.system(size: 12, weight: .bold, design: .monospaced))
                    .foregroundStyle(CatppuccinMocha.base)
                    .padding(.horizontal, 12)
                    .padding(.vertical, 6)
                    .background(Capsule().fill(CatppuccinMocha.green))
            }
            .buttonStyle(.plain)
        }
        .padding(16)
    }

    private var stackPanel: some View {
        VStack(spacing: 8) {
            stackRow("T", value: viewModel.rpnState.stack.t)
            stackRow("Z", value: viewModel.rpnState.stack.z)
            stackRow("Y", value: viewModel.rpnState.stack.y)
            stackRow("X", value: viewModel.rpnState.stack.x, highlight: true)

            HStack {
                Text("Prefixo: \(prefixLabel(viewModel.rpnState.prefix))")
                    .font(.system(size: 11, weight: .semibold, design: .monospaced))
                    .foregroundStyle(CatppuccinMocha.subtext0)
                Spacer()
                Text("LASTX: \(formatValue(viewModel.rpnState.lastX))")
                    .font(.system(size: 11, weight: .semibold, design: .monospaced))
                    .foregroundStyle(CatppuccinMocha.subtext0)
            }
        }
        .padding(12)
        .background(
            RoundedRectangle(cornerRadius: 12)
                .fill(CatppuccinMocha.mantle)
                .overlay(
                    RoundedRectangle(cornerRadius: 12)
                        .stroke(CatppuccinMocha.surface0, lineWidth: 1)
                )
        )
    }

    private var registerPanel: some View {
        VStack(spacing: 8) {
            Text("Registradores Financeiros")
                .font(.system(size: 13, weight: .bold, design: .rounded))
                .foregroundStyle(CatppuccinMocha.text)
                .frame(maxWidth: .infinity, alignment: .leading)

            ForEach(TVMRegister.allCases, id: \.self) { register in
                HStack(spacing: 8) {
                    Text(register.rawValue)
                        .font(.system(size: 12, weight: .bold, design: .monospaced))
                        .foregroundStyle(CatppuccinMocha.subtext0)
                        .frame(width: 34, alignment: .leading)

                    Text(viewModel.registerValue(register))
                        .font(.system(size: 13, design: .monospaced))
                        .foregroundStyle(CatppuccinMocha.text)
                        .frame(maxWidth: .infinity, alignment: .trailing)
                        .padding(.trailing, 4)

                    actionButton(register.rawValue) {
                        viewModel.press(.financial(register))
                    }
                }
            }

            HStack(spacing: 8) {
                Text("CF:")
                    .font(.system(size: 11, weight: .bold, design: .monospaced))
                    .foregroundStyle(CatppuccinMocha.subtext0)
                Text(viewModel.cashFlowCountLabel())
                    .font(.system(size: 11, design: .monospaced))
                    .foregroundStyle(CatppuccinMocha.text)
                Spacer()
                Text("f+NPV: add CF  |  g+NPV: clear CF")
                    .font(.system(size: 10, design: .monospaced))
                    .foregroundStyle(CatppuccinMocha.subtext0)
            }

            if !viewModel.amortizationSummaryLabel().isEmpty {
                HStack(spacing: 8) {
                    Text("Amort:")
                        .font(.system(size: 11, weight: .bold, design: .monospaced))
                        .foregroundStyle(CatppuccinMocha.subtext0)
                    Text(viewModel.amortizationSummaryLabel())
                        .font(.system(size: 11, design: .monospaced))
                        .foregroundStyle(CatppuccinMocha.text)
                    Spacer()
                }
            }
        }
        .padding(12)
        .background(
            RoundedRectangle(cornerRadius: 12)
                .fill(CatppuccinMocha.mantle)
        )
    }

    private var keypad: some View {
        VStack(spacing: 8) {
            keyRow {
                keyButton("f", style: .function) { viewModel.press(.prefixF) }
                keyButton("g", style: .function) { viewModel.press(.prefixG) }
                keyButton("CHS", style: .function) { viewModel.press(.chs) }
                keyButton("CLX", style: .clear) { viewModel.press(.clx) }
                keyButton("x<>y", style: .function) { viewModel.press(.swapXY) }
            }

            keyRow {
                digitButton(7)
                digitButton(8)
                digitButton(9)
                keyButton("÷", style: .operator) { viewModel.press(.divide) }
                keyButton("N", style: .accent) { viewModel.press(.financial(.n)) }
            }

            keyRow {
                digitButton(4)
                digitButton(5)
                digitButton(6)
                keyButton("×", style: .operator) { viewModel.press(.multiply) }
                keyButton("I", style: .accent) { viewModel.press(.financial(.i)) }
            }

            keyRow {
                digitButton(1)
                digitButton(2)
                digitButton(3)
                keyButton("−", style: .operator) { viewModel.press(.subtract) }
                keyButton("PV", style: .accent) { viewModel.press(.financial(.pv)) }
            }

            keyRow {
                digitButton(0)
                keyButton(".", style: .number) { viewModel.press(.decimalPoint) }
                keyButton("+", style: .operator) { viewModel.press(.add) }
                keyButton("PMT", style: .accent) { viewModel.press(.financial(.pmt)) }
                keyButton("FV", style: .accent) { viewModel.press(.financial(.fv)) }
            }

            keyRow {
                keyButton("ENTER", style: .accent) { viewModel.press(.enter) }
                keyButton("NPV", style: .function) { viewModel.press(.npv) }
                keyButton("IRR", style: .function) { viewModel.press(.irr) }
                keyButton("CLR", style: .clear) { viewModel.clearRPN() }
                keyButton("AC", style: .clear) { viewModel.clearAll() }
            }

            keyRow {
                keyButton("AMORT", style: .function) { viewModel.press(.advanced(.amort)) }
                keyButton("BOND", style: .function) { viewModel.press(.advanced(.bond)) }
                keyButton("DEP-SL", style: .function) { viewModel.press(.advanced(.depSL)) }
                keyButton("DEP-DB", style: .function) { viewModel.press(.advanced(.depDB)) }
                keyButton("DEP-SYD", style: .function) { viewModel.press(.advanced(.depSYD)) }
            }
            Text("Operacoes avancadas: use g + tecla")
                .font(.system(size: 10, design: .monospaced))
                .foregroundStyle(CatppuccinMocha.subtext0)
                .frame(maxWidth: .infinity, alignment: .leading)
        }
    }

    private var logPanel: some View {
        VStack(spacing: 8) {
            HStack {
                Text("Histórico")
                    .font(.system(size: 14, weight: .bold, design: .rounded))
                    .foregroundStyle(CatppuccinMocha.text)
                Spacer()
                Button("Limpar") { viewModel.clearLog() }
                    .font(.system(size: 12, weight: .semibold))
                    .foregroundStyle(CatppuccinMocha.red)
            }

            if viewModel.outputLog.isEmpty {
                Text("Sem ações ainda")
                    .font(.system(size: 12, design: .monospaced))
                    .foregroundStyle(CatppuccinMocha.subtext0)
                    .frame(maxWidth: .infinity, alignment: .leading)
            } else {
                ForEach(Array(viewModel.outputLog.prefix(8).enumerated()), id: \.offset) { _, entry in
                    Text(entry)
                        .font(.system(size: 12, design: .monospaced))
                        .foregroundStyle(CatppuccinMocha.text)
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .padding(.vertical, 4)
                }
            }
        }
        .padding(12)
        .background(
            RoundedRectangle(cornerRadius: 12)
                .fill(CatppuccinMocha.mantle)
        )
    }

    private func stackRow(_ label: String, value: Double, highlight: Bool = false) -> some View {
        HStack {
            Text(label)
                .font(.system(size: 12, weight: .bold, design: .monospaced))
                .foregroundStyle(highlight ? CatppuccinMocha.yellow : CatppuccinMocha.subtext0)
                .frame(width: 24, alignment: .leading)

            Text(formatValue(value))
                .font(.system(size: highlight ? 22 : 14, weight: .semibold, design: .monospaced))
                .foregroundStyle(highlight ? CatppuccinMocha.text : CatppuccinMocha.subtext0)
                .frame(maxWidth: .infinity, alignment: .trailing)
        }
    }

    private func keyRow<Content: View>(@ViewBuilder content: () -> Content) -> some View {
        HStack(spacing: 8) {
            content()
        }
    }

    private func digitButton(_ value: Int) -> some View {
        keyButton(String(value), style: .number) { viewModel.press(.digit(value)) }
    }

    private enum KeyStyle {
        case number
        case `operator`
        case function
        case clear
        case accent
    }

    private func keyButton(_ title: String, style: KeyStyle, action: @escaping () -> Void) -> some View {
        let colors: (bg: Color, fg: Color) = {
            switch style {
            case .number: return (CatppuccinMocha.surface0, CatppuccinMocha.text)
            case .operator: return (CatppuccinMocha.peach, CatppuccinMocha.base)
            case .function: return (CatppuccinMocha.surface1, CatppuccinMocha.text)
            case .clear: return (CatppuccinMocha.red, CatppuccinMocha.base)
            case .accent: return (CatppuccinMocha.blue, CatppuccinMocha.base)
            }
        }()

        return Button(action: action) {
            Text(title)
                .font(.system(size: 12, weight: .bold, design: .rounded))
                .foregroundStyle(colors.fg)
                .frame(maxWidth: .infinity)
                .padding(.vertical, 11)
                .background(
                    RoundedRectangle(cornerRadius: 10)
                        .fill(colors.bg)
                )
        }
        .buttonStyle(.plain)
    }

    private func actionButton(_ title: String, action: @escaping () -> Void) -> some View {
        Button(action: action) {
            Text(title)
                .font(.system(size: 10, weight: .bold, design: .rounded))
                .foregroundStyle(CatppuccinMocha.base)
                .padding(.horizontal, 10)
                .padding(.vertical, 6)
                .background(
                    RoundedRectangle(cornerRadius: 8)
                        .fill(CatppuccinMocha.teal)
                )
        }
        .buttonStyle(.plain)
    }

    private func formatValue(_ value: Double) -> String {
        if value.isNaN { return "NaN" }
        if value.isInfinite { return value > 0 ? "inf" : "-inf" }
        if value == value.rounded(), abs(value) < 1e15 {
            return String(format: "%.0f", value)
        }
        return String(format: "%.10g", value)
    }

    private func prefixLabel(_ prefix: RPNPrefix) -> String {
        switch prefix {
        case .none: return "-"
        case .f: return "f"
        case .g: return "g"
        }
    }
}

#Preview {
    FinancialCalculatorView()
}
