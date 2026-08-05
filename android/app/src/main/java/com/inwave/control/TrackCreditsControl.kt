package com.inwave.control

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridItemSpanScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

fun LazyGridScope.trackCreditsControlLazyListScope(
    credits: Map<String, List<String>>
) {
    items(credits.entries.toList()) { (role, names) ->
        Column(Modifier.padding(5.dp)) {
            Text(
                text = role,
                style = TextStyle.Default.copy(
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.W600
                )
            )

            FlowRow(
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                names.forEach { name ->
                    Text(
                        text = name,
                        style = TextStyle.Default.copy(
                            color = Color.White.copy(.6f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.W500
                        )
                    )
                }
            }
        }
    }
}