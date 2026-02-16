import Testing
@testable import ScientificCalculator

@Suite("ScientificEngine Tests")
struct ScientificEngineTests {

    // MARK: - Basic Arithmetic

    @Test func addition() {
        #expect(ScientificEngine.add(2, 3) == 5)
        #expect(ScientificEngine.add(-1, 1) == 0)
        #expect(ScientificEngine.add(0.1, 0.2) - 0.3 < 1e-10)
    }

    @Test func subtraction() {
        #expect(ScientificEngine.subtract(5, 3) == 2)
        #expect(ScientificEngine.subtract(0, 5) == -5)
    }

    @Test func multiplication() {
        #expect(ScientificEngine.multiply(3, 4) == 12)
        #expect(ScientificEngine.multiply(-2, 3) == -6)
        #expect(ScientificEngine.multiply(0, 100) == 0)
    }

    @Test func division() {
        #expect(ScientificEngine.divide(10, 2) == 5)
        #expect(ScientificEngine.divide(7, 3)! - 2.333333 < 0.001)
        #expect(ScientificEngine.divide(5, 0) == nil)
    }

    // MARK: - Powers & Roots

    @Test func powers() {
        #expect(ScientificEngine.power(2, 10) == 1024)
        #expect(ScientificEngine.square(7) == 49)
        #expect(ScientificEngine.cube(3) == 27)
    }

    @Test func roots() {
        #expect(ScientificEngine.squareRoot(144) == 12)
        #expect(ScientificEngine.squareRoot(-1) == nil)
        #expect(abs(ScientificEngine.cubeRoot(27) - 3) < 1e-10)
    }

    // MARK: - Logarithms

    @Test func logarithms() {
        #expect(abs(ScientificEngine.naturalLog(M_E)! - 1) < 1e-10)
        #expect(abs(ScientificEngine.log10(100)! - 2) < 1e-10)
        #expect(abs(ScientificEngine.log2(8)! - 3) < 1e-10)
        #expect(ScientificEngine.naturalLog(-1) == nil)
    }

    // MARK: - Trigonometry

    @Test func trigDegrees() {
        #expect(abs(ScientificEngine.sin(30, mode: .degrees) - 0.5) < 1e-10)
        #expect(abs(ScientificEngine.cos(60, mode: .degrees) - 0.5) < 1e-10)
        #expect(abs(ScientificEngine.tan(45, mode: .degrees)! - 1) < 1e-10)
    }

    @Test func trigRadians() {
        #expect(abs(ScientificEngine.sin(.pi / 2, mode: .radians) - 1) < 1e-10)
        #expect(abs(ScientificEngine.cos(0, mode: .radians) - 1) < 1e-10)
    }

    // MARK: - Factorial

    @Test func factorial() {
        #expect(ScientificEngine.factorial(0) == 1)
        #expect(ScientificEngine.factorial(1) == 1)
        #expect(ScientificEngine.factorial(5) == 120)
        #expect(ScientificEngine.factorial(10) == 3628800)
        #expect(ScientificEngine.factorial(-1) == nil)
    }

    // MARK: - Combinatorics

    @Test func permutations() {
        #expect(ScientificEngine.permutation(5, 2) == 20)
        #expect(ScientificEngine.permutation(10, 3) == 720)
    }

    @Test func combinations() {
        #expect(ScientificEngine.combination(5, 2) == 10)
        #expect(ScientificEngine.combination(10, 3) == 120)
    }

    // MARK: - Formatting

    @Test func formatting() {
        #expect(ScientificEngine.formatResult(42) == "42")
        #expect(ScientificEngine.formatResult(3.14159) == "3.14159")
        #expect(ScientificEngine.formatResult(.nan) == "Error")
        #expect(ScientificEngine.formatResult(.infinity) == "∞")
    }
}
