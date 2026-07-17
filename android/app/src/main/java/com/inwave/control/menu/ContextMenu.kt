package com.inwave.control.menu

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.holix.android.bottomsheetdialog.compose.BottomSheetBehaviorProperties
import com.holix.android.bottomsheetdialog.compose.BottomSheetDialog
import com.holix.android.bottomsheetdialog.compose.BottomSheetDialogProperties
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContextMenu(
    expanded: MutableState<Boolean>,
    containerColor: Color = Color.Black,
    isDraggable: Boolean = true,
    label: String? = null,
    description: String? = null,
    content: @Composable (padding: PaddingValues) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()

    if (expanded.value) {
        BottomSheetDialog(
            onDismissRequest = {
                coroutineScope.launch {
                    sheetState.hide()
                }.invokeOnCompletion {
                    expanded.value = false
                }
            },
            properties = BottomSheetDialogProperties(
                dismissWithAnimation = true,
                enableEdgeToEdge = true,
                behaviorProperties = BottomSheetBehaviorProperties(
                    isDraggable = isDraggable
                )
            )
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .background(containerColor)
                    .animateContentSize()
            ) {
                Box(Modifier.fillMaxWidth().height(40.dp)) {
                    Box(
                        modifier = Modifier
                            .width(45.dp)
                            .height(4.dp)
                            .clip(MaterialTheme.shapes.small)
                            .background(Color.White.copy(.7f))
                            .align(Alignment.Center)
                    )
                }

                Column(
                    modifier = Modifier
                        .then(
                            if (label != null) Modifier.padding(top = 40.dp) else Modifier
                        )
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    label?.let {
                        Text(
                            text = label,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.W700,
                            color = Color.White,
                            maxLines = 1
                        )
                    }

                    description?.let {
                        Text(
                            text = description,
                            color = Color.White.copy(.7f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.W600,
                            maxLines = 2
                        )
                    }

                    Box {
                        content(PaddingValues(top = 40.dp))
                    }
                }
            }
        }
    }
}