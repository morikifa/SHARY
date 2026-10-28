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
    canControlPlayback: Boolean = true,
    inRoom: Boolean = false,
    modifier: Modifier = Modifier
) {
    // Плеер партнёра: добавление файла в общую библиотеку и запуск локально.
    val musicPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            viewModel.addSong(uri)
            if (canControlPlayback) viewModel.songs.value.lastOrNull()?.let(viewModel::playSong)
        }
    }
    val isPlaying by viewModel.isPlaying.collectAsState()
    val currentSong by viewModel.currentSong.collectAsState()
    val songs by viewModel.songs.collectAsState()
    val position by viewModel.positionMs.collectAsState()
    val duration by viewModel.durationMs.collectAsState()
    var isShuffle by remember { mutableStateOf(false) }
    var isLiked by remember { mutableStateOf(false) }
    val hasTrack = currentSong != null
    val progress = if (duration > 0) (position.toFloat() / duration).coerceIn(0f, 1f) else 0f

    fun playAdjacent(step: Int) {
        if (!canControlPlayback || songs.isEmpty()) return
        val index = songs.indexOfFirst { it.id == currentSong?.id }
        val next = if (index < 0) 0 else (index + step + songs.size) % songs.size
        viewModel.playSong(songs[next])
    }

    MainScreenContent(
        isPlaying = isPlaying,
        isShuffle = isShuffle,
        isLiked = isLiked,
        canControlPlayback = canControlPlayback,
        inRoom = inRoom,
        onShuffleClick = { if (hasTrack) isShuffle = !isShuffle },
        onLikeClick = { if (hasTrack) isLiked = !isLiked },
        onAddMusicClick = { musicPicker.launch("audio/*") },
        onPlayStopClick = {
            if (canControlPlayback) {
                if (hasTrack) viewModel.toggleMusic() else musicPicker.launch("audio/*")
            }
        },
        onPreviousClick = { playAdjacent(-1) },
        onNextClick = { playAdjacent(1) },
        onSeekProgress = { if (canControlPlayback && duration > 0) viewModel.seekTo((it * duration).toLong()) },
        // Поворот пластинки перематывает реальную позицию плеера.
        onScrubDelta = { delta ->
            if (canControlPlayback && duration > 0) {
                viewModel.seekTo(position + (delta / (360f * 4f) * duration).toLong())
            }
        },
        onOpenLibraryClick = onOpenLibraryClick,
        trackTitle = currentSong?.title,
        artistName = if (hasTrack) "Локальное аудио" else null,
        progress = progress,
        positionText = formatPlaybackTime(position),
        durationText = if (duration > 0) formatPlaybackTime(duration) else "?",
        library = songs.map { FakeTrack(it.title, "Локальное аудио", listOf(Color(0xFFF64A8A), Color(0xFF323232))) },
        onLibraryTrackClick = { index -> if (canControlPlayback) songs.getOrNull(index)?.let(viewModel::playSong) },
        modifier = modifier
    )
}

private fun formatPlaybackTime(timeMs: Long): String {
    val seconds = timeMs.coerceAtLeast(0) / 1000
    return "%d:%02d".format(seconds / 60, seconds % 60)
}

// Чистая Composable-функция отрисовки главного экрана (пригодна для @Preview)
@Composable
fun MainScreenContent(
    isPlaying: Boolean,
    isShuffle: Boolean = false,
    isLiked: Boolean = false,
    canControlPlayback: Boolean = true,
    inRoom: Boolean = false,
    onLibraryTrackClick: (Int) -> Unit = {},
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
            ModeChip(text = if (inRoom) "Комната" else "Локальный режим")

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
            onScrub = if (canControlPlayback) onScrubDelta else null,
            modifier = Modifier.fillMaxWidth(0.82f)
        )

        Spacer(Modifier.height(22.dp))

        // Название трека: если ничего не добавлено — нейтральная подсказка
        FadingText(
            text = trackTitle ?: "Трек не выбран",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, textAlign = TextAlign.Center),
            color = MaterialTheme.colorScheme.onSurface
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
            enabled = hasTrack && canControlPlayback,
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
            canControlPlayback = canControlPlayback,
            isShuffle = isShuffle,
            isLiked = isLiked,
            hasTrack = hasTrack,
            onShuffleClick = onShuffleClick,
            onPreviousClick = onPreviousClick,
            onPlayPauseClick = onPlayStopClick,
            onNextClick = onNextClick,
            onLikeClick = onLikeClick
        )

        if (!canControlPlayback) {
            Text("Управляет хозяин комнаты", color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall)
        }
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
                library.forEachIndexed { index, track -> LibraryCard(track, enabled = canControlPlayback) { onLibraryTrackClick(index) } }
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
private fun LibraryCard(track: FakeTrack, enabled: Boolean = true, onClick: () -> Unit = {}) {
    Column(modifier = Modifier.width(104.dp).clickable(enabled = enabled, onClick = onClick)) {
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