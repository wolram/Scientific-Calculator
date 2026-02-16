# Multi Calculator Pro

Calculadora profissional multi-modo com 3 calculadoras integradas em uma interface moderna e dark theme.

**Swift 6.0 + SwiftUI** | macOS 14+ | iOS 17+

## Calculadoras

### Científica (Casio/HP)
- Funções trigonométricas (sin, cos, tan) em graus e radianos
- Funções inversas (asin, acos, atan) e hiperbólicas (sinh, cosh, tanh)
- Logaritmos (ln, log10, log2)
- Potências, raízes (quadrada, cúbica, n-ésima)
- Fatorial, permutação (nPr), combinação (nCr)
- Constantes (π, e, φ)
- Parser de expressões com parênteses
- Memória (MC, MR, M+, M−)
- Modo 2nd para funções inversas

### Financeira (HP 12C)
- **TVM**: N, i%, PV, PMT, FV (início/fim do período)
- **Fluxo de caixa**: NPV, IRR
- **Amortização**: Tabela Price completa
- **Depreciação**: Linear (SL), Saldo Decrescente (DB), Soma dos Dígitos (SYD)
- **Bonds**: Preço de títulos
- **Percentuais**: %, Δ%, Markup, Margem

### Gráfica (HP 50G)
- Plotagem de até 6 funções simultâneas
- Zoom, pan e reset de visualização
- Grade e eixos configuráveis
- Parser de expressões completo
- Avaliação rápida de expressões
- Presets de funções comuns
- Cores distintas por função
- Modo graus/radianos

## Requisitos

- Swift 6.0+
- macOS 14 (Sonoma)+ ou iOS 17+
- Xcode 16+

## Build

```bash
swift build
```

## Executar

```bash
swift run ScientificCalculator
```

Ou abra `Package.swift` no Xcode.

## Testes

```bash
swift test
```

## Arquitetura

```
Sources/ScientificCalculator/
├── App/
│   └── ScientificCalculatorApp.swift      # Entry point @main
├── Models/
│   ├── ScientificEngine.swift             # Motor científico completo
│   ├── FinancialEngine.swift              # Motor financeiro HP 12C
│   └── ExpressionParser.swift             # Parser recursivo de expressões
├── ViewModels/
│   ├── ScientificViewModel.swift          # Estado da calc. científica
│   ├── FinancialViewModel.swift           # Estado da calc. financeira
│   └── GraphingViewModel.swift            # Estado da calc. gráfica
├── Views/
│   ├── MainMenuView.swift                 # Menu principal com cards
│   ├── ScientificCalculatorView.swift     # Calc. científica
│   ├── FinancialCalculatorView.swift      # Calc. financeira
│   └── GraphingCalculatorView.swift       # Calc. gráfica com Canvas
└── Theme/
    └── Theme.swift                        # Catppuccin Mocha + estilos
```

## Tecnologias

- **Swift 6.0** com concorrência strict (`Sendable`, `@MainActor`)
- **SwiftUI** com `@Observable` macro (Observation framework)
- **Swift Package Manager** (multi-platform)
- **Swift Testing** framework para testes unitários
- **Canvas API** para plotagem de gráficos

## Design

- Interface dark theme **Catppuccin Mocha**
- Botões com cantos arredondados e animações de press
- Navegação `NavigationStack` entre calculadoras
- Layout adaptativo macOS/iOS com `#if os()`
