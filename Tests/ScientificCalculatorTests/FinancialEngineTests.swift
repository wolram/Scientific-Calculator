import Testing
@testable import ScientificCalculator

@Suite("FinancialEngine Tests")
struct FinancialEngineTests {

    // MARK: - TVM

    @Test func futureValue() {
        // $1000 at 5% for 10 years, no payments
        let fv = FinancialEngine.futureValue(n: 10, rate: 5, pv: -1000, pmt: 0)
        #expect(abs(fv - 1628.89) < 0.01)
    }

    @Test func presentValue() {
        // Need $10000 in 5 years at 8%
        let pv = FinancialEngine.presentValue(n: 5, rate: 8, fv: -10000, pmt: 0)
        #expect(abs(pv - 6805.83) < 0.01)
    }

    @Test func payment() {
        // $200000 loan at 0.5% monthly for 360 months
        let pmt = FinancialEngine.payment(n: 360, rate: 0.5, pv: 200000, fv: 0)
        #expect(abs(pmt + 1199.10) < 0.01)
    }

    // MARK: - NPV

    @Test func npv() {
        let flows = [-1000.0, 300.0, 400.0, 500.0, 200.0]
        let result = FinancialEngine.npv(rate: 10, cashFlows: flows)
        #expect(abs(result - 98.04) < 1)
    }

    // MARK: - Amortization

    @Test func amortizationSchedule() {
        let schedule = FinancialEngine.amortizationSchedule(
            principal: 10000,
            annualRate: 12,
            periods: 12
        )
        #expect(schedule.count == 12)
        #expect(abs(schedule.last!.balance) < 0.01)
    }

    // MARK: - Depreciation

    @Test func straightLineDepreciation() {
        let dep = FinancialEngine.straightLineDepreciation(cost: 10000, salvage: 1000, life: 5)
        #expect(abs(dep - 1800) < 0.01)
    }

    @Test func sydDepreciation() {
        let dep = FinancialEngine.sydDepreciation(cost: 10000, salvage: 1000, life: 5, year: 1)
        #expect(abs(dep - 3000) < 0.01)
    }

    // MARK: - Percentage

    @Test func percentChange() {
        let change = FinancialEngine.percentChange(from: 100, to: 150)
        #expect(change == 50)
    }

    @Test func percentOf() {
        #expect(FinancialEngine.percentOf(200, percent: 15) == 30)
    }

    // MARK: - Bond Pricing

    @Test func bondPrice() {
        let price = FinancialEngine.bondPrice(
            faceValue: 1000,
            couponRate: 6,
            yieldRate: 8,
            periods: 10
        )
        #expect(abs(price - 864.10) < 1)
    }

    // MARK: - Formatting

    @Test func currencyFormatting() {
        let formatted = FinancialEngine.formatCurrency(1234.56)
        #expect(formatted.contains("1.234,56") || formatted.contains("1234.56") || formatted.contains("1,234.56"))
    }
}
