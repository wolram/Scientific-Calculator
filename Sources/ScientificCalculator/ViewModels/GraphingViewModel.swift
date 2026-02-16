import SwiftUI

// MARK: - Function Entry

struct FunctionEntry: Identifiable, Sendable {
    let id = UUID()
    var expression: String = ""
    var color: Color
    var isEnabled: Bool = true
}

// MARK: - Graphing Calculator ViewModel

@MainActor
@Observable
final class GraphingViewModel {
    static let graphColors: [Color] = [
        CatppuccinMocha.blue,
        CatppuccinMocha.red,
        CatppuccinMocha.green,
        CatppuccinMocha.yellow,
        CatppuccinMocha.mauve,
        CatppuccinMocha.peach
    ]

    var functions: [FunctionEntry] = [
        FunctionEntry(expression: "", color: graphColors[0])
    ]

    var angleMode: AngleMode = .radians
    var showGrid: Bool = true
    var showAxes: Bool = true

    // Viewport
    var xMin: Double = -10
    var xMax: Double = 10
    var yMin: Double = -10
    var yMax: Double = 10

    // Quick evaluate
    var quickExpression: String = ""
    var quickResult: String = ""

    // MARK: - Functions Management

    func addFunction() {
        guard functions.count < 6 else { return }
        let colorIndex = functions.count % Self.graphColors.count
        functions.append(FunctionEntry(expression: "", color: Self.graphColors[colorIndex]))
    }

    func removeFunction(at index: Int) {
        guard functions.count > 1 else { return }
        functions.remove(at: index)
    }

    // MARK: - Evaluation

    func evaluateFunction(at index: Int, x: Double) -> Double? {
        guard index < functions.count else { return nil }
        let entry = functions[index]
        guard entry.isEnabled, !entry.expression.isEmpty else { return nil }

        let expr = entry.expression.replacingOccurrences(of: "x", with: "(\(x))")
        let result = ExpressionParser.evaluate(expr, angleMode: angleMode)
        switch result {
        case .success(let value):
            return value.isFinite ? value : nil
        case .failure:
            return nil
        }
    }

    func evaluateQuick() {
        guard !quickExpression.isEmpty else {
            quickResult = ""
            return
        }
        let result = ExpressionParser.evaluate(quickExpression, angleMode: angleMode)
        switch result {
        case .success(let value):
            quickResult = ScientificEngine.formatResult(value)
        case .failure(let error):
            quickResult = error.localizedDescription
        }
    }

    // MARK: - Viewport Controls

    func zoomIn() {
        let xCenter = (xMin + xMax) / 2
        let yCenter = (yMin + yMax) / 2
        let xRange = (xMax - xMin) * 0.4
        let yRange = (yMax - yMin) * 0.4
        xMin = xCenter - xRange
        xMax = xCenter + xRange
        yMin = yCenter - yRange
        yMax = yCenter + yRange
    }

    func zoomOut() {
        let xCenter = (xMin + xMax) / 2
        let yCenter = (yMin + yMax) / 2
        let xRange = (xMax - xMin) * 0.75
        let yRange = (yMax - yMin) * 0.75
        xMin = xCenter - xRange
        xMax = xCenter + xRange
        yMin = yCenter - yRange
        yMax = yCenter + yRange
    }

    func resetViewport() {
        xMin = -10
        xMax = 10
        yMin = -10
        yMax = 10
    }

    func loadPreset(_ preset: String) {
        guard let firstFunc = functions.first else { return }
        functions[0] = FunctionEntry(expression: preset, color: firstFunc.color, isEnabled: true)
    }
}
