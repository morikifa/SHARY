package com.example.sharedmusic.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

// Панель управления воспроизведением: Перемешивание, Назад, Играть/Пауза, Вперёд, Лайк
@Composable
fun PlayerControls(
    isPlaying: Boolean,
    isShuffle: Boolean,
    isLiked: Boolean,
    hasTrack: Boolean,
    canControlPlayback: Boolean = true,
    onShuffleClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onPlayPauseClick: () -> Unit,
    onNextClick: () -> Unit,
    onLikeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val onSurface = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant
    val onPrimary = MaterialTheme.colorScheme.onPrimary
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    val controlsActive = hasTrack && canControlPlayback

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        // Кнопка «Перемешивание» (активна — розовая, неактивна — серый текст)
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .clickable(enabled = hasTrack && canControlPlayback, onClick = onShuffleClick),
            contentAlignment = Alignment.Center
        ) {
            ShuffleIcon(
                tint = if (isShuffle && controlsActive) primaryColor else onSurfaceVariant.copy(alpha = if (controlsActive) 0.8f else 0.35f),
                iconSize = 22.dp
            )
        }

        // Кнопка «Предыдущий трек»
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .clickable(enabled = hasTrack && canControlPlayback, onClick = onPreviousClick),
            contentAlignment = Alignment.Center
        ) {
            PlayerIcon(
                kind = PlayerIconKind.PREVIOUS,
                tint = onSurface.copy(alpha = if (controlsActive) 1f else 0.35f),
                iconSize = 24.dp
            )
        }

        // Центральная большая кнопка Воспроизведение / Пауза (розовая при игре)
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(
                    if (isPlaying && canControlPlayback) primaryColor else surfaceVariant
                )
                .clickable(enabled = canControlPlayback, onClick = onPlayPauseClick),
            contentAlignment = Alignment.Center
        ) {
            PlayerIcon(
                kind = if (isPlaying) PlayerIconKind.PAUSE else PlayerIconKind.PLAY,
                tint = if (isPlaying && canControlPlayback) onPrimary else onSurface.copy(alpha = if (canControlPlayback) 1f else 0.35f),
                iconSize = 28.dp
            )
        }

        // Кнопка «Следующий трек»
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .clickable(enabled = hasTrack && canControlPlayback, onClick = onNextClick),
            contentAlignment = Alignment.Center
        ) {
            PlayerIcon(
                kind = PlayerIconKind.NEXT,
                tint = onSurface.copy(alpha = if (controlsActive) 1f else 0.35f),
                iconSize = 24.dp
            )
        }

        // Кнопка «Лайк» (розовое сердечко если отмечен)
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .clickable(enabled = hasTrack && canControlPlayback, onClick = onLikeClick),
            contentAlignment = Alignment.Center
        ) {
            HeartIcon(
                tint = if (isLiked && controlsActive) primaryColor else onSurfaceVariant.copy(alpha = if (controlsActive) 0.8f else 0.35f),
                isLiked = isLiked,
                iconSize = 22.dp
            )
        }
    }
}
