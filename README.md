# Swing Projects: Scientific Calculator & Graphics Templates

## Requisitos
- Java 17+
- Maven 3.9+

## Estrutura
- `scientific-calculator/` – Calculadora científica (Swing)
- `graphics-templates/` – Templates gráficos (Swing/AWT)

## Build
```bash
mvn clean install
```

## Executar
- Calculadora:
```bash
java -cp scientific-calculator/target/scientific-calculator-1.0.0.jar com.vibecoding.calculator.CalculatorApp
```

- Templates Gráficos:
```bash
java -cp graphics-templates/target/graphics-templates-1.0.0.jar com.vibecoding.templates.TemplateApp
```

## Notas
- Ambos os projetos usam `JFrame` com `main()` independente.
- A calculadora implementa: +, -, ×, ÷, potências, exp, ln, log10, sqrt, fatorial, trigonometria em radianos e graus, atalhos de teclado e tratamento de erros.
- Templates incluem: Banner estilizado, Cards, elementos interativos (botões, slider, color picker) e animação com `Timer`, usando `Graphics2D` e `AffineTransform`.

## Testes
```bash
mvn -pl scientific-calculator test
```

