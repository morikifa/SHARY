package com.example.sharedmusic.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.ui.unit.dp
import com.google.firebase.FirebaseApp
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sharedmusic.connection.ConnectionManager
import com.example.sharedmusic.ui.sync.RoomPlaybackSync
import com.example.sharedmusic.viewmodel.MusicViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.sharedmusic.ui.navigation.AppBottomBar
import com.example.sharedmusic.ui.navigation.Screen
import com.example.sharedmusic.ui.theme.SharedMusicTheme

// Корневой навигационный контейнер приложения
@Composable
fun AppNavigation() {
    // Состояние темы: по умолчанию тёмная (#212121), переключается в профиле
    var isDarkTheme by remember { mutableStateOf(true) }

    // Текущий открытый экран
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Main) }

    // Передача синхронизации: единый плеер и соединение для всех вкладок.
    val context = LocalContext.current
    val musicViewModel: MusicViewModel = viewModel()
    // Без настройки Firebase локальный экран остаётся доступным; комнату не запускаем.
    val connectionManager = remember(context.applicationContext) {
        if (FirebaseApp.getApps(context.applicationContext).isNotEmpty()) {
            ConnectionManager(context.applicationContext)
        } else null
    }
    var isHost by rememberSaveable { mutableStateOf(false) }
    val inRoom = connectionManager?.currentRoomCode != null
    val canControlPlayback = !inRoom || isHost
    val songs by musicViewModel.songs.collectAsState()
    val screenStateHolder = rememberSaveableStateHolder()
    if (connectionManager != null) RoomPlaybackSync(connectionManager, musicViewModel, canControlPlayback)

    // Оборачиваем всё приложение в выбранную тему Material 3
    SharedMusicTheme(darkTheme = isDarkTheme) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Область содержимого текущего экрана
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
            ) {
                when (currentScreen) {
                    is Screen.Main -> {
                        screenStateHolder.SaveableStateProvider("main") {
                            MainScreen(
                                viewModel = musicViewModel,
                                canControlPlayback = canControlPlayback,
                                inRoom = inRoom,
                                onOpenLibraryClick = { currentScreen = Screen.Library }
                            )
                        }
                    }

                    is Screen.Room -> {
                        if (connectionManager != null) {
                            RoomScreen(
                                connectionManager = connectionManager,
                                musicViewModel = musicViewModel,
                                isHost = isHost,
                                onRoleSelected = { isHost = it }
                            )
                        } else {
                            Text(
                                "Комната недоступна: Firebase ещё не настроен.",
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.statusBarsPadding().padding(20.dp)
                            )
                        }
                    }

                    is Screen.Library -> {
                        SongListScreen(
                            onBackClick = { currentScreen = Screen.Main },
                            songs = songs,
                            canControlPlayback = canControlPlayback,
                            onPlaySong = musicViewModel::playSong
                        )
                    }

                    is Screen.Profile -> {
                        ProfileScreen(
                            isDarkTheme = isDarkTheme,
                            onThemeChange = { isDarkTheme = it },
                            onOpenEqualizerClick = { currentScreen = Screen.Equalizer },
                            onAddMusicClick = { currentScreen = Screen.Main }
                        )
                    }

                    is Screen.Equalizer -> {
                        EqualizerScreen(
                            onBackClick = { currentScreen = Screen.Profile }
                        )
                    }
                }
            }

            // Нижняя панель навигации (скрывается только на подэкране Эквалайзера)
            if (currentScreen != Screen.Equalizer) {
                AppBottomBar(
                    currentScreen = currentScreen,
                    onScreenSelected = { newScreen -> currentScreen = newScreen }
                )
            }
        }
    }
}
