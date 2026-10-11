package com.example.sharedmusic.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle

// Одна строка текста. Если текст не вмещается, его правый край плавно гаснет
@Composable
fun FadingText(
    text: String,
    style: TextStyle,
    color: Color,
    modifier: Modifier = Modifier
) {
    // Запоминаем, не вмещается ли текст. Если да — рисуем затухание
    var isOverflowing by remember { mutableStateOf(false) }

    Text(
        text = text,
        style = style,
        color = color,
        maxLines = 1,
        softWrap = false,
        onTextLayout = { result: TextLayoutResult -> isOverflowing = result.hasVisualOverflow },
        modifier = modifier
            .fillMaxWidth()
            // Слой отдельно от фона, чтобы маска прозрачности не влияла на соседние элементы
            .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
            .drawWithContent {
                drawContent()
                if (isOverflowing) {
                    // Последние 20% ширины становятся прозрачными
                    drawRect(
                        brush = Brush.horizontalGradient(
                            0.8f to Color.Black,
                            1f to Color.Transparent,
                            startX = 0f,
                            endX = size.width
                        ),
                        blendMode = BlendMode.DstIn
                    )
                }
            }
    )
}
