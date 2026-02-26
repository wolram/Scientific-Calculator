package com.vibecoding.calculator.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.vibecoding.calculator.engine.ExpressionParser
import com.vibecoding.calculator.engine.ScientificEngine

class ScientificViewModel : ViewModel() {
    private val engine = ScientificEngine()

    var expression by mutableStateOf("")
        private set
    var result by mutableStateOf("0")
        private set
    var useDegrees by mutableStateOf(true)
        private set
    var secondMode by mutableStateOf(false)
        private set
    var memory by mutableStateOf(0.0)
        private set
    var hasMemory by mutableStateOf(false)
        private set

    private var lastResult: Double = 0.0
    private var justEvaluated = false

    fun toggleAngleMode() {
        useDegrees = !useDegrees
    }

    fun toggleSecondMode() {
        secondMode = !secondMode
    }

    fun onDigit(digit: String) {
        if (justEvaluated) {
            expression = digit
            justEvaluated = false
        } else {
            expression += digit
        }
    }

    fun onOperator(op: String) {
        justEvaluated = false
        expression += op
    }

    fun onFunction(func: String) {
        if (justEvaluated) {
            expression = "$func($result)"
            justEvaluated = false
        } else {
            expression += "$func("
        }
    }

    fun onConstant(name: String) {
        val value = when (name) {
            "π" -> "pi"
            "e" -> "e"
            "φ" -> "phi"
            else -> name
        }
        if (justEvaluated) {
            expression = value
            justEvaluated = false
        } else {
            expression += value
        }
    }

    fun onParenOpen() {
        if (justEvaluated) {
            expression = "("
            justEvaluated = false
        } else {
            expression += "("
        }
    }

    fun onParenClose() {
        expression += ")"
        justEvaluated = false
    }

    fun onDecimal() {
        if (justEvaluated) {
            expression = "0."
            justEvaluated = false
        } else {
            expression += "."
        }
    }

    fun onBackspace() {
        if (expression.isNotEmpty()) {
            expression = expression.dropLast(1)
        }
        justEvaluated = false
    }

    fun onClear() {
        expression = ""
        result = "0"
        justEvaluated = false
    }

    fun onEquals() {
        if (expression.isEmpty()) return
        try {
            val parser = ExpressionParser(expression, useDegrees)
            lastResult = parser.parse()
            result = engine.format(lastResult)
            justEvaluated = true
        } catch (e: Exception) {
            result = "Erro"
        }
    }

    fun onPercent() {
        try {
            val parser = ExpressionParser(expression, useDegrees)
            lastResult = parser.parse() / 100.0
            result = engine.format(lastResult)
            expression = result
            justEvaluated = true
        } catch (_: Exception) {
            result = "Erro"
        }
    }

    fun onNegate() {
        if (expression.isNotEmpty()) {
            expression = if (expression.startsWith("-")) {
                expression.drop(1)
            } else {
                "-$expression"
            }
        }
    }

    fun onFactorial() {
        try {
            val parser = ExpressionParser(expression, useDegrees)
            val value = parser.parse().toInt()
            lastResult = engine.factorial(value)
            result = engine.format(lastResult)
            expression = "$value!"
            justEvaluated = true
        } catch (_: Exception) {
            result = "Erro"
        }
    }

    fun onSquare() {
        onApplyUnary { engine.square(it) }
    }

    fun onCube() {
        onApplyUnary { engine.cube(it) }
    }

    fun onReciprocal() {
        onApplyUnary { engine.reciprocal(it) }
    }

    fun onPower() {
        expression += "^"
        justEvaluated = false
    }

    // Memory operations
    fun onMemoryClear() {
        memory = 0.0
        hasMemory = false
    }

    fun onMemoryRecall() {
        if (justEvaluated) {
            expression = engine.format(memory)
            justEvaluated = false
        } else {
            expression += engine.format(memory)
        }
    }

    fun onMemoryAdd() {
        try {
            val parser = ExpressionParser(expression, useDegrees)
            memory += parser.parse()
            hasMemory = true
        } catch (_: Exception) {}
    }

    fun onMemorySubtract() {
        try {
            val parser = ExpressionParser(expression, useDegrees)
            memory -= parser.parse()
            hasMemory = true
        } catch (_: Exception) {}
    }

    private fun onApplyUnary(operation: (Double) -> Double) {
        try {
            val parser = ExpressionParser(expression, useDegrees)
            val value = parser.parse()
            lastResult = operation(value)
            result = engine.format(lastResult)
            expression = result
            justEvaluated = true
        } catch (_: Exception) {
            result = "Erro"
        }
    }
}
