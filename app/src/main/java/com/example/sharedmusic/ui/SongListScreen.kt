package com.example.sharedmusic.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.sharedmusic.ui.components.BackIcon
import com.example.sharedmusic.ui.components.PlayerIcon
import com.example.sharedmusic.ui.components.PlayerIconKind
import com.example.sharedmusic.ui.components.SearchIcon
import com.example.sharedmusic.ui.components.ShuffleIcon
import com.example.sharedmusic.ui.components.SongRow
import com.example.sharedmusic.ui.theme.SharedMusicTheme

// Экран библиотеки треков: поиск, сортировка, запуск воспроизведения
@Composable
fun SongListScreen(
    onBackClick: (() -> Unit)? = null,
    onTrackSelect: ((FakeTrack) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val onSurface = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant

    var searchQuery by remember { mutableStateOf("") }
    var playingIndex by remember { mutableIntStateOf(0) }

    // Фильтрация треков по строке поиска
    val filteredTracks = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            FakeLibrary
        } else {
            FakeLibrary.filter {
                it.title.contains(searchQuery, ignoreCase = true) ||
                        it.artist.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Шапка экрана
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (onBackClick != null) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .clickable(onClick = onBackClick),
                        contentAlignment = Alignment.Center
                    ) {
                        BackIcon(tint = onSurface, iconSize = 22.dp)
                    }
                    Spacer(Modifier.width(8.dp))
                }
                Column {
                    Text(
                        text = "Библиотека треков",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = onSurface
                    )
                    Text(
                        text = "${filteredTracks.size} аудиофайлов на устройстве",
                        style = MaterialTheme.typography.bodySmall,
                        color = onSurfaceVariant
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Строка поиска с иконкой лупы
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = {
                Text("Поиск по названию или исполнителю...", style = MaterialTheme.typography.bodyMedium)
            },
            leadingIcon = {
                SearchIcon(tint = onSurfaceVariant, iconSize = 20.dp)
            },
            singleLine = true,
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = primaryColor,
                unfocusedBorderColor = surfaceVariant
            )
        )

        Spacer(Modifier.height(14.dp))

        // Кнопки быстрого действия: «Слушать всё» и «Перемешать»
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = { playingIndex = 0 },
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                modifier = Modifier.weight(1f)
            ) {
                PlayerIcon(kind = PlayerIconKind.PLAY, tint = Color.White, iconSize = 16.dp)
                Spacer(Modifier.width(8.dp))
                Text("Слушать всё", style = MaterialTheme.typography.labelLarge)
            }

            OutlinedButton(
                onClick = {
                    if (filteredTracks.isNotEmpty()) {
                        playingIndex = filteredTracks.indices.random()
                    }
                },
                shape = CircleShape,
                modifier = Modifier.weight(1f)
            ) {
                ShuffleIcon(tint = onSurface, iconSize = 18.dp)
                Spacer(Modifier.width(8.dp))
                Text("Перемешать", style = MaterialTheme.typography.labelLarge, color = onSurface)
            }
        }

        Spacer(Modifier.height(16.dp))

        // Список треков
        if (filteredTracks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Ничего не найдено",
                    style = MaterialTheme.typography.bodyLarge,
                    color = onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                itemsIndexed(filteredTracks) { index, track ->
                    val isCurrent = index == playingIndex
                    SongRow(
                        title = track.title,
                        artist = track.artist,
                        duration = when (index % 4) {
                            0 -> "3:24"
                            1 -> "2:48"
                            2 -> "4:15"
                            else -> "3:02"
                        },
                        coverColors = track.coverColors,
                        isPlaying = isCurrent,
                        onClick = {
                            playingIndex = index
                            onTrackSelect?.invoke(track)
                        }
                    )
                }
            }
        }
    }
}

@Preview(name = "Библиотека (Тёмная тема)", showBackground = true, backgroundColor = 0xFF212121)
@Composable
private fun SongListScreenDarkPreview() {
    SharedMusicTheme(darkTheme = true) {
        SongListScreen()
    }
}

@Preview(name = "Библиотека (Светлая тема)", showBackground = true)
@Composable
private fun SongListScreenLightPreview() {
    SharedMusicTheme(darkTheme = false) {
        SongListScreen()
    }
}
