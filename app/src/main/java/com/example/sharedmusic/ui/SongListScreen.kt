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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.sharedmusic.ui.components.BackIcon
import com.example.sharedmusic.ui.components.HeartIcon
import com.example.sharedmusic.ui.components.SearchIcon
import com.example.sharedmusic.ui.components.SongRow
import com.example.sharedmusic.ui.theme.SharedMusicTheme

// Экран библиотеки треков:
// — поиск по названию и исполнителю
// — вкладки «Все треки» и «♥ Любимые»
// — список треков (пуст по умолчанию)
@Composable
fun SongListScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    tracks: List<FakeTrack> = emptyList() // Пусто по умолчанию (начальное состояние)
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val onSurface = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant
    val surface = MaterialTheme.colorScheme.surface
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant

    // Поисковый запрос
    var searchQuery by remember { mutableStateOf("") }

    // Активная вкладка: 0 = Все треки, 1 = Любимые
    var selectedTab by remember { mutableIntStateOf(0) }

    // Множество индексов избранных треков
    var likedIndices by remember { mutableStateOf(setOf<Int>()) }

    // Фильтрация по поиску
    val filteredTracks = tracks.filter { track ->
        searchQuery.isBlank() ||
                track.title.contains(searchQuery, ignoreCase = true) ||
                track.artist.contains(searchQuery, ignoreCase = true)
    }

    // Фильтрация по вкладке (все / любимые)
    val displayTracks = if (selectedTab == 0) {
        filteredTracks
    } else {
        filteredTracks.filterIndexed { index, _ -> index in likedIndices }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            // Отступ от верхней шторки
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Шапка: кнопка «Назад» и заголовок
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(surfaceVariant)
                    .clickable(onClick = onBackClick),
                contentAlignment = Alignment.Center
            ) {
                BackIcon(tint = onSurface, iconSize = 22.dp)
            }
            Spacer(Modifier.width(12.dp))
            Text(
                text = "Библиотека",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = onSurface
            )
        }

        Spacer(Modifier.height(16.dp))

        // Поле поиска
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Поиск треков…") },
            leadingIcon = { SearchIcon(tint = onSurfaceVariant, iconSize = 20.dp) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = primaryColor,
                cursorColor = primaryColor,
                unfocusedBorderColor = surfaceVariant
            )
        )

        Spacer(Modifier.height(16.dp))

        // Вкладки: «Все треки» и «♥ Любимые»
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TabChip(
                text = "Все треки",
                isSelected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                primaryColor = primaryColor,
                surfaceVariant = surfaceVariant
            )
            TabChip(
                text = "♥ Любимые",
                isSelected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                primaryColor = primaryColor,
                surfaceVariant = surfaceVariant
            )
        }

        Spacer(Modifier.height(12.dp))

        // Список треков
        if (displayTracks.isEmpty()) {
            // Пустое состояние
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (selectedTab == 1) {
                        // Нет избранных треков
                        HeartIcon(
                            tint = onSurfaceVariant.copy(alpha = 0.4f),
                            isLiked = false,
                            iconSize = 48.dp
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = "Нет избранных треков",
                            style = MaterialTheme.typography.bodyLarge,
                            color = onSurfaceVariant
                        )
                        Text(
                            text = "Нажмите ♥ на треке, чтобы добавить в избранное",
                            style = MaterialTheme.typography.bodySmall,
                            color = onSurfaceVariant.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )
                    } else {
                        // Нет треков вообще
                        Text(
                            text = "Пока нет треков",
                            style = MaterialTheme.typography.bodyLarge,
                            color = onSurfaceVariant
                        )
                        Text(
                            text = "Добавьте музыку с главного экрана",
                            style = MaterialTheme.typography.bodySmall,
                            color = onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        } else {
            // Список треков
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                itemsIndexed(displayTracks) { index, track ->
                    val isLiked = index in likedIndices
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Трек (SongRow)
                        Box(modifier = Modifier.weight(1f)) {
                            SongRow(
                                title = track.title,
                                artist = track.artist,
                                duration = "0:00",
                                coverColors = track.coverColors,
                                onClick = { /* Воспроизвести трек */ }
                            )
                        }
                        // Кнопка «Лайк»
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .clickable {
                                    likedIndices = if (isLiked) {
                                        likedIndices - index
                                    } else {
                                        likedIndices + index
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            HeartIcon(
                                tint = if (isLiked) primaryColor else onSurfaceVariant.copy(alpha = 0.6f),
                                isLiked = isLiked,
                                iconSize = 20.dp
                            )
                        }
                    }
                }
            }
        }
    }
}

// Чип вкладки («Все треки» / «♥ Любимые»)
@Composable
private fun TabChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    primaryColor: Color,
    surfaceVariant: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) primaryColor else surfaceVariant)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
        )
    }
}

// Превью: пустая библиотека (тёмная тема)
@Preview(name = "Библиотека: Пустая (Тёмная)", showBackground = true, backgroundColor = 0xFF212121)
@Composable
private fun SongListScreenEmptyDarkPreview() {
    SharedMusicTheme(darkTheme = true) {
        SongListScreen(onBackClick = {})
    }
}

// Превью: пустая библиотека (светлая тема)
@Preview(name = "Библиотека: Пустая (Светлая)", showBackground = true)
@Composable
private fun SongListScreenEmptyLightPreview() {
    SharedMusicTheme(darkTheme = false) {
        SongListScreen(onBackClick = {})
    }
}