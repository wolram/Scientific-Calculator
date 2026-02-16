package com.vibecoding.calculator.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vibecoding.calculator.ui.components.CalcButton
import com.vibecoding.calculator.ui.components.CalcButtonStyle
import com.vibecoding.calculator.ui.components.DisplayPanel
import com.vibecoding.calculator.ui.theme.*
import com.vibecoding.calculator.viewmodel.ScientificViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScientificCalculatorScreen(
    onBack: () -> Unit,
    viewModel: ScientificViewModel = viewModel()
) {
    val angleLabel = if (viewModel.useDegrees) "DEG" else "RAD"
    val memLabel = if (viewModel.hasMemory) "M" else ""
    val statusLine = "$angleLabel  $memLabel"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
    ) {
        // Top bar
        TopAppBar(
            title = { Text("Científica", color = TextPrimary) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, "Voltar", tint = AccentBlue)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = BgDark)
        )

        // Display
        DisplayPanel(
            expression = viewModel.expression,
            result = viewModel.result,
            statusLine = statusLine,
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Button grid
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Row 1: Mode toggles + memory
            ButtonRow {
                CalcButton("2nd", onClick = { viewModel.toggleSecondMode() },
                    style = if (viewModel.secondMode) CalcButtonStyle.Accent else CalcButtonStyle.Function,
                    modifier = Modifier.weight(1f), fontSize = 13.sp)
                CalcButton(angleLabel, onClick = { viewModel.toggleAngleMode() },
                    style = CalcButtonStyle.Function, modifier = Modifier.weight(1f), fontSize = 13.sp)
                CalcButton("MC", onClick = { viewModel.onMemoryClear() },
                    style = CalcButtonStyle.Function, modifier = Modifier.weight(1f), fontSize = 13.sp)
                CalcButton("MR", onClick = { viewModel.onMemoryRecall() },
                    style = CalcButtonStyle.Function, modifier = Modifier.weight(1f), fontSize = 13.sp)
                CalcButton("M+", onClick = { viewModel.onMemoryAdd() },
                    style = CalcButtonStyle.Function, modifier = Modifier.weight(1f), fontSize = 13.sp)
                CalcButton("M−", onClick = { viewModel.onMemorySubtract() },
                    style = CalcButtonStyle.Function, modifier = Modifier.weight(1f), fontSize = 13.sp)
            }

            // Row 2: Scientific functions
            ButtonRow {
                if (viewModel.secondMode) {
                    CalcButton("sin⁻¹", onClick = { viewModel.onFunction("asin") },
                        style = CalcButtonStyle.Function, modifier = Modifier.weight(1f), fontSize = 12.sp)
                    CalcButton("cos⁻¹", onClick = { viewModel.onFunction("acos") },
                        style = CalcButtonStyle.Function, modifier = Modifier.weight(1f), fontSize = 12.sp)
                    CalcButton("tan⁻¹", onClick = { viewModel.onFunction("atan") },
                        style = CalcButtonStyle.Function, modifier = Modifier.weight(1f), fontSize = 12.sp)
                } else {
                    CalcButton("sin", onClick = { viewModel.onFunction("sin") },
                        style = CalcButtonStyle.Function, modifier = Modifier.weight(1f), fontSize = 13.sp)
                    CalcButton("cos", onClick = { viewModel.onFunction("cos") },
                        style = CalcButtonStyle.Function, modifier = Modifier.weight(1f), fontSize = 13.sp)
                    CalcButton("tan", onClick = { viewModel.onFunction("tan") },
                        style = CalcButtonStyle.Function, modifier = Modifier.weight(1f), fontSize = 13.sp)
                }
                CalcButton("x²", onClick = { viewModel.onSquare() },
                    style = CalcButtonStyle.Function, modifier = Modifier.weight(1f), fontSize = 13.sp)
                CalcButton("x³", onClick = { viewModel.onCube() },
                    style = CalcButtonStyle.Function, modifier = Modifier.weight(1f), fontSize = 13.sp)
                CalcButton("xⁿ", onClick = { viewModel.onPower() },
                    style = CalcButtonStyle.Function, modifier = Modifier.weight(1f), fontSize = 13.sp)
            }

            // Row 3: More scientific functions
            ButtonRow {
                if (viewModel.secondMode) {
                    CalcButton("sinh", onClick = { viewModel.onFunction("sinh") },
                        style = CalcButtonStyle.Function, modifier = Modifier.weight(1f), fontSize = 12.sp)
                    CalcButton("cosh", onClick = { viewModel.onFunction("cosh") },
                        style = CalcButtonStyle.Function, modifier = Modifier.weight(1f), fontSize = 12.sp)
                    CalcButton("tanh", onClick = { viewModel.onFunction("tanh") },
                        style = CalcButtonStyle.Function, modifier = Modifier.weight(1f), fontSize = 12.sp)
                } else {
                    CalcButton("ln", onClick = { viewModel.onFunction("ln") },
                        style = CalcButtonStyle.Function, modifier = Modifier.weight(1f), fontSize = 13.sp)
                    CalcButton("log", onClick = { viewModel.onFunction("log") },
                        style = CalcButtonStyle.Function, modifier = Modifier.weight(1f), fontSize = 13.sp)
                    CalcButton("log₂", onClick = { viewModel.onFunction("log2") },
                        style = CalcButtonStyle.Function, modifier = Modifier.weight(1f), fontSize = 12.sp)
                }
                CalcButton("√x", onClick = { viewModel.onFunction("sqrt") },
                    style = CalcButtonStyle.Function, modifier = Modifier.weight(1f), fontSize = 13.sp)
                CalcButton("³√x", onClick = { viewModel.onFunction("cbrt") },
                    style = CalcButtonStyle.Function, modifier = Modifier.weight(1f), fontSize = 12.sp)
                CalcButton("1/x", onClick = { viewModel.onReciprocal() },
                    style = CalcButtonStyle.Function, modifier = Modifier.weight(1f), fontSize = 13.sp)
            }

            // Row 4: Constants and special
            ButtonRow {
                CalcButton("π", onClick = { viewModel.onConstant("π") },
                    style = CalcButtonStyle.Function, modifier = Modifier.weight(1f), fontSize = 14.sp)
                CalcButton("e", onClick = { viewModel.onConstant("e") },
                    style = CalcButtonStyle.Function, modifier = Modifier.weight(1f), fontSize = 14.sp)
                CalcButton("n!", onClick = { viewModel.onFactorial() },
                    style = CalcButtonStyle.Function, modifier = Modifier.weight(1f), fontSize = 13.sp)
                CalcButton("|x|", onClick = { viewModel.onFunction("abs") },
                    style = CalcButtonStyle.Function, modifier = Modifier.weight(1f), fontSize = 13.sp)
                CalcButton("(", onClick = { viewModel.onParenOpen() },
                    style = CalcButtonStyle.Operator, modifier = Modifier.weight(1f))
                CalcButton(")", onClick = { viewModel.onParenClose() },
                    style = CalcButtonStyle.Operator, modifier = Modifier.weight(1f))
            }

            // Row 5: Clear, backspace, %, ÷
            ButtonRow {
                CalcButton("C", onClick = { viewModel.onClear() },
                    style = CalcButtonStyle.Clear, modifier = Modifier.weight(1f))
                CalcButton("⌫", onClick = { viewModel.onBackspace() },
                    style = CalcButtonStyle.Operator, modifier = Modifier.weight(1f))
                CalcButton("%", onClick = { viewModel.onPercent() },
                    style = CalcButtonStyle.Operator, modifier = Modifier.weight(1f))
                CalcButton("÷", onClick = { viewModel.onOperator("/") },
                    style = CalcButtonStyle.Operator, modifier = Modifier.weight(1f))
            }

            // Row 6: 7 8 9 ×
            ButtonRow {
                CalcButton("7", onClick = { viewModel.onDigit("7") }, modifier = Modifier.weight(1f))
                CalcButton("8", onClick = { viewModel.onDigit("8") }, modifier = Modifier.weight(1f))
                CalcButton("9", onClick = { viewModel.onDigit("9") }, modifier = Modifier.weight(1f))
                CalcButton("×", onClick = { viewModel.onOperator("*") },
                    style = CalcButtonStyle.Operator, modifier = Modifier.weight(1f))
            }

            // Row 7: 4 5 6 −
            ButtonRow {
                CalcButton("4", onClick = { viewModel.onDigit("4") }, modifier = Modifier.weight(1f))
                CalcButton("5", onClick = { viewModel.onDigit("5") }, modifier = Modifier.weight(1f))
                CalcButton("6", onClick = { viewModel.onDigit("6") }, modifier = Modifier.weight(1f))
                CalcButton("−", onClick = { viewModel.onOperator("-") },
                    style = CalcButtonStyle.Operator, modifier = Modifier.weight(1f))
            }

            // Row 8: 1 2 3 +
            ButtonRow {
                CalcButton("1", onClick = { viewModel.onDigit("1") }, modifier = Modifier.weight(1f))
                CalcButton("2", onClick = { viewModel.onDigit("2") }, modifier = Modifier.weight(1f))
                CalcButton("3", onClick = { viewModel.onDigit("3") }, modifier = Modifier.weight(1f))
                CalcButton("+", onClick = { viewModel.onOperator("+") },
                    style = CalcButtonStyle.Operator, modifier = Modifier.weight(1f))
            }

            // Row 9: +/- 0 . =
            ButtonRow {
                CalcButton("±", onClick = { viewModel.onNegate() },
                    style = CalcButtonStyle.Operator, modifier = Modifier.weight(1f))
                CalcButton("0", onClick = { viewModel.onDigit("0") }, modifier = Modifier.weight(1f))
                CalcButton(".", onClick = { viewModel.onDecimal() }, modifier = Modifier.weight(1f))
                CalcButton("=", onClick = { viewModel.onEquals() },
                    style = CalcButtonStyle.Accent, modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ButtonRow(content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        content = content
    )
}
