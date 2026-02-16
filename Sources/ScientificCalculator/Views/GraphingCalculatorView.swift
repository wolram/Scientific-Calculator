import SwiftUI

// MARK: - Graphing Calculator View

struct GraphingCalculatorView: View {
    @State private var viewModel = GraphingViewModel()

    var body: some View {
        VStack(spacing: 0) {
            headerBar

            #if os(macOS)
            HSplitView {
                graphCanvas
                    .frame(minWidth: 400)
                controlPanel
                    .frame(width: 280)
            }
            #else
            VStack(spacing: 0) {
                graphCanvas
                    .frame(minHeight: 300)
                controlPanel
            }
            #endif
        }
        .background(CatppuccinMocha.base)
    }

    // MARK: - Header

    private var headerBar: some View {
        HStack {
            Text("Calculadora Gráfica")
                .font(.system(size: 18, weight: .bold, design: .rounded))
                .foregroundStyle(CatppuccinMocha.text)

            Spacer()

            Button(viewModel.angleMode.rawValue) {
                viewModel.angleMode = viewModel.angleMode == .degrees ? .radians : .degrees
            }
            .font(.system(size: 13, weight: .bold, design: .monospaced))
            .foregroundStyle(CatppuccinMocha.base)
            .padding(.horizontal, 12)
            .padding(.vertical, 6)
            .background(Capsule().fill(CatppuccinMocha.teal))
        }
        .padding(16)
    }

    // MARK: - Graph Canvas

    private var graphCanvas: some View {
        Canvas { context, size in
            let width = size.width
            let height = size.height

            // Background
            context.fill(
                Path(CGRect(origin: .zero, size: size)),
                with: .color(CatppuccinMocha.mantle)
            )

            let xRange = viewModel.xMax - viewModel.xMin
            let yRange = viewModel.yMax - viewModel.yMin

            func toScreenX(_ x: Double) -> CGFloat {
                CGFloat((x - viewModel.xMin) / xRange) * width
            }

            func toScreenY(_ y: Double) -> CGFloat {
                height - CGFloat((y - viewModel.yMin) / yRange) * height
            }

            // Grid
            if viewModel.showGrid {
                let gridSpacing = calculateGridSpacing(range: xRange)
                var gridPath = Path()

                // Vertical grid lines
                var x = (viewModel.xMin / gridSpacing).rounded(.down) * gridSpacing
                while x <= viewModel.xMax {
                    let sx = toScreenX(x)
                    gridPath.move(to: CGPoint(x: sx, y: 0))
                    gridPath.addLine(to: CGPoint(x: sx, y: height))
                    x += gridSpacing
                }

                // Horizontal grid lines
                let yGridSpacing = calculateGridSpacing(range: yRange)
                var y = (viewModel.yMin / yGridSpacing).rounded(.down) * yGridSpacing
                while y <= viewModel.yMax {
                    let sy = toScreenY(y)
                    gridPath.move(to: CGPoint(x: 0, y: sy))
                    gridPath.addLine(to: CGPoint(x: width, y: sy))
                    y += yGridSpacing
                }

                context.stroke(gridPath, with: .color(CatppuccinMocha.surface0), lineWidth: 0.5)
            }

            // Axes
            if viewModel.showAxes {
                var axesPath = Path()

                // X axis
                if viewModel.yMin <= 0 && viewModel.yMax >= 0 {
                    let y0 = toScreenY(0)
                    axesPath.move(to: CGPoint(x: 0, y: y0))
                    axesPath.addLine(to: CGPoint(x: width, y: y0))
                }

                // Y axis
                if viewModel.xMin <= 0 && viewModel.xMax >= 0 {
                    let x0 = toScreenX(0)
                    axesPath.move(to: CGPoint(x: x0, y: 0))
                    axesPath.addLine(to: CGPoint(x: x0, y: height))
                }

                context.stroke(axesPath, with: .color(CatppuccinMocha.overlay0), lineWidth: 1.5)
            }

            // Plot functions
            let steps = Int(width)
            let dx = xRange / Double(steps)

            for (index, entry) in viewModel.functions.enumerated() {
                guard entry.isEnabled, !entry.expression.isEmpty else { continue }

                var path = Path()
                var isFirstPoint = true
                var lastY: Double?

                for step in 0...steps {
                    let x = viewModel.xMin + Double(step) * dx
                    guard let y = viewModel.evaluateFunction(at: index, x: x) else {
                        isFirstPoint = true
                        lastY = nil
                        continue
                    }

                    // Discontinuity detection
                    if let ly = lastY, abs(y - ly) > yRange * 2 {
                        isFirstPoint = true
                    }

                    let point = CGPoint(x: toScreenX(x), y: toScreenY(y))

                    if isFirstPoint {
                        path.move(to: point)
                        isFirstPoint = false
                    } else {
                        path.addLine(to: point)
                    }
                    lastY = y
                }

                context.stroke(path, with: .color(entry.color), lineWidth: 2)
            }
        }
        .clipShape(RoundedRectangle(cornerRadius: 0))
        .gesture(
            MagnifyGesture()
                .onChanged { value in
                    if value.magnification > 1 {
                        viewModel.zoomIn()
                    } else {
                        viewModel.zoomOut()
                    }
                }
        )
    }

    // MARK: - Control Panel

    private var controlPanel: some View {
        ScrollView {
            VStack(spacing: 16) {
                functionsSection
                viewportControls
                presetsSection
                quickEvaluator
            }
            .padding(16)
        }
        .background(CatppuccinMocha.base)
    }

    // MARK: - Functions Section

    private var functionsSection: some View {
        VStack(spacing: 10) {
            HStack {
                Text("Funções")
                    .font(.system(size: 14, weight: .bold, design: .rounded))
                    .foregroundStyle(CatppuccinMocha.text)
                Spacer()
                if viewModel.functions.count < 6 {
                    Button {
                        viewModel.addFunction()
                    } label: {
                        Image(systemName: "plus.circle.fill")
                            .foregroundStyle(CatppuccinMocha.green)
                    }
                    .buttonStyle(.plain)
                }
            }

            ForEach(Array(viewModel.functions.enumerated()), id: \.element.id) { index, entry in
                HStack(spacing: 8) {
                    Circle()
                        .fill(entry.color)
                        .frame(width: 12, height: 12)

                    Text("f\(index + 1)(x) =")
                        .font(.system(size: 12, weight: .bold, design: .monospaced))
                        .foregroundStyle(CatppuccinMocha.subtext0)

                    TextField("ex: sin(x)", text: $viewModel.functions[index].expression)
                        .font(.system(size: 13, design: .monospaced))
                        .textFieldStyle(.roundedBorder)

                    if viewModel.functions.count > 1 {
                        Button {
                            viewModel.removeFunction(at: index)
                        } label: {
                            Image(systemName: "xmark.circle.fill")
                                .foregroundStyle(CatppuccinMocha.red.opacity(0.7))
                                .font(.system(size: 14))
                        }
                        .buttonStyle(.plain)
                    }
                }
            }
        }
    }

    // MARK: - Viewport Controls

    private var viewportControls: some View {
        VStack(spacing: 10) {
            Text("Visualização")
                .font(.system(size: 14, weight: .bold, design: .rounded))
                .foregroundStyle(CatppuccinMocha.text)
                .frame(maxWidth: .infinity, alignment: .leading)

            HStack(spacing: 8) {
                controlButton("−", icon: "minus.magnifyingglass") { viewModel.zoomOut() }
                controlButton("+", icon: "plus.magnifyingglass") { viewModel.zoomIn() }
                controlButton("↺", icon: "arrow.counterclockwise") { viewModel.resetViewport() }
            }

            HStack(spacing: 8) {
                Toggle("Grade", isOn: $viewModel.showGrid)
                    .font(.system(size: 12, design: .rounded))
                    .foregroundStyle(CatppuccinMocha.subtext0)
                    .toggleStyle(.switch)
                    .controlSize(.mini)

                Toggle("Eixos", isOn: $viewModel.showAxes)
                    .font(.system(size: 12, design: .rounded))
                    .foregroundStyle(CatppuccinMocha.subtext0)
                    .toggleStyle(.switch)
                    .controlSize(.mini)
            }
        }
    }

    // MARK: - Presets Section

    private var presetsSection: some View {
        VStack(spacing: 8) {
            Text("Funções Rápidas")
                .font(.system(size: 14, weight: .bold, design: .rounded))
                .foregroundStyle(CatppuccinMocha.text)
                .frame(maxWidth: .infinity, alignment: .leading)

            let presets: [(String, String)] = [
                ("sin(x)", "sin(x)"),
                ("cos(x)", "cos(x)"),
                ("tan(x)", "tan(x)"),
                ("x²", "x^2"),
                ("√x", "sqrt(x)"),
                ("ln(x)", "ln(x)")
            ]

            LazyVGrid(columns: [
                GridItem(.flexible()),
                GridItem(.flexible()),
                GridItem(.flexible())
            ], spacing: 6) {
                ForEach(presets, id: \.0) { preset in
                    Button {
                        viewModel.loadPreset(preset.1)
                    } label: {
                        Text(preset.0)
                            .font(.system(size: 12, weight: .semibold, design: .monospaced))
                            .foregroundStyle(CatppuccinMocha.text)
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, 8)
                            .background(
                                RoundedRectangle(cornerRadius: 8)
                                    .fill(CatppuccinMocha.surface0)
                            )
                    }
                    .buttonStyle(.plain)
                }
            }
        }
    }

    // MARK: - Quick Evaluator

    private var quickEvaluator: some View {
        VStack(spacing: 8) {
            Text("Avaliar Expressão")
                .font(.system(size: 14, weight: .bold, design: .rounded))
                .foregroundStyle(CatppuccinMocha.text)
                .frame(maxWidth: .infinity, alignment: .leading)

            HStack(spacing: 8) {
                TextField("ex: 2+3*4", text: $viewModel.quickExpression)
                    .font(.system(size: 13, design: .monospaced))
                    .textFieldStyle(.roundedBorder)
                    .onSubmit { viewModel.evaluateQuick() }

                Button("=") { viewModel.evaluateQuick() }
                    .font(.system(size: 14, weight: .bold))
                    .foregroundStyle(CatppuccinMocha.base)
                    .padding(.horizontal, 12)
                    .padding(.vertical, 6)
                    .background(Capsule().fill(CatppuccinMocha.blue))
                    .buttonStyle(.plain)
            }

            if !viewModel.quickResult.isEmpty {
                Text("= \(viewModel.quickResult)")
                    .font(.system(size: 15, weight: .semibold, design: .monospaced))
                    .foregroundStyle(CatppuccinMocha.green)
                    .frame(maxWidth: .infinity, alignment: .trailing)
            }
        }
    }

    // MARK: - Helpers

    private func controlButton(_ title: String, icon: String, action: @escaping () -> Void) -> some View {
        Button(action: action) {
            Label(title, systemImage: icon)
                .font(.system(size: 13, weight: .semibold))
                .foregroundStyle(CatppuccinMocha.text)
                .frame(maxWidth: .infinity)
                .padding(.vertical, 8)
                .background(
                    RoundedRectangle(cornerRadius: 8)
                        .fill(CatppuccinMocha.surface0)
                )
        }
        .buttonStyle(.plain)
    }

    private func calculateGridSpacing(range: Double) -> Double {
        let rough = range / 10.0
        let magnitude = pow(10, floor(Foundation.log10(rough)))
        let normalized = rough / magnitude

        if normalized < 2 {
            return magnitude
        } else if normalized < 5 {
            return 2 * magnitude
        } else {
            return 5 * magnitude
        }
    }
}

#Preview {
    GraphingCalculatorView()
}
