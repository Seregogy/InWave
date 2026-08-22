package com.inwave.control

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
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

fun LazyGridScope.badgesControlLazyListScope(
    credits: List<Pair<String, String>>
) {
    items(credits) { (first, second) ->
        Column(Modifier.padding(5.dp)) {
            Text(
                text = first,
                style = TextStyle.Default.copy(
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.W600
                )
            )

            Text(
                text = second,
                style = TextStyle.Default.copy(
                    color = Color.White.copy(.6f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.W500
                )
            )
        }
    }
}