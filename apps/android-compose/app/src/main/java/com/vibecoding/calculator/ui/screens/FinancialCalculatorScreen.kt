package com.vibecoding.calculator.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vibecoding.calculator.ui.components.CalcButton
import com.vibecoding.calculator.ui.components.CalcButtonStyle
import com.vibecoding.calculator.ui.theme.*
import com.vibecoding.calculator.viewmodel.FinancialTab
import com.vibecoding.calculator.viewmodel.FinancialViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinancialCalculatorScreen(
    onBack: () -> Unit,
    viewModel: FinancialViewModel = viewModel()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
    ) {
        TopAppBar(
            title = { Text("Financeira", color = TextPrimary) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, "Voltar", tint = AccentBlue)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = BgDark)
        )

        // Result display
        if (viewModel.result.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(BgSurface)
                    .padding(16.dp),
                horizontalAlignment = Alignment.End
            ) {
                Text(viewModel.resultLabel, color = AccentBlue, fontSize = 14.sp)
                Text(
                    viewModel.result,
                    color = TextPrimary,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Tab bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            FinancialTab.values().forEach { tab ->
                val label = when (tab) {
                    FinancialTab.TVM -> "TVM"
                    FinancialTab.Amortization -> "Amortização"
                    FinancialTab.CashFlow -> "Fluxo Caixa"
                    FinancialTab.Depreciation -> "Depreciação"
                }
                FilterChip(
                    selected = viewModel.activeTab == tab,
                    onClick = { viewModel.setActiveTab(tab) },
                    label = { Text(label, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AccentBlue,
                        selectedLabelColor = BgDark,
                        containerColor = BgSurface,
                        labelColor = TextDim
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            when (viewModel.activeTab) {
                FinancialTab.TVM -> TVMContent(viewModel)
                FinancialTab.Amortization -> AmortizationContent(viewModel)
                FinancialTab.CashFlow -> CashFlowContent(viewModel)
                FinancialTab.Depreciation -> DepreciationContent(viewModel)
            }

            // Calculation log
            if (viewModel.log.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Histórico", color = TextDim, fontSize = 14.sp)
                    TextButton(onClick = { viewModel.clearLog() }) {
                        Text("Limpar", color = AccentRed, fontSize = 12.sp)
                    }
                }
                viewModel.log.take(10).forEach { entry ->
                    Text(
                        entry,
                        color = TextSubtle,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun TVMContent(viewModel: FinancialViewModel) {
    // Begin/End mode
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CalcButton(
            text = if (viewModel.beginMode) "BEGIN" else "END",
            onClick = { viewModel.toggleBeginMode() },
            style = CalcButtonStyle.Function,
            modifier = Modifier.weight(1f),
            fontSize = 13.sp
        )
    }

    // Input fields
    FinancialField("N (Períodos)", viewModel.n, viewModel::updateN)
    FinancialField("i% (Taxa)", viewModel.rate, viewModel::updateRate)
    FinancialField("PV (Valor Presente)", viewModel.pv, viewModel::updatePV)
    FinancialField("PMT (Pagamento)", viewModel.pmt, viewModel::updatePMT)
    FinancialField("FV (Valor Futuro)", viewModel.fv, viewModel::updateFV)

    // Compute buttons
    Spacer(modifier = Modifier.height(4.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        CalcButton("N", onClick = { viewModel.calcN() },
            style = CalcButtonStyle.Accent, modifier = Modifier.weight(1f), fontSize = 13.sp)
        CalcButton("i%", onClick = { viewModel.calcRate() },
            style = CalcButtonStyle.Accent, modifier = Modifier.weight(1f), fontSize = 13.sp)
        CalcButton("PV", onClick = { viewModel.calcPV() },
            style = CalcButtonStyle.Accent, modifier = Modifier.weight(1f), fontSize = 13.sp)
        CalcButton("PMT", onClick = { viewModel.calcPMT() },
            style = CalcButtonStyle.Accent, modifier = Modifier.weight(1f), fontSize = 12.sp)
        CalcButton("FV", onClick = { viewModel.calcFV() },
            style = CalcButtonStyle.Accent, modifier = Modifier.weight(1f), fontSize = 13.sp)
    }
}

@Composable
private fun AmortizationContent(viewModel: FinancialViewModel) {
    FinancialField("Principal", viewModel.amortPrincipal, viewModel::updateAmortPrincipal)
    FinancialField("Taxa (%)", viewModel.amortRate, viewModel::updateAmortRate)
    FinancialField("Períodos", viewModel.amortPeriods, viewModel::updateAmortPeriods)

    CalcButton("Calcular Amortização", onClick = { viewModel.calcAmortization() },
        style = CalcButtonStyle.Accent, modifier = Modifier.fillMaxWidth())

    // Amortization table
    if (viewModel.amortSchedule.isNotEmpty()) {
        Spacer(modifier = Modifier.height(8.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(BgSurface)
                .padding(8.dp)
        ) {
            // Header
            Row(modifier = Modifier.fillMaxWidth()) {
                listOf("#", "Parcela", "Juros", "Amort.", "Saldo").forEach { h ->
                    Text(
                        h, color = AccentBlue, fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.End
                    )
                }
            }
            HorizontalDivider(color = BgOverlay, modifier = Modifier.padding(vertical = 4.dp))

            viewModel.amortSchedule.take(24).forEach { row ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text("${row.period}", color = TextDim, fontSize = 10.sp,
                        modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                    Text(String.format("%.2f", row.payment), color = TextPrimary, fontSize = 10.sp,
                        modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                    Text(String.format("%.2f", row.interest), color = AccentPeach, fontSize = 10.sp,
                        modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                    Text(String.format("%.2f", row.principal), color = AccentGreen, fontSize = 10.sp,
                        modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                    Text(String.format("%.2f", row.balance), color = TextDim, fontSize = 10.sp,
                        modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                }
            }
            if (viewModel.amortSchedule.size > 24) {
                Text(
                    "... mais ${viewModel.amortSchedule.size - 24} períodos",
                    color = TextSubtle,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun CashFlowContent(viewModel: FinancialViewModel) {
    FinancialField("Taxa de desconto (%)", viewModel.cashFlowRate, viewModel::updateCashFlowRate)

    Text("Fluxos de Caixa:", color = TextDim, fontSize = 14.sp)

    viewModel.cashFlows.forEachIndexed { index, value ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("CF$index", color = TextSubtle, fontSize = 12.sp,
                modifier = Modifier.width(36.dp))
            OutlinedTextField(
                value = value,
                onValueChange = { viewModel.updateCashFlow(index, it) },
                modifier = Modifier.weight(1f).height(48.dp),
                textStyle = LocalTextStyle.current.copy(
                    color = TextPrimary, fontSize = 14.sp, fontFamily = FontFamily.Monospace
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentBlue,
                    unfocusedBorderColor = BgOverlay,
                    cursorColor = AccentBlue
                )
            )
            if (viewModel.cashFlows.size > 1) {
                IconButton(onClick = { viewModel.removeCashFlow(index) }, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.Delete, "Remover", tint = AccentRed, modifier = Modifier.size(18.dp))
                }
            }
        }
    }

    TextButton(onClick = { viewModel.addCashFlow() }) {
        Icon(Icons.Default.Add, null, tint = AccentGreen, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("Adicionar Fluxo", color = AccentGreen, fontSize = 13.sp)
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CalcButton("NPV", onClick = { viewModel.calcNPV() },
            style = CalcButtonStyle.Accent, modifier = Modifier.weight(1f))
        CalcButton("IRR", onClick = { viewModel.calcIRR() },
            style = CalcButtonStyle.Accent, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun DepreciationContent(viewModel: FinancialViewModel) {
    FinancialField("Custo", viewModel.deprCost, viewModel::updateDeprCost)
    FinancialField("Valor Residual", viewModel.deprSalvage, viewModel::updateDeprSalvage)
    FinancialField("Vida Útil (anos)", viewModel.deprLife, viewModel::updateDeprLife)
    FinancialField("Ano", viewModel.deprYear, viewModel::updateDeprYear)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        CalcButton("Linear", onClick = { viewModel.calcDepreciationSL() },
            style = CalcButtonStyle.Accent, modifier = Modifier.weight(1f), fontSize = 12.sp)
        CalcButton("DB", onClick = { viewModel.calcDepreciationDB() },
            style = CalcButtonStyle.Accent, modifier = Modifier.weight(1f), fontSize = 13.sp)
        CalcButton("SYD", onClick = { viewModel.calcDepreciationSYD() },
            style = CalcButtonStyle.Accent, modifier = Modifier.weight(1f), fontSize = 13.sp)
    }
}

@Composable
private fun FinancialField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 12.sp) },
        modifier = Modifier.fillMaxWidth().height(56.dp),
        textStyle = LocalTextStyle.current.copy(
            color = TextPrimary,
            fontSize = 16.sp,
            fontFamily = FontFamily.Monospace
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AccentBlue,
            unfocusedBorderColor = BgOverlay,
            focusedLabelColor = AccentBlue,
            unfocusedLabelColor = TextSubtle,
            cursorColor = AccentBlue
        )
    )
}
