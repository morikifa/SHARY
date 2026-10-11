package com.example.sharedmusic.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sharedmusic.ui.components.FadingText
import com.example.sharedmusic.ui.components.PlayerControls
import com.example.sharedmusic.ui.components.VinylRecord
import com.example.sharedmusic.ui.theme.SharedMusicTheme
import com.example.sharedmusic.viewmodel.MusicViewModel

// Главный экран: чистый верхний бар, виниловая пластинка с перемоткой,
// пустое начальное состояние (треки и библиотека пусты до добавления музыки)
@Composable
fun MainScreen(
    viewModel: MusicViewModel = viewModel(),
    onOpenLibraryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Системный выбор аудиофайлов из памяти устройства
    val musicPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        viewModel.onMusicSelected(uri)
    }

    val isPlaying by viewModel.isPlaying.collectAsState()
    val selectedUri = viewModel.selectedMusicUri.value

    var isShuffle by remember { mutableStateOf(false) }
    var isLiked by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0f) }

    // Определяем, выбран ли трек
    val hasTrack = selectedUri != null
    val trackTitle = if (hasTrack) "Загруженный трек" else null
    val artistName = if (hasTrack) "Локальное аудио" else null
    val positionText = "0:00"
    val durationText = if (hasTrack) "3:20" else "?"

    MainScreenContent(
        isPlaying = isPlaying,
        isShuffle = isShuffle,
        isLiked = isLiked,
        onShuffleClick = { if (hasTrack) isShuffle = !isShuffle },
        onLikeClick = { if (hasTrack) isLiked = !isLiked },
        onAddMusicClick = { musicPicker.launch("audio/*") },
        onPlayStopClick = {
            if (hasTrack) viewModel.toggleMusic()
            else musicPicker.launch("audio/*")
        },
        onPreviousClick = { progress = 0f },
        onNextClick = { progress = 0f },
        // Перемотка ползунком
        onSeekProgress = { newProgress -> progress = newProgress },
        // Перемотка вращением пластинки (4 оборота = 100% трека)
        onScrubDelta = { deltaDegrees ->
            if (hasTrack) {
                progress = (progress + deltaDegrees / (360f * 4f)).coerceIn(0f, 1f)
            }
        },
        onOpenLibraryClick = onOpenLibraryClick,
        trackTitle = trackTitle,
        artistName = artistName,
        progress = progress,
        positionText = positionText,
        durationText = durationText,
        library = emptyList(), // Библиотека пуста (начальное состояние)
        modifier = modifier
    )
}

// Чистая Composable-функция отрисовки главного экрана (пригодна для @Preview)
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
    onSeekProgress: (Float) -> Unit = {},
    onScrubDelta: (Float) -> Unit = {},
    onOpenLibraryClick: () -> Unit,
    trackTitle: String? = null,
    artistName: String? = null,
    cover: Painter? = null,
    progress: Float = 0f,
    positionText: String = "0:00",
    durationText: String = "?",
    library: List<FakeTrack> = emptyList(),
    modifier: Modifier = Modifier
) {
    val hasTrack = trackTitle != null
    val primaryColor = MaterialTheme.colorScheme.primary

    Column(
        modifier = modifier
            .fillMaxSize()
            // Отступ от верхней системной шторки (время, батарея, камера)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Верхняя панель: «Локальный режим» и «+ Добавить» на одном уровне
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Плашка режима слева
            ModeChip(text = "Локальный режим")

            // Кнопка добавления музыки справа
            Button(
                onClick = onAddMusicClick,
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "+ Добавить",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Центральная виниловая пластинка с волнистыми линиями и перемоткой
        VinylRecord(
            isPlaying = isPlaying,
            cover = cover,
            onScrub = onScrubDelta,
            modifier = Modifier.fillMaxWidth(0.82f)
        )

        Spacer(Modifier.height(22.dp))

        // Название трека: если ничего не добавлено — нейтральная подсказка
        Text(
            text = trackTitle ?: "Трек не выбран",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = artistName ?: "Нажмите «+ Добавить», чтобы выбрать музыку",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(16.dp))

        // Полоса прогресса воспроизведения
        Slider(
            value = progress,
            onValueChange = onSeekProgress,
            enabled = hasTrack,
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = primaryColor,
                activeTrackColor = primaryColor,
                disabledThumbColor = MaterialTheme.colorScheme.surfaceVariant,
                disabledActiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(positionText, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(durationText, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        Spacer(Modifier.height(10.dp))

        // Панель кнопок: Перемешивание, Назад, Играть/Пауза, Вперёд, Лайк
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

        Spacer(Modifier.height(24.dp))

        // Блок «Библиотека»
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
                color = primaryColor,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onOpenLibraryClick)
                    .padding(4.dp)
            )
        }

        Spacer(Modifier.height(12.dp))

        // Слайдер библиотеки: если пусто — серый квадрат-заглушка с «?»
        if (library.isEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                // Серый квадрат-заглушка (нельзя выбрать, пока нет музыки)
                Column(
                    modifier = Modifier.width(104.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(104.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "?",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "?",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "?",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            // Реальные карточки треков (появятся после добавления музыки)
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

// Плашка режима («Локальный режим»)
@Composable
private fun ModeChip(text: String) {
    Row(
        modifier = Modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 14.dp, vertical = 7.dp),
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
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

// Карточка трека в слайдере библиотеки
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

// Превью: пустое состояние (тёмная тема)
@Preview(name = "Главная: Пустое (Тёмная)", showBackground = true, backgroundColor = 0xFF212121)
@Composable
private fun MainScreenEmptyDarkPreview() {
    SharedMusicTheme(darkTheme = true) {
        MainScreenContent(
            isPlaying = false,
            onAddMusicClick = {},
            onPlayStopClick = {},
            onOpenLibraryClick = {},
            library = emptyList()
        )
    }
}

// Превью: воспроизведение с треком (тёмная тема)
@Preview(name = "Главная: Играет (Тёмная)", showBackground = true, backgroundColor = 0xFF212121)
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

// Превью: пустое состояние (светлая тема)
@Preview(name = "Главная: Пустое (Светлая)", showBackground = true)
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