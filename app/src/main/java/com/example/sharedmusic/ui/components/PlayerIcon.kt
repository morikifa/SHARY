package com.example.sharedmusic.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.Dp

// Иконки плеера нарисованы сами, чтобы не добавлять новую библиотеку иконок в Gradle
enum class PlayerIconKind { PLAY, PAUSE, PREVIOUS, NEXT }

@Composable
fun PlayerIcon(
    kind: PlayerIconKind,
    tint: Color,
    iconSize: Dp,
    modifier: Modifier = Modifier
) {
    Canvas(modifier.size(iconSize)) {
        val w = size.width
        val h = size.height
        when (kind) {
            // Треугольник «играть»
            PlayerIconKind.PLAY -> drawPath(
                Path().apply {
                    moveTo(w * 0.3f, h * 0.2f)
                    lineTo(w * 0.85f, h * 0.5f)
                    lineTo(w * 0.3f, h * 0.8f)
                    close()
                },
                tint
            )
            // Две полоски «пауза»
            PlayerIconKind.PAUSE -> {
                drawRect(tint, Offset(w * 0.25f, h * 0.2f), Size(w * 0.18f, h * 0.6f))
                drawRect(tint, Offset(w * 0.57f, h * 0.2f), Size(w * 0.18f, h * 0.6f))
            }
            // Треугольник влево и полоска слева
            PlayerIconKind.PREVIOUS -> {
                drawRect(tint, Offset(w * 0.15f, h * 0.2f), Size(w * 0.1f, h * 0.6f))
                drawPath(
                    Path().apply {
                        moveTo(w * 0.85f, h * 0.2f)
                        lineTo(w * 0.3f, h * 0.5f)
                        lineTo(w * 0.85f, h * 0.8f)
                        close()
                    },
                    tint
                )
            }
            // Треугольник вправо и полоска справа
            PlayerIconKind.NEXT -> {
                drawRect(tint, Offset(w * 0.75f, h * 0.2f), Size(w * 0.1f, h * 0.6f))
                drawPath(
                    Path().apply {
                        moveTo(w * 0.15f, h * 0.2f)
                        lineTo(w * 0.7f, h * 0.5f)
                        lineTo(w * 0.15f, h * 0.8f)
                        close()
                    },
                    tint
                )
            }
        }
    }
}
