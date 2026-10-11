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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.sharedmusic.ui.components.PlayerControls
import com.example.sharedmusic.ui.components.SharedLogo
import com.example.sharedmusic.ui.components.VinylRecord
import com.example.sharedmusic.ui.theme.SharedMusicTheme
import com.example.sharedmusic.viewmodel.MusicViewModel

@Composable
fun MainScreen(
    viewModel: MusicViewModel = viewModel(),
    onOpenLibraryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Выбор файла из файловой системы устройства
    val musicPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        viewModel.onMusicSelected(uri)
    }

    val isPlaying by viewModel.isPlaying.collectAsState()
    val selectedUri = viewModel.selectedMusicUri.value

    var isShuffle by remember { mutableStateOf(false) }
    var isLiked by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0.35f) }

    MainScreenContent(
        isPlaying = isPlaying,
        isShuffle = isShuffle,
        isLiked = isLiked,
        onShuffleClick = { isShuffle = !isShuffle },
        onLikeClick = { isLiked = !isLiked },
        onAddMusicClick = { musicPicker.launch("audio/*") },
        onPlayStopClick = { viewModel.toggleMusic() },
        onPreviousClick = { progress = 0f },
        onNextClick = { progress = 0.5f },
        onOpenLibraryClick = onOpenLibraryClick,
        trackTitle = if (selectedUri != null) "Мой локальный трек" else "Осень",
        artistName = if (selectedUri != null) "Локальный файл" else "Shared by You",
        progress = progress,
        positionText = "1:15",
        durationText = "3:24",
        library = FakeLibrary,
        modifier = modifier
    )
}

// Только отрисовка главного экрана: получает готовое состояние, пригодна для @Preview
@Composable
fun MainScreenContent(
    isPlaying: Boolean,
    isShuffle: Boolean = false,
    isLiked: Boolean = false,
    onShuffleClick: () -> Unit = {},
    onLikeClick: () -> Unit = {},
    onAddMusicClick: () -> Unit,
    onPlayStopClick: () -> Unit,
    onPreviousClick: () -> Unit = {},
    onNextClick: () -> Unit = {},
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
    val hasTrack = trackTitle != null

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Шапка с логотипом приложения и кнопкой добавления музыки
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            SharedLogo()

            Button(
                onClick = onAddMusicClick,
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("+ Добавить", style = MaterialTheme.typography.labelMedium)
            }
        }

        Spacer(Modifier.height(12.dp))

        // Метка текущего режима
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            ModeChip(text = "Локальный режим")
        }

        Spacer(Modifier.height(16.dp))

        // Центральная виниловая пластинка: вращается только во время воспроизведения
        VinylRecord(
            isPlaying = isPlaying,
            cover = cover,
            modifier = Modifier.fillMaxWidth(0.76f)
        )

        Spacer(Modifier.height(20.dp))

        // Название трека и исполнитель
        Text(
            text = trackTitle ?: "Трек не выбран",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = artistName ?: "Нажми «+ Добавить» для выбора музыки",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(14.dp))

        // Полоса прогресса воспроизведения
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(positionText, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(durationText, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        Spacer(Modifier.height(8.dp))

        // Панель кнопок управления: Перемешивание, Назад, Играть/Пауза, Вперёд, Лайк
        PlayerControls(
            isPlaying = isPlaying,
            isShuffle = isShuffle,
            isLiked = isLiked,
            hasTrack = hasTrack,
            onShuffleClick = onShuffleClick,
            onPreviousClick = onPreviousClick,
            onPlayPauseClick = onPlayStopClick,
            onNextClick = onNextClick,
            onLikeClick = onLikeClick
        )

        Spacer(Modifier.height(20.dp))

        // Блок библиотеки: горизонтальный слайдер треков
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Библиотека",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Все треки ›",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onOpenLibraryClick)
                    .padding(4.dp)
            )
        }

        Spacer(Modifier.height(10.dp))

        if (library.isEmpty()) {
            Text(
                text = "Пока нет треков.\n«Добавить» добавит их в список и запустит.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp)
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

// Плашка режима (Локальный / Комната)
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
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

// Карточка трека в горизонтальном слайдере библиотеки
@Composable
private fun LibraryCard(track: FakeTrack) {
    Column(modifier = Modifier.width(104.dp)) {
        Box(
            modifier = Modifier
                .size(104.dp)
                .clip(RoundedCornerShape(12.dp))
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

@Preview(name = "Главная (Тёмная тема, Пауза)", showBackground = true, backgroundColor = 0xFF212121)
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

@Preview(name = "Главная (Тёмная тема, Играет)", showBackground = true, backgroundColor = 0xFF212121)
@Composable
private fun MainScreenPlayingDarkPreview() {
    SharedMusicTheme(darkTheme = true) {
        MainScreenContent(
            isPlaying = true,
            isShuffle = true,
            isLiked = true,
            onAddMusicClick = {},
            onPlayStopClick = {},
            onOpenLibraryClick = {},
            trackTitle = "Осень",
            artistName = "Shared by You",
            progress = 0.35f,
            positionText = "1:24",
            durationText = "3:24",
            library = FakeLibrary
        )
    }
}

@Preview(name = "Главная (Светлая тема)", showBackground = true)
@Composable
private fun MainScreenLightPreview() {
    SharedMusicTheme(darkTheme = false) {
        MainScreenContent(
            isPlaying = false,
            onAddMusicClick = {},
            onPlayStopClick = {},
            onOpenLibraryClick = {},
            library = FakeLibrary
        )
    }
}
