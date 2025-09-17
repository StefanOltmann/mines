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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.stefan_oltmann.mines.FONT_SIZE
import de.stefan_oltmann.mines.ui.icons.IconGitHubSponsors
import de.stefan_oltmann.mines.ui.theme.colorForeground
import de.stefan_oltmann.mines.ui.theme.defaultSpacing

private val backgroundColor = Color(0xFF28292A)
private val heartColor = Color(0xFFEA4AAA)

@Composable
fun SponsorButton(
    fontFamily: FontFamily,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier
            .background(backgroundColor, RoundedCornerShape(4.dp))
            .height(32.dp)
            .padding(horizontal = defaultSpacing)
            .noRippleClickable(onClick)
    ) {
        Icon(
            imageVector = IconGitHubSponsors,
            contentDescription = null,
            tint = heartColor
        )
        Text(
            text = "Sponsor",
            fontFamily = fontFamily,
            fontSize = FONT_SIZE.sp,
            color = colorForeground,
            maxLines = 1,
            modifier = Modifier.offset(y = -1.dp)
        )
    }
}
