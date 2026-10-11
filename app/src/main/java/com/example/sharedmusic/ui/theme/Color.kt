package com.example.sharedmusic.ui.theme

import androidx.compose.ui.graphics.Color

// Палитра SHARY, утверждённая владельцем продукта:
// Акцент: #F64A8A | Фон: #212121 | Карточки: #323232 | Текст: #F3F4F6

// Тёмная тема (основная)
val DarkPrimary = Color(0xFFF64A8A)          // Акцент: кнопки, вращающаяся пластинка, прогресс
val DarkOnPrimary = Color(0xFFFFFFFF)        // Белый текст/иконки на акцентных элементах
val DarkBackground = Color(0xFF212121)       // Тёмный фон всего экрана
val DarkSurface = Color(0xFF323232)          // Фон карточек, диалогов, панелей
val DarkSurfaceVariant = Color(0xFF3D3D3D)   // Вторичный фон чипов и неактивных кнопок
val DarkOnSurface = Color(0xFFF3F4F6)        // Основной светлый текст
val DarkOnSurfaceVariant = Color(0xFFB0B3BA) // Второстепенный серый текст подписей

// Светлая тема (производная от утверждённых цветов)
val LightPrimary = Color(0xFFF64A8A)         // Сохраняем фирменный розовый акцент
val LightOnPrimary = Color(0xFFFFFFFF)       // Белый текст на акценте
val LightBackground = Color(0xFFF3F4F6)      // Светлый мягкий фон
val LightSurface = Color(0xFFFFFFFF)         // Белые карточки
val LightSurfaceVariant = Color(0xFFE5E7EB)  // Светло-серый фон чипов и неактивных кнопок
val LightOnSurface = Color(0xFF212121)       // Тёмный текст
val LightOnSurfaceVariant = Color(0xFF5F6368)// Приглушённый серый текст

// Цвета виниловой пластинки (одинаковые в обеих темах)
val VinylBlack = Color(0xFF151515)
val VinylGroove = Color(0xFF2E2E2E)
