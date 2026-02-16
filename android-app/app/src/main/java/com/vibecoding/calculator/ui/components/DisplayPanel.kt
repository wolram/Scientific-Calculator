package com.vibecoding.calculator.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vibecoding.calculator.ui.theme.*

@Composable
fun DisplayPanel(
    expression: String,
    result: String,
    modifier: Modifier = Modifier,
    statusLine: String? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(BgSurface)
            .padding(16.dp),
        horizontalAlignment = Alignment.End
    ) {
        if (statusLine != null) {
            Text(
                text = statusLine,
                color = AccentBlue,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        Text(
            text = expression,
            color = TextDim,
            fontSize = 16.sp,
            fontFamily = FontFamily.Monospace,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.End
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = result,
            color = TextPrimary,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.End
        )
    }
}
