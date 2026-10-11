package com.example.sharedmusic.ui

import androidx.compose.foundation.Canvas
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sharedmusic.ui.components.BackIcon
import com.example.sharedmusic.ui.theme.SharedMusicTheme

// Экран 5-полосного эквалайзера с пресетами и эффектами баса/объёма
@Composable
fun EqualizerScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val onSurface = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant
    val surface = MaterialTheme.colorScheme.surface
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant

    // Состояния 5 частотных полос (-10 дБ .. +10 дБ)
    var band60 by remember { mutableFloatStateOf(3f) }
    var band230 by remember { mutableFloatStateOf(1f) }
    var band910 by remember { mutableFloatStateOf(-1f) }
    var band3k6 by remember { mutableFloatStateOf(2f) }
    var band14k by remember { mutableFloatStateOf(4f) }

    // Эффекты
    var bassBoost by remember { mutableFloatStateOf(0.4f) }
    var surroundSound by remember { mutableFloatStateOf(0.2f) }

    // Выбранный пресет
    var selectedPreset by remember { mutableStateOf("Рок") }
    val presets = listOf("Обычный", "Рок", "Поп", "Бас", "Вокал", "Джаз", "Электро")

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Шапка экрана: стрелка назад, заголовок и кнопка сброса
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onBackClick),
                    contentAlignment = Alignment.Center
                ) {
                    BackIcon(tint = onSurface, iconSize = 22.dp)
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Эквалайзер",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = onSurface
                )
            }

            Text(
                text = "Сброс",
                style = MaterialTheme.typography.labelLarge,
                color = primaryColor,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        band60 = 0f
                        band230 = 0f
                        band910 = 0f
                        band3k6 = 0f
                        band14k = 0f
                        bassBoost = 0f
                        surroundSound = 0f
                        selectedPreset = "Обычный"
                    }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }

        Spacer(Modifier.height(18.dp))

        // Графическая визуализация частотного спектра
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Амплитудно-частотная характеристика",
                    style = MaterialTheme.typography.labelMedium,
                    color = onSurfaceVariant
                )

                Spacer(Modifier.height(14.dp))

                val bands = listOf(band60, band230, band910, band3k6, band14k)
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                ) {
                    val w = size.width
                    val h = size.height
                    val barWidth = 24.dp.toPx()
                    val gap = (w - (barWidth * bands.size)) / (bands.size + 1)

                    bands.forEachIndexed { i, value ->
                        val x = gap + i * (barWidth + gap)
                        // Нормализуем значение от -10..+10 к 0..1
                        val normalized = ((value + 10f) / 20f).coerceIn(0.1f, 1f)
                        val barHeight = h * normalized
                        val y = h - barHeight

                        drawRoundRect(
                            color = primaryColor,
                            topLeft = Offset(x, y),
                            size = Size(barWidth, barHeight),
                            cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(18.dp))

        // Горизонтальный скролл пресетов
        Text(
            text = "Пресеты звучания",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = onSurface
        )

        Spacer(Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            presets.forEach { preset ->
                val isSelected = preset == selectedPreset
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) primaryColor else surfaceVariant)
                        .clickable {
                            selectedPreset = preset
                            when (preset) {
                                "Обычный" -> { band60 = 0f; band230 = 0f; band910 = 0f; band3k6 = 0f; band14k = 0f }
                                "Рок" -> { band60 = 4f; band230 = 2f; band910 = -1f; band3k6 = 3f; band14k = 5f }
                                "Бас" -> { band60 = 8f; band230 = 6f; band910 = 1f; band3k6 = 0f; band14k = -1f; bassBoost = 0.8f }
                                "Вокал" -> { band60 = -2f; band230 = 0f; band910 = 5f; band3k6 = 3f; band14k = 1f }
                                "Поп" -> { band60 = 2f; band230 = 3f; band910 = 1f; band3k6 = 2f; band14k = 3f }
                                else -> { band60 = 1f; band230 = 1f; band910 = 1f; band3k6 = 1f; band14k = 1f }
                            }
                        }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = preset,
                        style = MaterialTheme.typography.labelLarge,
                        color = if (isSelected) Color.White else onSurface,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // 5 регуляторов частот
        Text(
            text = "Полосы частот",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = onSurface
        )

        Spacer(Modifier.height(10.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                BandSlider("60 Гц (Саб-бас)", band60) { band60 = it }
                BandSlider("230 Гц (Бас)", band230) { band230 = it }
                BandSlider("910 Гц (Средние)", band910) { band910 = it }
                BandSlider("3.6 кГц (Высокие средние)", band3k6) { band3k6 = it }
                BandSlider("14 кГц (Высокие)", band14k) { band14k = it }
            }
        }

        Spacer(Modifier.height(20.dp))

        // Звуковые эффекты: Bass Boost & Surround
        Text(
            text = "Звуковые эффекты",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = onSurface
        )

        Spacer(Modifier.height(10.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                EffectSlider(
                    title = "Усиление басов (Bass Boost)",
                    value = bassBoost,
                    onValueChange = { bassBoost = it }
                )
                Spacer(Modifier.height(12.dp))
                EffectSlider(
                    title = "Объёмный звук (Surround 3D)",
                    value = surroundSound,
                    onValueChange = { surroundSound = it }
                )
            }
        }
    }
}

// Ползунок отдельной полосы частот (-10 дБ .. +10 дБ)
@Composable
private fun BandSlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val onSurface = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant

    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, style = MaterialTheme.typography.bodyMedium, color = onSurface)
            Text(
                text = "${if (value > 0) "+" else ""}${value.toInt()} дБ",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = primaryColor
            )
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = -10f..10f,
            colors = SliderDefaults.colors(
                thumbColor = primaryColor,
                activeTrackColor = primaryColor
            )
        )
    }
}

// Ползунок эффекта (0% .. 100%)
@Composable
private fun EffectSlider(
    title: String,
    value: Float,
    onValueChange: (Float) -> Unit
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val onSurface = MaterialTheme.colorScheme.onSurface

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = title, style = MaterialTheme.typography.bodyMedium, color = onSurface)
            Text(
                text = "${(value * 100).toInt()}%",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = primaryColor
            )
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 0f..1f,
            colors = SliderDefaults.colors(
                thumbColor = primaryColor,
                activeTrackColor = primaryColor
            )
        )
    }
}

@Preview(name = "Эквалайзер (Тёмная тема)", showBackground = true, backgroundColor = 0xFF212121)
@Composable
private fun EqualizerScreenDarkPreview() {
    SharedMusicTheme(darkTheme = true) {
        EqualizerScreen(onBackClick = {})
    }
}

@Preview(name = "Эквалайзер (Светлая тема)", showBackground = true)
@Composable
private fun EqualizerScreenLightPreview() {
    SharedMusicTheme(darkTheme = false) {
        EqualizerScreen(onBackClick = {})
    }
}
