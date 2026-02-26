package com.vibecoding.calculator.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.vibecoding.calculator.engine.FinancialEngine

class FinancialViewModel : ViewModel() {
    private val engine = FinancialEngine()

    // TVM fields
    var n by mutableStateOf("")
        private set
    var rate by mutableStateOf("")
        private set
    var pv by mutableStateOf("")
        private set
    var pmt by mutableStateOf("")
        private set
    var fv by mutableStateOf("")
        private set
    var beginMode by mutableStateOf(false)
        private set

    // Result
    var result by mutableStateOf("")
        private set
    var resultLabel by mutableStateOf("")
        private set

    // Log
    val log = mutableStateListOf<String>()

    // Active tab
    var activeTab by mutableStateOf(FinancialTab.TVM)
        private set

    // Amortization fields
    var amortPrincipal by mutableStateOf("")
        private set
    var amortRate by mutableStateOf("")
        private set
    var amortPeriods by mutableStateOf("")
        private set
    var amortSchedule = mutableStateListOf<FinancialEngine.AmortRow>()
        private set

    // Cash flow fields
    var cashFlowRate by mutableStateOf("")
        private set
    var cashFlows = mutableStateListOf<String>()
        private set

    // Depreciation fields
    var deprCost by mutableStateOf("")
        private set
    var deprSalvage by mutableStateOf("")
        private set
    var deprLife by mutableStateOf("")
        private set
    var deprYear by mutableStateOf("")
        private set

    init {
        cashFlows.add("")
    }

    fun setActiveTab(tab: FinancialTab) {
        activeTab = tab
        result = ""
        resultLabel = ""
    }

    fun toggleBeginMode() {
        beginMode = !beginMode
    }

    fun updateN(value: String) { n = value }
    fun updateRate(value: String) { rate = value }
    fun updatePV(value: String) { pv = value }
    fun updatePMT(value: String) { pmt = value }
    fun updateFV(value: String) { fv = value }
    fun updateAmortPrincipal(value: String) { amortPrincipal = value }
    fun updateAmortRate(value: String) { amortRate = value }
    fun updateAmortPeriods(value: String) { amortPeriods = value }
    fun updateCashFlowRate(value: String) { cashFlowRate = value }
    fun updateDeprCost(value: String) { deprCost = value }
    fun updateDeprSalvage(value: String) { deprSalvage = value }
    fun updateDeprLife(value: String) { deprLife = value }
    fun updateDeprYear(value: String) { deprYear = value }

    fun updateCashFlow(index: Int, value: String) {
        if (index < cashFlows.size) cashFlows[index] = value
    }

    fun addCashFlow() {
        cashFlows.add("")
    }

    fun removeCashFlow(index: Int) {
        if (cashFlows.size > 1 && index < cashFlows.size) {
            cashFlows.removeAt(index)
        }
    }

    // TVM calculations
    fun calcFV() = tvmCalc("FV") {
        engine.futureValue(n.toDouble(), rate.toDouble(), pv.toDouble(), pmt.toDouble(), beginMode)
    }

    fun calcPV() = tvmCalc("PV") {
        engine.presentValue(n.toDouble(), rate.toDouble(), pmt.toDouble(), fv.toDouble(), beginMode)
    }

    fun calcPMT() = tvmCalc("PMT") {
        engine.payment(n.toDouble(), rate.toDouble(), pv.toDouble(), fv.toDouble(), beginMode)
    }

    fun calcN() = tvmCalc("N") {
        engine.periods(rate.toDouble(), pv.toDouble(), pmt.toDouble(), fv.toDouble(), beginMode)
    }

    fun calcRate() = tvmCalc("i%") {
        engine.interestRate(n.toDouble(), pv.toDouble(), pmt.toDouble(), fv.toDouble(), beginMode)
    }

    private fun tvmCalc(label: String, calc: () -> Double) {
        try {
            val value = calc()
            result = engine.format(value)
            resultLabel = label
            log.add(0, "$label = $result")
        } catch (e: Exception) {
            result = "Erro: ${e.message}"
            resultLabel = label
        }
    }

    // Amortization
    fun calcAmortization() {
        try {
            val schedule = engine.amortizationSchedule(
                amortPrincipal.toDouble(),
                amortRate.toDouble(),
                amortPeriods.toInt()
            )
            amortSchedule.clear()
            amortSchedule.addAll(schedule)
            result = "Parcela: ${engine.format(schedule.first().payment)}"
            resultLabel = "AMORT"
            log.add(0, "Amortização: ${amortPeriods}x de ${engine.format(schedule.first().payment)}")
        } catch (e: Exception) {
            result = "Erro: ${e.message}"
            resultLabel = "AMORT"
        }
    }

    // NPV/IRR
    fun calcNPV() {
        try {
            val flows = cashFlows.map { it.toDouble() }.toDoubleArray()
            val value = engine.npv(cashFlowRate.toDouble(), flows)
            result = engine.format(value)
            resultLabel = "NPV"
            log.add(0, "NPV = $result")
        } catch (e: Exception) {
            result = "Erro: ${e.message}"
            resultLabel = "NPV"
        }
    }

    fun calcIRR() {
        try {
            val flows = cashFlows.map { it.toDouble() }.toDoubleArray()
            val value = engine.irr(flows)
            result = engine.formatPercent(value)
            resultLabel = "IRR"
            log.add(0, "IRR = $result")
        } catch (e: Exception) {
            result = "Erro: ${e.message}"
            resultLabel = "IRR"
        }
    }

    // Depreciation
    fun calcDepreciationSL() {
        try {
            val value = engine.depreciationSL(deprCost.toDouble(), deprSalvage.toDouble(), deprLife.toDouble())
            result = engine.format(value)
            resultLabel = "DEP SL"
            log.add(0, "Depr. Linear = $result")
        } catch (e: Exception) {
            result = "Erro: ${e.message}"
            resultLabel = "DEP"
        }
    }

    fun calcDepreciationDB() {
        try {
            val value = engine.depreciationDB(
                deprCost.toDouble(), deprSalvage.toDouble(),
                deprLife.toDouble(), deprYear.toInt(), 2.0
            )
            result = engine.format(value)
            resultLabel = "DEP DB"
            log.add(0, "Depr. DB Ano ${deprYear} = $result")
        } catch (e: Exception) {
            result = "Erro: ${e.message}"
            resultLabel = "DEP"
        }
    }

    fun calcDepreciationSYD() {
        try {
            val value = engine.depreciationSYD(
                deprCost.toDouble(), deprSalvage.toDouble(),
                deprLife.toDouble(), deprYear.toInt()
            )
            result = engine.format(value)
            resultLabel = "DEP SYD"
            log.add(0, "Depr. SYD Ano ${deprYear} = $result")
        } catch (e: Exception) {
            result = "Erro: ${e.message}"
            resultLabel = "DEP"
        }
    }

    fun clearLog() {
        log.clear()
    }
}

enum class FinancialTab {
    TVM, Amortization, CashFlow, Depreciation
}
