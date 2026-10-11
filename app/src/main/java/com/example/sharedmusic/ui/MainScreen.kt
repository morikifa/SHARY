package com.example.sharedmusic.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
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

    //Сам экран отрисован отдельно, чтобы его можно было показать в @Preview без ViewModel
    MainScreenContent(
        isPlaying = isPlaying,
        onAddMusicClick = { musicPicker.launch("audio/*") },
        onPlayStopClick = { viewModel.toggleMusic() },
        onOpenLibraryClick = onOpenLibraryClick
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
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        //Режим всегда виден пользователю (пока только локальный)
        Text(
            text = "Режим: Локально",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.height(24.dp))

        //Центральная пластинка: крутится только во время воспроизведения
        VinylRecord(
            isPlaying = isPlaying,
            modifier = Modifier.fillMaxWidth(0.8f)
        )

        Spacer(Modifier.height(24.dp))

        //Название трека (если есть), иначе подсказка
        Text(
            text = trackTitle ?: "Трек не выбран",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(Modifier.height(32.dp))

        //Добавление музыки
        Button(
            onClick = onAddMusicClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Добавить музыку")
        }

        Spacer(Modifier.height(8.dp))

        //Play/Stop
        Button(
            onClick = onPlayStopClick,
            modifier = Modifier.fillMaxWidth()
        ){
            Text(
                text = if (isPlaying) "Стоп" else "Послушать"
            )
        }

        Spacer(Modifier.height(8.dp))

        //Кнопка перехода на второй экран
        Button(
            onClick = onOpenLibraryClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Список песен")
        }
    }
}

@Preview(name = "Пауза, светлая", showBackground = true)
@Composable
private fun MainScreenPausedLightPreview() {
    SharedMusicTheme(darkTheme = false) {
        MainScreenContent(
            isPlaying = false,
            onAddMusicClick = {},
            onPlayStopClick = {},
            onOpenLibraryClick = {}
        )
    }
}

@Preview(name = "Воспроизведение, светлая", showBackground = true)
@Composable
private fun MainScreenPlayingLightPreview() {
    SharedMusicTheme(darkTheme = false) {
        MainScreenContent(
            isPlaying = true,
            onAddMusicClick = {},
            onPlayStopClick = {},
            onOpenLibraryClick = {},
            trackTitle = "Тестовый трек"
        )
    }
}

@Preview(name = "Воспроизведение, тёмная", showBackground = true, backgroundColor = 0xFF121216)
@Composable
private fun MainScreenPlayingDarkPreview() {
    SharedMusicTheme(darkTheme = true) {
        MainScreenContent(
            isPlaying = true,
            onAddMusicClick = {},
            onPlayStopClick = {},
            onOpenLibraryClick = {},
            trackTitle = "Тестовый трек"
        )
    }
}
