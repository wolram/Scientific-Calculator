package com.vibecoding.calculator.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.vibecoding.calculator.engine.ExpressionParser

data class GraphFunction(
    val expression: String = "",
    val enabled: Boolean = true
)

class GraphingViewModel : ViewModel() {
    val functions = mutableStateListOf(
        GraphFunction("sin(x)"),
        GraphFunction("")
    )

    var useDegrees by mutableStateOf(false)
        private set
    var showGrid by mutableStateOf(true)
        private set

    // Viewport
    var xMin by mutableStateOf(-10.0)
        private set
    var xMax by mutableStateOf(10.0)
        private set
    var yMin by mutableStateOf(-6.0)
        private set
    var yMax by mutableStateOf(6.0)
        private set

    // Quick evaluator
    var evalExpression by mutableStateOf("")
        private set
    var evalResult by mutableStateOf("")
        private set

    fun updateFunction(index: Int, expression: String) {
        if (index < functions.size) {
            functions[index] = functions[index].copy(expression = expression)
        }
    }

    fun toggleFunction(index: Int) {
        if (index < functions.size) {
            functions[index] = functions[index].copy(enabled = !functions[index].enabled)
        }
    }

    fun addFunction() {
        if (functions.size < 6) {
            functions.add(GraphFunction(""))
        }
    }

    fun removeFunction(index: Int) {
        if (functions.size > 1 && index < functions.size) {
            functions.removeAt(index)
        }
    }

    fun toggleAngleMode() {
        useDegrees = !useDegrees
    }

    fun toggleGrid() {
        showGrid = !showGrid
    }

    fun zoomIn() {
        val xCenter = (xMin + xMax) / 2
        val yCenter = (yMin + yMax) / 2
        val xRange = (xMax - xMin) * 0.4
        val yRange = (yMax - yMin) * 0.4
        xMin = xCenter - xRange
        xMax = xCenter + xRange
        yMin = yCenter - yRange
        yMax = yCenter + yRange
    }

    fun zoomOut() {
        val xCenter = (xMin + xMax) / 2
        val yCenter = (yMin + yMax) / 2
        val xRange = (xMax - xMin) * 0.6
        val yRange = (yMax - yMin) * 0.6
        xMin = xCenter - xRange
        xMax = xCenter + xRange
        yMin = yCenter - yRange
        yMax = yCenter + yRange
    }

    fun resetViewport() {
        xMin = -10.0
        xMax = 10.0
        yMin = -6.0
        yMax = 6.0
    }

    fun updateEvalExpression(expr: String) {
        evalExpression = expr
    }

    fun evaluate() {
        if (evalExpression.isEmpty()) return
        try {
            val parser = ExpressionParser(evalExpression, useDegrees)
            val value = parser.parse()
            evalResult = formatResult(value)
        } catch (e: Exception) {
            evalResult = "Erro"
        }
    }

    fun evaluateAt(expression: String, x: Double): Double? {
        if (expression.isBlank()) return null
        return try {
            val parser = ExpressionParser(expression, useDegrees, variableX = x)
            parser.parse()
        } catch (_: Exception) {
            null
        }
    }

    fun loadPreset(preset: String) {
        val emptyIndex = functions.indexOfFirst { it.expression.isBlank() }
        if (emptyIndex >= 0) {
            functions[emptyIndex] = GraphFunction(preset, true)
        } else if (functions.size < 6) {
            functions.add(GraphFunction(preset, true))
        }
    }

    private fun formatResult(value: Double): String {
        if (value.isNaN()) return "NaN"
        if (value.isInfinite()) return if (value > 0) "∞" else "-∞"
        if (value == kotlin.math.floor(value) && kotlin.math.abs(value) < 1e15) {
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
