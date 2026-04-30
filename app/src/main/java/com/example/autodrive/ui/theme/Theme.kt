package com.example.autodrive.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


private val AppColorScheme = lightColorScheme(
    primary          = AppBlack,
    onPrimary        = AppWhite,
    primaryContainer = Color(0xFFF0F0F0),
    onPrimaryContainer = AppBlack,
    secondary        = Color(0xFF444444),
    onSecondary      = AppWhite,
    background       = AppBackground,
    onBackground     = AppBlack,
    surface          = AppWhite,
    onSurface        = AppBlack,
    onSurfaceVariant = AppGrey,
    outline          = AppDivider,
    error            = AppError,
    onError          = AppWhite,
    errorContainer   = AppErrorBg,
    onErrorContainer = AppError
)

@Composable
fun AutoDriveTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        typography  = Typography,
        content     = content
    )
}
