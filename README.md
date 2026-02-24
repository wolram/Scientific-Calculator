# Scientific Calculator — Multi-mode professional calculator for Apple and Android

A cross-platform calculator app with three integrated modes: Scientific (Casio/HP), Financial (HP 12C), and Graphing (HP 50G), built with a dark Catppuccin Mocha theme.

## Features

### Scientific Calculator
- Trigonometric, inverse, and hyperbolic functions
- Logarithms (ln, log10, log2), powers, and roots
- Factorial, permutation (nPr), combination (nCr)
- Constants (pi, e, phi), memory operations, expression parser

### Financial Calculator (HP 12C)
- Time Value of Money (N, i%, PV, PMT, FV)
- Cash flow analysis (NPV, IRR)
- Amortization (Price table), depreciation (SL, DB, SYD)
- Bond pricing, percentage calculations

### Graphing Calculator
- Plot up to 6 simultaneous functions
- Zoom, pan, grid, and axis controls
- Degree/radian mode toggle

## Tech Stack

### Apple (macOS/iOS)
- **Swift 6.0** with strict concurrency
- **SwiftUI** with `@Observable` macro
- **Swift Package Manager**
- **Swift Testing** for unit tests

### Android
- **Kotlin 1.9** with Jetpack Compose
- **Material Design 3** (Material You)
- **Navigation Compose**
- **Gradle 8.5** with Kotlin DSL

## Getting Started

### Swift (macOS 14+ / iOS 17+)

```bash
swift build
swift run ScientificCalculator
```

Or open `Package.swift` in Xcode 16+.

### Android (8.0+)

```bash
cd android-app
./gradlew assembleDebug
./gradlew installDebug   # Install on device
```

Or open `android-app/` in Android Studio.

### Tests

```bash
swift test                          # Swift
cd android-app && ./gradlew test    # Android
```

## Project Structure

```
├── Sources/ScientificCalculator/   # Swift source
│   ├── App/                        # Entry point
│   ├── Models/                     # Engines and expression parser
│   ├── ViewModels/                 # State management
│   ├── Views/                      # UI screens
│   └── Theme/                      # Catppuccin Mocha theme
├── android-app/                    # Kotlin source
│   └── app/src/main/java/.../
│       ├── engine/                 # Engines and expression parser
│       ├── viewmodel/              # State management
│       └── ui/                     # Screens, components, theme
└── Tests/                          # Swift unit tests
```
