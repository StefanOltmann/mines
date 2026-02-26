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
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset

import com.russhwolf.settings.Settings

expect val settings: Settings

expect val defaultMapWidth: Int
expect val defaultMapHeight: Int

expect val isDesktop: Boolean

expect fun Modifier.addRightClickListener(
    key: Any?,
    onClick: (Offset) -> Unit
): Modifier

@Composable
expect fun BoxScope.HorizontalScrollbar(scrollState: ScrollState)

@Composable
expect fun BoxScope.VerticalScrollbar(scrollState: ScrollState)
