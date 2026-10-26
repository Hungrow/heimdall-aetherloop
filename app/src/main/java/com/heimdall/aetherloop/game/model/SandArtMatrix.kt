package com.heimdall.aetherloop.game.model

import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin
import kotlin.random.Random

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
            3 -> generateDwarvenForge(cx, cy)
            4 -> generateMuspelCore(cx, cy)
            5 -> generateNiflheimSnowflake(cx, cy)
            6 -> generateYggdrasilTree(cx, cy)
            else -> generateEndlessProcedural(cx, cy)
        }

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
     * Realm 1: Idavoll - Sun of Asgard (Sol Invictus)
     */
    private fun generateSunDisc(cx: Float, cy: Float) {
        val radius = 7.8f
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
     * Realm 2: Bifröst - The Rainbow Crossing (Sol Gold & Astral Cyan)
     */
    private fun generateBifrostArch(cx: Float, cy: Float) {
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                val dx = (c - cx)
                val dy = (r - (cy + 4.5f))
                val dist = hypot(dx, dy)
                if (r <= cy + 4.5f) {
                    if (dist in 4.5f..7.5f) {
                        grid[r][c] = AetherColor.ASTRAL_CYAN
                    } else if (dist in 7.6f..10.5f) {
                        grid[r][c] = AetherColor.SOLAR_GOLD
                    }
                }
            }
        }
    }

    /**
     * Realm 3: Alfheim - Valkyrie Winged Rune (Gold, Cyan, Violet)
     */
    private fun generateValkyrieWings(cx: Float, cy: Float) {
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                val dx = kotlin.math.abs(c - cx)
                val dy = (r - cy)
                val dist = hypot(dx, dy)
                if (dist <= 9.5f) {
                    when {
                        dx < 2.5f -> grid[r][c] = AetherColor.SOLAR_GOLD
                        dx in 2.5f..5.8f && r > 3 -> grid[r][c] = AetherColor.ASTRAL_CYAN
                        dx in 5.8f..9.0f && r > 2 -> grid[r][c] = AetherColor.AURORA_VIOLET
                    }
                }
            }
        }
    }

    /**
     * Realm 4: Nidavellir - Dwarven Foundry (Cyan, Violet, Gold Cogwheel)
     */
    private fun generateDwarvenForge(cx: Float, cy: Float) {
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                val dx = kotlin.math.abs(c - cx)
                val dy = kotlin.math.abs(r - cy)
                val dist = hypot(dx, dy)
                // Cogwheel teeth & core
                if (dist <= 4.0f) {
                    grid[r][c] = AetherColor.SOLAR_GOLD
                } else if (dist <= 8.5f) {
                    val angle = kotlin.math.atan2(r - cy, c - cx)
                    val teeth = sin(angle * 6.0).toFloat()
                    if (teeth > -0.2f) {
                        grid[r][c] = if (dist <= 6.5f) AetherColor.AURORA_VIOLET else AetherColor.ASTRAL_CYAN
                    }
                }
            }
        }
    }

    /**
     * Realm 5: Muspelheim - Surtr Flame Core (Gold, Cyan, Violet, Crimson Flame)
     */
    private fun generateMuspelCore(cx: Float, cy: Float) {
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                val manhattan = kotlin.math.abs(c - cx) + kotlin.math.abs(r - cy)
                if (manhattan <= 9.8f) {
                    when {
                        manhattan <= 3.2f -> grid[r][c] = AetherColor.CRIMSON_FLAME
                        manhattan <= 5.8f -> grid[r][c] = AetherColor.AURORA_VIOLET
                        manhattan <= 7.8f -> grid[r][c] = AetherColor.ASTRAL_CYAN
                        else -> grid[r][c] = AetherColor.SOLAR_GOLD
                    }
                }
            }
        }
    }

    /**
     * Realm 6: Niflheim - Frost Mist Crystal (Cyan, Violet, Emerald, Crimson)
     */
    private fun generateNiflheimSnowflake(cx: Float, cy: Float) {
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                val dx = kotlin.math.abs(c - cx)
                val dy = kotlin.math.abs(r - cy)
                val dist = hypot(dx, dy)
                if (dist <= 9.2f) {
                    val onAxis = dx < 1.5f || dy < 1.5f
                    val onDiagonal = kotlin.math.abs(dx - dy) < 1.5f
                    if (onAxis || onDiagonal) {
                        when {
                            dist <= 3.5f -> grid[r][c] = AetherColor.ASTRAL_CYAN
                            dist <= 6.5f -> grid[r][c] = AetherColor.EMERALD_YGGDRASIL
                            else -> grid[r][c] = AetherColor.AURORA_VIOLET
                        }
                    }
                }
            }
        }
    }

    /**
     * Realm 7: Yggdrasil - The World Tree Convergence (All 5 Colors)
     */
    private fun generateYggdrasilTree(cx: Float, cy: Float) {
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                val dx = kotlin.math.abs(c - cx)
                val dy = r - cy
                val dist = hypot(dx, dy)
                if (dx <= 1.8f && r >= cy) {
                    grid[r][c] = AetherColor.SOLAR_GOLD
                } else if (dist <= 9.0f && r < cy + 2f) {
                    when {
                        r < cy - 4.5f -> grid[r][c] = AetherColor.EMERALD_YGGDRASIL
                        r < cy - 1.5f -> grid[r][c] = AetherColor.ASTRAL_CYAN
                        r < cy + 1.0f -> grid[r][c] = AetherColor.AURORA_VIOLET
                        else -> grid[r][c] = AetherColor.CRIMSON_FLAME
                    }
                }
            }
        }
    }

    /**
     * Mode: Endless / Nexus Incursion (Procedurally generated concentric mandala)
     */
    private fun generateEndlessProcedural(cx: Float, cy: Float) {
        val allColors = AetherColor.entries.toTypedArray()
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                val dist = hypot(c - cx, r - cy)
                if (dist in 1.5f..9.5f) {
                    val ringIndex = (dist / 2.0f).toInt() % allColors.size
                    grid[r][c] = allColors[ringIndex]
                }
            }
        }
    }

    fun drainGrainAtColumn(col: Int, targetColor: AetherColor? = null): AetherColor? {
        if (col !in 0 until cols) return null

        for (r in rows - 1 downTo 0) {
            val grain = grid[r][col]
            if (grain != null) {
                if (targetColor == null || grain == targetColor) {
                    grid[r][col] = null
                    remainingGrainCount--

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
}
