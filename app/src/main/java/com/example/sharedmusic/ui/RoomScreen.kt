package com.example.sharedmusic.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.collectAsState
import com.example.sharedmusic.connection.ConnectionManager
import com.example.sharedmusic.connection.RoomDevice
import com.example.sharedmusic.connection.RoomTrack
import com.example.sharedmusic.model.Song
import com.example.sharedmusic.viewmodel.MusicViewModel
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.sharedmusic.ui.components.ShareIcon
import com.example.sharedmusic.ui.theme.SharedMusicTheme

// Сообщение чата комнаты
data class RoomChatMessage(
    val senderName: String,
    val text: String,
    val time: String,
    val isMe: Boolean
)

// Состояние подключения к комнате
sealed interface RoomConnection {
    // Комната не подключена (начальное состояние)
    data object Disconnected : RoomConnection
    // Комната подключена (код, роль хост/гость)
    data class Connected(val code: String, val isHost: Boolean) : RoomConnection
}

// Экран комнаты для синхронного прослушивания:
// — начальное состояние: создание/вход в комнату
// — подключённое состояние: участники, мини-плеер, чат
@Composable
fun RoomScreen(
    connectionManager: ConnectionManager,
    musicViewModel: MusicViewModel,
    isHost: Boolean,
    onRoleSelected: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val onSurface = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant
    val surface = MaterialTheme.colorScheme.surface
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant

    // Передача музыки: данные комнаты и плеера получаем от механики партнёра.
    val roomCode = connectionManager.currentRoomCode
    val songs by musicViewModel.songs.collectAsState()
    val currentSong by musicViewModel.currentSong.collectAsState()
    val isPlaying by musicViewModel.isPlaying.collectAsState()

    // Поле ввода кода для входа
    var joinCodeInput by remember { mutableStateOf("") }

    // Сообщения чата (пусты до подключения)
    var chatMessages by remember { mutableStateOf(listOf<RoomChatMessage>()) }
    var chatInput by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            // Отступ от верхней шторки
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Шапка экрана
        Text(
            text = "Комната",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = onSurface,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(Modifier.height(16.dp))

        Text(connectionManager.statusMessage, color = onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall)
        if (connectionManager.isBusy) {
            if (connectionManager.progressText.isNotBlank()) Text(connectionManager.progressText)
            LinearProgressIndicator(progress = connectionManager.progress, modifier = Modifier.fillMaxWidth())
        }
        Spacer(Modifier.height(12.dp))
        if (roomCode == null) {
            DisconnectedContent(
                onCreateRoom = { onRoleSelected(true); connectionManager.createRoom() },
                joinCodeInput = joinCodeInput,
                onJoinCodeChange = { joinCodeInput = it },
                onJoinRoom = { onRoleSelected(false); connectionManager.joinRoom(joinCodeInput) },
                isBusy = connectionManager.isBusy,
                primaryColor = primaryColor,
                onSurface = onSurface,
                onSurfaceVariant = onSurfaceVariant,
                surface = surface,
                surfaceVariant = surfaceVariant
            )
        } else {
            ConnectedContent(
                roomCode = roomCode,
                isHost = isHost,
                devices = connectionManager.connectedDevices,
                roomTracks = connectionManager.roomTracks,
                songs = songs,
                currentSong = currentSong,
                isPlaying = isPlaying,
                isBusy = connectionManager.isBusy,
                onShareSong = connectionManager::shareSong,
                onDownloadTrack = { track ->
                    connectionManager.downloadTrack(track) { file ->
                        musicViewModel.addSongFromFile(file, track.title, track.id)
                    }
                },
                onToggleMusic = musicViewModel::toggleMusic,
                chatMessages = chatMessages,
                chatInput = chatInput,
                onChatInputChange = { chatInput = it },
                onSendMessage = {
                    val trimmed = chatInput.trim()
                    if (trimmed.isNotEmpty() && trimmed.length <= 500) {
                        chatMessages = chatMessages + RoomChatMessage("Вы", trimmed, "Сейчас", true)
                        chatInput = ""
                    }
                },
                onLeaveRoom = {
                    connectionManager.leaveRoom()
                    chatMessages = emptyList()
                },
                primaryColor = primaryColor,
                onSurface = onSurface,
                onSurfaceVariant = onSurfaceVariant,
                surface = surface,
                surfaceVariant = surfaceVariant
            )
        }
    }
}

// Содержимое при отключённой комнате: создание и вход
@Composable
private fun DisconnectedContent(
    onCreateRoom: () -> Unit,
    joinCodeInput: String,
    onJoinCodeChange: (String) -> Unit,
    onJoinRoom: () -> Unit,
    isBusy: Boolean = false,
    primaryColor: Color,
    onSurface: Color,
    onSurfaceVariant: Color,
    surface: Color,
    surfaceVariant: Color
) {
    // Иконка-заглушка
    Box(
        modifier = Modifier
            .size(80.dp)
            .clip(CircleShape)
            .background(surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        Text("🎵", fontSize = 36.sp)
    }

    Spacer(Modifier.height(20.dp))

    Text(
        text = "Комната не подключена",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = onSurface
    )
    Text(
        text = "Создайте комнату или войдите по коду",
        style = MaterialTheme.typography.bodyMedium,
        color = onSurfaceVariant
    )

    Spacer(Modifier.height(28.dp))

    // Кнопка «Создать комнату»
    Button(
        onClick = onCreateRoom,
        enabled = !isBusy,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
    ) {
        Text(
            text = "Создать комнату",
            fontWeight = FontWeight.Bold
        )
    }

    Spacer(Modifier.height(16.dp))

    // Разделитель
    Text(
        text = "или",
        style = MaterialTheme.typography.bodySmall,
        color = onSurfaceVariant
    )

    Spacer(Modifier.height(16.dp))

    // Поле ввода кода для входа
    OutlinedTextField(
        value = joinCodeInput,
        onValueChange = { onJoinCodeChange(it.uppercase().filter { char -> char in "ABCDEFGHJKLMNPQRSTUVWXYZ23456789" }.take(4)) },
        label = { Text("Код комнаты") },
        placeholder = { Text("Например: 7K9X") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = primaryColor,
            focusedLabelColor = primaryColor,
            cursorColor = primaryColor
        )
    )

    Spacer(Modifier.height(12.dp))

    Button(
        onClick = onJoinRoom,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(16.dp),
        enabled = !isBusy && joinCodeInput.length == 4,
        colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
    ) {
        Text(
            text = "Войти по коду",
            fontWeight = FontWeight.Bold
        )
    }
}

// Содержимое при подключённой комнате: участники, мини-плеер, чат
@Composable
private fun ConnectedContent(
    roomCode: String,
    isHost: Boolean,
    devices: List<RoomDevice>,
    roomTracks: List<RoomTrack>,
    songs: List<Song>,
    currentSong: Song?,
    isPlaying: Boolean,
    isBusy: Boolean,
    onShareSong: (Song) -> Unit,
    onDownloadTrack: (RoomTrack) -> Unit,
    onToggleMusic: () -> Unit,
    chatMessages: List<RoomChatMessage>,
    chatInput: String,
    onChatInputChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    onLeaveRoom: () -> Unit,
    primaryColor: Color,
    onSurface: Color,
    onSurfaceVariant: Color,
    surface: Color,
    surfaceVariant: Color
) {
    // Плашка с кодом комнаты
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "Комната #$roomCode",
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

        // Кнопка копирования кода
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(surfaceVariant)
                .clickable { /* Копировать код в буфер обмена */ }
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

    // Карточка участников
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
                text = "Участники",
                style = MaterialTheme.typography.labelMedium,
                color = onSurfaceVariant
            )

            Spacer(Modifier.height(16.dp))

            if (devices.isEmpty()) {
                Text("Ожидание участников…", color = onSurfaceVariant)
            } else {
                devices.forEach { device ->
                    Text(device.name, color = onSurface, modifier = Modifier.padding(vertical = 4.dp))
                }
            }
        }
    }

    Spacer(Modifier.height(16.dp))

    // Мини-плеер (заглушка — трек не выбран)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = currentSong?.title ?: "Трек не выбран",
                style = MaterialTheme.typography.bodyMedium,
                color = onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    Spacer(Modifier.height(16.dp))

    // Передача музыки: сначала отправьте песню, затем запустите её у хозяина.
    if (isHost && currentSong != null) {
        Button(onClick = onToggleMusic, enabled = !isBusy, modifier = Modifier.fillMaxWidth()) {
            Text(if (isPlaying) "Пауза" else "Воспроизвести")
        }
    } else if (!isHost) {
        Text("Управляет хозяин комнаты", color = onSurfaceVariant)
    }
    Spacer(Modifier.height(12.dp))
    Text("Музыка в комнате", style = MaterialTheme.typography.titleSmall, color = onSurface)
    if (roomTracks.isEmpty()) Text("Песен пока нет", color = onSurfaceVariant)
    roomTracks.forEach { track ->
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(track.title, modifier = Modifier.weight(1f), color = onSurface, maxLines = 1)
            if (songs.none { it.id == track.id }) {
                Button(onClick = { onDownloadTrack(track) }, enabled = !isBusy) { Text("Скачать") }
            }
        }
    }
    if (isHost) {
        Spacer(Modifier.height(12.dp))
        Text("Мои песни", style = MaterialTheme.typography.titleSmall, color = onSurface)
        Text("Сначала отправьте песню в комнату, затем запустите её в плеере.",
            style = MaterialTheme.typography.bodySmall, color = onSurfaceVariant)
        if (songs.isEmpty()) Text("Добавьте музыку на главном экране", color = onSurfaceVariant)
        songs.forEach { song ->
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(song.title, modifier = Modifier.weight(1f), color = onSurface, maxLines = 1)
                Button(onClick = { onShareSong(song) }, enabled = !isBusy) { Text("Отправить") }
            }
        }
    }
    Spacer(Modifier.height(16.dp))

    // Чат — только визуальная заготовка, сообщения не передаются.
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Чат (только на этом устройстве)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = onSurface
            )

            Spacer(Modifier.height(8.dp))

            // Сообщения чата (пусто по умолчанию)
            if (chatMessages.isEmpty()) {
                Text(
                    text = "Сообщений пока нет",
                    style = MaterialTheme.typography.bodySmall,
                    color = onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                )
            } else {
                chatMessages.forEach { msg ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = if (msg.isMe) Arrangement.End else Arrangement.Start
                    ) {
                        Column(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (msg.isMe) primaryColor.copy(alpha = 0.15f) else surfaceVariant)
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = msg.senderName,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (msg.isMe) primaryColor else onSurface
                            )
                            Text(
                                text = msg.text,
                                style = MaterialTheme.typography.bodySmall,
                                color = onSurface
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // Поле ввода сообщения
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = chatInput,
                    onValueChange = onChatInputChange,
                    placeholder = { Text("Сообщение…") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryColor,
                        cursorColor = primaryColor
                    )
                )
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = onSendMessage,
                    shape = CircleShape,
                    enabled = chatInput.trim().isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text("→", fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    Spacer(Modifier.height(16.dp))

    // Кнопка выхода из комнаты
    Button(
        onClick = onLeaveRoom,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
    ) {
        Text("Покинуть комнату", fontWeight = FontWeight.Bold)
    }
}

// Аватар участника комнаты
@Composable
private fun ParticipantAvatar(
    name: String,
    role: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = name.firstOrNull()?.uppercase() ?: "?",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = role,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// Превью: начальное состояние (тёмная тема)
@Preview(name = "Комната: Начальное (Тёмная)", showBackground = true, backgroundColor = 0xFF212121)
@Composable
private fun RoomScreenDisconnectedDarkPreview() {
    SharedMusicTheme(darkTheme = true) {
        DisconnectedContent(
            onCreateRoom = {}, joinCodeInput = "", onJoinCodeChange = {}, onJoinRoom = {},
            primaryColor = MaterialTheme.colorScheme.primary,
            onSurface = MaterialTheme.colorScheme.onSurface,
            onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant,
            surface = MaterialTheme.colorScheme.surface,
            surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

// Превью: начальное состояние (светлая тема)
@Preview(name = "Комната: Начальное (Светлая)", showBackground = true)
@Composable
private fun RoomScreenDisconnectedLightPreview() {
    SharedMusicTheme(darkTheme = false) {
        DisconnectedContent(
            onCreateRoom = {}, joinCodeInput = "", onJoinCodeChange = {}, onJoinRoom = {},
            primaryColor = MaterialTheme.colorScheme.primary,
            onSurface = MaterialTheme.colorScheme.onSurface,
            onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant,
            surface = MaterialTheme.colorScheme.surface,
            surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}