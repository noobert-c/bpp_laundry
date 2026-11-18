package com.example.laundryflow.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlin.math.abs

@Composable
fun <T> ReorderableFormCard(
    items: List<T>,
    itemKey: (T) -> Any,
    onMove: (from: Int, to: Int) -> Unit,
    modifier: Modifier = Modifier,
    contentSpacing: Dp = 14.dp,
    itemSpacing: Dp = 10.dp,
    longPressToDrag: Boolean = false,
    containerColor: Color = Color.White,
    borderColor: Color = Color(0xFFE2E8F0),
    header: @Composable ColumnScope.() -> Unit = {},
    emptyContent: @Composable ColumnScope.() -> Unit = {},
    itemContent: @Composable (
        item: T,
        index: Int,
        isDragging: Boolean,
        dragHandle: Modifier
    ) -> Unit
) {
    val latestItems by rememberUpdatedState(items)
    val latestOnMove by rememberUpdatedState(onMove)
    val latestItemKey by rememberUpdatedState(itemKey)

    var draggingKey by remember { mutableStateOf<Any?>(null) }
    var dragOffset by remember { mutableFloatStateOf(0f) }

    // Tinggi setiap item dalam pixel.
    val heights = remember { mutableStateMapOf<Any, Int>() }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(contentSpacing)
        ) {
            header()

            if (items.isEmpty()) {
                emptyContent()
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(itemSpacing)
                ) {
                    items.forEachIndexed { index, item ->
                        val keyValue = itemKey(item)

                        key(keyValue) {
                            val isDragging = draggingKey == keyValue

                            val dragHandle = Modifier.pointerInput(
                                keyValue,
                                longPressToDrag
                            ) {
                                fun startDrag() {
                                    draggingKey = keyValue
                                    dragOffset = 0f
                                }

                                fun endDrag() {
                                    draggingKey = null
                                    dragOffset = 0f
                                }

                                fun moveDraggedItem(
                                    change: PointerInputChange,
                                    amount: Offset
                                ) {
                                    change.consume()
                                    dragOffset += amount.y

                                    val currentItems = latestItems
                                    val currentIndex =
                                        currentItems.indexOfFirst {
                                            latestItemKey(it) == keyValue
                                        }

                                    if (currentIndex < 0) return

                                    val currentHeight =
                                        heights[keyValue] ?: return

                                    val density = this

                                    val spacingPx =
                                        with(density) {
                                            itemSpacing.toPx()
                                        }

                                    // Geser ke bawah melewati item berikutnya.
                                    if (
                                        dragOffset > 0f &&
                                        currentIndex < currentItems.lastIndex
                                    ) {
                                        val nextItem =
                                            currentItems[currentIndex + 1]

                                        val nextKey =
                                            latestItemKey(nextItem)

                                        val nextHeight =
                                            heights[nextKey] ?: currentHeight

                                        val threshold =
                                            (currentHeight + nextHeight) / 2f +
                                                    spacingPx

                                        if (dragOffset >= threshold) {
                                            latestOnMove(
                                                currentIndex,
                                                currentIndex + 1
                                            )

                                            dragOffset -=
                                                nextHeight + spacingPx
                                        }
                                    }

                                    // Geser ke atas melewati item sebelumnya.
                                    else if (
                                        dragOffset < 0f &&
                                        currentIndex > 0
                                    ) {
                                        val previousItem =
                                            currentItems[currentIndex - 1]

                                        val previousKey =
                                            latestItemKey(previousItem)

                                        val previousHeight =
                                            heights[previousKey] ?: currentHeight

                                        val threshold =
                                            (currentHeight + previousHeight) / 2f +
                                                    spacingPx

                                        if (abs(dragOffset) >= threshold) {
                                            latestOnMove(
                                                currentIndex,
                                                currentIndex - 1
                                            )

                                            dragOffset +=
                                                previousHeight + spacingPx
                                        }
                                    }
                                }

                                if (longPressToDrag) {
                                    detectDragGesturesAfterLongPress(
                                        onDragStart = { startDrag() },
                                        onDragEnd = { endDrag() },
                                        onDragCancel = { endDrag() },
                                        onDrag = { change, amount ->
                                            moveDraggedItem(change, amount)
                                        }
                                    )
                                } else {
                                    detectDragGestures(
                                        onDragStart = { startDrag() },
                                        onDragEnd = { endDrag() },
                                        onDragCancel = { endDrag() },
                                        onDrag = { change, amount ->
                                            moveDraggedItem(change, amount)
                                        }
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .onSizeChanged { size ->
                                        heights[keyValue] = size.height
                                    }
                                    .zIndex(
                                        if (isDragging) 1f else 0f
                                    )
                                    .graphicsLayer {
                                        translationY =
                                            if (isDragging) {
                                                dragOffset
                                            } else {
                                                0f
                                            }
                                    }
                                    .then(
                                        if (isDragging) {
                                            Modifier.shadow(
                                                elevation = 8.dp,
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                        } else {
                                            Modifier
                                        }
                                    )
                            ) {
                                itemContent(
                                    item,
                                    index,
                                    isDragging,
                                    dragHandle
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
