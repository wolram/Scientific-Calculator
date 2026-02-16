package com.vibecoding.calculator.engine

import kotlin.math.*

class ExpressionParser(
    input: String,
    private val useDegrees: Boolean = false,
    private val variableX: Double? = null
) {
    private val input: String = input.replace(Regex("\\s+"), "")
    private var pos: Int = 0

    fun parse(): Double {
        val result = parseExpression()
        if (pos < input.length) {
            throw ArithmeticException("Caractere inesperado: '${input[pos]}' na posição $pos")
        }
        return result
    }

    // expression = term (('+' | '-') term)*
    private fun parseExpression(): Double {
        var result = parseTerm()
        while (pos < input.length) {
            when (input[pos]) {
                '+' -> { pos++; result += parseTerm() }
                '-' -> { pos++; result -= parseTerm() }
                else -> break
            }
        }
        return result
    }

    // term = power (('*' | '/') power)*
    private fun parseTerm(): Double {
        var result = parsePower()
        while (pos < input.length) {
            when (input[pos]) {
                '*', '\u00D7' -> { pos++; result *= parsePower() }
                '/', '\u00F7' -> {
                    pos++
                    val d = parsePower()
                    if (d == 0.0) throw ArithmeticException("Divisão por zero")
                    result /= d
                }
                else -> break
            }
        }
        return result
    }

    // power = unary ('^' unary)*
    private fun parsePower(): Double {
        val base = parseUnary()
        if (pos < input.length && input[pos] == '^') {
            pos++
            val exp = parseUnary()
            return base.pow(exp)
        }
        return base
    }

    // unary = ('+' | '-') unary | atom
    private fun parseUnary(): Double {
        if (pos < input.length) {
            if (input[pos] == '-') { pos++; return -parseUnary() }
            if (input[pos] == '+') { pos++; return parseUnary() }
        }
        return parseAtom()
    }

    // atom = number | '(' expression ')' | function '(' expression ')' | constant
    private fun parseAtom(): Double {
        if (pos >= input.length) throw ArithmeticException("Expressão incompleta")

        val c = input[pos]

        // Parentheses
        if (c == '(') {
            pos++
            val result = parseExpression()
            expect(')')
            return result
        }

        // Number
        if (c.isDigit() || c == '.') {
            return parseNumber()
        }

        // Function or constant name
        val name = parseName()
        if (name.isEmpty()) {
            throw ArithmeticException("Caractere inesperado: '$c'")
        }

        // Variable x (for graphing)
        if (name.lowercase() == "x" && variableX != null) {
            return variableX
        }

        // Constants
        when (name.lowercase()) {
            "pi", "\u03C0" -> return PI
            "e" -> return E
            "phi", "\u03C6" -> return (1 + sqrt(5.0)) / 2
            "ans" -> return 0.0
        }

        // Functions requiring parentheses
        if (pos < input.length && input[pos] == '(') {
            pos++
            val arg = parseExpression()
            // Check for two-argument functions
            if (pos < input.length && input[pos] == ',') {
                pos++
                val arg2 = parseExpression()
                expect(')')
                return applyFunction2(name.lowercase(), arg, arg2)
            }
            expect(')')
            return applyFunction(name.lowercase(), arg)
        }

        // Functions with implicit argument
        return applyFunction(name.lowercase(), parseUnary())
    }

    private fun applyFunction(name: String, arg: Double): Double = when (name) {
        "sin" -> if (useDegrees) sin(Math.toRadians(arg)) else sin(arg)
        "cos" -> if (useDegrees) cos(Math.toRadians(arg)) else cos(arg)
        "tan" -> if (useDegrees) tan(Math.toRadians(arg)) else tan(arg)
        "asin", "arcsin" -> {
            val r = asin(arg)
            if (useDegrees) Math.toDegrees(r) else r
        }
        "acos", "arccos" -> {
            val r = acos(arg)
            if (useDegrees) Math.toDegrees(r) else r
        }
        "atan", "arctan" -> {
            val r = atan(arg)
            if (useDegrees) Math.toDegrees(r) else r
        }
        "sinh" -> sinh(arg)
        "cosh" -> cosh(arg)
        "tanh" -> tanh(arg)
        "asinh" -> ln(arg + sqrt(arg * arg + 1))
        "acosh" -> ln(arg + sqrt(arg * arg - 1))
        "atanh" -> 0.5 * ln((1 + arg) / (1 - arg))
        "ln" -> ln(arg)
        "log", "log10" -> log10(arg)
        "log2" -> log2(arg)
        "sqrt", "\u221A" -> sqrt(arg)
        "cbrt" -> Math.cbrt(arg)
        "abs" -> abs(arg)
        "exp" -> exp(arg)
        "floor" -> floor(arg)
        "ceil" -> ceil(arg)
        "round" -> round(arg).toDouble()
        "sign", "sgn" -> sign(arg)
        "fact" -> {
            val n = arg.roundToInt()
            if (n < 0 || n > 170) throw ArithmeticException("Fatorial fora do domínio")
            var r = 1.0
            for (i in 2..n) r *= i
            r
        }
        else -> throw ArithmeticException("Função desconhecida: $name")
    }

    private fun applyFunction2(name: String, arg1: Double, arg2: Double): Double = when (name) {
        "pow" -> arg1.pow(arg2)
        "log" -> ln(arg1) / ln(arg2)
        "ncr" -> {
            val n = arg1.roundToInt()
            val r = arg2.roundToInt()
            val engine = ScientificEngine()
            engine.combination(n, r)
        }
        "npr" -> {
            val n = arg1.roundToInt()
            val r = arg2.roundToInt()
            val engine = ScientificEngine()
            engine.permutation(n, r)
        }
        "root" -> arg2.pow(1.0 / arg1)
        else -> throw ArithmeticException("Função desconhecida: $name")
    }

    private fun parseNumber(): Double {
        val start = pos
        var hasDot = false
        var hasE = false
        while (pos < input.length) {
            val ch = input[pos]
            when {
                ch.isDigit() -> pos++
                ch == '.' && !hasDot -> { hasDot = true; pos++ }
                (ch == 'e' || ch == 'E') && !hasE && pos > start -> {
                    hasE = true; pos++
                    if (pos < input.length && (input[pos] == '+' || input[pos] == '-')) pos++
                }
                else -> break
            }
        }
        val numStr = input.substring(start, pos)
        return numStr.toDoubleOrNull() ?: throw ArithmeticException("Número inválido: $numStr")
    }

    private fun parseName(): String {
        val start = pos
        while (pos < input.length && (input[pos].isLetter() || input[pos] == '_')) {
            pos++
        }
        return input.substring(start, pos)
    }

    private fun expect(c: Char) {
        if (pos >= input.length || input[pos] != c) {
            throw ArithmeticException("Esperado '$c' na posição $pos")
        }
        pos++
    }
}

private fun Double.roundToInt(): Int = kotlin.math.round(this).toInt()
