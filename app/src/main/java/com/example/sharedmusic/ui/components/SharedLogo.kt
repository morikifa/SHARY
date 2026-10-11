package com.example.sharedmusic.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Логотип приложения в шапке (стилизованная виниловая пластинка с розовым центром и текст)
@Composable
fun SharedLogo(
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Векторная мини-пластинка
        Canvas(modifier = Modifier.size(32.dp)) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = size.minDimension / 2f

            // Внешний чёрный обод
            drawCircle(color = Color(0xFF151515), radius = radius, center = center)
            // Тонкая дорожка
            drawCircle(
                color = Color(0xFF383838),
                radius = radius * 0.72f,
                center = center,
                style = Stroke(width = 1.2.dp.toPx())
            )
            // Розовый центр (акцент)
            drawCircle(color = primaryColor, radius = radius * 0.42f, center = center)
            // Центровое отверстие
            drawCircle(color = Color(0xFF151515), radius = radius * 0.14f, center = center)
        }

        Spacer(Modifier.width(10.dp))

        Column(verticalArrangement = Arrangement.Center) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Shared",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = onSurfaceColor,
                    letterSpacing = 0.5.sp
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "Music",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = primaryColor,
                    letterSpacing = 0.5.sp
                )
            }
            Text(
                text = "Синхронный плеер",
                style = MaterialTheme.typography.labelSmall,
                color = onSurfaceVariant,
                fontSize = 10.sp
            )
        }
    }
}
