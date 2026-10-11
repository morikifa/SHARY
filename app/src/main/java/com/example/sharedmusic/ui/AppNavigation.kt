package com.example.sharedmusic.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
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
                        MainScreen(
                            onOpenLibraryClick = { currentScreen = Screen.Library }
                        )
                    }

                    is Screen.Room -> {
                        RoomScreen()
                    }

                    is Screen.Library -> {
                        SongListScreen(
                            onBackClick = { currentScreen = Screen.Main }
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
