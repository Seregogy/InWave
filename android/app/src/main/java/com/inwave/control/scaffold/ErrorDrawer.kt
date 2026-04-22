package com.inwave.control.scaffold

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

@Composable
fun ErrorDrawer(
    modifier: Modifier,
    throwable: Throwable
) {
    Box(modifier) {
        Column {
            Text(
                text = throwable.message ?: "unknown",
                style = TextStyle.Default.copy(
                    fontSize = 25.sp,
                    fontWeight = FontWeight.W600
                ),
                textAlign = TextAlign.Center
            )

            Text(
                text = throwable.stackTrace.joinToString("\n"),
                style = TextStyle.Default.copy(
                    fontWeight = FontWeight.W600
                ),
                textAlign = TextAlign.Justify
            )
        }
    }
}