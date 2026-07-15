package com.inwave.control

import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import com.inwave.control.scaffold.color.ColoredScaffoldState

@Composable
fun ColoredScaffoldState.BorderedIconToggleButton(
    modifier: Modifier = Modifier,
    checked: State<Boolean>,
    onCheckedChange: (currentState: Boolean) -> Unit,
    icon: Painter,
    enabled: Boolean = true
) {
    BorderedIconToggleButton(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        icon = icon,
        borderColor = onBackgroundColorAnimated.value.copy(.15f),
        iconColor = onBackgroundColorAnimated.value,
        enabled = enabled
    )
}

@Composable
fun BorderedIconToggleButton(
    modifier: Modifier = Modifier,
    checked: State<Boolean>,
    onCheckedChange: (currentState: Boolean) -> Unit,
    icon: Painter,
    borderColor: Color,
    iconColor: Color,
    enabled: Boolean
) {
    IconToggleButton(
        checked = checked.value,
        onCheckedChange = onCheckedChange,
        modifier = modifier
            .then(
                if (checked.value) {
                    Modifier.border(
                        width = 3.dp,
                        color = borderColor,
                        shape = CircleShape
                    )
                } else { Modifier }
            ),
        enabled = enabled
    ) {
        Icon(
            painter = icon,
            contentDescription = "icon",
            tint = if (enabled) iconColor else iconColor.copy(.15f)
        )
    }
}

