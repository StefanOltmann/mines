package de.stefan_oltmann.mines

import android.content.SharedPreferences
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings

@Suppress("LateinitUsage", "MatchingDeclarationName")
object SettingsProvider {

    lateinit var settings: SharedPreferencesSettings
        private set

    fun init(prefs: SharedPreferences) {
        settings = SharedPreferencesSettings(prefs)
    }
}

actual val settings: Settings = SettingsProvider.settings

actual val defaultMapWidth: Int = 7
actual val defaultMapHeight: Int = 7

actual val isDesktop: Boolean = false

/* Not effective as there is no right-click on Android */
actual fun Modifier.addRightClickListener(key: Any?, onClick: (Offset) -> Unit): Modifier = this

@Composable
actual fun BoxScope.HorizontalScrollbar(scrollState: ScrollState) = Unit

@Composable
actual fun BoxScope.VerticalScrollbar(scrollState: ScrollState) = Unit
