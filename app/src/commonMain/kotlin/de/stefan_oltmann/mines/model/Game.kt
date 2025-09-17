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

package de.stefan_oltmann.mines.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

private val gameStateScope = CoroutineScope(Dispatchers.Default)

class Game {

    private val _elapsedSeconds = MutableStateFlow(0L)
    val elapsedSeconds = _elapsedSeconds.asStateFlow()

    @OptIn(ExperimentalTime::class)
    private var gameStartTime = Instant.DISTANT_PAST

    private var isTimerRunning = false

    var gameOver = false

    var gameWon = false

    var gameState: GameState? = null

    val minefield: Minefield?
        get() = gameState?.minefield

    private fun generateSeed() =
        (1..Int.MAX_VALUE).random()

    @OptIn(ExperimentalTime::class)
    private fun startTimer() {

        if (isTimerRunning)
            return

        isTimerRunning = true

        gameStateScope.launch {

            gameStartTime = Clock.System.now()

            while (isTimerRunning) {

                /*
                 * We try to prevent shifts that occur from delay() not being super-accurate.
                 */

                val result = Clock.System.now() - gameStartTime

                _elapsedSeconds.value = result.inWholeSeconds

                delay(1000)
            }
        }
    }

    fun restart(
        gameConfig: GameConfig
    ) {

        isTimerRunning = false
        _elapsedSeconds.value = 0

        gameOver = false
        gameWon = false

        gameState =
            GameState(
                minefield = Minefield.create(
                    config = gameConfig,
                    seed = generateSeed()
                )
            )
    }

    fun hit(x: Int, y: Int) {

        val gameState = gameState ?: return

        /* Ignore further inputs if game ended. */
        if (gameOver || gameWon)
            return

        /* Start timer on first interaction after reset. */
        if (!isTimerRunning)
            startTimer()

        /* Ignore clicks on flagged cells as these are most likely accidents. */
        if (gameState.isFlagged(x, y))
            return

        val revealed = gameState.isRevealed(x, y)

        if (!revealed) {

            /* Reveal the field in any case */
            gameState.reveal(x, y)

            /* On hitting a mine the game is over. */
            if (gameState.minefield.isMine(x, y)) {

                isTimerRunning = false

                gameOver = true

                return
            }
        }

        /*
         * Tapping on a revealed number should reveal all
         * adjacent fields if a matching number of flags is set.
         */
        if (revealed) {

            val hitMineWhileRevealingAdjacentCells = gameState.revealAdjacentCells(x, y)

            if (hitMineWhileRevealingAdjacentCells) {

                isTimerRunning = false

                gameOver = true

                return
            }
        }

        /* Check win condition */
        if (gameState.isAllRevealed()) {

            /*
             * Many games flag all remaining mines
             * for the finish screen.
             */
            gameState.flagAllMines()

            isTimerRunning = false

            gameWon = true
        }
    }

    fun flag(x: Int, y: Int) {

        val gameState = gameState ?: return

        /* Ignore further inputs if game ended. */
        if (gameOver || gameWon)
            return

        /* Start timer on first interaction after reset. */
        if (!isTimerRunning)
            startTimer()

        /* Only non-revealed fields can be flagged. */
        if (gameState.isRevealed(x, y))
            return

        gameState.toggleFlag(x, y)
    }
}
