# Multi Calculator Pro

Calculadora profissional multi-modo com 3 calculadoras integradas em uma interface moderna e dark theme.

## Calculadoras

### Científica (Casio/HP)
- Funções trigonométricas (sin, cos, tan) em graus e radianos
- Funções inversas (asin, acos, atan) e hiperbólicas (sinh, cosh, tanh)
- Logaritmos (ln, log10, log2)
- Potências, raízes (quadrada, cúbica, n-ésima)
- Fatorial, permutação (nPr), combinação (nCr)
- Constantes (π, e, φ)
- Parser de expressões com parênteses
- Memória (MC, MR, M+)
- Modo 2nd para funções inversas

### Financeira (HP 12C)
- **TVM**: N, i%, PV, PMT, FV (início/fim do período)
- **Fluxo de caixa**: NPV, IRR
- **Amortização**: Tabela Price completa
- **Depreciação**: Linear (SL), Saldo Decrescente (DB), Soma dos Dígitos (SYD)
- **Bonds**: Preço de títulos
- **Percentuais**: %, Δ%, %T, Markup, Margem
- **Estatística**: Média, desvio padrão, regressão linear

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
- Java 17+
- Maven 3.9+

## Build
```bash
mvn clean install
```

## Executar
```bash
java -cp scientific-calculator/target/scientific-calculator-1.0.0.jar com.vibecoding.calculator.CalculatorApp
```

## Arquitetura

```
com.vibecoding.calculator/
├── CalculatorApp.java              # Entry point → MainMenuUI
├── engine/
│   ├── ScientificEngine.java       # Motor científico completo
│   └── FinancialEngine.java        # Motor financeiro HP 12C
├── parser/
│   └── ExpressionParser.java       # Parser recursivo de expressões
├── graph/
│   └── GraphPanel.java             # Painel de plotagem de funções
└── ui/
    ├── Theme.java                  # Tema dark compartilhado
    ├── MainMenuUI.java             # Menu principal
    ├── ScientificCalculatorUI.java # Calc. científica
    ├── FinancialCalculatorUI.java  # Calc. financeira
    └── GraphingCalculatorUI.java   # Calc. gráfica
```

## Tecnologias
- Java 17+ com Swing/AWT
- Maven multi-module
- JUnit Jupiter 5.10.2

## Notas
- Interface dark theme inspirada no Catppuccin Mocha
- Botões com cantos arredondados e efeitos de hover
- Navegação entre calculadoras via menu principal
- Atalhos de teclado em todas as calculadoras
