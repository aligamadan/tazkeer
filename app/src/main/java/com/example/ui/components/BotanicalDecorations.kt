package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp

@Composable
fun BotanicalDivider(
    modifier: Modifier = Modifier
) {
    val oliveColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
    val terracottaColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Canvas(modifier = Modifier.width(160.dp).height(20.dp)) {
            val midY = size.height / 2f
            val midX = size.width / 2f

            // Left soft stem
            drawLine(
                color = oliveColor,
                start = Offset(20f, midY),
                end = Offset(midX - 16f, midY),
                strokeWidth = 1.5.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Right soft stem
            drawLine(
                color = oliveColor,
                start = Offset(midX + 16f, midY),
                end = Offset(size.width - 20f, midY),
                strokeWidth = 1.5.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Center blossom
            drawCircle(
                color = terracottaColor,
                radius = 3.5.dp.toPx(),
                center = Offset(midX, midY)
            )

            // Subtle side leaves
            drawCircle(
                color = oliveColor,
                radius = 2.dp.toPx(),
                center = Offset(midX - 8f, midY - 3f)
            )
            drawCircle(
                color = oliveColor,
                radius = 2.dp.toPx(),
                center = Offset(midX + 8f, midY + 3f)
            )
        }
    }
}
