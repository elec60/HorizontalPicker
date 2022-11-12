package com.example.horizontalpicker

import android.graphics.Paint
import android.text.TextPaint
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs

@Composable
fun HorizontalPicker(
    modifier: Modifier = Modifier,
    items: List<String>,
    visibleItemsCount: Int,
    onValueSelectedListener: (String) -> Unit
) {
    val isEven = remember {
        items.size % 2 == 0
    }

    var currentDragX by remember {
        mutableStateOf(0f)
    }

    var totalWidth by remember(items.size) {
        mutableStateOf(0f)
    }

    var space by remember {
        mutableStateOf(0f)
    }

    val path = remember {
        Path()
    }

    val textSize = with(LocalDensity.current) { 16.sp.toPx() }

    val nativePaint = remember {
        TextPaint().apply {
            textAlign = Paint.Align.CENTER
            color = android.graphics.Color.BLACK
            this.textSize = textSize
            isFakeBoldText = true
        }
    }

    val nativeRect = remember {
        android.graphics.Rect()
    }

    LaunchedEffect(key1 = true) {
        calculateSelectedItem(
            currentDragX,
            space,
            isEven,
            items,
            onValueSelectedListener
        )
    }

    Canvas(
        modifier = modifier
            .pointerInput(true) {
                detectDragGestures(
                    onDrag = { change, dragAmount ->
                        change.consume()
                        currentDragX = (currentDragX - dragAmount.x).coerceIn(
                            minimumValue = -totalWidth / 2f - (if (isEven) -space / 2f else 0f),
                            maximumValue = totalWidth / 2f - (if (isEven) -space / 2f else 0f)
                        )
                    },
                    onDragEnd = { // snapping
                        val multiplesCount = abs(currentDragX / space).toInt()
                        val diff = abs(abs(currentDragX) - multiplesCount * space)

                        if (currentDragX >= 0) {
                            if (diff <= space / 2) {
                                currentDragX -= diff
                            } else {
                                currentDragX += (space - diff)
                            }
                        } else {
                            if (diff <= space / 2) {
                                currentDragX += diff
                            } else {
                                currentDragX -= (space - diff)
                            }
                        }

                        calculateSelectedItem(
                            currentDragX,
                            space,
                            isEven,
                            items,
                            onValueSelectedListener
                        )
                    }
                )
            }
    ) {
        val width = this.size.width
        val height = this.size.height
        space = width / visibleItemsCount
        val middleY = height / 2f
        totalWidth = (items.size - 1) * space

        items.forEachIndexed { index, item ->
            nativePaint.getTextBounds(item, 0, item.length, nativeRect)
            val currentX =
                index * space - totalWidth / 2f + width / 2f - currentDragX + (if (isEven) space / 2f else 0f)

            //draw texts
            //no stable version of compose drawText yet
            drawContext.canvas.nativeCanvas.apply {
                nativePaint.getTextBounds(item, 0, item.length, nativeRect)
                drawText(
                    item,
                    currentX,
                    middleY + nativeRect.height() / 2,
                    nativePaint
                )
            }

            //draw lines
            val start = Offset(x = currentX, y = 0f)
            val end = Offset(x = currentX, y = height * 0.2f)
            drawLine(
                color = Color.Magenta,
                start = start,
                end = end,
                strokeWidth = 1.5.dp.toPx()
            )
        }

        translate(top = -10.dp.toPx()) {
            val indicatorSize = 16.dp.toPx()
            path.reset()
            path.apply {
                moveTo(x = (width - indicatorSize) / 2, y = -indicatorSize)
                relativeLineTo(dx = indicatorSize, dy = 0f)
                relativeLineTo(dx = -indicatorSize / 2, dy = indicatorSize)
                close()
            }
            drawPath(
                path = path,
                color = Color.Magenta
            )
        }
    }

}

private fun calculateSelectedItem(
    currentDragX: Float,
    space: Float,
    isEven: Boolean,
    items: List<String>,
    onValueSelectedListener: (String) -> Unit
) {
    val relativeIndex = (currentDragX / space).toInt()
    val middleItemIndex = if (isEven) {
        items.size / 2 - 1
    } else {
        (items.size - 1) / 2
    }

    val index = middleItemIndex + relativeIndex
    onValueSelectedListener(items[index])
}

@Preview(widthDp = 200)
@Composable
fun Preview() {
    HorizontalPicker(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(Color.White),
        items = (1..10).map { it.toString() },
        visibleItemsCount = 6,
        onValueSelectedListener = {

        }
    )
}