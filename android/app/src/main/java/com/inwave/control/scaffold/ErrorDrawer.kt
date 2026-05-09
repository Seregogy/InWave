package com.inwave.control.scaffold

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ErrorDrawer(
    modifier: Modifier,
    throwable: Throwable,
    additionalContent: @Composable () -> Unit = { }
) {
    Box(modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Rounded.Warning,
                modifier = Modifier
                    .size(60.dp),
                contentDescription = ""
            )

            Text(
                text = throwable.message ?: "unknown",
                style = TextStyle.Default.copy(
                    fontSize = 27.sp,
                    fontWeight = FontWeight.W600
                ),
                maxLines = 2,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 50.dp)
            )

            Text(
                text = throwable.stackTrace.joinToString("\n"),
                style = TextStyle.Default.copy(
                    fontWeight = FontWeight.W600
                ),
                textAlign = TextAlign.Justify,
                modifier = Modifier.padding(horizontal = 50.dp),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(20.dp))

            additionalContent()
        }
    }
}