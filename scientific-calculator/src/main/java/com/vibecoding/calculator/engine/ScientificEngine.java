package com.vibecoding.calculator.engine;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class ScientificEngine {
    private final MathContext ctx = new MathContext(15, RoundingMode.HALF_UP);

    // Basic arithmetic
    public double add(double a, double b) { return a + b; }
    public double subtract(double a, double b) { return a - b; }
    public double multiply(double a, double b) { return a * b; }
    public double divide(double a, double b) {
        if (b == 0.0) throw new ArithmeticException("Divisão por zero");
        return a / b;
    }
    public double mod(double a, double b) {
        if (b == 0.0) throw new ArithmeticException("Módulo por zero");
        return a % b;
    }
    public double abs(double a) { return Math.abs(a); }

    // Powers and roots
    public double pow(double a, double b) { return Math.pow(a, b); }
    public double exp(double a) { return Math.exp(a); }
    public double sqrt(double a) {
        if (a < 0) throw new ArithmeticException("Raiz de número negativo");
        return Math.sqrt(a);
    }
    public double cbrt(double a) { return Math.cbrt(a); }
    public double nthRoot(double a, double n) {
        if (n == 0) throw new ArithmeticException("Raiz de índice zero");
        if (a < 0 && n % 2 == 0) throw new ArithmeticException("Raiz par de número negativo");
        if (a < 0) return -Math.pow(-a, 1.0 / n);
        return Math.pow(a, 1.0 / n);
    }
    public double square(double a) { return a * a; }
    public double cube(double a) { return a * a * a; }
    public double tenPow(double a) { return Math.pow(10, a); }
    public double twoPow(double a) { return Math.pow(2, a); }
    public double reciprocal(double a) {
        if (a == 0.0) throw new ArithmeticException("Inverso de zero");
        return 1.0 / a;
    }

    // Logarithms
    public double ln(double a) {
        if (a <= 0) throw new ArithmeticException("Logaritmo de valor não-positivo");
        return Math.log(a);
    }
    public double log10(double a) {
        if (a <= 0) throw new ArithmeticException("Logaritmo de valor não-positivo");
        return Math.log10(a);
    }
    public double log2(double a) {
        if (a <= 0) throw new ArithmeticException("Logaritmo de valor não-positivo");
        return Math.log(a) / Math.log(2);
    }
    public double logBase(double a, double base) {
        if (a <= 0 || base <= 0 || base == 1) throw new ArithmeticException("Logaritmo inválido");
        return Math.log(a) / Math.log(base);
    }

    // Trigonometric (radians)
    public double sin(double a) { return Math.sin(a); }
    public double cos(double a) { return Math.cos(a); }
    public double tan(double a) { return Math.tan(a); }
    public double asin(double a) {
        if (a < -1 || a > 1) throw new ArithmeticException("arcsin fora do domínio [-1,1]");
        return Math.asin(a);
    }
    public double acos(double a) {
        if (a < -1 || a > 1) throw new ArithmeticException("arccos fora do domínio [-1,1]");
        return Math.acos(a);
    }
    public double atan(double a) { return Math.atan(a); }
    public double atan2(double y, double x) { return Math.atan2(y, x); }

    // Trigonometric (degrees)
    public double sinDeg(double a) { return Math.sin(Math.toRadians(a)); }
    public double cosDeg(double a) { return Math.cos(Math.toRadians(a)); }
    public double tanDeg(double a) { return Math.tan(Math.toRadians(a)); }
    public double asinDeg(double a) { return Math.toDegrees(asin(a)); }
    public double acosDeg(double a) { return Math.toDegrees(acos(a)); }
    public double atanDeg(double a) { return Math.toDegrees(atan(a)); }

    // Hyperbolic
    public double sinh(double a) { return Math.sinh(a); }
    public double cosh(double a) { return Math.cosh(a); }
    public double tanh(double a) { return Math.tanh(a); }
    public double asinh(double a) { return Math.log(a + Math.sqrt(a * a + 1)); }
    public double acosh(double a) {
        if (a < 1) throw new ArithmeticException("acosh domínio: x >= 1");
        return Math.log(a + Math.sqrt(a * a - 1));
    }
    public double atanh(double a) {
        if (a <= -1 || a >= 1) throw new ArithmeticException("atanh domínio: -1 < x < 1");
        return 0.5 * Math.log((1 + a) / (1 - a));
    }

    // Factorial and combinatorics
    public double factorial(int n) {
        if (n < 0) throw new ArithmeticException("Fatorial de negativo");
        if (n > 170) throw new ArithmeticException("Fatorial overflow (max 170!)");
        BigDecimal result = BigDecimal.ONE;
        for (int i = 2; i <= n; i++) {
            result = result.multiply(BigDecimal.valueOf(i), ctx);
        }
        return result.doubleValue();
    }

    public double permutation(int n, int r) {
        if (n < 0 || r < 0 || r > n) throw new ArithmeticException("nPr inválido");
        BigDecimal result = BigDecimal.ONE;
        for (int i = n; i > n - r; i--) {
            result = result.multiply(BigDecimal.valueOf(i), ctx);
        }
        return result.doubleValue();
    }

    public double combination(int n, int r) {
        if (n < 0 || r < 0 || r > n) throw new ArithmeticException("nCr inválido");
        if (r > n - r) r = n - r;
        BigDecimal result = BigDecimal.ONE;
        for (int i = 0; i < r; i++) {
            result = result.multiply(BigDecimal.valueOf(n - i), ctx);
            result = result.divide(BigDecimal.valueOf(i + 1), ctx);
        }
        return result.doubleValue();
    }

    // Conversions
    public double degToRad(double deg) { return Math.toRadians(deg); }
    public double radToDeg(double rad) { return Math.toDegrees(rad); }

    // Constants
    public double pi() { return Math.PI; }
    public double e() { return Math.E; }
    public double phi() { return (1 + Math.sqrt(5)) / 2; } // golden ratio

    // Utility
    public double floor(double a) { return Math.floor(a); }
    public double ceil(double a) { return Math.ceil(a); }
    public double round(double a) { return Math.round(a); }
    public double signum(double a) { return Math.signum(a); }

    // Format result for display
    public String format(double value) {
        if (Double.isNaN(value)) return "NaN";
        if (Double.isInfinite(value)) return value > 0 ? "∞" : "-∞";
        if (value == Math.floor(value) && Math.abs(value) < 1e15) {
            return String.valueOf((long) value);
        }
        String s = String.format("%.10g", value);
        // Remove trailing zeros
        if (s.contains(".")) {
            s = s.replaceAll("0+$", "");
            s = s.replaceAll("\\.$", "");
        }
        return s;
    }
}
