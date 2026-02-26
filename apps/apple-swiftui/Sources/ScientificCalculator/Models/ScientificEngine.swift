import Foundation

// MARK: - Angle Mode

enum AngleMode: String, CaseIterable, Sendable {
    case degrees = "DEG"
    case radians = "RAD"
}

// MARK: - Scientific Engine

struct ScientificEngine: Sendable {

    // MARK: - Angle Conversion

    static func toRadians(_ value: Double, mode: AngleMode) -> Double {
        switch mode {
        case .degrees: return value * .pi / 180.0
        case .radians: return value
        }
    }

    static func fromRadians(_ value: Double, mode: AngleMode) -> Double {
        switch mode {
        case .degrees: return value * 180.0 / .pi
        case .radians: return value
        }
    }

    // MARK: - Basic Arithmetic

    static func add(_ a: Double, _ b: Double) -> Double { a + b }
    static func subtract(_ a: Double, _ b: Double) -> Double { a - b }
    static func multiply(_ a: Double, _ b: Double) -> Double { a * b }

    static func divide(_ a: Double, _ b: Double) -> Double? {
        guard b != 0 else { return nil }
        return a / b
    }

    // MARK: - Powers & Roots

    static func power(_ base: Double, _ exp: Double) -> Double {
        pow(base, exp)
    }

    static func squareRoot(_ value: Double) -> Double? {
        guard value >= 0 else { return nil }
        return sqrt(value)
    }

    static func cubeRoot(_ value: Double) -> Double {
        cbrt(value)
    }

    static func nthRoot(_ value: Double, n: Double) -> Double? {
        guard n != 0 else { return nil }
        if value < 0 && n.truncatingRemainder(dividingBy: 2) != 0 {
            return -pow(-value, 1.0 / n)
        }
        guard value >= 0 || n == Double(Int(n)) else { return nil }
        return pow(value, 1.0 / n)
    }

    static func square(_ value: Double) -> Double { value * value }
    static func cube(_ value: Double) -> Double { value * value * value }

    // MARK: - Logarithms

    static func naturalLog(_ value: Double) -> Double? {
        guard value > 0 else { return nil }
        return log(value)
    }

    static func log10(_ value: Double) -> Double? {
        guard value > 0 else { return nil }
        return Foundation.log10(value)
    }

    static func log2(_ value: Double) -> Double? {
        guard value > 0 else { return nil }
        return Foundation.log2(value)
    }

    static func exp(_ value: Double) -> Double {
        Foundation.exp(value)
    }

    static func tenToThe(_ value: Double) -> Double {
        pow(10, value)
    }

    // MARK: - Trigonometric

    static func sin(_ value: Double, mode: AngleMode) -> Double {
        Foundation.sin(toRadians(value, mode: mode))
    }

    static func cos(_ value: Double, mode: AngleMode) -> Double {
        Foundation.cos(toRadians(value, mode: mode))
    }

    static func tan(_ value: Double, mode: AngleMode) -> Double? {
        let rad = toRadians(value, mode: mode)
        let cosVal = Foundation.cos(rad)
        guard abs(cosVal) > 1e-15 else { return nil }
        return Foundation.tan(rad)
    }

    // MARK: - Inverse Trigonometric

    static func asin(_ value: Double, mode: AngleMode) -> Double? {
        guard value >= -1 && value <= 1 else { return nil }
        return fromRadians(Foundation.asin(value), mode: mode)
    }

    static func acos(_ value: Double, mode: AngleMode) -> Double? {
        guard value >= -1 && value <= 1 else { return nil }
        return fromRadians(Foundation.acos(value), mode: mode)
    }

    static func atan(_ value: Double, mode: AngleMode) -> Double {
        fromRadians(Foundation.atan(value), mode: mode)
    }

    // MARK: - Hyperbolic

    static func sinh(_ value: Double) -> Double { Foundation.sinh(value) }
    static func cosh(_ value: Double) -> Double { Foundation.cosh(value) }
    static func tanh(_ value: Double) -> Double { Foundation.tanh(value) }

    static func asinh(_ value: Double) -> Double { Foundation.asinh(value) }
    static func acosh(_ value: Double) -> Double? {
        guard value >= 1 else { return nil }
        return Foundation.acosh(value)
    }
    static func atanh(_ value: Double) -> Double? {
        guard value > -1 && value < 1 else { return nil }
        return Foundation.atanh(value)
    }

    // MARK: - Factorial & Combinatorics

    static func factorial(_ n: Int) -> Double? {
        guard n >= 0 && n <= 170 else { return nil }
        if n <= 1 { return 1 }
        var result = 1.0
        for i in 2...n {
            result *= Double(i)
        }
        return result
    }

    static func permutation(_ n: Int, _ r: Int) -> Double? {
        guard n >= 0, r >= 0, r <= n else { return nil }
        guard let nFact = factorial(n), let nrFact = factorial(n - r) else { return nil }
        return nFact / nrFact
    }

    static func combination(_ n: Int, _ r: Int) -> Double? {
        guard n >= 0, r >= 0, r <= n else { return nil }
        guard let perm = permutation(n, r), let rFact = factorial(r) else { return nil }
        return perm / rFact
    }

    // MARK: - Constants

    static let pi = Double.pi
    static let e = M_E
    static let phi = (1.0 + sqrt(5.0)) / 2.0

    // MARK: - Utility

    static func reciprocal(_ value: Double) -> Double? {
        guard value != 0 else { return nil }
        return 1.0 / value
    }

    static func absolute(_ value: Double) -> Double {
        abs(value)
    }

    static func negate(_ value: Double) -> Double {
        -value
    }

    static func percentage(_ value: Double) -> Double {
        value / 100.0
    }

    // MARK: - Formatting

    static func formatResult(_ value: Double) -> String {
        if value.isNaN { return "Error" }
        if value.isInfinite { return value > 0 ? "∞" : "-∞" }

        if value == value.rounded() && abs(value) < 1e15 {
            return String(format: "%.0f", value)
        }

        let formatted = String(format: "%.10g", value)
        return formatted
    }
}
