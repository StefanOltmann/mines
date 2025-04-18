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

/**
 * A solver for Minefields that determines if a minefield is solvable without taking 50:50 chances.
 * The solver starts from the protected middle of the minefield and uses logic to determine
 * which cells are safe to reveal.
 */
class MinefieldSolver(
    private val minefield: Minefield
) {
    /* GameState to track revealed cells and flagged mines */
    private val gameState = GameState(minefield)

    /* Set to track cells that are known to be safe */
    private val safeCells = mutableSetOf<Pair<Int, Int>>()

    /* Set to track cells that are known to contain mines */
    private val mineCells = mutableSetOf<Pair<Int, Int>>()

    /* Set to track cells that have been processed */
    private val processedCells = mutableSetOf<Pair<Int, Int>>()

    /**
     * Attempts to solve the minefield and returns true if it's solvable without taking 50:50 chances.
     */
    fun isSolvable(): Boolean {
        /* Start by revealing cells in the protected middle */
        revealProtectedMiddle()

        /* Continue solving until no more progress can be made */
        var madeProgress: Boolean
        var iterations = 0
        do {
            madeProgress = makeProgress()
            iterations++

            /* Add debug output */
            if (iterations % 10 == 0) {
                println("[DEBUG_LOG] Solver iteration $iterations, made progress: $madeProgress")
            }

            /* Prevent infinite loops during debugging */
            if (iterations > 100) {
                println("[DEBUG_LOG] Reached maximum iterations (100). Stopping solver.")
                break
            }
        } while (madeProgress)

        /* Check if all non-mine cells have been revealed */
        val allRevealed = gameState.isAllRevealed()
        println("[DEBUG_LOG] All non-mine cells revealed: $allRevealed")

        return allRevealed
    }

    /**
     * Reveals all cells in the protected middle of the minefield.
     */
    private fun revealProtectedMiddle() {
        val protectedXRange = Minefield.calcProtectedRange(minefield.width)
        val protectedYRange = Minefield.calcProtectedRange(minefield.height)

        println("[DEBUG_LOG] Protected X range: $protectedXRange")
        println("[DEBUG_LOG] Protected Y range: $protectedYRange")

        for (x in protectedXRange) {
            for (y in protectedYRange) {
                println("[DEBUG_LOG] Revealing protected cell at ($x, $y): ${minefield.getCellType(x, y)}")
                gameState.reveal(x, y)
                processedCells.add(x to y)
            }
        }
    }

    /**
     * Makes one step of progress in solving the minefield.
     * Returns true if progress was made, false otherwise.
     */
    private fun makeProgress(): Boolean {
        /* Find all revealed cells that haven't been processed yet */
        val cellsToProcess = findCellsToProcess()

        println("[DEBUG_LOG] Cells to process: ${cellsToProcess.size}")

        if (cellsToProcess.isEmpty()) {
            /* Count unrevealed non-mine cells */
            var unrevealedNonMineCells = 0
            for (x in 0 until minefield.width) {
                for (y in 0 until minefield.height) {
                    if (!minefield.isMine(x, y) && !gameState.isRevealed(x, y)) {
                        println("[DEBUG_LOG] Unrevealed non-mine cell at ($x, $y): ${minefield.getCellType(x, y)}")
                        unrevealedNonMineCells++
                    }
                }
            }
            println("[DEBUG_LOG] Unrevealed non-mine cells: $unrevealedNonMineCells")
            return false
        }

        var madeProgress = false

        for (cell in cellsToProcess) {
            val (x, y) = cell

            /* Mark the cell as processed */
            processedCells.add(cell)

            /* If the cell is empty, all adjacent cells are safe */
            if (minefield.getCellType(x, y) == CellType.EMPTY) {
                val progress = markAdjacentCellsAsSafe(x, y)
                if (progress) {
                    println("[DEBUG_LOG] Made progress by marking adjacent cells as safe from empty cell at ($x, $y)")
                }
                madeProgress = madeProgress or progress
            } else if (minefield.getCellType(x, y) != CellType.MINE) {
                /* For numbered cells, try to deduce safe cells and mines */
                val progress = processNumberedCell(x, y)
                if (progress) {
                    println(
                        "[DEBUG_LOG] Made progress by processing numbered cell at ($x, $y): ${
                            minefield.getCellType(
                                x,
                                y
                            )
                        }"
                    )
                }
                madeProgress = madeProgress or progress
            }
        }

        /* Reveal all cells known to be safe */
        val revealProgress = revealSafeCells()
        if (revealProgress) {
            println("[DEBUG_LOG] Made progress by revealing safe cells")
        }
        madeProgress = madeProgress or revealProgress

        /* Flag all cells known to contain mines */
        val flagProgress = flagMineCells()
        if (flagProgress) {
            println("[DEBUG_LOG] Made progress by flagging mine cells")
        }
        madeProgress = madeProgress or flagProgress

        return madeProgress
    }

    /**
     * Finds all revealed cells that haven't been processed yet.
     */
    private fun findCellsToProcess(): List<Pair<Int, Int>> {
        val result = mutableListOf<Pair<Int, Int>>()

        for (x in 0 until minefield.width) {
            for (y in 0 until minefield.height) {
                val cell = x to y
                if (gameState.isRevealed(x, y) && !processedCells.contains(cell)) {
                    result.add(cell)
                }
            }
        }

        return result
    }

    /**
     * Marks all adjacent cells of an empty cell as safe.
     */
    private fun markAdjacentCellsAsSafe(x: Int, y: Int): Boolean {
        var madeProgress = false

        for ((dx, dy) in directionsOfAdjacentCells) {
            val adjX = x + dx
            val adjY = y + dy

            if (isCellWithinBounds(adjX, adjY) && !gameState.isRevealed(
                    adjX,
                    adjY
                ) && !safeCells.contains(adjX to adjY)
            ) {
                safeCells.add(adjX to adjY)
                madeProgress = true
            }
        }

        return madeProgress
    }

    /**
     * Processes a numbered cell to deduce safe cells and mines.
     */
    private fun processNumberedCell(x: Int, y: Int): Boolean {
        val cellType = minefield.getCellType(x, y)
        val adjacentMineCount = cellType.adjacentMineCount

        // Get all adjacent cells
        val adjacentCells = getAdjacentCells(x, y)

        // Count already flagged mines and unrevealed cells
        val flaggedCount = adjacentCells.count { (adjX, adjY) -> gameState.isFlagged(adjX, adjY) }
        val unrevealedCount = adjacentCells.count { (adjX, adjY) ->
            !gameState.isRevealed(adjX, adjY) && !gameState.isFlagged(
                adjX,
                adjY
            )
        }

        var madeProgress = false

        // If the number of unrevealed cells equals the number of remaining mines, all unrevealed cells are mines
        if (adjacentMineCount - flaggedCount == unrevealedCount && unrevealedCount > 0) {
            for ((adjX, adjY) in adjacentCells) {
                if (!gameState.isRevealed(adjX, adjY) && !gameState.isFlagged(
                        adjX,
                        adjY
                    ) && !mineCells.contains(adjX to adjY)
                ) {
                    mineCells.add(adjX to adjY)
                    madeProgress = true
                }
            }
        }

        // If all mines are flagged, all remaining unrevealed cells are safe
        if (flaggedCount == adjacentMineCount && unrevealedCount > 0) {
            for ((adjX, adjY) in adjacentCells) {
                if (!gameState.isRevealed(adjX, adjY) && !gameState.isFlagged(
                        adjX,
                        adjY
                    ) && !safeCells.contains(adjX to adjY)
                ) {
                    safeCells.add(adjX to adjY)
                    madeProgress = true
                }
            }
        }

        return madeProgress
    }

    /**
     * Gets all adjacent cells within bounds.
     */
    private fun getAdjacentCells(x: Int, y: Int): List<Pair<Int, Int>> {
        return directionsOfAdjacentCells
            .map { (dx, dy) -> x + dx to y + dy }
            .filter { (adjX, adjY) -> isCellWithinBounds(adjX, adjY) }
    }

    /**
     * Reveals all cells known to be safe.
     */
    private fun revealSafeCells(): Boolean {
        if (safeCells.isEmpty()) {
            return false
        }

        var madeProgress = false

        val cellsToReveal = safeCells.toList()
        safeCells.clear()

        for ((x, y) in cellsToReveal) {
            if (!gameState.isRevealed(x, y)) {
                gameState.reveal(x, y)
                madeProgress = true
            }
        }

        return madeProgress
    }

    /**
     * Flags all cells known to contain mines.
     */
    private fun flagMineCells(): Boolean {
        if (mineCells.isEmpty()) {
            return false
        }

        var madeProgress = false

        val cellsToFlag = mineCells.toList()
        mineCells.clear()

        for ((x, y) in cellsToFlag) {
            if (!gameState.isFlagged(x, y)) {
                gameState.toggleFlag(x, y)
                madeProgress = true
            }
        }

        return madeProgress
    }

    /**
     * Checks if a cell is within the bounds of the minefield.
     */
    private fun isCellWithinBounds(x: Int, y: Int): Boolean =
        x in 0 until minefield.width && y in 0 until minefield.height

    companion object {
        /**
         * Checks if a minefield is solvable without taking 50:50 chances.
         */
        fun isSolvable(minefield: Minefield): Boolean {
            return MinefieldSolver(minefield).isSolvable()
        }

        /**
         * Generates a solvable minefield with the given configuration.
         * Keeps generating minefields until a solvable one is found or max attempts is reached.
         * Uses the provided seed as a starting point.
         *
         * @param config The game configuration
         * @param seed The starting seed for minefield generation
         * @return A solvable minefield or the last generated minefield if max attempts is reached
         */
        fun generateSolvableMinefield(config: GameConfig, seed: Int = 1): Minefield {
            var currentSeed = seed
            var minefield: Minefield
            val maxAttempts = 1000 /* Limit attempts to prevent infinite loops */
            var attempts = 0

            do {
                minefield = Minefield.create(config, currentSeed)
                currentSeed++
                attempts++

                /* Print debug info every 100 attempts */
                if (attempts % 100 == 0) {
                    println("[DEBUG_LOG] Attempted $attempts minefields with difficulty ${config.difficulty}")
                }

                /* Break after max attempts to prevent infinite loops */
                if (attempts >= maxAttempts) {
                    println("[DEBUG_LOG] Reached maximum attempts ($maxAttempts). Returning last generated minefield.")
                    break
                }
            } while (!isSolvable(minefield))

            return minefield
        }
    }
}
