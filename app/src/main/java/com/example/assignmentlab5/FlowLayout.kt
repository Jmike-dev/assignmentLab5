package com.example.assignmentlab5.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.max

@Composable
fun FlowLayout(
    modifier: Modifier = Modifier,
    horizontalSpacing: Dp = 8.dp,
    verticalSpacing: Dp = 8.dp,
    content: @Composable () -> Unit
) {
    Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->
        val hSpacingPx = horizontalSpacing.roundToPx()
        val vSpacingPx = verticalSpacing.roundToPx()

        val rows = mutableListOf<List<Placeable>>()
        val rowHeights = mutableListOf<Int>()

        var currentLineWidth = 0
        var currentLineHeight = 0
        var currentLine = mutableListOf<Placeable>()

        for (measurable in measurables) {
            val placeable = measurable.measure(constraints)

            if (currentLineWidth + placeable.width > constraints.maxWidth && currentLine.isNotEmpty()) {
                rows.add(currentLine)
                rowHeights.add(currentLineHeight)

                currentLine = mutableListOf()
                currentLineWidth = 0
                currentLineHeight = 0
            }

            if (currentLine.isNotEmpty()) {
                currentLineWidth += hSpacingPx
            }
            currentLine.add(placeable)
            currentLineWidth += placeable.width
            currentLineHeight = max(currentLineHeight, placeable.height)
        }

        if (currentLine.isNotEmpty()) {
            rows.add(currentLine)
            rowHeights.add(currentLineHeight)
        }

        val totalHeight = rowHeights.sum() + (max(0, rows.size - 1) * vSpacingPx)

        layout(width = constraints.maxWidth, height = totalHeight) {
            var yOffset = 0
            for (i in rows.indices) {
                val row = rows[i]
                var xOffset = 0
                for (placeable in row) {
                    placeable.placeRelative(x = xOffset, y = yOffset)
                    xOffset += placeable.width + hSpacingPx
                }
                yOffset += rowHeights[i] + vSpacingPx
            }
        }
    }
}