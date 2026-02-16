package com.vibecoding.calculator.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vibecoding.calculator.ui.theme.*

@Composable
fun MainMenuScreen(
    onNavigateToScientific: () -> Unit,
    onNavigateToFinancial: () -> Unit,
    onNavigateToGraphing: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Title
        Text(
            text = "Multi Calculator",
            color = TextPrimary,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Pro",
            color = AccentBlue,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Escolha a calculadora",
            color = TextDim,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Calculator cards
        MenuCard(
            icon = Icons.Default.Calculate,
            title = "Científica",
            subtitle = "Funções trigonométricas, logaritmos,\npotências, fatorial e mais",
            accentColor = AccentBlue,
            onClick = onNavigateToScientific
        )

        Spacer(modifier = Modifier.height(16.dp))

        MenuCard(
            icon = Icons.Default.AccountBalance,
            title = "Financeira",
            subtitle = "TVM, amortização, NPV/IRR,\ndepreciação e fluxo de caixa",
            accentColor = AccentGreen,
            onClick = onNavigateToFinancial
        )

        Spacer(modifier = Modifier.height(16.dp))

        MenuCard(
            icon = Icons.Default.ShowChart,
            title = "Gráficos",
            subtitle = "Plotagem de funções, zoom,\navaliação de expressões",
            accentColor = AccentMauve,
            onClick = onNavigateToGraphing
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Tema Catppuccin Mocha",
            color = TextSubtle,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun MenuCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    accentColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(BgSurface)
            .clickable(onClick = onClick)
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(accentColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = title,
                tint = accentColor,
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                color = TextDim,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }
    }
}
