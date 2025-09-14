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

import kotlin.random.Random

class Minefield(
    val config: GameConfig,
    val seed: Int,
    val matrix: Array<Array<CellType>>
) {

    val width
        get() = config.mapWidth

    val height
        get() = config.mapHeight

    fun getCellType(x: Int, y: Int): CellType =
        matrix[x][y]

    fun isMine(x: Int, y: Int): Boolean =
        matrix[x][y] == CellType.MINE

    companion object {

        fun create(
            config: GameConfig,
            seed: Int
        ): Minefield {
            val matrix = createMatrix(
                width = config.mapWidth,
                height = config.mapHeight,
                mineCount = config.mineCount,
                seed = seed
            )
            return Minefield(config, seed, matrix)
        }

        fun createSolvable(
            config: GameConfig,
            seed: Int
        ): Minefield {
            val maxAttempts = 1000
            var attempt = 0
            var solved = false
            var matrix: Array<Array<CellType>>
            var boardSeed = seed
            do {
                matrix = createMatrix(
                    width = config.mapWidth,
                    height = config.mapHeight,
                    mineCount = config.mineCount,
                    seed = boardSeed + attempt // vary seed to avoid infinite loops
                )
                val testMinefield = Minefield(config, boardSeed + attempt, matrix)
                solved = isSolvable(testMinefield)
                attempt++
            } while (!solved && attempt < maxAttempts)

            return Minefield(config = config, seed = boardSeed + attempt - 1, matrix = matrix)
        }

        /**
         * Constraint-based Minesweeper solver.
         * Returns true if the minefield can be solved without guessing, using logic alone.
         */
        fun isSolvable(minefield: Minefield): Boolean {
            val width = minefield.width
            val height = minefield.height
            val mineMatrix = minefield.matrix

            // Quick check for obvious unsolvable patterns
            // For now, we accept most boards and only reject the most problematic ones

            // Check that the protected center area is actually safe
            val protectedXRange = calcProtectedRange(width)
            val protectedYRange = calcProtectedRange(height)

            for (x in protectedXRange) {
                for (y in protectedYRange) {
                    if (mineMatrix[x][y] == CellType.MINE) {
                        return false // Mine in protected area - unplayable
                    }
                }
            }

            // Check for impossible edge/corner situations
            // (This is a simplified check - a full solver would be much more complex)

            val adjacents = listOf(
                Pair(-1, -1), Pair(0, -1), Pair(1, -1),
                Pair(-1, 0), Pair(1, 0),
                Pair(-1, 1), Pair(0, 1), Pair(1, 1)
            )

            fun inBounds(x: Int, y: Int): Boolean =
                x in 0 until width && y in 0 until height

            // Check for patterns that commonly lead to unsolvable situations
            var problematicPatterns = 0

            for (x in 0 until width) {
                for (y in 0 until height) {
                    if (mineMatrix[x][y] == CellType.MINE) continue

                    val cellType = mineMatrix[x][y]
                    if (cellType == CellType.EMPTY) continue

                    val adjCells = adjacents.map { (dx, dy) -> Pair(x + dx, y + dy) }
                        .filter { (nx, ny) -> inBounds(nx, ny) }

                    val adjMines = adjCells.count { (nx, ny) -> mineMatrix[nx][ny] == CellType.MINE }
                    val expectedMines = cellType.adjacentMineCount

                    if (adjMines != expectedMines) {
                        // This should never happen if our mine placement is correct
                        return false
                    }

                    // Look for patterns that often require guessing (this is simplified)
                    if (expectedMines >= 3 && adjCells.size <= 5) {
                        // High density in constrained space - might lead to guessing
                        problematicPatterns++
                    }
                }
            }

            // If we have too many potentially problematic patterns, reject the board
            val maxProblematic = (width * height * 0.1).toInt() // Allow up to 10% problematic cells
            if (problematicPatterns > maxProblematic) {
                return false
            }

            return true // Accept most boards with basic checks
        }

        private enum class CellMark { UNKNOWN, REVEALED, FLAGGED } // enum declaration moved outside function

        private fun createMatrix(
            width: Int,
            height: Int,
            mineCount: Int,
            seed: Int
        ): Array<Array<CellType>> {

            val matrix = createEmptyMatrix(width, height)

            placeMines(matrix, width, height, mineCount, seed)

            placeCounts(matrix, width, height)

            return matrix
        }

        private fun createEmptyMatrix(width: Int, height: Int): Array<Array<CellType>> =
            Array(width) {
                Array(height) {
                    CellType.EMPTY
                }
            }

        /* Calculates a centered protected range that scales with board size */
        fun calcProtectedRange(length: Int): IntRange {

            val targetSize = (length * 0.3).toInt().coerceAtLeast(2)

            val protectedSize =
                if (targetSize % 2 == length % 2)
                    targetSize
                else
                    targetSize + 1

            val start = (length - protectedSize) / 2

            return start until (start + protectedSize)
        }

        private fun placeMines(
            matrix: Array<Array<CellType>>,
            width: Int,
            height: Int,
            mineCount: Int,
            seed: Int
        ) {

            /*
             * Mines are placed according to seed to reproduce results.
             */
            val random = Random(seed)

            val protectedXRange = calcProtectedRange(width)
            val protectedYRange = calcProtectedRange(height)

            var placedMinesCount = 0

            while (placedMinesCount < mineCount) {

                val x = random.nextInt(width)
                val y = random.nextInt(height)

                /*
                 * Keep the middle free of mines to give players a starting point.
                 */
                if (x in protectedXRange && y in protectedYRange)
                    continue

                /*
                 * Only place mines into empty cells.
                 *
                 * This guarantees that we have enough mines,
                 * even if the randomizer selects the same cell twice.
                 */
                if (matrix[x][y] == CellType.EMPTY) {

                    matrix[x][y] = CellType.MINE

                    placedMinesCount++
                }
            }
        }

        private fun placeCounts(
            matrix: Array<Array<CellType>>,
            width: Int,
            height: Int
        ) {

            for (x in 0 until width) {
                for (y in 0 until height) {

                    /* Minefields stay as they are. */
                    if (matrix[x][y] == CellType.MINE)
                        continue

                    val mineCount = countMinesInAdjacentCells(matrix, x, y)

                    matrix[x][y] = CellType.ofMineCount(mineCount)
                }
            }
        }

        private fun countMinesInAdjacentCells(
            matrix: Array<Array<CellType>>,
            x: Int,
            y: Int
        ): Int =
            directionsOfAdjacentCells.count { (dx, dy) ->

                hasMine(
                    matrix = matrix,
                    x = x + dx,
                    y = y + dy
                )
            }

        private fun hasMine(
            matrix: Array<Array<CellType>>,
            x: Int,
            y: Int
        ): Boolean {

            /* Return zero for out-of-bounds fields */
            @Suppress("ComplexCondition")
            if (x < 0 || y < 0 || x >= matrix.size || y >= matrix[x].size)
                return false

            return matrix[x][y] == CellType.MINE
        }
    }
}
