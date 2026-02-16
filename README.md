# Multi Calculator Pro

Calculadora profissional multi-modo com 3 calculadoras integradas em uma interface moderna e dark theme.

**Swift 6.0 + SwiftUI** | macOS 14+ | iOS 17+ | **Kotlin + Jetpack Compose** | Android 8.0+

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

### Swift (macOS/iOS)
- Swift 6.0+
- macOS 14 (Sonoma)+ ou iOS 17+
- Xcode 16+

### Android
- Android Studio Hedgehog (2023.1.1)+
- Android SDK 34
- JDK 17+
- Dispositivo ou emulador com Android 8.0+ (API 26+)

## Build & Executar

### Swift

```bash
swift build
swift run ScientificCalculator
```

Ou abra `Package.swift` no Xcode.

### Android

```bash
cd android-app
./gradlew assembleDebug
```

Ou abra a pasta `android-app/` no Android Studio.

Para instalar no dispositivo:
```bash
./gradlew installDebug
```

## Testes

```bash
# Swift
swift test

# Android
cd android-app && ./gradlew test
```

## Arquitetura

### Swift (macOS/iOS)

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

### Android (Kotlin + Jetpack Compose)

```
android-app/app/src/main/java/com/vibecoding/calculator/
├── MainActivity.kt                        # Entry point
├── engine/
│   ├── ScientificEngine.kt               # Motor científico completo
│   ├── FinancialEngine.kt                # Motor financeiro HP 12C
│   └── ExpressionParser.kt               # Parser recursivo de expressões
├── viewmodel/
│   ├── ScientificViewModel.kt            # Estado da calc. científica
│   ├── FinancialViewModel.kt             # Estado da calc. financeira
│   └── GraphingViewModel.kt              # Estado da calc. gráfica
├── ui/
│   ├── theme/
│   │   ├── Color.kt                      # Catppuccin Mocha palette
│   │   ├── Theme.kt                      # Material3 dark theme
│   │   └── Type.kt                       # Tipografia
│   ├── navigation/
│   │   └── AppNavigation.kt              # NavHost com rotas
│   ├── screens/
│   │   ├── MainMenuScreen.kt             # Menu principal com cards
│   │   ├── ScientificCalculatorScreen.kt # Calc. científica
│   │   ├── FinancialCalculatorScreen.kt  # Calc. financeira
│   │   └── GraphingCalculatorScreen.kt   # Calc. gráfica com Canvas
│   └── components/
│       ├── CalcButton.kt                 # Botão reutilizável
│       └── DisplayPanel.kt              # Painel de display
```

## Tecnologias

### Swift
- **Swift 6.0** com concorrência strict (`Sendable`, `@MainActor`)
- **SwiftUI** com `@Observable` macro (Observation framework)
- **Swift Package Manager** (multi-platform)
- **Swift Testing** framework para testes unitários
- **Canvas API** para plotagem de gráficos

### Android
- **Kotlin 1.9** com Jetpack Compose
- **Material Design 3** (Material You)
- **Navigation Compose** para navegação entre telas
- **ViewModel** com Compose state management
- **Canvas API** para plotagem de gráficos
- **Gradle 8.5** com Kotlin DSL

## Design

- Interface dark theme **Catppuccin Mocha**
- Botões com cantos arredondados e animações de press
- Navegação entre calculadoras (NavigationStack no iOS, NavHost no Android)
- Layout adaptativo por plataforma
