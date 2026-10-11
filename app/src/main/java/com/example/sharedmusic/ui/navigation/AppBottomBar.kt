package com.example.sharedmusic.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sharedmusic.ui.components.HomeIcon
import com.example.sharedmusic.ui.components.LibraryIcon
import com.example.sharedmusic.ui.components.ProfileIcon
import com.example.sharedmusic.ui.components.RoomIcon

// Нижняя панель навигации из 4 вкладок: Главная, Комната, Библиотека, Профиль
@Composable
fun AppBottomBar(
    currentScreen: Screen,
    onScreenSelected: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp,
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomBarItem(
                title = "Главная",
                isSelected = currentScreen is Screen.Main,
                onClick = { onScreenSelected(Screen.Main) }
            ) { tint ->
                HomeIcon(tint = tint, iconSize = 22.dp)
            }

            BottomBarItem(
                title = "Комната",
                isSelected = currentScreen is Screen.Room,
                onClick = { onScreenSelected(Screen.Room) }
            ) { tint ->
                RoomIcon(tint = tint, iconSize = 22.dp)
            }

            BottomBarItem(
                title = "Библиотека",
                isSelected = currentScreen is Screen.Library,
                onClick = { onScreenSelected(Screen.Library) }
            ) { tint ->
                LibraryIcon(tint = tint, iconSize = 22.dp)
            }

            BottomBarItem(
                title = "Профиль",
                isSelected = currentScreen is Screen.Profile || currentScreen is Screen.Equalizer,
                onClick = { onScreenSelected(Screen.Profile) }
            ) { tint ->
                ProfileIcon(tint = tint, iconSize = 22.dp)
            }
        }
    }
}

// Отдельная вкладка нижней панели
@Composable
private fun BottomBarItem(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    icon: @Composable (tint: androidx.compose.ui.graphics.Color) -> Unit
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant
    val activeTint = primaryColor
    val inactiveTint = onSurfaceVariant.copy(alpha = 0.7f)

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Контейнер иконки с подсветкой при выборе
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(
                    if (isSelected) primaryColor.copy(alpha = 0.15f) else androidx.compose.ui.graphics.Color.Transparent
                ),
            contentAlignment = Alignment.Center
        ) {
            icon(if (isSelected) activeTint else inactiveTint)
        }

        Spacer(Modifier.height(2.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) activeTint else inactiveTint
        )
    }
}
