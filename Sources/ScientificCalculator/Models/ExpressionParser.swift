import Foundation

// MARK: - Expression Parser (Recursive Descent)

struct ExpressionParser: Sendable {
    let expression: String
    let angleMode: AngleMode
    private var index: String.Index

    init(expression: String, angleMode: AngleMode = .radians) {
        self.expression = expression.replacingOccurrences(of: " ", with: "")
        self.angleMode = angleMode
        self.index = self.expression.startIndex
    }

    // MARK: - Public API

    mutating func parse() throws -> Double {
        let result = try parseExpression()
        if index != expression.endIndex {
            throw ParserError.unexpectedCharacter(current)
        }
        return result
    }

    static func evaluate(_ expr: String, angleMode: AngleMode = .radians) -> Result<Double, ParserError> {
        var parser = ExpressionParser(expression: expr, angleMode: angleMode)
        do {
            let result = try parser.parse()
            return .success(result)
        } catch let error as ParserError {
            return .failure(error)
        } catch {
            return .failure(.unknownError)
        }
    }

    // MARK: - Error

    enum ParserError: Error, LocalizedError, Sendable {
        case unexpectedCharacter(Character)
        case unexpectedEnd
        case unknownFunction(String)
        case domainError(String)
        case unknownError

        var errorDescription: String? {
            switch self {
            case .unexpectedCharacter(let ch): return "Caractere inesperado: '\(ch)'"
            case .unexpectedEnd: return "Expressão incompleta"
            case .unknownFunction(let name): return "Função desconhecida: '\(name)'"
            case .domainError(let msg): return "Erro de domínio: \(msg)"
            case .unknownError: return "Erro desconhecido"
            }
        }
    }

    // MARK: - Helpers

    private var current: Character {
        expression[index]
    }

    private var isAtEnd: Bool {
        index >= expression.endIndex
    }

    private mutating func advance() {
        index = expression.index(after: index)
    }

    private mutating func consume(_ expected: Character) throws {
        guard !isAtEnd, current == expected else {
            if isAtEnd { throw ParserError.unexpectedEnd }
            throw ParserError.unexpectedCharacter(current)
        }
        advance()
    }

    // MARK: - Grammar: expression = term (('+' | '-') term)*

    private mutating func parseExpression() throws -> Double {
        var result = try parseTerm()
        while !isAtEnd {
            if current == "+" {
                advance()
                result += try parseTerm()
            } else if current == "-" {
                advance()
                result -= try parseTerm()
            } else {
                break
            }
        }
        return result
    }

    // MARK: - term = power (('*' | '/') power)*

    private mutating func parseTerm() throws -> Double {
        var result = try parsePower()
        while !isAtEnd {
            if current == "*" || current == "×" {
                advance()
                result *= try parsePower()
            } else if current == "/" || current == "÷" {
                advance()
                let divisor = try parsePower()
                guard divisor != 0 else {
                    throw ParserError.domainError("Divisão por zero")
                }
                result /= divisor
            } else {
                break
            }
        }
        return result
    }

    // MARK: - power = unary ('^' unary)*

    private mutating func parsePower() throws -> Double {
        let base = try parseUnary()
        if !isAtEnd && current == "^" {
            advance()
            let exp = try parseUnary()
            return pow(base, exp)
        }
        return base
    }

    // MARK: - unary = '-' unary | primary

    private mutating func parseUnary() throws -> Double {
        if !isAtEnd && current == "-" {
            advance()
            return -(try parseUnary())
        }
        if !isAtEnd && current == "+" {
            advance()
            return try parseUnary()
        }
        return try parsePrimary()
    }

    // MARK: - primary = number | '(' expression ')' | function '(' expression ')' | constant

    private mutating func parsePrimary() throws -> Double {
        guard !isAtEnd else { throw ParserError.unexpectedEnd }

        // Parenthesized expression
        if current == "(" {
            advance()
            let result = try parseExpression()
            try consume(")")
            return result
        }

        // Number
        if current.isNumber || current == "." {
            return try parseNumber()
        }

        // Function or constant name
        if current.isLetter {
            let name = parseName()
            return try evaluateNamedToken(name)
        }

        throw ParserError.unexpectedCharacter(current)
    }

    // MARK: - Parse Number

    private mutating func parseNumber() throws -> Double {
        var numStr = ""
        while !isAtEnd && (current.isNumber || current == ".") {
            numStr.append(current)
            advance()
        }

        // Scientific notation
        if !isAtEnd && (current == "e" || current == "E") {
            numStr.append(current)
            advance()
            if !isAtEnd && (current == "+" || current == "-") {
                numStr.append(current)
                advance()
            }
            while !isAtEnd && current.isNumber {
                numStr.append(current)
                advance()
            }
        }

        guard let value = Double(numStr) else {
            throw ParserError.unexpectedCharacter(numStr.first ?? " ")
        }
        return value
    }

    // MARK: - Parse Name

    private mutating func parseName() -> String {
        var name = ""
        while !isAtEnd && (current.isLetter || current.isNumber) {
            name.append(current)
            advance()
        }
        return name.lowercased()
    }

    // MARK: - Evaluate Named Token

    private mutating func evaluateNamedToken(_ name: String) throws -> Double {
        // Constants
        switch name {
        case "pi", "π": return Double.pi
        case "e": return M_E
        case "phi", "φ": return (1.0 + sqrt(5.0)) / 2.0
        default: break
        }

        // Functions require parentheses
        guard !isAtEnd, current == "(" else {
            throw ParserError.unknownFunction(name)
        }
        advance()
        let arg = try parseExpression()

        // Check for comma (two-argument functions)
        if !isAtEnd && current == "," {
            advance()
            let arg2 = try parseExpression()
            try consume(")")
            return try evaluateTwoArgFunction(name, arg, arg2)
        }

        try consume(")")
        return try evaluateOneArgFunction(name, arg)
    }

    // MARK: - One-Argument Functions

    private func evaluateOneArgFunction(_ name: String, _ arg: Double) throws -> Double {
        switch name {
        // Trig
        case "sin": return ScientificEngine.sin(arg, mode: angleMode)
        case "cos": return ScientificEngine.cos(arg, mode: angleMode)
        case "tan":
            guard let result = ScientificEngine.tan(arg, mode: angleMode) else {
                throw ParserError.domainError("tan indefinida")
            }
            return result

        // Inverse Trig
        case "asin", "arcsin":
            guard let result = ScientificEngine.asin(arg, mode: angleMode) else {
                throw ParserError.domainError("asin requer [-1, 1]")
            }
            return result
        case "acos", "arccos":
            guard let result = ScientificEngine.acos(arg, mode: angleMode) else {
                throw ParserError.domainError("acos requer [-1, 1]")
            }
            return result
        case "atan", "arctan":
            return ScientificEngine.atan(arg, mode: angleMode)

        // Hyperbolic
        case "sinh": return ScientificEngine.sinh(arg)
        case "cosh": return ScientificEngine.cosh(arg)
        case "tanh": return ScientificEngine.tanh(arg)
        case "asinh": return ScientificEngine.asinh(arg)
        case "acosh":
            guard let result = ScientificEngine.acosh(arg) else {
                throw ParserError.domainError("acosh requer valor >= 1")
            }
            return result
        case "atanh":
            guard let result = ScientificEngine.atanh(arg) else {
                throw ParserError.domainError("atanh requer (-1, 1)")
            }
            return result

        // Logarithms
        case "ln":
            guard let result = ScientificEngine.naturalLog(arg) else {
                throw ParserError.domainError("ln requer valor > 0")
            }
            return result
        case "log", "log10":
            guard let result = ScientificEngine.log10(arg) else {
                throw ParserError.domainError("log requer valor > 0")
            }
            return result
        case "log2":
            guard let result = ScientificEngine.log2(arg) else {
                throw ParserError.domainError("log2 requer valor > 0")
            }
            return result
        case "exp": return ScientificEngine.exp(arg)

        // Roots & Powers
        case "sqrt":
            guard let result = ScientificEngine.squareRoot(arg) else {
                throw ParserError.domainError("sqrt requer valor >= 0")
            }
            return result
        case "cbrt": return ScientificEngine.cubeRoot(arg)
        case "abs": return ScientificEngine.absolute(arg)
        case "floor": return floor(arg)
        case "ceil": return ceil(arg)
        case "round": return (arg).rounded()

        default:
            throw ParserError.unknownFunction(name)
        }
    }

    // MARK: - Two-Argument Functions

    private func evaluateTwoArgFunction(_ name: String, _ a: Double, _ b: Double) throws -> Double {
        switch name {
        case "pow": return pow(a, b)
        case "root":
            guard let result = ScientificEngine.nthRoot(a, n: b) else {
                throw ParserError.domainError("Raiz inválida")
            }
            return result
        case "npr":
            guard let result = ScientificEngine.permutation(Int(a), Int(b)) else {
                throw ParserError.domainError("nPr inválido")
            }
            return result
        case "ncr":
            guard let result = ScientificEngine.combination(Int(a), Int(b)) else {
                throw ParserError.domainError("nCr inválido")
            }
            return result
        default:
            throw ParserError.unknownFunction(name)
        }
    }
}
