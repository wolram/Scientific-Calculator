package com.vibecoding.calculator.engine

import kotlin.math.*

class FinancialEngine {

    // ========== TIME VALUE OF MONEY (TVM) ==========

    fun futureValue(n: Double, annualRate: Double, pv: Double, pmt: Double, beginMode: Boolean): Double {
        val i = annualRate / 100.0
        if (i == 0.0) return -(pv + pmt * n)
        val t = if (beginMode) 1.0 else 0.0
        val factor = (1 + i).pow(n)
        return -(pv * factor + pmt * ((factor - 1) / i) * (1 + i * t))
    }

    fun presentValue(n: Double, annualRate: Double, pmt: Double, fv: Double, beginMode: Boolean): Double {
        val i = annualRate / 100.0
        if (i == 0.0) return -(fv + pmt * n)
        val t = if (beginMode) 1.0 else 0.0
        val factor = (1 + i).pow(n)
        return -(fv / factor + pmt * ((factor - 1) / (i * factor)) * (1 + i * t))
    }

    fun payment(n: Double, annualRate: Double, pv: Double, fv: Double, beginMode: Boolean): Double {
        val i = annualRate / 100.0
        if (i == 0.0) return -(pv + fv) / n
        val t = if (beginMode) 1.0 else 0.0
        val factor = (1 + i).pow(n)
        return -(pv * factor + fv) / (((factor - 1) / i) * (1 + i * t))
    }

    fun periods(annualRate: Double, pv: Double, pmt: Double, fv: Double, beginMode: Boolean): Double {
        val i = annualRate / 100.0
        if (i == 0.0) {
            if (pmt == 0.0) throw ArithmeticException("Impossível calcular N")
            return -(pv + fv) / pmt
        }
        val t = if (beginMode) 1.0 else 0.0
        val pmtAdj = pmt * (1 + i * t)
        val num = pmtAdj - fv * i
        val den = pmtAdj + pv * i
        if (num / den <= 0) throw ArithmeticException("Valores TVM inconsistentes")
        return ln(num / den) / ln(1 + i)
    }

    fun interestRate(n: Double, pv: Double, pmt: Double, fv: Double, beginMode: Boolean): Double {
        var guess = 0.1
        for (iter in 0 until 1000) {
            val i = guess
            val t = if (beginMode) 1.0 else 0.0
            val factor = (1 + i).pow(n)
            val f = pv * factor + pmt * ((factor - 1) / i) * (1 + i * t) + fv

            val dfactor = n * (1 + i).pow(n - 1)
            val dAnnuity = (dfactor * i - (factor - 1)) / (i * i)
            val df = pv * dfactor + pmt * (dAnnuity * (1 + i * t) + ((factor - 1) / i) * t)

            if (abs(df) < 1e-20) break
            val next = i - f / df
            if (abs(next - i) < 1e-12) return next * 100
            guess = next
        }
        return guess * 100
    }

    // ========== NPV / IRR ==========

    fun npv(rate: Double, cashFlows: DoubleArray): Double {
        val r = rate / 100.0
        var result = 0.0
        for (t in cashFlows.indices) {
            result += cashFlows[t] / (1 + r).pow(t.toDouble())
        }
        return result
    }

    fun irr(cashFlows: DoubleArray): Double {
        var lo = -0.999
        var hi = 10.0
        for (iter in 0 until 1000) {
            val mid = (lo + hi) / 2
            var value = 0.0
            for (t in cashFlows.indices) {
                value += cashFlows[t] / (1 + mid).pow(t.toDouble())
            }
            if (abs(value) < 1e-10) return mid * 100
            var loVal = 0.0
            for (t in cashFlows.indices) {
                loVal += cashFlows[t] / (1 + lo).pow(t.toDouble())
            }
            if ((value > 0) == (loVal > 0)) lo = mid else hi = mid
        }
        return ((lo + hi) / 2) * 100
    }

    // ========== AMORTIZATION ==========

    data class AmortRow(
        val period: Int,
        val payment: Double,
        val interest: Double,
        val principal: Double,
        val balance: Double
    )

    fun amortizationSchedule(principal: Double, annualRate: Double, totalPeriods: Int): List<AmortRow> {
        val i = annualRate / 100.0
        val pmt = if (i == 0.0) {
            principal / totalPeriods
        } else {
            principal * (i * (1 + i).pow(totalPeriods.toDouble())) / ((1 + i).pow(totalPeriods.toDouble()) - 1)
        }

        val schedule = mutableListOf<AmortRow>()
        var balance = principal
        for (p in 1..totalPeriods) {
            val interestPart = balance * i
            val principalPart = pmt - interestPart
            balance -= principalPart
            if (abs(balance) < 0.01) balance = 0.0
            schedule.add(AmortRow(p, pmt, interestPart, principalPart, balance))
        }
        return schedule
    }

    // ========== DEPRECIATION ==========

    fun depreciationSL(cost: Double, salvage: Double, life: Double): Double {
        if (life <= 0) throw ArithmeticException("Vida útil inválida")
        return (cost - salvage) / life
    }

    fun depreciationDB(cost: Double, salvage: Double, life: Double, year: Int, factor: Double): Double {
        if (life <= 0 || year <= 0) throw ArithmeticException("Parâmetros inválidos")
        val rate = factor / life
        var bookValue = cost
        for (y in 1 until year) {
            val dep = bookValue * rate
            bookValue -= dep
            if (bookValue < salvage) bookValue = salvage
        }
        var dep = bookValue * rate
        if (bookValue - dep < salvage) dep = bookValue - salvage
        return maxOf(dep, 0.0)
    }

    fun depreciationSYD(cost: Double, salvage: Double, life: Double, year: Int): Double {
        if (life <= 0 || year <= 0) throw ArithmeticException("Parâmetros inválidos")
        val sumYears = life * (life + 1) / 2
        val remaining = life - year + 1
        return (cost - salvage) * remaining / sumYears
    }

    // ========== BONDS ==========

    fun bondPrice(faceValue: Double, couponRate: Double, yieldRate: Double, periods: Int): Double {
        val c = faceValue * couponRate / 100.0
        val y = yieldRate / 100.0
        if (y == 0.0) return c * periods + faceValue
        val pvCoupons = c * (1 - (1 + y).pow(-periods.toDouble())) / y
        val pvFace = faceValue / (1 + y).pow(periods.toDouble())
        return pvCoupons + pvFace
    }

    // ========== PERCENTAGE CALCULATIONS ==========

    fun percentOf(base: Double, percent: Double): Double = base * percent / 100.0

    fun percentChange(oldVal: Double, newVal: Double): Double {
        if (oldVal == 0.0) throw ArithmeticException("Valor base zero")
        return ((newVal - oldVal) / abs(oldVal)) * 100.0
    }

    fun markup(cost: Double, price: Double): Double {
        if (cost == 0.0) throw ArithmeticException("Custo zero")
        return ((price - cost) / cost) * 100.0
    }

    fun margin(cost: Double, price: Double): Double {
        if (price == 0.0) throw ArithmeticException("Preço zero")
        return ((price - cost) / price) * 100.0
    }

    // Format
    fun format(value: Double): String {
        if (value.isNaN()) return "NaN"
        if (value.isInfinite()) return if (value > 0) "∞" else "-∞"
        return String.format("%,.2f", value)
    }

    fun formatPercent(value: Double): String = String.format("%.4f%%", value)
}
