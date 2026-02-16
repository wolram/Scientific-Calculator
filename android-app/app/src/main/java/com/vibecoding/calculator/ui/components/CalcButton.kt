package com.vibecoding.calculator.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vibecoding.calculator.ui.theme.*

enum class CalcButtonStyle {
    Number, Operator, Function, Accent, Clear
}

@Composable
fun CalcButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: CalcButtonStyle = CalcButtonStyle.Number,
    fontSize: TextUnit = 15.sp,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.92f else 1f, label = "btnScale")

    val (bgColor, textColor) = when (style) {
        CalcButtonStyle.Number -> BtnNumber to TextPrimary
        CalcButtonStyle.Operator -> BtnOperator to AccentPeach
        CalcButtonStyle.Function -> BtnFunction to AccentBlue
        CalcButtonStyle.Accent -> BtnAccent to BgDark
        CalcButtonStyle.Clear -> BtnClear to BgDark
    }

    Button(
        onClick = onClick,
        modifier = modifier
            .defaultMinSize(minWidth = 56.dp, minHeight = 44.dp)
            .scale(scale),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = bgColor,
            contentColor = textColor,
            disabledContainerColor = bgColor.copy(alpha = 0.5f),
            disabledContentColor = textColor.copy(alpha = 0.5f)
        ),
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp),
        interactionSource = interactionSource,
        enabled = enabled
    ) {
        Text(
            text = text,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}
