package com.vibecoding.calculator.engine;

import java.util.ArrayList;
import java.util.List;

/**
 * Financial calculator engine inspired by HP 12C.
 * Implements TVM, NPV, IRR, amortization, depreciation, bonds, and statistics.
 */
public class FinancialEngine {

    // ========== TIME VALUE OF MONEY (TVM) ==========

    /**
     * Calculate Future Value.
     * FV = -PV*(1+i)^n - PMT*((1+i)^n - 1)/i * (1 + i*type)
     * type=0 end-of-period, type=1 begin-of-period
     */
    public double futureValue(double n, double annualRate, double pv, double pmt, boolean beginMode) {
        double i = annualRate / 100.0;
        if (i == 0) return -(pv + pmt * n);
        double t = beginMode ? 1 : 0;
        double factor = Math.pow(1 + i, n);
        return -(pv * factor + pmt * ((factor - 1) / i) * (1 + i * t));
    }

    /**
     * Calculate Present Value.
     */
    public double presentValue(double n, double annualRate, double pmt, double fv, boolean beginMode) {
        double i = annualRate / 100.0;
        if (i == 0) return -(fv + pmt * n);
        double t = beginMode ? 1 : 0;
        double factor = Math.pow(1 + i, n);
        return -(fv / factor + pmt * ((factor - 1) / (i * factor)) * (1 + i * t));
    }

    /**
     * Calculate Payment (PMT).
     */
    public double payment(double n, double annualRate, double pv, double fv, boolean beginMode) {
        double i = annualRate / 100.0;
        if (i == 0) return -(pv + fv) / n;
        double t = beginMode ? 1 : 0;
        double factor = Math.pow(1 + i, n);
        return -(pv * factor + fv) / (((factor - 1) / i) * (1 + i * t));
    }

    /**
     * Calculate number of periods (N).
     */
    public double periods(double annualRate, double pv, double pmt, double fv, boolean beginMode) {
        double i = annualRate / 100.0;
        if (i == 0) {
            if (pmt == 0) throw new ArithmeticException("Impossível calcular N");
            return -(pv + fv) / pmt;
        }
        double t = beginMode ? 1 : 0;
        double pmtAdj = pmt * (1 + i * t);
        double num = pmtAdj - fv * i;
        double den = pmtAdj + pv * i;
        if (num / den <= 0) throw new ArithmeticException("Valores TVM inconsistentes");
        return Math.log(num / den) / Math.log(1 + i);
    }

    /**
     * Calculate interest rate using Newton-Raphson iteration.
     */
    public double interestRate(double n, double pv, double pmt, double fv, boolean beginMode) {
        double guess = 0.1;
        for (int iter = 0; iter < 1000; iter++) {
            double i = guess;
            double t = beginMode ? 1 : 0;
            double factor = Math.pow(1 + i, n);
            double f = pv * factor + pmt * ((factor - 1) / i) * (1 + i * t) + fv;

            // Derivative
            double dfactor = n * Math.pow(1 + i, n - 1);
            double dAnnuity = (dfactor * i - (factor - 1)) / (i * i);
            double df = pv * dfactor + pmt * (dAnnuity * (1 + i * t) + ((factor - 1) / i) * t);

            if (Math.abs(df) < 1e-20) break;
            double next = i - f / df;
            if (Math.abs(next - i) < 1e-12) return next * 100;
            guess = next;
        }
        return guess * 100;
    }

    // ========== NPV / IRR ==========

    /**
     * Net Present Value given a rate and cash flows.
     */
    public double npv(double rate, double[] cashFlows) {
        double r = rate / 100.0;
        double npv = 0;
        for (int t = 0; t < cashFlows.length; t++) {
            npv += cashFlows[t] / Math.pow(1 + r, t);
        }
        return npv;
    }

    /**
     * Internal Rate of Return using bisection method.
     */
    public double irr(double[] cashFlows) {
        double lo = -0.999, hi = 10.0;
        for (int iter = 0; iter < 1000; iter++) {
            double mid = (lo + hi) / 2;
            double val = 0;
            for (int t = 0; t < cashFlows.length; t++) {
                val += cashFlows[t] / Math.pow(1 + mid, t);
            }
            if (Math.abs(val) < 1e-10) return mid * 100;
            double loVal = 0;
            for (int t = 0; t < cashFlows.length; t++) {
                loVal += cashFlows[t] / Math.pow(1 + lo, t);
            }
            if ((val > 0) == (loVal > 0)) lo = mid; else hi = mid;
        }
        return ((lo + hi) / 2) * 100;
    }

    // ========== AMORTIZATION ==========

    public static class AmortRow {
        public final int period;
        public final double payment;
        public final double interest;
        public final double principal;
        public final double balance;

        public AmortRow(int period, double payment, double interest, double principal, double balance) {
            this.period = period;
            this.payment = payment;
            this.interest = interest;
            this.principal = principal;
            this.balance = balance;
        }
    }

    /**
     * Generate amortization schedule (Price/French system).
     */
    public List<AmortRow> amortizationSchedule(double principal, double annualRate, int totalPeriods) {
        double i = annualRate / 100.0;
        double pmt;
        if (i == 0) {
            pmt = principal / totalPeriods;
        } else {
            pmt = principal * (i * Math.pow(1 + i, totalPeriods)) / (Math.pow(1 + i, totalPeriods) - 1);
        }

        List<AmortRow> schedule = new ArrayList<>();
        double balance = principal;
        for (int p = 1; p <= totalPeriods; p++) {
            double interestPart = balance * i;
            double principalPart = pmt - interestPart;
            balance -= principalPart;
            if (Math.abs(balance) < 0.01) balance = 0;
            schedule.add(new AmortRow(p, pmt, interestPart, principalPart, balance));
        }
        return schedule;
    }

    // ========== DEPRECIATION ==========

    /** Straight-line depreciation per period. */
    public double depreciationSL(double cost, double salvage, double life) {
        if (life <= 0) throw new ArithmeticException("Vida útil inválida");
        return (cost - salvage) / life;
    }

    /** Declining balance depreciation for year j. */
    public double depreciationDB(double cost, double salvage, double life, int year, double factor) {
        if (life <= 0 || year <= 0) throw new ArithmeticException("Parâmetros inválidos");
        double rate = factor / life;
        double bookValue = cost;
        for (int y = 1; y < year; y++) {
            double dep = bookValue * rate;
            bookValue -= dep;
            if (bookValue < salvage) bookValue = salvage;
        }
        double dep = bookValue * rate;
        if (bookValue - dep < salvage) dep = bookValue - salvage;
        return Math.max(dep, 0);
    }

    /** Sum-of-years'-digits depreciation for year j. */
    public double depreciationSYD(double cost, double salvage, double life, int year) {
        if (life <= 0 || year <= 0) throw new ArithmeticException("Parâmetros inválidos");
        double sumYears = life * (life + 1) / 2;
        double remaining = life - year + 1;
        return (cost - salvage) * remaining / sumYears;
    }

    // ========== BONDS ==========

    /**
     * Bond price given yield, coupon rate, periods.
     */
    public double bondPrice(double faceValue, double couponRate, double yieldRate, int periods) {
        double c = faceValue * couponRate / 100.0;
        double y = yieldRate / 100.0;
        if (y == 0) return c * periods + faceValue;
        double pvCoupons = c * (1 - Math.pow(1 + y, -periods)) / y;
        double pvFace = faceValue / Math.pow(1 + y, periods);
        return pvCoupons + pvFace;
    }

    // ========== PERCENTAGE CALCULATIONS ==========

    public double percentOf(double base, double percent) {
        return base * percent / 100.0;
    }

    public double percentChange(double oldVal, double newVal) {
        if (oldVal == 0) throw new ArithmeticException("Valor base zero");
        return ((newVal - oldVal) / Math.abs(oldVal)) * 100.0;
    }

    public double percentTotal(double part, double total) {
        if (total == 0) throw new ArithmeticException("Total zero");
        return (part / total) * 100.0;
    }

    public double markup(double cost, double price) {
        if (cost == 0) throw new ArithmeticException("Custo zero");
        return ((price - cost) / cost) * 100.0;
    }

    public double margin(double cost, double price) {
        if (price == 0) throw new ArithmeticException("Preço zero");
        return ((price - cost) / price) * 100.0;
    }

    // ========== STATISTICS ==========

    public double mean(double[] values) {
        if (values.length == 0) throw new ArithmeticException("Sem dados");
        double sum = 0;
        for (double v : values) sum += v;
        return sum / values.length;
    }

    public double stdDev(double[] values) {
        if (values.length < 2) throw new ArithmeticException("Dados insuficientes");
        double m = mean(values);
        double sum = 0;
        for (double v : values) sum += (v - m) * (v - m);
        return Math.sqrt(sum / (values.length - 1));
    }

    public double[] linearRegression(double[] x, double[] y) {
        if (x.length != y.length || x.length < 2) throw new ArithmeticException("Dados insuficientes");
        int n = x.length;
        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;
        for (int i = 0; i < n; i++) {
            sumX += x[i]; sumY += y[i]; sumXY += x[i] * y[i]; sumX2 += x[i] * x[i];
        }
        double denom = n * sumX2 - sumX * sumX;
        if (denom == 0) throw new ArithmeticException("Regressão indefinida");
        double b = (n * sumXY - sumX * sumY) / denom;
        double a = (sumY - b * sumX) / n;
        // r^2
        double meanY = sumY / n;
        double ssTot = 0, ssRes = 0;
        for (int i = 0; i < n; i++) {
            ssTot += (y[i] - meanY) * (y[i] - meanY);
            double pred = a + b * x[i];
            ssRes += (y[i] - pred) * (y[i] - pred);
        }
        double r2 = ssTot == 0 ? 1 : 1 - ssRes / ssTot;
        return new double[]{a, b, r2};
    }

    // Format
    public String format(double value) {
        if (Double.isNaN(value)) return "NaN";
        if (Double.isInfinite(value)) return value > 0 ? "∞" : "-∞";
        return String.format("%,.2f", value);
    }

    public String formatPercent(double value) {
        return String.format("%.4f%%", value);
    }
}
