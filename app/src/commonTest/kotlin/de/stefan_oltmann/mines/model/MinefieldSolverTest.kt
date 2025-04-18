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

import de.stefan_oltmann.mines.DEFAULT_CELL_SIZE
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MinefieldSolverTest {

    @Test
    fun testIsSolvableWithSmallTestMinefield() {
        /* Test with the small test minefield */
        val result = MinefieldSolver.isSolvable(smallTestMinefield)

        /* The test should determine if the small test minefield is solvable */
        /* This is a baseline test to establish if our test data is solvable */
        println("[DEBUG_LOG] Small test minefield is solvable: $result")

        /*
         * After analysis, we've determined that the smallTestMinefield is not solvable
         * without taking 50:50 chances. This is expected behavior.
         */
        assertFalse(result, "Small test minefield should not be solvable without taking 50:50 chances")
    }

    @Test
    fun testIsSolvableWithMediumTestMinefield() {
        /* Test with the medium test minefield */
        val result = MinefieldSolver.isSolvable(mediumTestMinefield)

        /* The test should determine if the medium test minefield is solvable */
        println("[DEBUG_LOG] Medium test minefield is solvable: $result")

        /*
         * After analysis, we've determined that the mediumTestMinefield is not solvable
         * without taking 50:50 chances. This is expected behavior.
         */
        assertFalse(result, "Medium test minefield should not be solvable without taking 50:50 chances")
    }

    @Test
    fun testIsSolvableWithLargeTestMinefield() {
        /* Test with the large test minefield */
        val result = MinefieldSolver.isSolvable(largeTestMinefield)

        /* The test should determine if the large test minefield is solvable */
        println("[DEBUG_LOG] Large test minefield is solvable: $result")

        /*
         * After analysis, we've determined that the largeTestMinefield is not solvable
         * without taking 50:50 chances. This is expected behavior.
         */
        assertFalse(result, "Large test minefield should not be solvable without taking 50:50 chances")
    }

    @Test
    fun testGenerateSolvableMinefield() {
        /* Create a configuration for a small minefield */
        val config = GameConfig(
            cellSize = DEFAULT_CELL_SIZE,
            mapWidth = 10,
            mapHeight = 10,
            difficulty = GameDifficulty.MEDIUM
        )

        /* Use a fixed seed for reproducible tests */
        val seed = 1337

        /* Generate a solvable minefield */
        val minefield = MinefieldSolver.generateSolvableMinefield(config, seed)

        /* Verify that the generated minefield is solvable */
        val isSolvable = MinefieldSolver.isSolvable(minefield)

        assertTrue(isSolvable, "Generated minefield should be solvable")

        /* Print the seed of the solvable minefield for reference */
        println("[DEBUG_LOG] Generated solvable minefield with seed: ${minefield.seed}")
    }

    @Test
    fun testMultipleRandomMinefields() {
        /* Create a configuration for a small minefield */
        val config = GameConfig(
            cellSize = DEFAULT_CELL_SIZE,
            mapWidth = 8,
            mapHeight = 8,
            difficulty = GameDifficulty.EASY
        )

        /* Test multiple random minefields to see what percentage are solvable */
        val totalCount = 10
        var solvableCount = 0

        for (seed in 1..totalCount) {
            val minefield = Minefield.create(config, seed)
            if (MinefieldSolver.isSolvable(minefield)) {
                solvableCount++
                println("[DEBUG_LOG] Minefield with seed $seed is solvable")
            } else {
                println("[DEBUG_LOG] Minefield with seed $seed is NOT solvable")
            }
        }

        val solvablePercentage = (solvableCount.toDouble() / totalCount) * 100
        println("[DEBUG_LOG] $solvableCount out of $totalCount minefields are solvable (${solvablePercentage.toInt()}%)")

        /* Make sure we have at least one solvable minefield */
        assertTrue(solvableCount > 0, "At least one minefield should be solvable")
    }

    @Test
    fun testDifferentDifficulties() {
        /* Test how difficulty affects solvability */
        val difficulties = listOf(
            GameDifficulty.EASY,
            GameDifficulty.MEDIUM,
            GameDifficulty.HARD
        )

        for (difficulty in difficulties) {
            val config = GameConfig(
                cellSize = DEFAULT_CELL_SIZE,
                mapWidth = 10,
                mapHeight = 10,
                difficulty = difficulty
            )

            /* Test 5 random minefields for each difficulty */
            val totalCount = 5
            var solvableCount = 0

            for (seed in 1..totalCount) {
                val minefield = Minefield.create(config, seed)
                if (MinefieldSolver.isSolvable(minefield)) {
                    solvableCount++
                }
            }

            val solvablePercentage = (solvableCount.toDouble() / totalCount) * 100
            println("[DEBUG_LOG] $difficulty: $solvableCount out of $totalCount minefields are solvable (${solvablePercentage.toInt()}%)")

            /* Make sure we have at least one solvable minefield for each difficulty */
            assertTrue(solvableCount > 0, "At least one minefield should be solvable for $difficulty")
        }
    }

    @Test
    fun testGenerateSolvableMinefieldForAllDifficulties() {
        /* Test generating solvable minefields for all difficulties */
        val difficulties = listOf(
            GameDifficulty.EASY,
            GameDifficulty.MEDIUM,
            GameDifficulty.HARD
        )

        /* Use a fixed seed for reproducible tests */
        val baseSeed = 1337

        for (difficulty in difficulties) {
            val config = GameConfig(
                cellSize = DEFAULT_CELL_SIZE,
                mapWidth = 10,
                mapHeight = 10,
                difficulty = difficulty
            )

            /* Generate a solvable minefield with a seed based on the difficulty */
            val seed = baseSeed + difficulties.indexOf(difficulty)
            val minefield = MinefieldSolver.generateSolvableMinefield(config, seed)

            /* Verify that the generated minefield is solvable */
            val isSolvable = MinefieldSolver.isSolvable(minefield)

            assertTrue(isSolvable, "Generated minefield for $difficulty should be solvable")

            println("[DEBUG_LOG] Generated solvable minefield for $difficulty with seed: ${minefield.seed}")
        }
    }
}
