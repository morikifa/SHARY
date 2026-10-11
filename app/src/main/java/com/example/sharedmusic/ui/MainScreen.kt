package com.example.sharedmusic.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sharedmusic.ui.components.FadingText
import com.example.sharedmusic.ui.components.PlayerIcon
import com.example.sharedmusic.ui.components.PlayerIconKind
import com.example.sharedmusic.ui.components.VinylRecord
import com.example.sharedmusic.ui.theme.SharedMusicTheme
import com.example.sharedmusic.viewmodel.MusicViewModel


@Composable
fun MainScreen(
    //Мозг для отрисовки экрана в разных ситуациях
    viewModel: MusicViewModel = viewModel(),
    //Открытие окна со списком песен
    onOpenLibraryClick: () -> Unit
){
    //Берём музыку с файловой системы
    val musicPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        //Передаём файл в мозг когда получаем
        viewModel.onMusicSelected(uri)
    }

    //Есл значение кнопки меняется - он её перерисует (кнопка стоп/пуск)
    val isPlaying by viewModel.isPlaying.collectAsState()

    //Сам экран отрисован отдельно, чтобы его можно было показать в @Preview без ViewModel.
    //Библиотека пока фейковая (этап 0)
    MainScreenContent(
        isPlaying = isPlaying,
        onAddMusicClick = { musicPicker.launch("audio/*") },
        onPlayStopClick = { viewModel.toggleMusic() },
        onOpenLibraryClick = onOpenLibraryClick,
        library = FakeLibrary
    )
}

// Только отрисовка: получает готовые данные и передаёт нажатия наружу
@Composable
fun MainScreenContent(
    isPlaying: Boolean,
    onAddMusicClick: () -> Unit,
    onPlayStopClick: () -> Unit,
    onOpenLibraryClick: () -> Unit,
    trackTitle: String? = null,
    artistName: String? = null,
    cover: Painter? = null,
    progress: Float = 0f,
    positionText: String = "0:00",
    durationText: String = "--:--",
    library: List<FakeTrack> = emptyList(),
    modifier: Modifier = Modifier
) {
    // Есть ли выбранный трек: от этого зависит, доступны ли кнопки управления
    val hasTrack = trackTitle != null

    Column(
        modifier = modifier
            .fillMaxSize()
            // Если экран низкий, всё содержимое можно прокрутить
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //Верхняя строка: режим слева (виден всегда), добавление музыки справа
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ModeChip(text = "Локальный режим")

            Button(
                onClick = onAddMusicClick,
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("+ Добавить музыку", style = MaterialTheme.typography.labelLarge)
            }
        }

        Spacer(Modifier.height(24.dp))

        //Центральная пластинка: крутится только во время воспроизведения
        VinylRecord(
            isPlaying = isPlaying,
            cover = cover,
            modifier = Modifier.fillMaxWidth(0.78f)
        )

        Spacer(Modifier.height(24.dp))

        //Название и исполнитель трека, иначе подсказка
        Text(
            text = trackTitle ?: "Трек не выбран",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Text(
            text = artistName ?: "Нажми «Добавить музыку» и выбери трек",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(16.dp))

        //Полоса прогресса. Перемотка появится на следующем этапе, пока только показ
        Slider(
            value = progress,
            onValueChange = { },
            enabled = hasTrack,
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary
            )
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(positionText, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(durationText, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        Spacer(Modifier.height(12.dp))

        //Управление: назад, большая кнопка play/pause, вперёд
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ControlIconButton(kind = PlayerIconKind.PREVIOUS, enabled = hasTrack, onClick = { })

            //Большая кнопка: розовая во время воспроизведения, серая на паузе
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(
                        if (isPlaying) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                    )
                    .clickable(onClick = onPlayStopClick),
                contentAlignment = Alignment.Center
            ) {
                PlayerIcon(
                    kind = if (isPlaying) PlayerIconKind.PAUSE else PlayerIconKind.PLAY,
                    tint = if (isPlaying) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                    iconSize = 28.dp
                )
            }

            ControlIconButton(kind = PlayerIconKind.NEXT, enabled = hasTrack, onClick = { })
        }

        Spacer(Modifier.height(24.dp))

        //Библиотека: горизонтальный слайдер с обложками
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Библиотека",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            //Переход на полный список песен
            Text(
                text = "Все песни",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable(onClick = onOpenLibraryClick)
            )
        }

        Spacer(Modifier.height(12.dp))

        if (library.isEmpty()) {
            Text(
                text = "Пока нет треков.\n«Добавить музыку» добавит их в список и запустит.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp)
            )
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                library.forEach { track -> LibraryCard(track) }
            }
        }
    }
}

// Плашка режима: маленькая точка и подпись
@Composable
private fun ModeChip(text: String) {
    Row(
        modifier = Modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
        )
        Spacer(Modifier.width(8.dp))
        Text(text = text, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurface)
    }
}

// Кнопка-иконка для назад/вперёд. Когда недоступна, становится полупрозрачной
@Composable
private fun ControlIconButton(kind: PlayerIconKind, enabled: Boolean, onClick: () -> Unit) {
    val tint = MaterialTheme.colorScheme.onSurface.copy(alpha = if (enabled) 1f else 0.38f)
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        PlayerIcon(kind = kind, tint = tint, iconSize = 24.dp)
    }
}

// Карточка трека в библиотеке: обложка и название, длинные строки плавно гаснут
@Composable
private fun LibraryCard(track: FakeTrack) {
    Column(modifier = Modifier.width(104.dp)) {
        Box(
            modifier = Modifier
                .size(104.dp)
                .clip(RoundedCornerShape(12.dp))
                // Пока обложки нет, показываем цветной градиент-заглушку
                .background(Brush.linearGradient(track.coverColors))
        )
        Spacer(Modifier.height(6.dp))
        FadingText(
            text = track.title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        FadingText(
            text = track.artist,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(name = "Пауза, тёмная", showBackground = true, backgroundColor = 0xFF212121)
@Composable
private fun MainScreenPausedDarkPreview() {
    SharedMusicTheme(darkTheme = true) {
        MainScreenContent(
            isPlaying = false,
            onAddMusicClick = {},
            onPlayStopClick = {},
            onOpenLibraryClick = {},
            library = FakeLibrary
        )
    }
}

@Preview(name = "Воспроизведение, тёмная", showBackground = true, backgroundColor = 0xFF212121)
@Composable
private fun MainScreenPlayingDarkPreview() {
    SharedMusicTheme(darkTheme = true) {
        MainScreenContent(
            isPlaying = true,
            onAddMusicClick = {},
            onPlayStopClick = {},
            onOpenLibraryClick = {},
            trackTitle = "Осень",
            artistName = "Shared by You",
            progress = 0.35f,
            positionText = "1:24",
            durationText = "-2:10",
            library = FakeLibrary
        )
    }
}

@Preview(name = "Пустая библиотека, светлая", showBackground = true)
@Composable
private fun MainScreenEmptyLightPreview() {
    SharedMusicTheme(darkTheme = false) {
        MainScreenContent(
            isPlaying = false,
            onAddMusicClick = {},
            onPlayStopClick = {},
            onOpenLibraryClick = {},
            library = emptyList()
        )
    }
}
