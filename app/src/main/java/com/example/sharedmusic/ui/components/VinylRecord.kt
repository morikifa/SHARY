package com.example.sharedmusic.ui.components

import android.provider.Settings
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.withFrameNanos
import androidx.compose.runtime.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.sharedmusic.ui.theme.VinylBlack
import com.example.sharedmusic.ui.theme.VinylGroove
import com.example.sharedmusic.ui.theme.VinylLabel

// Скорость вращения в градусах в секунду (примерно 33 оборота в минуту)
private const val DEGREES_PER_SECOND = 90f

@Composable
fun VinylRecord(
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    // Если пользователь выключил анимации в системных настройках, пластинка не крутится
    val animationsOff = Settings.Global.getFloat(
        context.contentResolver,
        Settings.Global.ANIMATOR_DURATION_SCALE,
        1f
    ) == 0f

    // Текущий угол хранится здесь, а не внутри анимации.
    // rememberSaveable сохраняет его при пересоздании экрана, поэтому вращение не сбрасывается в ноль.
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

    Canvas(
        modifier = modifier
            .aspectRatio(1f)
            // Поворот применяется на уровне слоя отрисовки, без перерисовки всего Composable
            .graphicsLayer { rotationZ = angle }
    ) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = size.minDimension / 2f

        // Чёрный диск
        drawCircle(color = VinylBlack, radius = radius, center = center)

        // Дорожки: несколько тонких колец от края к центру
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

        // Этикетка по центру (заглушка вместо обложки)
        drawCircle(color = VinylLabel, radius = radius * 0.36f, center = center)
        // Отверстие для шпинделя
        drawCircle(color = VinylBlack, radius = radius * 0.04f, center = center)
    }
}
