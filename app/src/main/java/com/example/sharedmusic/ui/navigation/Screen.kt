package com.example.sharedmusic.ui.navigation

// Экраны приложения для навигации
sealed interface Screen {
    // Главный экран с вращающейся пластинкой
    data object Main : Screen
    // Экран комнаты для синхронного прослушивания музыки
    data object Room : Screen
    // Экран библиотеки со списком всех треков
    data object Library : Screen
    // Экран профиля пользователя и настроек темы
    data object Profile : Screen
    // Экран эквалайзера (открывается из экрана профиля)
    data object Equalizer : Screen
}
