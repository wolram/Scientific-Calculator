import Testing
@testable import ScientificCalculator

@Suite("CoreRPN Tests")
struct CoreRPNTests {
    @Test func enterPushesXToStack() {
        var state = CoreRPNState()

        state.apply(.digit(1))
        state.apply(.digit(2))
        state.apply(.enter)

        #expect(state.stack.x == 12)
        #expect(state.stack.y == 12)
        #expect(state.stack.z == 0)
    }

    @Test func chsWorksInEntryAndInStack() {
        var state = CoreRPNState()

        state.apply(.digit(5))
        state.apply(.chs)
        #expect(state.stack.x == -5)
        #expect(state.display == "-5")

        state.apply(.enter)
        state.apply(.chs)
        #expect(state.stack.x == 5)
        #expect(state.stack.y == -5)
    }

    @Test func clxClearsOnlyX() {
        var state = CoreRPNState()

        inputNumber(7, into: &state)
        state.apply(.enter)
        inputNumber(3, into: &state)
        state.apply(.clx)

        #expect(state.stack.x == 0)
        #expect(state.stack.y == 7)
        #expect(state.stack.z == 0)
    }

    @Test func swapXYExchangesRegisters() {
        var state = CoreRPNState()

        inputNumber(8, into: &state)
        state.apply(.enter)
        inputNumber(2, into: &state)
        state.apply(.swapXY)

        #expect(state.stack.x == 8)
        #expect(state.stack.y == 2)
    }

    @Test func addConsumesXAndY() {
        var state = CoreRPNState()

        inputNumber(2, into: &state)
        state.apply(.enter)
        inputNumber(3, into: &state)
        state.apply(.add)

        #expect(state.stack.x == 5)
        #expect(state.stack.y == 0)
        #expect(state.lastX == 3)
    }

    @Test func financialRegisterRecallStoreAndSolve() {
        var state = CoreRPNState()

        // STO N
        inputNumber(12, into: &state)
        state.apply(.prefixF)
        state.apply(.financial(.n))
        #expect(state.registerValue(.n) == 12)

        // RCL N
        state.apply(.clx)
        state.apply(.financial(.n))
        #expect(state.stack.x == 12)

        // g FV => solve FV from N/I/PV/PMT
        state.loadX(10)
        state.apply(.prefixF)
        state.apply(.financial(.i))
        state.loadX(-1000)
        state.apply(.prefixF)
        state.apply(.financial(.pv))
        state.loadX(0)
        state.apply(.prefixF)
        state.apply(.financial(.pmt))
        state.apply(.prefixG)
        state.apply(.financial(.fv))

        #expect(abs(state.stack.x - 3138.428) < 0.01)
        #expect(abs(state.registerValue(.fv) - 3138.428) < 0.01)
    }

    @Test func npvAndIrrKeysUseCashFlowBuffer() {
        var state = CoreRPNState()

        // Store rate in I register.
        state.loadX(10)
        state.apply(.prefixF)
        state.apply(.financial(.i))

        // Add cash flows with f+NPV.
        for cf in [-1000.0, 300.0, 400.0, 500.0, 200.0] {
            state.loadX(cf)
            state.apply(.prefixF)
            state.apply(.npv)
        }
        #expect(state.cashFlows.count == 5)

        // NPV
        state.apply(.npv)
        #expect(abs(state.stack.x - 115.57) < 0.01)

        // IRR
        state.apply(.irr)
        #expect(state.stack.x.isFinite)
        #expect(state.stack.x > -100 && state.stack.x < 1000)
    }

    @Test func advancedGShiftOperations() {
        var state = CoreRPNState()

        // TVM setup for amortization
        state.loadX(-10000)
        state.apply(.prefixF)
        state.apply(.financial(.pv))
        state.loadX(12)
        state.apply(.prefixF)
        state.apply(.financial(.i))
        state.loadX(12)
        state.apply(.prefixF)
        state.apply(.financial(.n))
        state.apply(.prefixG)
        state.apply(.advanced(.amort))
        #expect(abs(state.stack.x - 888.49) < 0.5)
        #expect(state.lastAmortization.count == 12)

        // Bond pricing: T=face, Z=coupon, Y=yield, X=periods
        state.clearAll()
        state.loadX(1000)
        state.apply(.enter)
        state.loadX(6)
        state.apply(.enter)
        state.loadX(8)
        state.apply(.enter)
        state.loadX(10)
        state.apply(.prefixG)
        state.apply(.advanced(.bond))
        #expect(abs(state.stack.x - 864.10) < 1)

        // Depreciation SL: Z=cost, Y=salvage, X=life
        state.clearAll()
        state.loadX(10000)
        state.apply(.enter)
        state.loadX(1000)
        state.apply(.enter)
        state.loadX(5)
        state.apply(.prefixG)
        state.apply(.advanced(.depSL))
        #expect(abs(state.stack.x - 1800) < 0.01)

        // Depreciation DB/SYD: T=cost, Z=salvage, Y=life, X=year
        state.clearAll()
        state.loadX(10000)
        state.apply(.enter)
        state.loadX(1000)
        state.apply(.enter)
        state.loadX(5)
        state.apply(.enter)
        state.loadX(1)
        state.apply(.prefixG)
        state.apply(.advanced(.depDB))
        #expect(abs(state.stack.x - 2000) < 0.01)

        state.clearAll()
        state.loadX(10000)
        state.apply(.enter)
        state.loadX(1000)
        state.apply(.enter)
        state.loadX(5)
        state.apply(.enter)
        state.loadX(1)
        state.apply(.prefixG)
        state.apply(.advanced(.depSYD))
        #expect(abs(state.stack.x - 3000) < 0.01)
    }

    private func inputNumber(_ value: Int, into state: inout CoreRPNState) {
        for ch in String(value) {
            if let digit = ch.wholeNumberValue {
                state.apply(.digit(digit))
            }
        }
    }
}
