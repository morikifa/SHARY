package com.example.sharedmusic.ui

import androidx.compose.ui.graphics.Color

// Тестовые данные этапа 0: настоящей библиотеки пока нет.
// Обложки заменены цветными заглушками, позже здесь будут обложки пользователей.
data class FakeTrack(
    val title: String,
    val artist: String,
    val coverColors: List<Color>
)

val FakeLibrary = listOf(
    FakeTrack("Осень", "Shared by You", listOf(Color(0xFFF64A8A), Color(0xFF323232))),
    FakeTrack("Мелодия", "Shared by You", listOf(Color(0xFF323232), Color(0xFFF64A8A))),
    FakeTrack("Ритм", "Shared by You", listOf(Color(0xFFF64A8A), Color(0xFF212121))),
    FakeTrack("Закат", "Shared by You", listOf(Color(0xFF3A3A3A), Color(0xFFF3F4F6))),
    FakeTrack("Дорога через очень длинное название", "Исполнитель с длинным именем", listOf(Color(0xFF6B2E4A), Color(0xFF212121)))
)
