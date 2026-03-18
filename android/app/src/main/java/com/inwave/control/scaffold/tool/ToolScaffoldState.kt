package com.inwave.control.scaffold.tool

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color

class ToolScaffoldState(
    var onBackRequest: () -> Unit,
    var onSearchRequest: () -> Unit,
    var foregroundColor: State<Color> = mutableStateOf(Color.White),
    var toolBarTitle: MutableState<String?> = mutableStateOf(null),
    var onLaunchContextAction: (content: @Composable (padding: PaddingValues) -> Unit) -> Unit = { }
) {
    fun launchContextAction(
        content: @Composable (padding: PaddingValues) -> Unit = { }
    ) {
        onLaunchContextAction(content)
    }
}

@Composable
fun rememberToolScaffoldState(
    onBackRequest: () -> Unit = { },
    onSearchRequest: () -> Unit = { },
    foregroundColor: State<Color> = mutableStateOf(Color.White),
    toolBarTitle: String? = null
): ToolScaffoldState {
    val toolBarTitleState = rememberSaveable {
        mutableStateOf(toolBarTitle)
    }

    return remember {
        ToolScaffoldState(onBackRequest, onSearchRequest, foregroundColor, toolBarTitleState)
    }
}

@Composable
fun rememberToolScaffoldState(): ToolScaffoldState {
    return remember {
        ToolScaffoldState({ }, { }, mutableStateOf(Color.White))
    }
}