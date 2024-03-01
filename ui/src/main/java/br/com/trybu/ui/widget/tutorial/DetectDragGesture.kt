package br.com.trybu.ui.widget.tutorial

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.math.abs

enum class Direction {
    LEFT, UP, RIGHT, DOWN;
}

fun Modifier.detectDragDirection(onDrag: (Direction) -> Unit): Modifier = composed {
    var movementOffset by remember { mutableStateOf(Offset(.0f, .0f)) }
    pointerInput(Unit) {
        detectDragGestures(
            onDragStart = { movementOffset = Offset(.0f, .0f) },
            onDragCancel = { movementOffset = Offset(.0f, .0f) },
            onDrag = { input, offset ->
                input.consume()
                movementOffset = Offset(
                    movementOffset.x + offset.x,
                    movementOffset.y + offset.y
                )
            },
            onDragEnd = {
                val direction =
                    if (abs(movementOffset.x) > abs(movementOffset.y)) {
                        if (movementOffset.x > 0) Direction.RIGHT else Direction.LEFT
                    } else {
                        if (movementOffset.y > 0) Direction.DOWN else Direction.UP
                    }
                onDrag(direction)
            }
        )
    }
}