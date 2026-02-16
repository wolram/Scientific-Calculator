package com.vibecoding.calculator.parser;

/**
 * Recursive descent parser for mathematical expressions.
 * Supports: +, -, *, /, ^, unary minus, parentheses,
 * and functions: sin, cos, tan, asin, acos, atan, sinh, cosh, tanh,
 * ln, log, log2, sqrt, cbrt, abs, exp, floor, ceil, round.
 * Constants: pi, e, phi.
 */
public class ExpressionParser {
    private final String input;
    private int pos;
    private boolean useDegrees;

    public ExpressionParser(String input, boolean useDegrees) {
        this.input = input.replaceAll("\\s+", "");
        this.pos = 0;
        this.useDegrees = useDegrees;
    }

    public ExpressionParser(String input) {
        this(input, false);
    }

    public double parse() {
        double result = parseExpression();
        if (pos < input.length()) {
            throw new ArithmeticException("Caractere inesperado: '" + input.charAt(pos) + "' na posição " + pos);
        }
        return result;
    }

    // expression = term (('+' | '-') term)*
    private double parseExpression() {
        double result = parseTerm();
        while (pos < input.length()) {
            char c = input.charAt(pos);
            if (c == '+') { pos++; result += parseTerm(); }
            else if (c == '-') { pos++; result -= parseTerm(); }
            else break;
        }
        return result;
    }

    // term = power (('*' | '/') power)*
    private double parseTerm() {
        double result = parsePower();
        while (pos < input.length()) {
            char c = input.charAt(pos);
            if (c == '*' || c == '\u00D7') { pos++; result *= parsePower(); }
            else if (c == '/' || c == '\u00F7') {
                pos++;
                double d = parsePower();
                if (d == 0) throw new ArithmeticException("Divisão por zero");
                result /= d;
            }
            else break;
        }
        return result;
    }

    // power = unary ('^' unary)*
    private double parsePower() {
        double base = parseUnary();
        if (pos < input.length() && input.charAt(pos) == '^') {
            pos++;
            double exp = parseUnary();
            return Math.pow(base, exp);
        }
        return base;
    }

    // unary = ('+' | '-') unary | atom
    private double parseUnary() {
        if (pos < input.length()) {
            if (input.charAt(pos) == '-') { pos++; return -parseUnary(); }
            if (input.charAt(pos) == '+') { pos++; return parseUnary(); }
        }
        return parseAtom();
    }

    // atom = number | '(' expression ')' | function '(' expression ')' | constant
    private double parseAtom() {
        if (pos >= input.length()) throw new ArithmeticException("Expressão incompleta");

        char c = input.charAt(pos);

        // Parentheses
        if (c == '(') {
            pos++;
            double result = parseExpression();
            expect(')');
            return result;
        }

        // Number
        if (Character.isDigit(c) || c == '.') {
            return parseNumber();
        }

        // Function or constant name
        String name = parseName();
        if (name.isEmpty()) {
            throw new ArithmeticException("Caractere inesperado: '" + c + "'");
        }

        // Constants
        switch (name.toLowerCase()) {
            case "pi": case "\u03C0": return Math.PI;
            case "e": return Math.E;
            case "phi": case "\u03C6": return (1 + Math.sqrt(5)) / 2;
            case "ans": return 0; // placeholder for last answer
        }

        // Functions requiring parentheses
        if (pos < input.length() && input.charAt(pos) == '(') {
            pos++;
            double arg = parseExpression();
            expect(')');
            return applyFunction(name.toLowerCase(), arg);
        }

        // Functions with implicit argument (no parens) - treat next atom as argument
        return applyFunction(name.toLowerCase(), parseUnary());
    }

    private double applyFunction(String name, double arg) {
        return switch (name) {
            case "sin" -> useDegrees ? Math.sin(Math.toRadians(arg)) : Math.sin(arg);
            case "cos" -> useDegrees ? Math.cos(Math.toRadians(arg)) : Math.cos(arg);
            case "tan" -> useDegrees ? Math.tan(Math.toRadians(arg)) : Math.tan(arg);
            case "asin", "arcsin" -> {
                double r = Math.asin(arg);
                yield useDegrees ? Math.toDegrees(r) : r;
            }
            case "acos", "arccos" -> {
                double r = Math.acos(arg);
                yield useDegrees ? Math.toDegrees(r) : r;
            }
            case "atan", "arctan" -> {
                double r = Math.atan(arg);
                yield useDegrees ? Math.toDegrees(r) : r;
            }
            case "sinh" -> Math.sinh(arg);
            case "cosh" -> Math.cosh(arg);
            case "tanh" -> Math.tanh(arg);
            case "asinh" -> Math.log(arg + Math.sqrt(arg * arg + 1));
            case "acosh" -> Math.log(arg + Math.sqrt(arg * arg - 1));
            case "atanh" -> 0.5 * Math.log((1 + arg) / (1 - arg));
            case "ln" -> Math.log(arg);
            case "log", "log10" -> Math.log10(arg);
            case "log2" -> Math.log(arg) / Math.log(2);
            case "sqrt", "\u221A" -> Math.sqrt(arg);
            case "cbrt" -> Math.cbrt(arg);
            case "abs" -> Math.abs(arg);
            case "exp" -> Math.exp(arg);
            case "floor" -> Math.floor(arg);
            case "ceil" -> Math.ceil(arg);
            case "round" -> Math.round(arg);
            case "sign", "sgn" -> Math.signum(arg);
            case "fact" -> {
                int n = (int) Math.round(arg);
                if (n < 0 || n > 170) throw new ArithmeticException("Fatorial fora do domínio");
                double r = 1;
                for (int i = 2; i <= n; i++) r *= i;
                yield r;
            }
            default -> throw new ArithmeticException("Função desconhecida: " + name);
        };
    }

    private double parseNumber() {
        int start = pos;
        boolean hasDot = false;
        boolean hasE = false;
        while (pos < input.length()) {
            char c = input.charAt(pos);
            if (Character.isDigit(c)) { pos++; }
            else if (c == '.' && !hasDot) { hasDot = true; pos++; }
            else if ((c == 'e' || c == 'E') && !hasE && pos > start) {
                hasE = true;
                pos++;
                if (pos < input.length() && (input.charAt(pos) == '+' || input.charAt(pos) == '-')) pos++;
            }
            else break;
        }
        String numStr = input.substring(start, pos);
        try {
            return Double.parseDouble(numStr);
        } catch (NumberFormatException e) {
            throw new ArithmeticException("Número inválido: " + numStr);
        }
    }

    private String parseName() {
        int start = pos;
        while (pos < input.length() && (Character.isLetter(input.charAt(pos)) || input.charAt(pos) == '_')) {
            pos++;
        }
        return input.substring(start, pos);
    }

    private void expect(char c) {
        if (pos >= input.length() || input.charAt(pos) != c) {
            throw new ArithmeticException("Esperado '" + c + "' na posição " + pos);
        }
        pos++;
    }
}
