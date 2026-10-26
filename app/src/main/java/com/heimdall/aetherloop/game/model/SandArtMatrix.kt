package com.heimdall.aetherloop.game.model

import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

/**
 * Sand Art Matrix (Mosaic of Celestial Grains).
 *
 * Models the sacred sand-art relic composed of hundreds of physical grains.
 * As buckets pass under the discharge funnels, the artwork is deconstructed
 * grain by grain with realistic granular settling physics.
 */
class SandArtMatrix(
    val cols: Int = 26,
    val rows: Int = 22
) {
    // 2D grid storing the color of each grain (null if cleared)
    val grid: Array<Array<AetherColor?>> = Array(rows) { Array(cols) { null } }

    var initialGrainCount: Int = 0
        private set
    var remainingGrainCount: Int = 0
        private set

    val clearedRatio: Float
        get() = if (initialGrainCount > 0) {
            1.0f - (remainingGrainCount.toFloat() / initialGrainCount.toFloat())
        } else 1.0f

    /**
     * Loads the rune sand-art pattern corresponding to the given realm level.
     */
    fun loadPattern(levelIndex: Int) {
        // Clear existing grid
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                grid[r][c] = null
            }
        }

        val cx = (cols - 1) / 2f
        val cy = (rows - 1) / 2f

        when (levelIndex) {
            0 -> generateSunDisc(cx, cy)
            1 -> generateBifrostArch(cx, cy)
            2 -> generateValkyrieWings(cx, cy)
            3 -> generateMuspelCore(cx, cy)
            else -> generateYggdrasilTree(cx, cy)
        }

        // Count initial grains
        var count = 0
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                if (grid[r][c] != null) count++
            }
        }
        initialGrainCount = count
        remainingGrainCount = count
    }

    /**
     * Level 1: Sun of Asgard (Sol Invictus)
     */
    private fun generateSunDisc(cx: Float, cy: Float) {
        val radius = 7.5f
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                val dist = hypot(c - cx, r - cy)
                if (dist <= radius) {
                    grid[r][c] = AetherColor.SOLAR_GOLD
                }
            }
        }
    }

    /**
     * Level 2: Bifrost Arches (Dual Sol Gold and Astral Cyan rainbow bridge)
     */
    private fun generateBifrostArch(cx: Float, cy: Float) {
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                val dx = (c - cx)
                val dy = (r - (cy + 4f))
                val dist = hypot(dx, dy)
                if (r <= cy + 4f) {
                    if (dist in 4.5f..7.2f) {
                        grid[r][c] = AetherColor.ASTRAL_CYAN
                    } else if (dist in 7.3f..10.0f) {
                        grid[r][c] = AetherColor.SOLAR_GOLD
                    }
                }
            }
        }
    }

    /**
     * Level 3: Valkyrie Winged Rune (Gold, Cyan, Aurora Violet)
     */
    private fun generateValkyrieWings(cx: Float, cy: Float) {
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                val dx = kotlin.math.abs(c - cx)
                val dy = (r - cy)
                val dist = hypot(dx, dy)
                if (dist <= 9f) {
                    when {
                        dx < 2.5f -> grid[r][c] = AetherColor.SOLAR_GOLD
                        dx in 2.5f..5.5f && r > 4 -> grid[r][c] = AetherColor.ASTRAL_CYAN
                        dx in 5.5f..8.5f && r > 2 -> grid[r][c] = AetherColor.AURORA_VIOLET
                    }
                }
            }
        }
    }

    /**
     * Level 4: Muspelheim Core (Sol, Cyan, Violet, Crimson Flame)
     */
    private fun generateMuspelCore(cx: Float, cy: Float) {
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                val manhattan = kotlin.math.abs(c - cx) + kotlin.math.abs(r - cy)
                if (manhattan <= 9.5f) {
                    when {
                        manhattan <= 3.0f -> grid[r][c] = AetherColor.CRIMSON_FLAME
                        manhattan <= 5.5f -> grid[r][c] = AetherColor.AURORA_VIOLET
                        manhattan <= 7.5f -> grid[r][c] = AetherColor.ASTRAL_CYAN
                        else -> grid[r][c] = AetherColor.SOLAR_GOLD
                    }
                }
            }
        }
    }

    /**
     * Level 5: Yggdrasil World Tree (All 5 Celestial Frequencies)
     */
    private fun generateYggdrasilTree(cx: Float, cy: Float) {
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                val dx = kotlin.math.abs(c - cx)
                val dy = r - cy
                val dist = hypot(dx, dy)
                // Tree trunk & roots
                if (dx <= 1.5f && r >= cy) {
                    grid[r][c] = AetherColor.SOLAR_GOLD
                } else if (dist <= 8.5f && r < cy + 2f) {
                    // Foliage foliage layers
                    when {
                        r < cy - 4f -> grid[r][c] = AetherColor.EMERALD_YGGDRASIL
                        r < cy - 1f -> grid[r][c] = AetherColor.ASTRAL_CYAN
                        r < cy + 1f -> grid[r][c] = AetherColor.AURORA_VIOLET
                        else -> grid[r][c] = AetherColor.CRIMSON_FLAME
                    }
                }
            }
        }
    }

    /**
     * Attempts to drain a grain from the bottom of a specific column.
     * When drained, the sand column above settles down automatically.
     */
    fun drainGrainAtColumn(col: Int, targetColor: AetherColor? = null): AetherColor? {
        if (col !in 0 until cols) return null

        // Search from bottom row up to find lowest grain in this column
        for (r in rows - 1 downTo 0) {
            val grain = grid[r][col]
            if (grain != null) {
                if (targetColor == null || grain == targetColor) {
                    grid[r][col] = null
                    remainingGrainCount--

                    // Collapse sand column above this row down by 1
                    for (k in r downTo 1) {
                        grid[k][col] = grid[k - 1][col]
                    }
                    grid[0][col] = null

                    return grain
                }
            }
        }
        return null
    }

    /**
     * Finds the lowest available grain color in a range of columns near a funnel.
     */
    fun peekLowestGrainColor(colStart: Int, colEnd: Int): AetherColor? {
        val start = colStart.coerceIn(0, cols - 1)
        val end = colEnd.coerceIn(0, cols - 1)
        for (r in rows - 1 downTo 0) {
            for (c in start..end) {
                val grain = grid[r][c]
                if (grain != null) return grain
            }
        }
        return null
    }
}
