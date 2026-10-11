package com.example.sharedmusic.ui.components

import android.provider.Settings
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import com.example.sharedmusic.ui.theme.VinylBlack
import com.example.sharedmusic.ui.theme.VinylGroove

// Скорость вращения в градусах в секунду (примерно 33 оборота в минуту)
private const val DEGREES_PER_SECOND = 90f

@Composable
fun VinylRecord(
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    // Обложка загруженной песни. Пока null — показываем розовую заглушку
    cover: Painter? = null
) {
    val context = LocalContext.current
    // Если пользователь выключил анимации в системных настройках, пластинка не крутится
    val animationsOff = Settings.Global.getFloat(
        context.contentResolver,
        Settings.Global.ANIMATOR_DURATION_SCALE,
        1f
    ) == 0f

    // Текущий угол хранится здесь. rememberSaveable сохраняет его при пересоздании экрана,
    // поэтому вращение не сбрасывается в ноль.
    var angle by rememberSaveable { mutableStateOf(0f) }

    // Эффект запускается только когда играет музыка. При паузе корутина отменяется,
    // и угол остаётся на месте.
    LaunchedEffect(isPlaying, animationsOff) {
        if (!isPlaying || animationsOff) return@LaunchedEffect
        var lastFrameNanos = withFrameNanos { it }
        while (true) {
            withFrameNanos { now ->
                // Сколько секунд прошло с прошлого кадра
                val seconds = (now - lastFrameNanos) / 1_000_000_000f
                lastFrameNanos = now
                angle = (angle + seconds * DEGREES_PER_SECOND) % 360f
            }
        }
    }

    // Весь диск (дорожки, обложка, отверстие) поворачивается одним слоем
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .graphicsLayer { rotationZ = angle },
        contentAlignment = Alignment.Center
    ) {
        // Дорожки: тонкие кольца от края к центру
        Canvas(Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = size.minDimension / 2f
            drawCircle(color = VinylBlack, radius = radius, center = center)
            var grooveRadius = radius * 0.95f
            while (grooveRadius > radius * 0.4f) {
                drawCircle(
                    color = VinylGroove,
                    radius = grooveRadius,
                    center = center,
                    style = Stroke(width = 1.dp.toPx())
                )
                grooveRadius -= radius * 0.07f
            }
        }

        // Центральный круг: обложка песни или розовая заглушка
        if (cover != null) {
            Image(
                painter = cover,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize(0.72f)
                    .clip(CircleShape)
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize(0.72f)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            )
        }

        // Отверстие для шпинделя
        Box(
            modifier = Modifier
                .fillMaxSize(0.08f)
                .clip(CircleShape)
                .background(VinylBlack)
        )
    }
}
