package de.stefan_oltmann.mines.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf

val LocalSpaces = staticCompositionLocalOf { AppThemeSpaces() }

val LocalColors = staticCompositionLocalOf { lightColors() }

val DarkMode = staticCompositionLocalOf { false }

object AppTheme {

    val colors: AppThemeColors
        @Composable
        @ReadOnlyComposable
        get() = LocalColors.current

    val spaces: AppThemeSpaces
        @Composable
        @ReadOnlyComposable
        get() = LocalSpaces.current
}

@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {

    val currentColor = remember { if (DarkMode.current) darkColors() else lightColors() }

    CompositionLocalProvider(
        LocalColors provides currentColor,
        LocalSpaces provides AppTheme.spaces,
        DarkMode provides isSystemInDarkTheme(),
        content = content
    )
}
