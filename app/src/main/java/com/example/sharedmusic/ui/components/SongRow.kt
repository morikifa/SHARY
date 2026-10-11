package com.example.sharedmusic.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

// Элемент списка треков: обложка, затухающее длинное название, исполнитель и длительность
@Composable
fun SongRow(
    title: String,
    artist: String,
    duration: String,
    coverColors: List<Color>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isPlaying: Boolean = false
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val onSurface = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Квадратная заглушка обложки с цветным градиентом
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Brush.linearGradient(coverColors)),
            contentAlignment = Alignment.Center
        ) {
            if (isPlaying) {
                // Если трек сейчас играет — показываем мини-индикатор
                PlayerIcon(
                    kind = PlayerIconKind.PLAY,
                    tint = Color.White,
                    iconSize = 16.dp
                )
            }
        }

        Spacer(Modifier.width(14.dp))

        // Название и исполнитель
        Column(
            modifier = Modifier.weight(1f)
        ) {
            FadingText(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = if (isPlaying) FontWeight.Bold else FontWeight.Medium
                ),
                color = if (isPlaying) primaryColor else onSurface
            )
            Spacer(Modifier.height(2.dp))
            FadingText(
                text = artist,
                style = MaterialTheme.typography.bodySmall,
                color = onSurfaceVariant
            )
        }

        Spacer(Modifier.width(12.dp))

        // Длительность трека
        Text(
            text = duration,
            style = MaterialTheme.typography.labelSmall,
            color = onSurfaceVariant
        )
    }
}
