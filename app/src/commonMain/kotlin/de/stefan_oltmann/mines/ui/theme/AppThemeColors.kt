package de.stefan_oltmann.mines.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

class AppThemeColors(
    primary: Color,
    text: Color,
    background: Color,
    success: Color,
    error: Color,
    isLight: Boolean,
) {

    var primary by mutableStateOf(primary)
        private set

    var text by mutableStateOf(text)
        private set

    var success by mutableStateOf(success)
        private set

    var error by mutableStateOf(error)
        private set

    var background by mutableStateOf(background)
        private set

    private var isLight by mutableStateOf(isLight)
        private set

    fun copy(
        primary: Color = this.primary,
        text: Color = this.text,
        background: Color = this.background,
        success: Color = this.success,
        error: Color = this.error,
        isLight: Boolean = this.isLight,
    ) = AppThemeColors(
        primary = primary,
        text = text,
        background = background,
        success = success,
        error = error,
        isLight = isLight,
    )

    fun updateColorsFrom(other: AppThemeColors) {
        primary = other.primary
        text = other.text
        success = other.success
        background = other.background
        error = other.error
        isLight = other.isLight
    }
}
