/*
 * 💣 Mines 💣
 * Copyright (C) 2025 Stefan Oltmann
 * https://github.com/StefanOltmann/mines
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.stefan_oltmann.mines

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.defaultScrollbarStyle
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.isSecondary
import androidx.compose.ui.input.pointer.pointerInput
import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.Settings
import de.stefan_oltmann.mines.ui.theme.colorForeground
import java.util.prefs.Preferences

private val preferences: Preferences = Preferences.userRoot().node("stefan-oltmann-mines")

actual val settings: Settings = PreferencesSettings(preferences)

actual val defaultMapWidth: Int = 10
actual val defaultMapHeight: Int = 10

actual val isDesktop: Boolean = true

@OptIn(ExperimentalComposeUiApi::class)
actual fun Modifier.addRightClickListener(key: Any?, onClick: (Offset) -> Unit): Modifier =
    this.pointerInput(key) {
        awaitPointerEventScope {
            while (true) {

                val event = awaitPointerEvent()

                val change = event.changes.first()

                if (change.pressed && event.button.isSecondary) {

                    val offset = change.position

                    onClick(offset)
                }
            }
        }
    }

@Composable
actual fun BoxScope.HorizontalScrollbar(scrollState: ScrollState) {

    androidx.compose.foundation.HorizontalScrollbar(
        adapter = rememberScrollbarAdapter(scrollState),
        modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter),
        style = defaultScrollbarStyle().copy(
            unhoverColor = colorForeground.copy(alpha = 0.4f),
            hoverColor = colorForeground
        )
    )
}

@Composable
actual fun BoxScope.VerticalScrollbar(scrollState: ScrollState) {

    androidx.compose.foundation.VerticalScrollbar(
        adapter = rememberScrollbarAdapter(scrollState),
        modifier = Modifier.fillMaxHeight().align(Alignment.CenterEnd),
        style = defaultScrollbarStyle().copy(
            unhoverColor = colorForeground.copy(alpha = 0.4f),
            hoverColor = colorForeground
        )
    )
}
