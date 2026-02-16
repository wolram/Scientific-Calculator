import SwiftUI

// MARK: - Calculator Type

enum CalculatorType: String, CaseIterable, Identifiable {
    case scientific = "Científica"
    case financial = "Financeira"
    case graphing = "Gráfica"

    var id: String { rawValue }

    var icon: String {
        switch self {
        case .scientific: return "function"
        case .financial: return "chart.bar.fill"
        case .graphing: return "chart.xyaxis.line"
        }
    }

    var description: String {
        switch self {
        case .scientific:
            return "Funções trigonométricas, logarítmicas, potências, raízes, fatorial e constantes matemáticas"
        case .financial:
            return "TVM, NPV, IRR, amortização, depreciação e cálculos financeiros estilo HP 12C"
        case .graphing:
            return "Plotagem de até 6 funções simultâneas com zoom, pan e avaliação de expressões"
        }
    }

    var accentColor: Color {
        switch self {
        case .scientific: return CatppuccinMocha.blue
        case .financial: return CatppuccinMocha.green
        case .graphing: return CatppuccinMocha.mauve
        }
    }
}

// MARK: - Main Menu View

struct MainMenuView: View {
    @State private var selectedCalculator: CalculatorType?
    @State private var hoveredCard: CalculatorType?

    var body: some View {
        NavigationStack {
            ZStack {
                CatppuccinMocha.base.ignoresSafeArea()

                ScrollView {
                    VStack(spacing: 28) {
                        headerSection
                        calculatorCards
                        footerSection
                    }
                    .padding(24)
                }
            }
            .navigationDestination(item: $selectedCalculator) { calculator in
                destinationView(for: calculator)
            }
        }
        #if os(macOS)
        .frame(minWidth: 520, minHeight: 700)
        #endif
    }

    // MARK: - Header

    private var headerSection: some View {
        VStack(spacing: 12) {
            Image(systemName: "sum")
                .font(.system(size: 48, weight: .thin))
                .foregroundStyle(
                    LinearGradient(
                        colors: [CatppuccinMocha.blue, CatppuccinMocha.mauve],
                        startPoint: .topLeading,
                        endPoint: .bottomTrailing
                    )
                )

            Text("Multi Calculator Pro")
                .font(.system(size: 28, weight: .bold, design: .rounded))
                .foregroundStyle(CatppuccinMocha.text)

            Text("Escolha sua calculadora")
                .font(.system(size: 16, weight: .medium, design: .rounded))
                .foregroundStyle(CatppuccinMocha.subtext0)
        }
        .padding(.top, 20)
    }

    // MARK: - Calculator Cards

    private var calculatorCards: some View {
        VStack(spacing: 16) {
            ForEach(CalculatorType.allCases) { calculator in
                calculatorCard(calculator)
            }
        }
    }

    private func calculatorCard(_ calculator: CalculatorType) -> some View {
        Button {
            selectedCalculator = calculator
        } label: {
            HStack(spacing: 16) {
                // Icon
                ZStack {
                    RoundedRectangle(cornerRadius: 14)
                        .fill(calculator.accentColor.opacity(0.15))
                        .frame(width: 56, height: 56)

                    Image(systemName: calculator.icon)
                        .font(.system(size: 24, weight: .semibold))
                        .foregroundStyle(calculator.accentColor)
                }

                // Text
                VStack(alignment: .leading, spacing: 6) {
                    Text("Calculadora \(calculator.rawValue)")
                        .font(.system(size: 17, weight: .bold, design: .rounded))
                        .foregroundStyle(CatppuccinMocha.text)

                    Text(calculator.description)
                        .font(.system(size: 13, weight: .regular, design: .rounded))
                        .foregroundStyle(CatppuccinMocha.subtext0)
                        .lineLimit(2)
                        .multilineTextAlignment(.leading)
                }

                Spacer()

                Image(systemName: "chevron.right")
                    .font(.system(size: 14, weight: .semibold))
                    .foregroundStyle(CatppuccinMocha.overlay0)
            }
            .cardStyle(isHovered: hoveredCard == calculator)
        }
        .buttonStyle(.plain)
        .onHover { isHovered in
            hoveredCard = isHovered ? calculator : nil
        }
    }

    // MARK: - Footer

    private var footerSection: some View {
        VStack(spacing: 8) {
            Divider()
                .background(CatppuccinMocha.surface0)

            Text("Swift 6.0 • SwiftUI • Catppuccin Mocha")
                .font(.system(size: 12, weight: .medium, design: .monospaced))
                .foregroundStyle(CatppuccinMocha.overlay0)
        }
        .padding(.top, 8)
    }

    // MARK: - Destination

    @ViewBuilder
    private func destinationView(for calculator: CalculatorType) -> some View {
        switch calculator {
        case .scientific:
            ScientificCalculatorView()
                .navigationTitle("Calculadora Científica")
                #if os(iOS)
                .navigationBarTitleDisplayMode(.inline)
                #endif
        case .financial:
            FinancialCalculatorView()
                .navigationTitle("Calculadora Financeira")
                #if os(iOS)
                .navigationBarTitleDisplayMode(.inline)
                #endif
        case .graphing:
            GraphingCalculatorView()
                .navigationTitle("Calculadora Gráfica")
                #if os(iOS)
                .navigationBarTitleDisplayMode(.inline)
                #endif
        }
    }
}

#Preview {
    MainMenuView()
}
