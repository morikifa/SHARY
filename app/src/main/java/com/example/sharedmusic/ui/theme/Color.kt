package com.example.sharedmusic.ui.theme

import androidx.compose.ui.graphics.Color

// Палитра SHARY по цветам владельца продукта (тёмная тема — основная).
// Светлая тема — производная, её владелец ещё не утверждал.

// Тёмная тема
val DarkPrimary = Color(0xFFF64A8A)      // акцент: кнопки, активная пластинка, прогресс
val DarkOnPrimary = Color(0xFFFFFFFF)    // текст на акценте
val DarkBackground = Color(0xFF212121)   // фон экрана
val DarkSurface = Color(0xFF323232)      // карточки, неактивные кнопки
val DarkOnSurface = Color(0xFFF3F4F6)    // основной текст
val DarkOnSurfaceVariant = Color(0xFFB0B3BA) // второстепенный текст (производный)

// Светлая тема (черновик)
val LightPrimary = Color(0xFFE0306F)
val LightOnPrimary = Color(0xFFFFFFFF)
val LightBackground = Color(0xFFF3F4F6)
val LightSurface = Color(0xFFFFFFFF)
val LightOnSurface = Color(0xFF212121)
val LightOnSurfaceVariant = Color(0xFF5F6368)

// Цвета самой пластинки (не зависят от темы)
val VinylBlack = Color(0xFF151515)
val VinylGroove = Color(0xFF2E2E2E)
