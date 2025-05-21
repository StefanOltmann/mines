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

package de.stefan_oltmann.mines.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.requiredWidthIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalOf
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.stefan_oltmann.mines.FONT_SIZE
import de.stefan_oltmann.mines.ui.icons.IconDonate
import de.stefan_oltmann.mines.ui.icons.IconFlag
import de.stefan_oltmann.mines.ui.icons.IconKofi
import de.stefan_oltmann.mines.ui.icons.IconRestart
import de.stefan_oltmann.mines.ui.icons.IconSettings
import de.stefan_oltmann.mines.ui.icons.IconTimer
import de.stefan_oltmann.mines.ui.theme.DoubleSpacer
import de.stefan_oltmann.mines.ui.theme.HalfSpacer
import de.stefan_oltmann.mines.ui.theme.buttonSize
import de.stefan_oltmann.mines.ui.theme.colorForeground

@Composable
fun Toolbar(
    highlightRestartButton: Boolean,
    elapsedSeconds: Long,
    remainingFlagsCount: Int,
    fontFamily: FontFamily,
    showSettings: () -> Unit,
    restartGame: () -> Unit
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.requiredWidthIn(min = 288.dp)
    ) {

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(buttonSize)
                .noRippleClickable(onClick = restartGame)
        ) {

            Icon(
                imageVector = IconRestart,
                contentDescription = null,
                tint = if (highlightRestartButton)
                    Color.Yellow
                else
                    colorForeground
            )
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(buttonSize)
                .noRippleClickable(onClick = showSettings)
        ) {

            Icon(
                imageVector = IconSettings,
                contentDescription = null,
                tint = colorForeground
            )
        }

        val uriHandler = LocalUriHandler.current

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(buttonSize)
                .noRippleClickable {
                    uriHandler.openUri("https://ko-fi.com/G2G61FBTJX")
                }
        ) {

            Image(
                imageVector = IconKofi,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }

        DoubleSpacer()

        Icon(
            imageVector = IconTimer,
            contentDescription = null,
            tint = colorForeground
        )

        HalfSpacer()

        Text(
            text = elapsedSeconds.toString(),
            fontFamily = fontFamily,
            color = colorForeground,
            fontSize = FONT_SIZE.sp,
            textAlign = TextAlign.Right,
            modifier = Modifier.widthIn(min = 20.dp)
        )

        DoubleSpacer()

        Icon(
            imageVector = IconFlag,
            contentDescription = null,
            tint = colorForeground
        )

        HalfSpacer()

        Text(
            text = remainingFlagsCount.toString(),
            fontFamily = fontFamily,
            color = colorForeground,
            fontSize = FONT_SIZE.sp,
            textAlign = TextAlign.Right,
            modifier = Modifier.widthIn(min = 20.dp)
        )
    }
}
