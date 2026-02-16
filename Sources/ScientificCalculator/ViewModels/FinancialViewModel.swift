import SwiftUI

// MARK: - Financial Calculator ViewModel

@MainActor
@Observable
final class FinancialViewModel {
    // TVM Registers
    var nPeriods: String = ""
    var interestRate: String = ""
    var presentValue: String = ""
    var payment: String = ""
    var futureValue: String = ""
    var beginMode: Bool = false

    // Cash Flows
    var cashFlows: [String] = [""]
    var npvResult: String = ""
    var irrResult: String = ""

    // Amortization
    var amortPrincipal: String = ""
    var amortRate: String = ""
    var amortPeriods: String = ""
    var amortSchedule: [FinancialEngine.AmortizationRow] = []

    // Output Log
    var outputLog: [String] = []

    // MARK: - TVM Calculations

    func calculateFV() {
        guard let n = Double(nPeriods),
              let i = Double(interestRate),
              let pv = Double(presentValue),
              let pmt = Double(payment) else {
            addLog("Erro: Preencha N, i%, PV e PMT")
            return
        }
        let fv = FinancialEngine.futureValue(n: n, rate: i, pv: pv, pmt: pmt, beginMode: beginMode)
        futureValue = String(format: "%.2f", fv)
        addLog("FV = \(FinancialEngine.formatCurrency(fv))")
    }

    func calculatePV() {
        guard let n = Double(nPeriods),
              let i = Double(interestRate),
              let pmt = Double(payment),
              let fv = Double(futureValue) else {
            addLog("Erro: Preencha N, i%, PMT e FV")
            return
        }
        let pv = FinancialEngine.presentValue(n: n, rate: i, fv: fv, pmt: pmt, beginMode: beginMode)
        presentValue = String(format: "%.2f", pv)
        addLog("PV = \(FinancialEngine.formatCurrency(pv))")
    }

    func calculatePMT() {
        guard let n = Double(nPeriods),
              let i = Double(interestRate),
              let pv = Double(presentValue),
              let fv = Double(futureValue) else {
            addLog("Erro: Preencha N, i%, PV e FV")
            return
        }
        let pmt = FinancialEngine.payment(n: n, rate: i, pv: pv, fv: fv, beginMode: beginMode)
        payment = String(format: "%.2f", pmt)
        addLog("PMT = \(FinancialEngine.formatCurrency(pmt))")
    }

    func calculateN() {
        guard let i = Double(interestRate),
              let pv = Double(presentValue),
              let pmt = Double(payment),
              let fv = Double(futureValue) else {
            addLog("Erro: Preencha i%, PV, PMT e FV")
            return
        }
        if let n = FinancialEngine.numberOfPeriods(rate: i, pv: pv, pmt: pmt, fv: fv, beginMode: beginMode) {
            nPeriods = String(format: "%.2f", n)
            addLog("N = \(String(format: "%.2f", n)) períodos")
        } else {
            addLog("Erro: Não foi possível calcular N")
        }
    }

    func calculateI() {
        guard let n = Double(nPeriods),
              let pv = Double(presentValue),
              let pmt = Double(payment),
              let fv = Double(futureValue) else {
            addLog("Erro: Preencha N, PV, PMT e FV")
            return
        }
        if let i = FinancialEngine.interestRate(n: n, pv: pv, pmt: pmt, fv: fv, beginMode: beginMode) {
            interestRate = String(format: "%.4f", i)
            addLog("i% = \(FinancialEngine.formatPercent(i))")
        } else {
            addLog("Erro: Não foi possível calcular i%")
        }
    }

    // MARK: - Cash Flows

    func addCashFlow() {
        cashFlows.append("")
    }

    func removeCashFlow(at index: Int) {
        guard cashFlows.count > 1 else { return }
        cashFlows.remove(at: index)
    }

    func calculateNPV() {
        guard let rate = Double(interestRate) else {
            addLog("Erro: Informe i% para NPV")
            return
        }
        let flows = cashFlows.compactMap { Double($0) }
        guard !flows.isEmpty else {
            addLog("Erro: Informe os fluxos de caixa")
            return
        }
        let result = FinancialEngine.npv(rate: rate, cashFlows: flows)
        npvResult = String(format: "%.2f", result)
        addLog("NPV = \(FinancialEngine.formatCurrency(result))")
    }

    func calculateIRR() {
        let flows = cashFlows.compactMap { Double($0) }
        guard flows.count >= 2 else {
            addLog("Erro: Mínimo 2 fluxos de caixa")
            return
        }
        if let result = FinancialEngine.irr(cashFlows: flows) {
            irrResult = String(format: "%.4f", result)
            addLog("IRR = \(FinancialEngine.formatPercent(result))")
        } else {
            addLog("Erro: Não foi possível calcular IRR")
        }
    }

    // MARK: - Amortization

    func calculateAmortization() {
        guard let principal = Double(amortPrincipal),
              let rate = Double(amortRate),
              let periods = Int(amortPeriods) else {
            addLog("Erro: Preencha principal, taxa e períodos")
            return
        }
        amortSchedule = FinancialEngine.amortizationSchedule(
            principal: principal,
            annualRate: rate,
            periods: periods
        )
        addLog("Tabela de amortização gerada: \(periods) períodos")
    }

    // MARK: - Controls

    func clearAll() {
        nPeriods = ""
        interestRate = ""
        presentValue = ""
        payment = ""
        futureValue = ""
        beginMode = false
        cashFlows = [""]
        npvResult = ""
        irrResult = ""
        amortPrincipal = ""
        amortRate = ""
        amortPeriods = ""
        amortSchedule = []
        outputLog = []
    }

    func clearLog() {
        outputLog = []
    }

    // MARK: - Private

    private func addLog(_ message: String) {
        outputLog.insert(message, at: 0)
    }
}
