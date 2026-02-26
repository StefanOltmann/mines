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
 * Difficulty tiers that map to a percentage of mines on the board.
 */
enum class GameDifficulty(
    private val minePercentage: Int,
    private val settingsKey: String
) {

    /** Lower mine density for a relaxed game. */
    EASY(10, "easy"),

    /** Balanced mine density for typical play. */
    MEDIUM(15, "medium"),

    /** Higher mine density for advanced play. */
    HARD(20, "hard");

    /**
     * Calculate mine count as a percentage of total cells.
     *
     * @param mapWidth Board width in cells.
     * @param mapHeight Board height in cells.
     */
    fun calcMineCount(mapWidth: Int, mapHeight: Int): Int {

        val cellCount = mapWidth * mapHeight

        return (cellCount * (minePercentage / 100f)).toInt().coerceAtLeast(1)
    }

    /**
     * Return the stable key used for persisting this difficulty in settings.
     *
     * This value stays constant even when enum names are obfuscated.
     */
    fun toSettingsValue(): String = settingsKey

    companion object {

        /**
         * Resolve a persisted difficulty value without relying on enum names.
         *
         * This keeps settings compatible with older builds that stored enum names,
         * while allowing ProGuard/R8 to freely obfuscate the enum.
         *
         * @param value Stored settings value, accepts legacy enum names as well.
         */
        fun fromSettingsValue(value: String?): GameDifficulty {

            val normalized = value?.trim()?.lowercase()

            return when (normalized) {
                EASY.settingsKey -> EASY
                MEDIUM.settingsKey -> MEDIUM
                HARD.settingsKey -> HARD
                else -> EASY
            }
        }
    }
}
