package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AyuDarkColorScheme = darkColorScheme(
  primary = AyuGreenPrimary,
  onPrimary = Color.Black,
  primaryContainer = AyuGreenPill,
  onPrimaryContainer = AyuGreenLight,
  secondary = AyuGreenLight,
  onSecondary = Color.Black,
  secondaryContainer = AyuDarkCard,
  onSecondaryContainer = AyuTextPrimary,
  tertiary = AyuGreenDark,
  onTertiary = Color.White,
  background = AyuDarkBackground,
  onBackground = AyuTextPrimary,
  surface = AyuDarkSurface,
  onSurface = AyuTextPrimary,
  surfaceVariant = AyuDarkCard,
  onSurfaceVariant = AyuTextSecondary,
  outline = AyuBorder,
  error = AyuGhostBadgeRed
)

@Composable
fun MailChatTheme(
  darkTheme: Boolean = true,
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = AyuDarkColorScheme,
    typography = Typography,
    content = content
  )
}
