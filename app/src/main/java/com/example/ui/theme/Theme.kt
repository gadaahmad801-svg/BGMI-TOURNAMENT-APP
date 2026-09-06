package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val ArenaDarkColorScheme = darkColorScheme(
  primary = ArenaCyan,
  onPrimary = ArenaBgDark,
  secondary = ArenaPurpleBright,
  onSecondary = ArenaTextPrimary,
  tertiary = ArenaCyanSecondary,
  onTertiary = ArenaBgDark,
  background = ArenaBgDark,
  onBackground = ArenaTextPrimary,
  surface = ArenaCardBg,
  onSurface = ArenaTextPrimary,
  surfaceVariant = ArenaCardElevated,
  onSurfaceVariant = ArenaTextSecondary,
  outline = ArenaBorder,
  error = ArenaDanger,
  onError = ArenaTextPrimary
)

@Composable
fun MyApplicationTheme(
  content: @Composable () -> Unit
) {
  MaterialTheme(
    colorScheme = ArenaDarkColorScheme,
    typography = Typography,
    content = content
  )
}

