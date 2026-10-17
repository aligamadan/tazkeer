package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.MarigoldYellow
import com.example.ui.theme.OliveSageGreen
import com.example.ui.theme.TerracottaOrange

@Composable
fun FloralWreathLogo(
    modifier: Modifier = Modifier,
    size: Dp = 120.dp,
    showSubtitle: Boolean = true
) {
    val charcoalColor = MaterialTheme.colorScheme.onBackground

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Botanical arrangement above the text (matching channels4_profile.jpg exactly)
        Canvas(
            modifier = Modifier
                .width(size * 1.5f)
                .height(size * 0.45f)
        ) {
            val midX = this.size.width / 2f
            val midY = this.size.height * 0.65f

            // Central floral stem (Olive Green)
            drawLine(
                color = OliveSageGreen,
                start = Offset(midX, midY + 10.dp.toPx()),
                end = Offset(midX, midY - 6.dp.toPx()),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Side branching stems
            drawLine(
                color = OliveSageGreen,
                start = Offset(midX, midY + 8.dp.toPx()),
                end = Offset(midX - 34.dp.toPx(), midY - 2.dp.toPx()),
                strokeWidth = 1.8.dp.toPx(),
                cap = StrokeCap.Round
            )
            drawLine(
                color = OliveSageGreen,
                start = Offset(midX, midY + 8.dp.toPx()),
                end = Offset(midX + 34.dp.toPx(), midY - 2.dp.toPx()),
                strokeWidth = 1.8.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Outer delicate stems
            drawLine(
                color = OliveSageGreen.copy(alpha = 0.8f),
                start = Offset(midX - 20.dp.toPx(), midY + 4.dp.toPx()),
                end = Offset(midX - 52.dp.toPx(), midY + 8.dp.toPx()),
                strokeWidth = 1.5.dp.toPx(),
                cap = StrokeCap.Round
            )
            drawLine(
                color = OliveSageGreen.copy(alpha = 0.8f),
                start = Offset(midX + 20.dp.toPx(), midY + 4.dp.toPx()),
                end = Offset(midX + 52.dp.toPx(), midY + 8.dp.toPx()),
                strokeWidth = 1.5.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Olive Leaves along the stems
            val leafOffsets = listOf(
                Offset(midX - 14.dp.toPx(), midY - 4.dp.toPx()),
                Offset(midX + 14.dp.toPx(), midY - 4.dp.toPx()),
                Offset(midX - 26.dp.toPx(), midY + 2.dp.toPx()),
                Offset(midX + 26.dp.toPx(), midY + 2.dp.toPx()),
                Offset(midX - 44.dp.toPx(), midY + 8.dp.toPx()),
                Offset(midX + 44.dp.toPx(), midY + 8.dp.toPx())
            )
            leafOffsets.forEach { pos ->
                drawCircle(
                    color = OliveSageGreen,
                    radius = 2.5.dp.toPx(),
                    center = pos
                )
            }

            // Center Orange Blossom (Terracotta Orange)
            drawCircle(
                color = TerracottaOrange,
                radius = 5.dp.toPx(),
                center = Offset(midX, midY - 10.dp.toPx())
            )
            // Petals around center orange blossom
            for (angle in 0 until 6) {
                val rad = Math.toRadians(angle * 60.0)
                val px = midX + (6.dp.toPx() * Math.cos(rad)).toFloat()
                val py = (midY - 10.dp.toPx()) + (6.dp.toPx() * Math.sin(rad)).toFloat()
                drawCircle(
                    color = TerracottaOrange.copy(alpha = 0.85f),
                    radius = 3.dp.toPx(),
                    center = Offset(px, py)
                )
            }

            // Left & Right Yellow Marigold Blossoms
            val leftYellow = Offset(midX - 36.dp.toPx(), midY - 4.dp.toPx())
            val rightYellow = Offset(midX + 36.dp.toPx(), midY - 4.dp.toPx())
            listOf(leftYellow, rightYellow).forEach { centerPoint ->
                drawCircle(
                    color = MarigoldYellow,
                    radius = 4.dp.toPx(),
                    center = centerPoint
                )
                for (angle in 0 until 5) {
                    val rad = Math.toRadians(angle * 72.0)
                    val px = centerPoint.x + (4.5.dp.toPx() * Math.cos(rad)).toFloat()
                    val py = centerPoint.y + (4.5.dp.toPx() * Math.sin(rad)).toFloat()
                    drawCircle(
                        color = MarigoldYellow.copy(alpha = 0.8f),
                        radius = 2.2.dp.toPx(),
                        center = Offset(px, py)
                    )
                }
            }

            // Little side terracotta berries
            drawCircle(color = TerracottaOrange, radius = 2.dp.toPx(), center = Offset(midX - 56.dp.toPx(), midY + 8.dp.toPx()))
            drawCircle(color = TerracottaOrange, radius = 2.dp.toPx(), center = Offset(midX + 56.dp.toPx(), midY + 8.dp.toPx()))
        }

        Spacer(modifier = Modifier.height(2.dp))

        // Exact Calligraphy Text "تَذْكِير" with tashkeel
        Text(
            text = "تَذْكِير",
            fontFamily = AmiriFontFamily,
            fontSize = (size.value * 0.38f).sp,
            fontWeight = FontWeight.Bold,
            color = charcoalColor,
            textAlign = TextAlign.Center
        )

        // Delicate decorative underline flourish
        Canvas(modifier = Modifier.width(size * 0.9f).height(6.dp)) {
            val w = this.size.width
            val h = this.size.height / 2f
            drawLine(
                color = OliveSageGreen.copy(alpha = 0.5f),
                start = Offset(10.dp.toPx(), h),
                end = Offset(w - 10.dp.toPx(), h),
                strokeWidth = 1.2.dp.toPx(),
                cap = StrokeCap.Round
            )
            drawCircle(
                color = TerracottaOrange,
                radius = 2.dp.toPx(),
                center = Offset(w / 2f, h)
            )
        }

        if (showSubtitle) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "رفيقك اليومي للصلوات والذكر والقرآن",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
