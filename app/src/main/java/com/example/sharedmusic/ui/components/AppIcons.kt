package com.example.sharedmusic.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Векторные иконки, нарисованные через Canvas, чтобы не подключать тяжёлые сторонние библиотеки иконок в Gradle

// Иконка «Главная» (домик)
@Composable
fun HomeIcon(
    tint: Color,
    iconSize: Dp = 24.dp,
    modifier: Modifier = Modifier
) {
    Canvas(modifier.size(iconSize)) {
        val w = size.width
        val h = size.height
        val stroke = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)

        val roofPath = Path().apply {
            moveTo(w * 0.15f, h * 0.48f)
            lineTo(w * 0.5f, h * 0.18f)
            lineTo(w * 0.85f, h * 0.48f)
        }
        drawPath(roofPath, tint, style = stroke)

        val bodyPath = Path().apply {
            moveTo(w * 0.25f, h * 0.45f)
            lineTo(w * 0.25f, h * 0.82f)
            lineTo(w * 0.75f, h * 0.82f)
            lineTo(w * 0.75f, h * 0.45f)
        }
        drawPath(bodyPath, tint, style = stroke)

        // Дверца
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.42f, h * 0.58f),
            size = Size(w * 0.16f, h * 0.24f),
            cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx()),
            style = stroke
        )
    }
}

// Иконка «Комната» (два силуэта участников)
@Composable
fun RoomIcon(
    tint: Color,
    iconSize: Dp = 24.dp,
    modifier: Modifier = Modifier
) {
    Canvas(modifier.size(iconSize)) {
        val w = size.width
        val h = size.height
        val stroke = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)

        // Голова первого участника (слева)
        drawCircle(
            color = tint,
            radius = w * 0.13f,
            center = Offset(w * 0.38f, h * 0.32f),
            style = stroke
        )
        // Плечи первого участника
        val shoulders1 = Path().apply {
            moveTo(w * 0.18f, h * 0.78f)
            cubicTo(w * 0.18f, h * 0.56f, w * 0.58f, h * 0.56f, w * 0.58f, h * 0.78f)
        }
        drawPath(shoulders1, tint, style = stroke)

        // Голова второго участника (справа, чуть позади)
        drawCircle(
            color = tint,
            radius = w * 0.11f,
            center = Offset(w * 0.68f, h * 0.38f),
            style = stroke
        )
        // Плечи второго участника
        val shoulders2 = Path().apply {
            moveTo(w * 0.56f, h * 0.70f)
            cubicTo(w * 0.62f, h * 0.58f, w * 0.84f, h * 0.58f, w * 0.84f, h * 0.78f)
        }
        drawPath(shoulders2, tint, style = stroke)
    }
}

// Иконка «Библиотека» (музыкальная нота)
@Composable
fun LibraryIcon(
    tint: Color,
    iconSize: Dp = 24.dp,
    modifier: Modifier = Modifier
) {
    Canvas(modifier.size(iconSize)) {
        val w = size.width
        val h = size.height
        val stroke = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)

        // Две головки ноты
        drawCircle(color = tint, radius = w * 0.12f, center = Offset(w * 0.32f, h * 0.72f))
        drawCircle(color = tint, radius = w * 0.12f, center = Offset(w * 0.72f, h * 0.62f))

        // Штили и перекладина
        val stemPath = Path().apply {
            moveTo(w * 0.42f, h * 0.72f)
            lineTo(w * 0.42f, h * 0.25f)
            lineTo(w * 0.82f, h * 0.17f)
            lineTo(w * 0.82f, h * 0.62f)
        }
        drawPath(stemPath, tint, style = stroke)

        // Вторая перекладина для двойной ноты
        val beamPath = Path().apply {
            moveTo(w * 0.42f, h * 0.37f)
            lineTo(w * 0.82f, h * 0.29f)
        }
        drawPath(beamPath, tint, style = stroke)
    }
}

// Иконка «Профиль» (пользователь)
@Composable
fun ProfileIcon(
    tint: Color,
    iconSize: Dp = 24.dp,
    modifier: Modifier = Modifier
) {
    Canvas(modifier.size(iconSize)) {
        val w = size.width
        val h = size.height
        val stroke = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)

        // Голова по центру
        drawCircle(
            color = tint,
            radius = w * 0.18f,
            center = Offset(w * 0.5f, h * 0.34f),
            style = stroke
        )

        // Плечи
        val shoulders = Path().apply {
            moveTo(w * 0.2f, h * 0.82f)
            cubicTo(w * 0.2f, h * 0.58f, w * 0.8f, h * 0.58f, w * 0.8f, h * 0.82f)
        }
        drawPath(shoulders, tint, style = stroke)
    }
}

// Иконка перемешивания треков (Shuffle)
@Composable
fun ShuffleIcon(
    tint: Color,
    iconSize: Dp = 24.dp,
    modifier: Modifier = Modifier
) {
    Canvas(modifier.size(iconSize)) {
        val w = size.width
        val h = size.height
        val stroke = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)

        // Верхняя стрелка (слева направо с изгибом вниз-вверх)
        val path1 = Path().apply {
            moveTo(w * 0.18f, h * 0.32f)
            lineTo(w * 0.36f, h * 0.32f)
            lineTo(w * 0.64f, h * 0.68f)
            lineTo(w * 0.82f, h * 0.68f)
        }
        drawPath(path1, tint, style = stroke)

        // Наконечник стрелки 1
        val arrow1 = Path().apply {
            moveTo(w * 0.72f, h * 0.56f)
            lineTo(w * 0.84f, h * 0.68f)
            lineTo(w * 0.72f, h * 0.80f)
        }
        drawPath(arrow1, tint, style = stroke)

        // Нижняя стрелка (слева снизу наверх)
        val path2 = Path().apply {
            moveTo(w * 0.18f, h * 0.68f)
            lineTo(w * 0.36f, h * 0.68f)
            lineTo(w * 0.46f, h * 0.55f)
        }
        drawPath(path2, tint, style = stroke)

        val path3 = Path().apply {
            moveTo(w * 0.54f, h * 0.45f)
            lineTo(w * 0.64f, h * 0.32f)
            lineTo(w * 0.82f, h * 0.32f)
        }
        drawPath(path3, tint, style = stroke)

        // Наконечник стрелки 2
        val arrow2 = Path().apply {
            moveTo(w * 0.72f, h * 0.20f)
            lineTo(w * 0.84f, h * 0.32f)
            lineTo(w * 0.72f, h * 0.44f)
        }
        drawPath(arrow2, tint, style = stroke)
    }
}

// Иконка «Лайк» (сердечко, закрашенное или контурное)
@Composable
fun HeartIcon(
    tint: Color,
    isLiked: Boolean,
    iconSize: Dp = 24.dp,
    modifier: Modifier = Modifier
) {
    Canvas(modifier.size(iconSize)) {
        val w = size.width
        val h = size.height

        val heartPath = Path().apply {
            moveTo(w * 0.5f, h * 0.82f)
            cubicTo(w * 0.15f, h * 0.6f, w * 0.12f, h * 0.28f, w * 0.32f, h * 0.24f)
            cubicTo(w * 0.44f, h * 0.24f, w * 0.5f, h * 0.36f, w * 0.5f, h * 0.36f)
            cubicTo(w * 0.5f, h * 0.36f, w * 0.56f, h * 0.24f, w * 0.68f, h * 0.24f)
            cubicTo(w * 0.88f, h * 0.28f, w * 0.85f, h * 0.6f, w * 0.5f, h * 0.82f)
            close()
        }

        if (isLiked) {
            drawPath(heartPath, tint)
        } else {
            drawPath(
                heartPath,
                tint,
                style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
        }
    }
}

// Иконка эквалайзера (полоски звуковых частот)
@Composable
fun EqualizerIcon(
    tint: Color,
    iconSize: Dp = 24.dp,
    modifier: Modifier = Modifier
) {
    Canvas(modifier.size(iconSize)) {
        val w = size.width
        val h = size.height
        val barWidth = 3.dp.toPx()
        val corner = CornerRadius(2.dp.toPx(), 2.dp.toPx())

        // 4 полоски разной высоты
        drawRoundRect(tint, Offset(w * 0.2f - barWidth / 2, h * 0.4f), Size(barWidth, h * 0.45f), corner)
        drawRoundRect(tint, Offset(w * 0.4f - barWidth / 2, h * 0.2f), Size(barWidth, h * 0.65f), corner)
        drawRoundRect(tint, Offset(w * 0.6f - barWidth / 2, h * 0.32f), Size(barWidth, h * 0.53f), corner)
        drawRoundRect(tint, Offset(w * 0.8f - barWidth / 2, h * 0.15f), Size(barWidth, h * 0.70f), corner)
    }
}

// Иконка «Назад» (стрелка влево)
@Composable
fun BackIcon(
    tint: Color,
    iconSize: Dp = 24.dp,
    modifier: Modifier = Modifier
) {
    Canvas(modifier.size(iconSize)) {
        val w = size.width
        val h = size.height
        val stroke = Stroke(width = 2.2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)

        val arrow = Path().apply {
            moveTo(w * 0.62f, h * 0.22f)
            lineTo(w * 0.34f, h * 0.5f)
            lineTo(w * 0.62f, h * 0.78f)
        }
        drawPath(arrow, tint, style = stroke)
    }
}

// Иконка поиска (лупа)
@Composable
fun SearchIcon(
    tint: Color,
    iconSize: Dp = 24.dp,
    modifier: Modifier = Modifier
) {
    Canvas(modifier.size(iconSize)) {
        val w = size.width
        val h = size.height
        val stroke = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)

        // Окружность линзы
        drawCircle(
            color = tint,
            radius = w * 0.26f,
            center = Offset(w * 0.42f, h * 0.42f),
            style = stroke
        )

        // Ручка лупы
        drawLine(
            color = tint,
            start = Offset(w * 0.62f, h * 0.62f),
            end = Offset(w * 0.82f, h * 0.82f),
            strokeWidth = 2.4.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}

// Иконка «Поделиться / Скопировать»
@Composable
fun ShareIcon(
    tint: Color,
    iconSize: Dp = 24.dp,
    modifier: Modifier = Modifier
) {
    Canvas(modifier.size(iconSize)) {
        val w = size.width
        val h = size.height
        val stroke = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)

        // Три точки
        val p1 = Offset(w * 0.75f, h * 0.25f)
        val p2 = Offset(w * 0.25f, h * 0.50f)
        val p3 = Offset(w * 0.75f, h * 0.75f)

        // Линии соединения
        drawLine(tint, p2, p1, strokeWidth = 1.8.dp.toPx())
        drawLine(tint, p2, p3, strokeWidth = 1.8.dp.toPx())

        // Кружки узлов
        drawCircle(tint, radius = w * 0.1f, center = p1)
        drawCircle(tint, radius = w * 0.1f, center = p2)
        drawCircle(tint, radius = w * 0.1f, center = p3)
    }
}
