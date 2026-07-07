package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = SoccerGreen,
    onPrimary = PitchBlack,
    secondary = AccentGreen,
    onSecondary = PitchBlack,
    tertiary = FieldGreen,
    onTertiary = PureWhite,
    background = PitchBlack,
    onBackground = PureWhite,
    surface = SurfaceCard,
    onSurface = PureWhite,
    surfaceVariant = SurfaceCard,
    onSurfaceVariant = LightGrayText
  )

private val LightColorScheme =
  lightColorScheme(
    primary = SoccerGreenDark,
    onPrimary = PureWhite,
    secondary = FieldGreen,
    onSecondary = PureWhite,
    tertiary = SoftGreenBg,
    onTertiary = DarkGrayText,
    background = PitchBlackLight,
    onBackground = DarkGrayText,
    surface = SurfaceCardLight,
    onSurface = DarkGrayText,
    surfaceVariant = SurfaceCardLight,
    onSurfaceVariant = LightGrayText
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Default to true for the immersive football look!
  dynamicColor: Boolean = false, // Disable to ensure our premium colors take effect
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
