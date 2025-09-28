package com.vibecoding.calculator;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class CalculatorEngine {
  private final MathContext context;

  public CalculatorEngine() {
    this.context = new MathContext(15, RoundingMode.HALF_UP);
  }

  public double add(double a, double b) { return a + b; }
  public double subtract(double a, double b) { return a - b; }
  public double multiply(double a, double b) { return a * b; }
  public double divide(double a, double b) {
    if (b == 0.0) throw new ArithmeticException("Division by zero");
    return a / b;
  }

  public double pow(double a, double b) { return Math.pow(a, b); }
  public double exp(double a) { return Math.exp(a); }
  public double ln(double a) {
    if (a <= 0) throw new ArithmeticException("ln domain error");
    return Math.log(a);
  }
  public double log10(double a) {
    if (a <= 0) throw new ArithmeticException("log10 domain error");
    return Math.log10(a);
  }

  public double sqrt(double a) {
    if (a < 0) throw new ArithmeticException("sqrt domain error");
    return Math.sqrt(a);
  }

  public double factorial(int n) {
    if (n < 0) throw new ArithmeticException("factorial domain error");
    BigDecimal result = BigDecimal.ONE;
    for (int i = 2; i <= n; i++) {
      result = result.multiply(BigDecimal.valueOf(i), context);
    }
    return result.doubleValue();
  }

  public double sinRad(double a) { return Math.sin(a); }
  public double cosRad(double a) { return Math.cos(a); }
  public double tanRad(double a) { return Math.tan(a); }

  public double sinDeg(double a) { return Math.sin(Math.toRadians(a)); }
  public double cosDeg(double a) { return Math.cos(Math.toRadians(a)); }
  public double tanDeg(double a) { return Math.tan(Math.toRadians(a)); }
}



