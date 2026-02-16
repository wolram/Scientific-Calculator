package com.vibecoding.calculator.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vibecoding.calculator.ui.components.CalcButton
import com.vibecoding.calculator.ui.components.CalcButtonStyle
import com.vibecoding.calculator.ui.theme.*
import com.vibecoding.calculator.viewmodel.GraphingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GraphingCalculatorScreen(
    onBack: () -> Unit,
    viewModel: GraphingViewModel = viewModel()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
    ) {
        TopAppBar(
            title = { Text("Gráficos", color = TextPrimary) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, "Voltar", tint = AccentBlue)
                }
            },
            actions = {
                IconButton(onClick = { viewModel.toggleGrid() }) {
                    Icon(Icons.Default.GridOn, "Grid",
                        tint = if (viewModel.showGrid) AccentBlue else TextSubtle)
                }
                IconButton(onClick = { viewModel.toggleAngleMode() }) {
                    Text(
                        if (viewModel.useDegrees) "DEG" else "RAD",
                        color = AccentPeach,
                        fontSize = 12.sp
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = BgDark)
        )

        // Graph canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .padding(horizontal = 12.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(BgSurface)
        ) {
            GraphCanvas(viewModel)

            // Zoom controls overlay
            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                SmallFloatingActionButton(
                    onClick = { viewModel.zoomIn() },
                    containerColor = BgOverlay.copy(alpha = 0.8f),
                    contentColor = TextPrimary
                ) { Icon(Icons.Default.Add, "Zoom In", modifier = Modifier.size(18.dp)) }
                SmallFloatingActionButton(
                    onClick = { viewModel.zoomOut() },
                    containerColor = BgOverlay.copy(alpha = 0.8f),
                    contentColor = TextPrimary
                ) { Icon(Icons.Default.Remove, "Zoom Out", modifier = Modifier.size(18.dp)) }
                SmallFloatingActionButton(
                    onClick = { viewModel.resetViewport() },
                    containerColor = BgOverlay.copy(alpha = 0.8f),
                    contentColor = TextPrimary
                ) { Icon(Icons.Default.CenterFocusStrong, "Reset", modifier = Modifier.size(18.dp)) }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Scrollable content below graph
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Function inputs
            viewModel.functions.forEachIndexed { index, func ->
                val color = GraphColors.getOrElse(index) { AccentBlue }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Color indicator
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (func.enabled) color else TextSubtle)
                    )
                    Text("f${index + 1}=", color = TextSubtle, fontSize = 12.sp)
                    OutlinedTextField(
                        value = func.expression,
                        onValueChange = { viewModel.updateFunction(index, it) },
                        modifier = Modifier.weight(1f).height(48.dp),
                        textStyle = LocalTextStyle.current.copy(
                            color = TextPrimary, fontSize = 14.sp, fontFamily = FontFamily.Monospace
                        ),
                        singleLine = true,
                        placeholder = { Text("ex: sin(x)", color = TextSubtle, fontSize = 13.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = color,
                            unfocusedBorderColor = BgOverlay,
                            cursorColor = color
                        )
                    )
                    IconButton(
                        onClick = { viewModel.toggleFunction(index) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            if (func.enabled) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            "Toggle", tint = if (func.enabled) color else TextSubtle,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    if (viewModel.functions.size > 1) {
                        IconButton(
                            onClick = { viewModel.removeFunction(index) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Close, "Remover", tint = AccentRed,
                                modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            if (viewModel.functions.size < 6) {
                TextButton(onClick = { viewModel.addFunction() }) {
                    Icon(Icons.Default.Add, null, tint = AccentGreen, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Adicionar Função", color = AccentGreen, fontSize = 13.sp)
                }
            }

            // Presets
            Text("Funções Rápidas:", color = TextDim, fontSize = 13.sp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf("sin(x)", "cos(x)", "x^2", "1/x").forEach { preset ->
                    CalcButton(preset, onClick = { viewModel.loadPreset(preset) },
                        style = CalcButtonStyle.Function,
                        modifier = Modifier.weight(1f), fontSize = 11.sp)
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf("sqrt(x)", "ln(x)", "exp(x)", "x^3").forEach { preset ->
                    CalcButton(preset, onClick = { viewModel.loadPreset(preset) },
                        style = CalcButtonStyle.Function,
                        modifier = Modifier.weight(1f), fontSize = 11.sp)
                }
            }

            // Quick evaluator
            Spacer(modifier = Modifier.height(4.dp))
            Text("Avaliar Expressão:", color = TextDim, fontSize = 13.sp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = viewModel.evalExpression,
                    onValueChange = { viewModel.updateEvalExpression(it) },
                    modifier = Modifier.weight(1f).height(48.dp),
                    textStyle = LocalTextStyle.current.copy(
                        color = TextPrimary, fontSize = 14.sp, fontFamily = FontFamily.Monospace
                    ),
                    singleLine = true,
                    placeholder = { Text("ex: sin(pi/4)", color = TextSubtle, fontSize = 13.sp) },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { viewModel.evaluate() }),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentBlue,
                        unfocusedBorderColor = BgOverlay,
                        cursorColor = AccentBlue
                    )
                )
                CalcButton("=", onClick = { viewModel.evaluate() },
                    style = CalcButtonStyle.Accent)
            }

            if (viewModel.evalResult.isNotEmpty()) {
                Text(
                    "= ${viewModel.evalResult}",
                    color = AccentGreen,
                    fontSize = 18.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun GraphCanvas(viewModel: GraphingViewModel) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val xRange = viewModel.xMax - viewModel.xMin
        val yRange = viewModel.yMax - viewModel.yMin

        fun toScreenX(x: Double): Float = ((x - viewModel.xMin) / xRange * w).toFloat()
        fun toScreenY(y: Double): Float = ((viewModel.yMax - y) / yRange * h).toFloat()

        // Grid
        if (viewModel.showGrid) {
            drawGrid(viewModel, w, h, ::toScreenX, ::toScreenY)
        }

        // Axes
        val axisColor = TextSubtle
        val xAxisY = toScreenY(0.0)
        val yAxisX = toScreenX(0.0)

        if (xAxisY in 0f..h) {
            drawLine(axisColor, Offset(0f, xAxisY), Offset(w, xAxisY), strokeWidth = 1.5f)
        }
        if (yAxisX in 0f..w) {
            drawLine(axisColor, Offset(yAxisX, 0f), Offset(yAxisX, h), strokeWidth = 1.5f)
        }

        // Plot functions
        val steps = w.toInt()
        viewModel.functions.forEachIndexed { index, func ->
            if (!func.enabled || func.expression.isBlank()) return@forEachIndexed
            val color = GraphColors.getOrElse(index) { AccentBlue }
            val path = Path()
            var pathStarted = false

            for (i in 0..steps) {
                val x = viewModel.xMin + (i.toDouble() / steps) * xRange
                val y = viewModel.evaluateAt(func.expression, x) ?: continue

                if (y.isNaN() || y.isInfinite() || y < viewModel.yMin - yRange || y > viewModel.yMax + yRange) {
                    pathStarted = false
                    continue
                }

                val sx = toScreenX(x)
                val sy = toScreenY(y)

                if (!pathStarted) {
                    path.moveTo(sx, sy)
                    pathStarted = true
                } else {
                    path.lineTo(sx, sy)
                }
            }

            drawPath(path, color, style = Stroke(width = 2.5f, cap = StrokeCap.Round))
        }
    }
}

private fun DrawScope.drawGrid(
    viewModel: GraphingViewModel,
    w: Float,
    h: Float,
    toScreenX: (Double) -> Float,
    toScreenY: (Double) -> Float
) {
    val gridColor = BgOverlay.copy(alpha = 0.5f)
    val xRange = viewModel.xMax - viewModel.xMin
    val yRange = viewModel.yMax - viewModel.yMin

    val xStep = calculateGridStep(xRange)
    val yStep = calculateGridStep(yRange)

    // Vertical grid lines
    var x = kotlin.math.ceil(viewModel.xMin / xStep) * xStep
    while (x <= viewModel.xMax) {
        val sx = toScreenX(x)
        drawLine(gridColor, Offset(sx, 0f), Offset(sx, h), strokeWidth = 0.5f)

        // Draw label
        if (kotlin.math.abs(x) > xStep * 0.1) {
            drawContext.canvas.nativeCanvas.drawText(
                formatAxisLabel(x),
                sx + 2f,
                h - 4f,
                android.graphics.Paint().apply {
                    color = 0xFF6C7086.toInt()
                    textSize = 20f
                }
            )
        }
        x += xStep
    }

    // Horizontal grid lines
    var y = kotlin.math.ceil(viewModel.yMin / yStep) * yStep
    while (y <= viewModel.yMax) {
        val sy = toScreenY(y)
        drawLine(gridColor, Offset(0f, sy), Offset(w, sy), strokeWidth = 0.5f)

        if (kotlin.math.abs(y) > yStep * 0.1) {
            drawContext.canvas.nativeCanvas.drawText(
                formatAxisLabel(y),
                4f,
                sy - 4f,
                android.graphics.Paint().apply {
                    color = 0xFF6C7086.toInt()
                    textSize = 20f
                }
            )
        }
        y += yStep
    }
}

private fun calculateGridStep(range: Double): Double {
    val rawStep = range / 8
    val magnitude = kotlin.math.pow(10.0, kotlin.math.floor(kotlin.math.log10(rawStep)))
    val normalized = rawStep / magnitude
    return when {
        normalized < 1.5 -> magnitude
        normalized < 3.5 -> 2 * magnitude
        normalized < 7.5 -> 5 * magnitude
        else -> 10 * magnitude
    }
}

private fun formatAxisLabel(value: Double): String {
    return if (value == kotlin.math.floor(value) && kotlin.math.abs(value) < 1e6) {
        value.toInt().toString()
    } else {
        String.format("%.1f", value)
    }
}
