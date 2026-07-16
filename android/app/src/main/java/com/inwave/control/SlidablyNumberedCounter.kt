package com.inwave.control

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp

data class DigitItem(val char: Char, val index: Int)

@Composable
fun SlidablyNumberedCounter(
    value: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = LocalTextStyle.current.copy(
        fontSize = 50.sp,
        color = Color.White
    )
) {
    Row(modifier) {
        value.forEachIndexed { index, char ->
            AnimatedContent(
                targetState = DigitItem(char, index),
                transitionSpec = {
                    slideInVertically { height -> -height } + fadeIn() togetherWith
                            slideOutVertically { height -> height } + fadeOut()
                },
                label = "digit_counter"
            ) { digitItem ->
                Text(
                    text = digitItem.char.toString(),
                    style = textStyle,
                    color = if (digitItem.char == '0')
                            textStyle.color.copy(.5f)
                        else
                            textStyle.color,
                    softWrap = true
                )
            }
        }
    }
}