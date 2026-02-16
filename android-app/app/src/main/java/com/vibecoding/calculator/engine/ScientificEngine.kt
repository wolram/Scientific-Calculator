package com.vibecoding.calculator.engine

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import kotlin.math.*

class ScientificEngine {
    private val ctx = MathContext(15, RoundingMode.HALF_UP)

    // Basic arithmetic
    fun add(a: Double, b: Double): Double = a + b
    fun subtract(a: Double, b: Double): Double = a - b
    fun multiply(a: Double, b: Double): Double = a * b
    fun divide(a: Double, b: Double): Double {
        if (b == 0.0) throw ArithmeticException("Divisão por zero")
        return a / b
    }
    fun mod(a: Double, b: Double): Double {
        if (b == 0.0) throw ArithmeticException("Módulo por zero")
        return a % b
    }
    fun abs(a: Double): Double = kotlin.math.abs(a)

    // Powers and roots
    fun pow(a: Double, b: Double): Double = a.pow(b)
    fun exp(a: Double): Double = kotlin.math.exp(a)
    fun sqrt(a: Double): Double {
        if (a < 0) throw ArithmeticException("Raiz de número negativo")
        return kotlin.math.sqrt(a)
    }
    fun cbrt(a: Double): Double = Math.cbrt(a)
    fun nthRoot(a: Double, n: Double): Double {
        if (n == 0.0) throw ArithmeticException("Raiz de índice zero")
        if (a < 0 && n % 2.0 == 0.0) throw ArithmeticException("Raiz par de número negativo")
        if (a < 0) return -(-a).pow(1.0 / n)
        return a.pow(1.0 / n)
    }
    fun square(a: Double): Double = a * a
    fun cube(a: Double): Double = a * a * a
    fun tenPow(a: Double): Double = 10.0.pow(a)
    fun twoPow(a: Double): Double = 2.0.pow(a)
    fun reciprocal(a: Double): Double {
        if (a == 0.0) throw ArithmeticException("Inverso de zero")
        return 1.0 / a
    }

    // Logarithms
    fun ln(a: Double): Double {
        if (a <= 0) throw ArithmeticException("Logaritmo de valor não-positivo")
        return kotlin.math.ln(a)
    }
    fun log10(a: Double): Double {
        if (a <= 0) throw ArithmeticException("Logaritmo de valor não-positivo")
        return kotlin.math.log10(a)
    }
    fun log2(a: Double): Double {
        if (a <= 0) throw ArithmeticException("Logaritmo de valor não-positivo")
        return kotlin.math.log2(a)
    }
    fun logBase(a: Double, base: Double): Double {
        if (a <= 0 || base <= 0 || base == 1.0) throw ArithmeticException("Logaritmo inválido")
        return kotlin.math.ln(a) / kotlin.math.ln(base)
    }

    // Trigonometric (radians)
    fun sin(a: Double): Double = kotlin.math.sin(a)
    fun cos(a: Double): Double = kotlin.math.cos(a)
    fun tan(a: Double): Double = kotlin.math.tan(a)
    fun asin(a: Double): Double {
        if (a < -1 || a > 1) throw ArithmeticException("arcsin fora do domínio [-1,1]")
        return kotlin.math.asin(a)
    }
    fun acos(a: Double): Double {
        if (a < -1 || a > 1) throw ArithmeticException("arccos fora do domínio [-1,1]")
        return kotlin.math.acos(a)
    }
    fun atan(a: Double): Double = kotlin.math.atan(a)
    fun atan2(y: Double, x: Double): Double = kotlin.math.atan2(y, x)

    // Trigonometric (degrees)
    fun sinDeg(a: Double): Double = kotlin.math.sin(Math.toRadians(a))
    fun cosDeg(a: Double): Double = kotlin.math.cos(Math.toRadians(a))
    fun tanDeg(a: Double): Double = kotlin.math.tan(Math.toRadians(a))
    fun asinDeg(a: Double): Double = Math.toDegrees(asin(a))
    fun acosDeg(a: Double): Double = Math.toDegrees(acos(a))
    fun atanDeg(a: Double): Double = Math.toDegrees(atan(a))

    // Hyperbolic
    fun sinh(a: Double): Double = kotlin.math.sinh(a)
    fun cosh(a: Double): Double = kotlin.math.cosh(a)
    fun tanh(a: Double): Double = kotlin.math.tanh(a)
    fun asinh(a: Double): Double = kotlin.math.ln(a + kotlin.math.sqrt(a * a + 1))
    fun acosh(a: Double): Double {
        if (a < 1) throw ArithmeticException("acosh domínio: x >= 1")
        return kotlin.math.ln(a + kotlin.math.sqrt(a * a - 1))
    }
    fun atanh(a: Double): Double {
        if (a <= -1 || a >= 1) throw ArithmeticException("atanh domínio: -1 < x < 1")
        return 0.5 * kotlin.math.ln((1 + a) / (1 - a))
    }

    // Factorial and combinatorics
    fun factorial(n: Int): Double {
        if (n < 0) throw ArithmeticException("Fatorial de negativo")
        if (n > 170) throw ArithmeticException("Fatorial overflow (max 170!)")
        var result = BigDecimal.ONE
        for (i in 2..n) {
            result = result.multiply(BigDecimal.valueOf(i.toLong()), ctx)
        }
        return result.toDouble()
    }

    fun permutation(n: Int, r: Int): Double {
        if (n < 0 || r < 0 || r > n) throw ArithmeticException("nPr inválido")
        var result = BigDecimal.ONE
        for (i in n downTo n - r + 1) {
            result = result.multiply(BigDecimal.valueOf(i.toLong()), ctx)
        }
        return result.toDouble()
    }

    fun combination(n: Int, r: Int): Double {
        if (n < 0 || r < 0 || r > n) throw ArithmeticException("nCr inválido")
        var rr = r
        if (rr > n - rr) rr = n - rr
        var result = BigDecimal.ONE
        for (i in 0 until rr) {
            result = result.multiply(BigDecimal.valueOf((n - i).toLong()), ctx)
            result = result.divide(BigDecimal.valueOf((i + 1).toLong()), ctx)
        }
        return result.toDouble()
    }

    // Conversions
    fun degToRad(deg: Double): Double = Math.toRadians(deg)
    fun radToDeg(rad: Double): Double = Math.toDegrees(rad)

    // Constants
    fun pi(): Double = PI
    fun e(): Double = E
    fun phi(): Double = (1 + kotlin.math.sqrt(5.0)) / 2

    // Format result for display
    fun format(value: Double): String {
        if (value.isNaN()) return "NaN"
        if (value.isInfinite()) return if (value > 0) "∞" else "-∞"
        if (value == floor(value) && kotlin.math.abs(value) < 1e15) {
            return value.toLong().toString()
        }
        var s = String.format("%.10g", value)
        if (s.contains(".")) {
            s = s.replace(Regex("0+$"), "")
            s = s.replace(Regex("\\.$"), "")
        }
        return s
    }
}
