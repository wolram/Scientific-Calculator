# Scientific Calculator Monorepo

Calculadora multi-plataforma organizada por tecnologia para acelerar distribuicao, marketing e construcao em publico (build in public).

## Plataformas

- Windows: app desktop (Java Swing)
- Linux: app desktop (Java Swing)
- macOS: app desktop (Java Swing) + app Apple (SwiftUI)
- iPadOS: app Apple (SwiftUI)
- iOS: app Apple (SwiftUI)
- Android: app Kotlin + Jetpack Compose

## Estrutura do repositorio

```text
.
├── apps/
│   ├── android-compose/                 # Android app (Kotlin/Compose)
│   ├── apple-swiftui/                   # Apple app (macOS/iPadOS/iOS)
│   ├── desktop-java/                    # Desktop app (Windows/Linux/macOS)
│       └── scientific-calculator/
│   └── web/                             # Website oficial (landing page)
├── docs/
│   ├── BUILD_IN_PUBLIC.md               # Ritual de publicacao e progresso
│   └── PLATFORM_MATRIX.md               # Mapa de plataforma x tecnologia
├── marketing/
│   └── graphics-templates/              # Assets e templates de conteudo
└── .github/workflows/
```

Ver tambem `apps/README.md` para o mapa rapido por tecnologia.

## Como rodar

### Apple (SwiftUI)

```bash
cd apps/apple-swiftui
swift build
swift run ScientificCalculator
```

### Android (Compose)

```bash
cd apps/android-compose
gradle assembleDebug
```

### Desktop (Java Swing)

```bash
cd apps/desktop-java
mvn -q -pl scientific-calculator package
java -jar scientific-calculator/target/scientific-calculator-1.0.0.jar
```

### Website (Landing Page)

```bash
cd apps/web
python3 -m http.server 8080
# abrir http://localhost:8080
```

## Build in Public

- Ver o playbook em `docs/BUILD_IN_PUBLIC.md`
- Ver o status de plataformas em `docs/PLATFORM_MATRIX.md`

## Posicionamento

Um produto unico com 3 modos (cientifica, financeira e grafica), distribuido como familia de apps por tecnologia, com roadmap publico e narrativa de evolucao por plataforma.
