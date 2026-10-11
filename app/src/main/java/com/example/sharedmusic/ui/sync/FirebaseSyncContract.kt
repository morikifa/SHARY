package com.example.sharedmusic.ui.sync

// ═══════════════════════════════════════════════════════════════════════
// КОНТРАКТ СИНХРОНИЗАЦИИ ЧЕРЕЗ FIREBASE
// Этот файл описывает архитектуру синхронизации для будущей реализации.
// Здесь нет рабочего кода Firebase — только документация и структуры данных.
// ═══════════════════════════════════════════════════════════════════════

// Структура базы данных Firebase Realtime Database:
//
// /rooms/{roomId}/info
//   - code: String          — 6-значный код комнаты (без 0, O, 1, I)
//   - hostId: String        — UID хоста
//   - trackId: String       — идентификатор текущего трека
//   - createdAt: Long       — SERVER_TIMESTAMP
//
// /rooms/{roomId}/playback
//   - isPlaying: Boolean    — играет ли музыка
//   - positionMs: Long      — текущая позиция воспроизведения
//   - updatedAt: Long       — SERVER_TIMESTAMP (серверное время обновления)
//
// /rooms/{roomId}/participants/{userId}
//   - nickname: String      — никнейм участника
//   - isHost: Boolean       — является ли хостом
//   - online: Boolean       — онлайн-статус
//   - lastSeen: Long        — SERVER_TIMESTAMP
//   Присутствие: через onDisconnect().remove() или onDisconnect().set(online=false)
//
// /rooms/{roomId}/chat/{messageId}
//   - senderId: String      — UID отправителя (совпадает с auth.uid)
//   - senderName: String    — никнейм отправителя
//   - text: String          — текст сообщения (макс. 500 символов)
//   - timestamp: Long       — SERVER_TIMESTAMP

// Алгоритм синхронизации для гостя:
//
// 1. Гость слушает изменения /rooms/{roomId}/playback
// 2. При получении нового состояния вычисляет ожидаемую позицию:
//
//    expectedPositionMs = positionMs + (currentServerTime - updatedAt)
//
//    где currentServerTime = серверное время + offset (вычисляется один раз при подключении)
//
// 3. Вычисляет дрейф: drift = |localPosition - expectedPositionMs|
//
// 4. Пороги дрейфа:
//    — до 150 мс:  игнорировать (терпимый дрейф)
//    — 150–800 мс: мягкая коррекция (небольшой seek или подстройка скорости на 3–5%)
//    — более 800 мс: выравнивающий seekTo(expectedPositionMs)
//
// 5. Проверять состояние каждые 1–2 секунды во время воспроизведения
//
// Важно: аудиофайлы НИКОГДА не загружаются на сервер.
// Сервер хранит только метаданные и состояние синхронизации.

// Идентичность локального трека:
//
// Предпочтительный trackId — детерминированная хеш-сумма от содержимого файла.
// Если читать байты файла нельзя, допустим слабый fallback:
//   trackId = SHA-256(displayName + sizeBytes + durationMs) + флаг weak=true
//
// Предупреждение: разные файлы с одинаковыми метаданными могут дать совпадение.

// Правила безопасности Firebase (требуют проверки тестами):
//
// 1. Читать и писать могут только аутентифицированные пользователи.
// 2. Пользователь меняет только собственный профиль и запись участия.
// 3. messages.senderId совпадает с auth.uid, длина сообщения ≤ 500 символов.
// 4. Состояние воспроизведения меняет только хост.
// 5. Чтение комнаты ограничено её участниками.
// 6. Типы и размеры входящих данных проверяются.

// Минимальная структура данных для UI-контракта синхронизации
data class SyncPlaybackState(
    val isPlaying: Boolean = false,
    val positionMs: Long = 0L,
    val updatedAt: Long = 0L // Серверное время обновления
)

data class SyncParticipant(
    val userId: String = "",
    val nickname: String = "",
    val isHost: Boolean = false,
    val isOnline: Boolean = false
)

data class SyncChatMessage(
    val messageId: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val text: String = "",
    val timestamp: Long = 0L
)

// Статус синхронизации для отображения в UI
sealed interface SyncStatus {
    // Синхронизировано (дрейф < 150 мс)
    data object Synced : SyncStatus
    // Догоняем (мягкая коррекция)
    data object CatchingUp : SyncStatus
    // Трек недоступен на устройстве гостя
    data object TrackUnavailable : SyncStatus
    // Синхронизация не требуется (локальный режим)
    data object None : SyncStatus
}