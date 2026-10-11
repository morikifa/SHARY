package com.example.sharedmusic.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sharedmusic.ui.components.EqualizerIcon
import com.example.sharedmusic.ui.components.SongRow
import com.example.sharedmusic.ui.theme.SharedMusicTheme

// Экран профиля пользователя с переключением темы и настройками звука
@Composable
fun ProfileScreen(
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    onOpenEqualizerClick: () -> Unit,
    onAddMusicClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val onSurface = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant
    val surface = MaterialTheme.colorScheme.surface

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Шапка профиля
        Text(
            text = "Профиль",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = onSurface,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(Modifier.height(20.dp))

        // Аватарка и информация о пользователе
        Box(
            modifier = Modifier
                .size(84.dp)
                .clip(CircleShape)
                .background(primaryColor.copy(alpha = 0.2f))
                .border(3.dp, primaryColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "M",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = primaryColor
            )
        }

        Spacer(Modifier.height(10.dp))

        Text(
            text = "morikifa",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = onSurface
        )
        Text(
            text = "ID: #8392 • Локальный профиль",
            style = MaterialTheme.typography.bodySmall,
            color = onSurfaceVariant
        )

        Spacer(Modifier.height(18.dp))

        // Карточка статистики
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = surface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(value = "5", label = "Треков")
                StatItem(value = "12 ч", label = "В комнатах")
                StatItem(value = "24", label = "Синхронов")
            }
        }

        Spacer(Modifier.height(20.dp))

        // Раздел «Настройки внешнего вида и звука»
        Text(
            text = "Настройки",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = onSurface,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(Modifier.height(10.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Переключатель темы: Тёмная / Светлая
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Тёмная тема",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = onSurface
                        )
                        Text(
                            text = if (isDarkTheme) "Включена тёмная палитра #212121" else "Включена светлая тема",
                            style = MaterialTheme.typography.bodySmall,
                            color = onSurfaceVariant
                        )
                    }

                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = onThemeChange,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = primaryColor
                        )
                    )
                }

                Spacer(Modifier.height(16.dp))

                // Пункт меню: Эквалайзер (переход на экран эквалайзера)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(onClick = onOpenEqualizerClick)
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(primaryColor.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            EqualizerIcon(tint = primaryColor, iconSize = 18.dp)
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Эквалайзер звука",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = onSurface
                            )
                            Text(
                                text = "5 полос, басы и объёмный звук",
                                style = MaterialTheme.typography.bodySmall,
                                color = onSurfaceVariant
                            )
                        }
                    }

                    Text(
                        text = "›",
                        style = MaterialTheme.typography.titleLarge,
                        color = onSurfaceVariant
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // Раздел «Локальная библиотека»
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Скачанные треки",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = onSurface
            )

            Button(
                onClick = onAddMusicClick,
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
            ) {
                Text("+ Загрузить", style = MaterialTheme.typography.labelMedium)
            }
        }

        Spacer(Modifier.height(10.dp))

        // Примеры треков
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = surface)
        ) {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                SongRow(
                    title = "Осень",
                    artist = "Shared by You",
                    duration = "3:24",
                    coverColors = listOf(Color(0xFFF64A8A), Color(0xFF323232)),
                    onClick = {}
                )
                SongRow(
                    title = "Мелодия",
                    artist = "Shared by You",
                    duration = "2:50",
                    coverColors = listOf(Color(0xFF323232), Color(0xFFF64A8A)),
                    onClick = {}
                )
                SongRow(
                    title = "Ритм",
                    artist = "Shared by You",
                    duration = "4:12",
                    coverColors = listOf(Color(0xFFF64A8A), Color(0xFF212121)),
                    onClick = {}
                )
            }
        }
    }
}

// Элемент статистики
@Composable
private fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(name = "Профиль (Тёмная тема)", showBackground = true, backgroundColor = 0xFF212121)
@Composable
private fun ProfileScreenDarkPreview() {
    SharedMusicTheme(darkTheme = true) {
        ProfileScreen(
            isDarkTheme = true,
            onThemeChange = {},
            onOpenEqualizerClick = {},
            onAddMusicClick = {}
        )
    }
}

@Preview(name = "Профиль (Светлая тема)", showBackground = true)
@Composable
private fun ProfileScreenLightPreview() {
    SharedMusicTheme(darkTheme = false) {
        ProfileScreen(
            isDarkTheme = false,
            onThemeChange = {},
            onOpenEqualizerClick = {},
            onAddMusicClick = {}
        )
    }
}
