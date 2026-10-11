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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sharedmusic.ui.components.FadingText
import com.example.sharedmusic.ui.components.PlayerIcon
import com.example.sharedmusic.ui.components.PlayerIconKind
import com.example.sharedmusic.ui.components.ShareIcon
import com.example.sharedmusic.ui.theme.SharedMusicTheme

// Сообщение чата комнаты
data class RoomChatMessage(
    val senderName: String,
    val text: String,
    val time: String,
    val isMe: Boolean
)

// Экран совместной комнаты прослушивания
@Composable
fun RoomScreen(
    roomCode: String = "SH-4821",
    isHost: Boolean = true,
    currentTrackTitle: String = "Осень",
    currentTrackArtist: String = "Shared by You",
    isPlaying: Boolean = true,
    progress: Float = 0.42f,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val onSurface = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant
    val surface = MaterialTheme.colorScheme.surface
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant

    var chatInput by remember { mutableStateOf("") }
    var chatMessages by remember {
        mutableStateOf(
            listOf(
                RoomChatMessage("Анна", "Привет! Звук отличный 🔥", "22:15", isMe = false),
                RoomChatMessage("Вы", "Да, синхронизация работает идеально!", "22:16", isMe = true),
                RoomChatMessage("Анна", "Включи следующий трек после этого)", "22:17", isMe = false)
            )
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Шапка экрана: код комнаты и кнопка «Поделиться кодом»
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Комната прослушивания",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = onSurface
                )
                Text(
                    text = if (isHost) "Вы организатор (Хост)" else "Вы слушатель (Гость)",
                    style = MaterialTheme.typography.bodySmall,
                    color = primaryColor
                )
            }

            // Плашка с кодом комнаты
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(surfaceVariant)
                    .clickable { /* Копировать код */ }
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "#$roomCode",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = primaryColor
                )
                Spacer(Modifier.width(6.dp))
                ShareIcon(tint = onSurfaceVariant, iconSize = 16.dp)
            }
        }

        Spacer(Modifier.height(20.dp))

        // Карточка участников с линией синхронизации между ними
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = surface)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Участники в синхроне",
                    style = MaterialTheme.typography.labelMedium,
                    color = onSurfaceVariant
                )

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Аватар Хоста (Вы)
                    UserAvatar(
                        name = "Вы",
                        role = "Хост",
                        isHost = true,
                        color = primaryColor
                    )

                    // Индикатор связи между участниками
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(70.dp)
                                .height(3.dp)
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(primaryColor, Color(0xFF4CAF50))
                                    ),
                                    shape = RoundedCornerShape(2.dp)
                                )
                        )
                        Spacer(Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF4CAF50))
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "18 мс",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                color = Color(0xFF4CAF50),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Аватар Гостя
                    UserAvatar(
                        name = "Анна",
                        role = "Гость",
                        isHost = false,
                        color = Color(0xFF8E24AA)
                    )
                }

                Spacer(Modifier.height(12.dp))

                // Подпись статуса синхронизации
                Text(
                    text = "● Синхронизировано в реальном времени",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF4CAF50),
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Мини-плеер комнаты
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Мини-пластинка
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF151515)),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(primaryColor)
                        )
                    }

                    Spacer(Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        FadingText(
                            text = currentTrackTitle,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = onSurface
                        )
                        Spacer(Modifier.height(2.dp))
                        FadingText(
                            text = currentTrackArtist,
                            style = MaterialTheme.typography.bodySmall,
                            color = onSurfaceVariant
                        )
                    }

                    // Статус управления
                    if (!isHost) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(surfaceVariant)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Хост управляет",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                color = onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Полоса прогресса
                Slider(
                    value = progress,
                    onValueChange = {},
                    enabled = isHost,
                    modifier = Modifier.fillMaxWidth(),
                    colors = SliderDefaults.colors(
                        thumbColor = primaryColor,
                        activeTrackColor = primaryColor
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("1:24", style = MaterialTheme.typography.labelSmall, color = onSurfaceVariant)
                    Text("3:18", style = MaterialTheme.typography.labelSmall, color = onSurfaceVariant)
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // Блок текстового чата комнаты
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Чат комнаты",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = onSurface
                )

                Spacer(Modifier.height(12.dp))

                // Список сообщений
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    chatMessages.forEach { msg ->
                        ChatMessageItem(msg = msg)
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Поле ввода сообщения
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = chatInput,
                        onValueChange = { chatInput = it },
                        placeholder = {
                            Text("Сообщение...", style = MaterialTheme.typography.bodySmall)
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(24.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = primaryColor,
                            unfocusedBorderColor = surfaceVariant
                        )
                    )

                    Spacer(Modifier.width(8.dp))

                    Button(
                        onClick = {
                            if (chatInput.isNotBlank()) {
                                chatMessages = chatMessages + RoomChatMessage("Вы", chatInput.trim(), "Сейчас", isMe = true)
                                chatInput = ""
                            }
                        },
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                        modifier = Modifier.size(46.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                    ) {
                        PlayerIcon(kind = PlayerIconKind.PLAY, tint = Color.White, iconSize = 16.dp)
                    }
                }
            }
        }
    }
}

// Аватарка участника комнаты с подписью роли
@Composable
private fun UserAvatar(
    name: String,
    role: String,
    isHost: Boolean,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.2f))
                .border(2.dp, color, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = name.take(1).uppercase(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = role,
            style = MaterialTheme.typography.labelSmall,
            color = if (isHost) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// Элемент сообщения чата
@Composable
private fun ChatMessageItem(msg: RoomChatMessage) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    val onSurface = MaterialTheme.colorScheme.onSurface

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (msg.isMe) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 14.dp,
                        topEnd = 14.dp,
                        bottomStart = if (msg.isMe) 14.dp else 2.dp,
                        bottomEnd = if (msg.isMe) 2.dp else 14.dp
                    )
                )
                .background(if (msg.isMe) primaryColor.copy(alpha = 0.2f) else surfaceVariant)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            if (!msg.isMe) {
                Text(
                    text = msg.senderName,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = primaryColor
                )
                Spacer(Modifier.height(2.dp))
            }
            Text(
                text = msg.text,
                style = MaterialTheme.typography.bodySmall,
                color = onSurface
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = msg.time,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 9.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.End,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

@Preview(name = "Комната (Тёмная тема)", showBackground = true, backgroundColor = 0xFF212121)
@Composable
private fun RoomScreenDarkPreview() {
    SharedMusicTheme(darkTheme = true) {
        RoomScreen()
    }
}

@Preview(name = "Комната (Светлая тема)", showBackground = true)
@Composable
private fun RoomScreenLightPreview() {
    SharedMusicTheme(darkTheme = false) {
        RoomScreen(isHost = false)
    }
}
