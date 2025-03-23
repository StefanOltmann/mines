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

package de.stefan_oltmann.mines.ui.theme

import androidx.compose.material3.SliderColors
import androidx.compose.ui.graphics.Color

val colorForeground = Color(0xFFF8F8F8)

val colorBackground = Color(0xFF111111)

val colorCardBackground = Color(0xFF1D1D1D)
val colorCardBorder = Color(0xFF3A3B3C)

val colorCellHidden = Color(0xFF3D3D3D)
val colorCellHiddenPressed = Color(0xFF777777)
val colorCellBorder = Color(0xFF3F3F3F)

val colorCardBorderGameOver = Color.Red
val colorCardBorderGameWon = Color.Green

val colorMine = Color.Red

val colorOneAdjacentMine = Color(0xFF64A8FF)
val colorTwoAdjacentMines = Color(0xFF00C000)
val colorThreeAdjacentMines = Color(0xFFFF6060)
val colorFourAdjacentMines = Color(0xFF3B6EFF)
val colorFiveAdjacentMines = Color(0xFFFF4444)
val colorSixAdjacentMines = Color(0xFF40E0D0)
val colorSevenAdjacentMines = Color(0xFF808080)
val colorEightAdjacentMines = Color(0xFFD0D0D0)

val sliderColors = SliderColors(
    thumbColor = colorForeground,
    activeTrackColor = colorForeground,
    inactiveTrackColor = colorCellHidden,
    activeTickColor = colorCellHidden,
    inactiveTickColor = colorCellHidden,
    /* Unused values */
    disabledThumbColor = Color.Red,
    disabledActiveTrackColor = Color.Red,
    disabledActiveTickColor = Color.Red,
    disabledInactiveTrackColor = Color.Red,
    disabledInactiveTickColor = Color.Red
)

fun lightColors() = AppThemeColors(
    primary = Color(0xFFE67E22),
    text = Color(0xFF000000),
    background = Color(0xFFF5F5F5),
    success = Color(0xFF2ECC71),
    error = Color(0xFFE74C3C),
    isLight = true,
)

fun darkColors() = AppThemeColors(
    primary = Color(0xFFDF6900),
    text = Color(0xFFFFFFFF),
    background = Color(0xFF353B48),
    success = Color(0xFF44BD32),
    error = Color(0xFFC23616),
    isLight = false,
)
