import Testing
@testable import ScientificCalculator

@Suite("ExpressionParser Tests")
struct ExpressionParserTests {

    private func eval(_ expr: String, mode: AngleMode = .radians) -> Double? {
        switch ExpressionParser.evaluate(expr, angleMode: mode) {
        case .success(let value): return value
        case .failure: return nil
        }
    }

    // MARK: - Basic Arithmetic

    @Test func simpleArithmetic() {
        #expect(eval("2+3") == 5)
        #expect(eval("10-4") == 6)
        #expect(eval("3*7") == 21)
        #expect(eval("15/3") == 5)
    }

    @Test func operatorPrecedence() {
        #expect(eval("2+3*4") == 14)
        #expect(eval("(2+3)*4") == 20)
        #expect(eval("10-2*3") == 4)
    }

    @Test func unaryMinus() {
        #expect(eval("-5") == -5)
        #expect(eval("-5+3") == -2)
        #expect(eval("-(2+3)") == -5)
    }

    @Test func powers() {
        #expect(eval("2^10") == 1024)
        #expect(eval("3^3") == 27)
    }

    // MARK: - Functions

    @Test func mathFunctions() {
        #expect(abs(eval("sqrt(144)")! - 12) < 1e-10)
        #expect(abs(eval("abs(-42)")! - 42) < 1e-10)
        #expect(abs(eval("ln(1)")! - 0) < 1e-10)
        #expect(abs(eval("exp(0)")! - 1) < 1e-10)
    }

    @Test func trigFunctions() {
        #expect(abs(eval("sin(0)")! - 0) < 1e-10)
        #expect(abs(eval("cos(0)")! - 1) < 1e-10)
    }

    @Test func constants() {
        #expect(abs(eval("pi")! - Double.pi) < 1e-10)
        #expect(abs(eval("e")! - M_E) < 1e-10)
    }

    // MARK: - Errors

    @Test func divisionByZero() {
        #expect(eval("1/0") == nil)
    }

    @Test func invalidSqrt() {
        #expect(eval("sqrt(-1)") == nil)
    }

    @Test func invalidExpression() {
        #expect(eval(")") == nil)
    }

    // MARK: - Complex Expressions

    @Test func complexExpressions() {
        #expect(abs(eval("2^3 + sqrt(16) * 2")! - 16) < 1e-10)
        #expect(abs(eval("(1+2)*(3+4)")! - 21) < 1e-10)
    }

    // MARK: - Two-arg functions

    @Test func twoArgFunctions() {
        #expect(abs(eval("pow(2,8)")! - 256) < 1e-10)
        #expect(abs(eval("ncr(10,3)")! - 120) < 1e-10)
    }
}
