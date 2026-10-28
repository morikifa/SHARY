package com.example.sharedmusic.ui.sync

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import com.example.sharedmusic.connection.ConnectionManager
import com.example.sharedmusic.connection.RemotePlaybackCommand
import com.example.sharedmusic.viewmodel.MusicViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicReference

// Передача синхронизации: связка плеера и механики партнёра живёт вне экранов.
@Composable
fun RoomPlaybackSync(
    connectionManager: ConnectionManager,
    musicViewModel: MusicViewModel,
    canControlPlayback: Boolean
) {
    val canControl by rememberUpdatedState(canControlPlayback)
    val scope = rememberCoroutineScope()
    val latestCommand = remember { AtomicReference<RemotePlaybackCommand?>(null) }
    val pendingTracks = remember { ConcurrentHashMap.newKeySet<String>() }

    DisposableEffect(musicViewModel, connectionManager) {
        musicViewModel.setPlaybackPublisher { song, positionMs, isPlaying ->
            // Гость не отправляет команды, даже если интерфейс случайно их вызовет.
            if (canControl) {
                connectionManager.publishPlaybackState(song, positionMs, isPlaying)
            }
        }
        connectionManager.setPlaybackListener { command ->
            latestCommand.set(command)
            if (!musicViewModel.applyRemotePlayback(command)) {
                val trackId = command.trackId
                if (pendingTracks.add(trackId)) {
                    scope.launch {
                        try {
                            repeat(120) {
                                val latest = latestCommand.get() ?: return@launch
                                if (latest.trackId != trackId ||
                                    !connectionManager.isLatestPlaybackCommand(latest)
                                ) return@launch
                                if (musicViewModel.applyRemotePlayback(latest)) return@launch
                                val track = connectionManager.roomTracks.firstOrNull { it.id == trackId }
                                if (track != null && !connectionManager.isBusy) {
                                    connectionManager.downloadTrack(track) { file ->
                                        musicViewModel.addSongFromFile(file, track.title, track.id)
                                        val newest = latestCommand.get()
                                        if (newest != null && newest.trackId == track.id &&
                                            connectionManager.isLatestPlaybackCommand(newest)
                                        ) musicViewModel.applyRemotePlayback(newest)
                                    }
                                    return@launch
                                }
                                delay(250L)
                            }
                        } finally {
                            pendingTracks.remove(trackId)
                        }
                    }
                }
            }
        }
        onDispose {
            musicViewModel.setPlaybackPublisher(null)
            connectionManager.setPlaybackListener(null)
            connectionManager.dispose()
        }
    }

    // Контрольная позиция, как в исходном проекте партнёра.
    LaunchedEffect(connectionManager, musicViewModel) {
        while (true) {
            delay(3_000L)
            val song = musicViewModel.currentSong.value
            if (canControl && song != null && musicViewModel.isPlaying.value) {
                connectionManager.publishHeartbeat(song, musicViewModel.positionMs.value)
            }
        }
    }
}
