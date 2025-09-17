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

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import de.stefan_oltmann.mines.model.Game
import de.stefan_oltmann.mines.model.GameConfig
import de.stefan_oltmann.mines.model.GameDifficulty
import de.stefan_oltmann.mines.ui.AppFooter
import de.stefan_oltmann.mines.ui.GameOverOverlay
import de.stefan_oltmann.mines.ui.MinefieldCanvas
import de.stefan_oltmann.mines.ui.SettingsDialog
import de.stefan_oltmann.mines.ui.Toolbar
import de.stefan_oltmann.mines.ui.lottie.ConfettiLottieImage
import de.stefan_oltmann.mines.ui.theme.EconomicaFontFamily
import de.stefan_oltmann.mines.ui.theme.colorBackground
import de.stefan_oltmann.mines.ui.theme.colorCardBackground
import de.stefan_oltmann.mines.ui.theme.colorCardBorder
import de.stefan_oltmann.mines.ui.theme.colorCardBorderGameOver
import de.stefan_oltmann.mines.ui.theme.colorCardBorderGameWon
import de.stefan_oltmann.mines.ui.theme.defaultRoundedCornerShape
import de.stefan_oltmann.mines.ui.theme.defaultSpacing
import de.stefan_oltmann.mines.ui.theme.doublePadding
import de.stefan_oltmann.mines.ui.theme.doubleSpacing
import io.github.alexzhirkevich.compottie.DotLottie
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import mines.app.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalResourceApi::class)
@Composable
fun App() {

    val fontFamily = EconomicaFontFamily()

    val game = remember { Game() }

    val gameConfig = remember {
        mutableStateOf(
            GameConfig(
                cellSize = settings["mines_cell_size"] ?: DEFAULT_CELL_SIZE,
                mapWidth = settings["mines_map_width"] ?: defaultMapWidth,
                mapHeight = settings["mines_map_height"] ?: defaultMapHeight,
                difficulty = GameDifficulty.valueOf(settings["mines_difficulty"] ?: GameDifficulty.EASY.name)
            )
        )
    }

    val redrawState = remember { mutableStateOf(0) }

    /* State to trigger scrolling to the middle of the field */
    val scrollToMiddleTrigger = remember { mutableStateOf(0) }

    /* Remember scroll states so they can be accessed from the restartGame lambda */
    val verticalScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()

    /*
     * Pre-load lotties to avoid delays in playback.
     */

    val confettiLottieComposition by rememberLottieComposition {

        LottieCompositionSpec.DotLottie(
            archive = Res.readBytes("files/confetti.lottie")
        )
    }

    val explosionLottieComposition by rememberLottieComposition {

        LottieCompositionSpec.DotLottie(
            archive = Res.readBytes("files/explosion.lottie")
        )
    }

    /*
     * Initially start a new game when opened.
     */
    LaunchedEffect(Unit) {

        game.restart(gameConfig.value)

        /* Trigger scrolling to the middle of the field */
        scrollToMiddleTrigger.value += 1
    }

    LaunchedEffect(gameConfig.value) {

        val newGameConfig = gameConfig.value

        val oldMapWidth = settings["mines_map_width"] ?: defaultMapWidth
        val oldMapHeight = settings["mines_map_height"] ?: defaultMapHeight
        val oldDifficulty = GameDifficulty.valueOf(settings["mines_difficulty"] ?: GameDifficulty.EASY.name)

        /* Save new settings to config */
        settings["mines_cell_size"] = newGameConfig.cellSize
        settings["mines_map_width"] = newGameConfig.mapWidth
        settings["mines_map_height"] = newGameConfig.mapHeight
        settings["mines_difficulty"] = newGameConfig.difficulty.name

        val mapSettingsChanged =
            oldMapWidth != newGameConfig.mapWidth ||
                oldMapHeight != newGameConfig.mapHeight ||
                oldDifficulty != newGameConfig.difficulty

        /* Launch a new game every time the settings change something that influences the map */
        if (mapSettingsChanged) {

            game.restart(gameConfig.value)

            /* Trigger scrolling to the middle of the field */
            scrollToMiddleTrigger.value += 1
        }

        /* HACK */
        redrawState.value += 1
    }

    /* Effect to scroll to the middle of the field when the trigger changes */
    LaunchedEffect(scrollToMiddleTrigger.value) {

        horizontalScrollState.scrollTo(horizontalScrollState.maxValue / 2)

        verticalScrollState.scrollTo(verticalScrollState.maxValue / 2)
    }

    val elapsedSeconds by game.elapsedSeconds.collectAsState()

    val showSettings = remember { mutableStateOf(false) }

    /*
     * Force redraw if state changes.
     *
     * FIXME This is a hack
     */
    redrawState.value

    Column {

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .weight(1F)
                .fillMaxWidth()
                .background(colorBackground)
        ) {

            val borderColor = when {
                game.gameOver -> colorCardBorderGameOver
                game.gameWon -> colorCardBorderGameWon
                else -> colorCardBorder
            }

            Card(
                colors = CardDefaults.cardColors().copy(
                    containerColor = colorCardBackground
                ),
                shape = defaultRoundedCornerShape,
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier.doublePadding()
            ) {

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(
                            start = defaultSpacing,
                            end = defaultSpacing,
                            bottom = doubleSpacing
                        )
                ) {

                    Toolbar(
                        highlightRestartButton = game.gameOver || game.gameWon,
                        elapsedSeconds = elapsedSeconds,
                        remainingFlagsCount = game.gameState?.getRemainingFlagsCount() ?: 0,
                        fontFamily = fontFamily,
                        showSettings = {
                            showSettings.value = true
                        },
                        restartGame = {

                            game.restart(gameConfig.value)

                            /* FIXME This is a hack */
                            redrawState.value += 1

                            /* Trigger scrolling to the middle of the field */
                            scrollToMiddleTrigger.value += 1
                        }
                    )

                    Box {

                        Box(
                            modifier = Modifier
                                .verticalScroll(verticalScrollState)
                                .horizontalScroll(horizontalScrollState)
                        ) {

                            val gameState = game.gameState

                            if (gameState != null) {

                                MinefieldCanvas(
                                    gameState,
                                    gameConfig,
                                    redrawState,
                                    fontFamily,
                                    hit = { x, y -> game.hit(x, y) },
                                    flag = { x, y -> game.flag(x, y) }
                                )
                            }
                        }

                        if (verticalScrollState.canScrollForward || verticalScrollState.canScrollBackward)
                            VerticalScrollbar(verticalScrollState)

                        if (horizontalScrollState.canScrollForward || horizontalScrollState.canScrollBackward)
                            HorizontalScrollbar(horizontalScrollState)
                    }
                }
            }

            when {

                game.gameWon ->
                    confettiLottieComposition?.let { lottieComposition ->
                        ConfettiLottieImage(lottieComposition)
                    }

                game.gameOver ->
                    explosionLottieComposition?.let { lottieComposition ->
                        GameOverOverlay(
                            explosionLottieComposition = lottieComposition,
                            fontFamily = fontFamily
                        )
                    }
            }

            /*
             * The settings must overlay the "game over" text.
             */
            if (showSettings.value)
                SettingsDialog(
                    gameConfig = gameConfig.value,
                    fontFamily = fontFamily,
                    onCancel = {
                        showSettings.value = false
                    },
                    onConfirm = { newGameSettings ->

                        showSettings.value = false

                        gameConfig.value = newGameSettings
                    }
                )
        }

        AppFooter(fontFamily)
    }
}
