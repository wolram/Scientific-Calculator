import SwiftUI

// MARK: - Financial Calculator View (HP 12C-style)

struct FinancialCalculatorView: View {
    @State private var viewModel = FinancialViewModel()
    @State private var selectedTab = 0

    var body: some View {
        VStack(spacing: 0) {
            headerBar
            tabSelector
            tabContent
        }
        .background(CatppuccinMocha.base)
    }

    // MARK: - Header

    private var headerBar: some View {
        HStack {
            Text("Calculadora Financeira")
                .font(.system(size: 18, weight: .bold, design: .rounded))
                .foregroundStyle(CatppuccinMocha.text)

            Spacer()

            Toggle(isOn: $viewModel.beginMode) {
                Text(viewModel.beginMode ? "BEGIN" : "END")
                    .font(.system(size: 12, weight: .bold, design: .monospaced))
                    .foregroundStyle(CatppuccinMocha.base)
            }
            .toggleStyle(.button)
            .tint(CatppuccinMocha.green)
        }
        .padding(16)
    }

    // MARK: - Tab Selector

    private var tabSelector: some View {
        HStack(spacing: 0) {
            tabButton("TVM", index: 0)
            tabButton("Fluxo", index: 1)
            tabButton("Amort.", index: 2)
            tabButton("Log", index: 3)
        }
        .background(CatppuccinMocha.mantle)
    }

    private func tabButton(_ title: String, index: Int) -> some View {
        Button {
            withAnimation(.easeInOut(duration: 0.2)) {
                selectedTab = index
            }
        } label: {
            Text(title)
                .font(.system(size: 14, weight: .semibold, design: .rounded))
                .foregroundStyle(selectedTab == index ? CatppuccinMocha.blue : CatppuccinMocha.subtext0)
                .frame(maxWidth: .infinity)
                .padding(.vertical, 12)
                .overlay(alignment: .bottom) {
                    if selectedTab == index {
                        Rectangle()
                            .fill(CatppuccinMocha.blue)
                            .frame(height: 2)
                    }
                }
        }
        .buttonStyle(.plain)
    }

    // MARK: - Tab Content

    @ViewBuilder
    private var tabContent: some View {
        ScrollView {
            switch selectedTab {
            case 0: tvmSection
            case 1: cashFlowSection
            case 2: amortizationSection
            case 3: logSection
            default: EmptyView()
            }
        }
        .padding(16)
    }

    // MARK: - TVM Section

    private var tvmSection: some View {
        VStack(spacing: 16) {
            VStack(spacing: 12) {
                registerField("N (Períodos)", text: $viewModel.nPeriods)
                registerField("i% (Taxa)", text: $viewModel.interestRate)
                registerField("PV (Valor Presente)", text: $viewModel.presentValue)
                registerField("PMT (Pagamento)", text: $viewModel.payment)
                registerField("FV (Valor Futuro)", text: $viewModel.futureValue)
            }

            LazyVGrid(columns: [
                GridItem(.flexible()),
                GridItem(.flexible()),
                GridItem(.flexible())
            ], spacing: 10) {
                calcButton("N") { viewModel.calculateN() }
                calcButton("i%") { viewModel.calculateI() }
                calcButton("PV") { viewModel.calculatePV() }
                calcButton("PMT") { viewModel.calculatePMT() }
                calcButton("FV") { viewModel.calculateFV() }
                clearButton { viewModel.clearAll() }
            }
        }
    }

    // MARK: - Cash Flow Section

    private var cashFlowSection: some View {
        VStack(spacing: 16) {
            Text("Fluxos de Caixa")
                .font(.system(size: 16, weight: .semibold, design: .rounded))
                .foregroundStyle(CatppuccinMocha.text)
                .frame(maxWidth: .infinity, alignment: .leading)

            ForEach(Array(viewModel.cashFlows.enumerated()), id: \.offset) { index, _ in
                HStack(spacing: 8) {
                    Text("CF\(index)")
                        .font(.system(size: 13, weight: .bold, design: .monospaced))
                        .foregroundStyle(CatppuccinMocha.subtext0)
                        .frame(width: 40)

                    TextField("0.00", text: $viewModel.cashFlows[index])
                        .textFieldStyle(.roundedBorder)
                        .font(.system(size: 15, design: .monospaced))
                        #if os(iOS)
                        .keyboardType(.decimalPad)
                        #endif

                    if viewModel.cashFlows.count > 1 {
                        Button {
                            viewModel.removeCashFlow(at: index)
                        } label: {
                            Image(systemName: "minus.circle.fill")
                                .foregroundStyle(CatppuccinMocha.red)
                        }
                        .buttonStyle(.plain)
                    }
                }
            }

            Button {
                viewModel.addCashFlow()
            } label: {
                Label("Adicionar Fluxo", systemImage: "plus.circle.fill")
                    .font(.system(size: 14, weight: .semibold))
                    .foregroundStyle(CatppuccinMocha.green)
            }
            .buttonStyle(.plain)

            HStack(spacing: 12) {
                calcButton("NPV") { viewModel.calculateNPV() }
                calcButton("IRR") { viewModel.calculateIRR() }
            }

            if !viewModel.npvResult.isEmpty {
                resultRow("NPV", value: viewModel.npvResult)
            }
            if !viewModel.irrResult.isEmpty {
                resultRow("IRR", value: "\(viewModel.irrResult)%")
            }
        }
    }

    // MARK: - Amortization Section

    private var amortizationSection: some View {
        VStack(spacing: 16) {
            VStack(spacing: 12) {
                registerField("Principal", text: $viewModel.amortPrincipal)
                registerField("Taxa Anual (%)", text: $viewModel.amortRate)
                registerField("Períodos", text: $viewModel.amortPeriods)
            }

            Button {
                viewModel.calculateAmortization()
            } label: {
                Text("Gerar Tabela")
                    .font(.system(size: 15, weight: .bold, design: .rounded))
                    .foregroundStyle(CatppuccinMocha.base)
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, 12)
                    .background(
                        RoundedRectangle(cornerRadius: 12)
                            .fill(CatppuccinMocha.blue)
                    )
            }
            .buttonStyle(.plain)

            if !viewModel.amortSchedule.isEmpty {
                amortizationTable
            }
        }
    }

    private var amortizationTable: some View {
        VStack(spacing: 0) {
            // Header
            HStack(spacing: 0) {
                tableHeader("#", width: 40)
                tableHeader("Pgto", width: nil)
                tableHeader("Principal", width: nil)
                tableHeader("Juros", width: nil)
                tableHeader("Saldo", width: nil)
            }
            .background(CatppuccinMocha.surface0)

            // Rows
            ForEach(viewModel.amortSchedule.prefix(60), id: \.period) { row in
                HStack(spacing: 0) {
                    tableCell("\(row.period)", width: 40)
                    tableCell(String(format: "%.2f", row.payment), width: nil)
                    tableCell(String(format: "%.2f", row.principal), width: nil)
                    tableCell(String(format: "%.2f", row.interest), width: nil)
                    tableCell(String(format: "%.2f", row.balance), width: nil)
                }
                .background(
                    row.period % 2 == 0
                    ? CatppuccinMocha.surface0.opacity(0.3)
                    : Color.clear
                )
            }
        }
        .clipShape(RoundedRectangle(cornerRadius: 12))
        .overlay(
            RoundedRectangle(cornerRadius: 12)
                .stroke(CatppuccinMocha.surface1, lineWidth: 1)
        )
    }

    // MARK: - Log Section

    private var logSection: some View {
        VStack(spacing: 12) {
            HStack {
                Text("Histórico")
                    .font(.system(size: 16, weight: .semibold, design: .rounded))
                    .foregroundStyle(CatppuccinMocha.text)
                Spacer()
                Button("Limpar") { viewModel.clearLog() }
                    .font(.system(size: 13, weight: .semibold))
                    .foregroundStyle(CatppuccinMocha.red)
            }

            if viewModel.outputLog.isEmpty {
                Text("Nenhum cálculo realizado")
                    .font(.system(size: 14, design: .monospaced))
                    .foregroundStyle(CatppuccinMocha.subtext0)
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, 40)
            } else {
                ForEach(Array(viewModel.outputLog.enumerated()), id: \.offset) { _, entry in
                    Text(entry)
                        .font(.system(size: 14, design: .monospaced))
                        .foregroundStyle(CatppuccinMocha.text)
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .padding(10)
                        .background(
                            RoundedRectangle(cornerRadius: 8)
                                .fill(CatppuccinMocha.surface0)
                        )
                }
            }
        }
    }

    // MARK: - Reusable Components

    private func registerField(_ label: String, text: Binding<String>) -> some View {
        VStack(alignment: .leading, spacing: 4) {
            Text(label)
                .font(.system(size: 12, weight: .semibold, design: .rounded))
                .foregroundStyle(CatppuccinMocha.subtext0)

            TextField("0", text: text)
                .font(.system(size: 16, design: .monospaced))
                .foregroundStyle(CatppuccinMocha.text)
                .padding(10)
                .background(
                    RoundedRectangle(cornerRadius: 10)
                        .fill(CatppuccinMocha.mantle)
                        .overlay(
                            RoundedRectangle(cornerRadius: 10)
                                .stroke(CatppuccinMocha.surface0, lineWidth: 1)
                        )
                )
                #if os(iOS)
                .keyboardType(.decimalPad)
                #endif
        }
    }

    private func calcButton(_ title: String, action: @escaping () -> Void) -> some View {
        Button(action: action) {
            Text(title)
                .font(.system(size: 14, weight: .bold, design: .rounded))
                .foregroundStyle(CatppuccinMocha.base)
                .frame(maxWidth: .infinity)
                .padding(.vertical, 12)
                .background(
                    RoundedRectangle(cornerRadius: 10)
                        .fill(CatppuccinMocha.peach)
                )
        }
        .buttonStyle(.plain)
    }

    private func clearButton(action: @escaping () -> Void) -> some View {
        Button(action: action) {
            Text("CLR")
                .font(.system(size: 14, weight: .bold, design: .rounded))
                .foregroundStyle(CatppuccinMocha.base)
                .frame(maxWidth: .infinity)
                .padding(.vertical, 12)
                .background(
                    RoundedRectangle(cornerRadius: 10)
                        .fill(CatppuccinMocha.red)
                )
        }
        .buttonStyle(.plain)
    }

    private func resultRow(_ label: String, value: String) -> some View {
        HStack {
            Text(label)
                .font(.system(size: 14, weight: .bold, design: .monospaced))
                .foregroundStyle(CatppuccinMocha.subtext0)
            Spacer()
            Text(value)
                .font(.system(size: 16, weight: .semibold, design: .monospaced))
                .foregroundStyle(CatppuccinMocha.green)
        }
        .padding(12)
        .background(
            RoundedRectangle(cornerRadius: 10)
                .fill(CatppuccinMocha.surface0)
        )
    }

    private func tableHeader(_ text: String, width: CGFloat?) -> some View {
        Group {
            if let width {
                Text(text)
                    .frame(width: width)
            } else {
                Text(text)
                    .frame(maxWidth: .infinity)
            }
        }
        .font(.system(size: 11, weight: .bold, design: .monospaced))
        .foregroundStyle(CatppuccinMocha.subtext0)
        .padding(.vertical, 8)
    }

    private func tableCell(_ text: String, width: CGFloat?) -> some View {
        Group {
            if let width {
                Text(text)
                    .frame(width: width)
            } else {
                Text(text)
                    .frame(maxWidth: .infinity)
            }
        }
        .font(.system(size: 11, design: .monospaced))
        .foregroundStyle(CatppuccinMocha.text)
        .padding(.vertical, 6)
    }
}

#Preview {
    FinancialCalculatorView()
}
