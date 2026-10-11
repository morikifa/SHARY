package com.example.sharedmusic.ui.components

import android.provider.Settings
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

// Скорость автоматического вращения: 90 градусов в секунду (~33 об/мин)
private const val DEGREES_PER_SECOND = 90f

// Виниловая пластинка по референсу:
// — концентрические волнистые дорожки (6 колец с синусоидальной модуляцией)
// — розовый центр с белой нотой (38% диаметра)
// — анимация вращения при воспроизведении
// — перемотка трека вращением пластинки пальцем
@Composable
fun VinylRecord(
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    // Обложка трека: если есть — показываем в центре, иначе розовый круг с нотой
    cover: Painter? = null,
    // Колбэк перемотки при ручном вращении (дельта угла в градусах)
    onScrub: ((deltaDegrees: Float) -> Unit)? = null
) {
    val context = LocalContext.current

    // Проверяем системную настройку отключения анимаций (режим энергосбережения)
    val animationsOff = Settings.Global.getFloat(
        context.contentResolver,
        Settings.Global.ANIMATOR_DURATION_SCALE,
        1f
    ) == 0f

    // Угол поворота пластинки (rememberSaveable сохраняет при пересоздании экрана)
    var angle by rememberSaveable { mutableStateOf(0f) }

    // Флаг: крутит ли пользователь пластинку пальцем
    var isDragging by remember { mutableStateOf(false) }

    // Предыдущий угол касания для расчёта дельты вращения
    var lastTouchAngle by remember { mutableFloatStateOf(0f) }

    // Автоматическое вращение при воспроизведении (останавливается при паузе)
    LaunchedEffect(isPlaying, animationsOff, isDragging) {
        if (!isPlaying || animationsOff || isDragging) return@LaunchedEffect
        var lastFrameNanos = withFrameNanos { it }
        while (true) {
            withFrameNanos { now ->
                val seconds = (now - lastFrameNanos) / 1_000_000_000f
                lastFrameNanos = now
                angle = (angle + seconds * DEGREES_PER_SECOND) % 360f
            }
        }
    }

    val primaryColor = MaterialTheme.colorScheme.primary

    // Контейнер пластинки с перехватом жестов вращения пальцем
    Box(
        modifier = modifier
            .aspectRatio(1f)
            // Перехват жестов для ручной перемотки вращением
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        isDragging = true
                        val cx = size.width / 2f
                        val cy = size.height / 2f
                        lastTouchAngle = atan2(offset.y - cy, offset.x - cx) * 180f / PI.toFloat()
                    },
                    onDragEnd = { isDragging = false },
                    onDragCancel = { isDragging = false },
                    onDrag = { change, _ ->
                        change.consume()
                        val cx = size.width / 2f
                        val cy = size.height / 2f
                        // Вычисляем текущий угол пальца относительно центра
                        val cur = atan2(change.position.y - cy, change.position.x - cx) * 180f / PI.toFloat()
                        var delta = cur - lastTouchAngle
                        // Корректируем скачок через границу ±180°
                        if (delta > 180f) delta -= 360f
                        if (delta < -180f) delta += 360f
                        lastTouchAngle = cur
                        // Поворачиваем пластинку в направлении пальца
                        angle = (angle + delta) % 360f
                        // Передаём дельту для перемотки трека
                        onScrub?.invoke(delta)
                    }
                )
            }
            .graphicsLayer { rotationZ = angle },
        contentAlignment = Alignment.Center
    ) {
        // Отрисовка чёрного диска и волнистых звуковых дорожек
        Canvas(Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val maxRadius = size.minDimension / 2f

            // Чёрный диск винила
            drawCircle(color = Color(0xFF0F0F0F), radius = maxRadius, center = center)

            // Внешняя тонкая кромка пластинки
            drawCircle(
                color = Color(0xFF282828),
                radius = maxRadius * 0.98f,
                center = center,
                style = Stroke(width = 1.5.dp.toPx())
            )

            // 6 концентрических волнистых звуковых дорожек (синусоидальные кольца)
            // sin(14 * angle) создаёт 14 гребней волны по окружности
            val ringFractions = listOf(0.46f, 0.55f, 0.64f, 0.73f, 0.82f, 0.91f)
            val waveColor = Color(0xFF383838)
            val waveCount = 14          // Количество гребней волны
            val pointsCount = 120       // Точек для гладкого контура
            val waveAmplitude = 3.dp.toPx()

            ringFractions.forEach { fraction ->
                val baseRadius = maxRadius * fraction
                val wavePath = Path()

                for (i in 0..pointsCount) {
                    val angleRad = (2 * PI * i / pointsCount).toFloat()
                    // Волнообразный радиус: базовый + амплитуда * sin
                    val r = baseRadius + waveAmplitude * sin(waveCount * angleRad)
                    val x = center.x + r * cos(angleRad)
                    val y = center.y + r * sin(angleRad)
                    if (i == 0) wavePath.moveTo(x, y) else wavePath.lineTo(x, y)
                }
                wavePath.close()

                drawPath(wavePath, waveColor, style = Stroke(width = 1.3.dp.toPx()))
            }
        }

        // Центральный элемент: обложка альбома или розовый круг с белой нотой
        if (cover != null) {
            Image(
                painter = cover,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize(0.38f)
                    .clip(CircleShape)
            )
        } else {
            // Розовый лейбл с белой нотой (38% диаметра пластинки)
            Box(
                modifier = Modifier
                    .fillMaxSize(0.38f)
                    .clip(CircleShape)
                    .background(primaryColor),
                contentAlignment = Alignment.Center
            ) {
                // Белая нота и отверстие шпинделя
                Canvas(Modifier.fillMaxSize(0.48f)) {
                    val w = size.width
                    val h = size.height

                    // Центральное отверстие для шпинделя (маленький чёрный круг)
                    drawCircle(
                        color = Color(0xFF0F0F0F),
                        radius = w * 0.22f,
                        center = Offset(w * 0.5f, h * 0.5f)
                    )

                    // Головка ноты (овальный круг внизу-слева)
                    drawCircle(
                        color = Color.White,
                        radius = w * 0.18f,
                        center = Offset(w * 0.42f, h * 0.62f)
                    )

                    // Штиль ноты (вертикальная полоса вверх)
                    drawRect(
                        color = Color.White,
                        topLeft = Offset(w * 0.52f, h * 0.22f),
                        size = Size(w * 0.10f, h * 0.44f)
                    )

                    // Флаг ноты (горизонтальная перекладина)
                    drawRect(
                        color = Color.White,
                        topLeft = Offset(w * 0.52f, h * 0.22f),
                        size = Size(w * 0.28f, h * 0.14f)
                    )
                }
            }
        }
    }
}