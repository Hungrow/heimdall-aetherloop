package com.heimdall.aetherloop.ui.components

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.heimdall.aetherloop.R
import com.heimdall.aetherloop.game.engine.VikingArkanoidEngine
import com.heimdall.aetherloop.game.model.*
import com.heimdall.aetherloop.ui.theme.*
import kotlin.math.sin
import kotlin.random.Random

/**
 * High-performance hardware-accelerated Arkanoid Canvas with Custom Sprites,
 * Ship Evolution Tiers, Flaming Cannonballs, Procedural Maps, and Viking Einherjars.
 */
@Composable
fun ArkanoidCanvasView(
    engine: VikingArkanoidEngine,
    frameTick: Long,
    modifier: Modifier = Modifier
) {
    // Ship Sprites
    val spriteScout = ImageBitmap.imageResource(id = R.drawable.ship_scout)
    val spriteDrakkar = ImageBitmap.imageResource(id = R.drawable.ship_drakkar)
    val spriteFrost = ImageBitmap.imageResource(id = R.drawable.ship_frost)
    val spriteFire = ImageBitmap.imageResource(id = R.drawable.ship_fire)
    val spriteNaglfar = ImageBitmap.imageResource(id = R.drawable.ship_naglfar)

    // Paddle Evolution Tier Sprites
    val spritePaddleTier1 = ImageBitmap.imageResource(id = R.drawable.paddle_tier1)
    val spritePaddleTier2 = ImageBitmap.imageResource(id = R.drawable.paddle_tier2)
    val spritePaddleTier3 = ImageBitmap.imageResource(id = R.drawable.paddle_flagship)
    val spritePaddleTier4 = ImageBitmap.imageResource(id = R.drawable.paddle_tier4)

    val spriteJarl = ImageBitmap.imageResource(id = R.drawable.viking_jarl)
    val spriteWarrior = ImageBitmap.imageResource(id = R.drawable.viking_warrior)
    val spriteEnemy = ImageBitmap.imageResource(id = R.drawable.viking_enemy)
    val spriteCannonball = ImageBitmap.imageResource(id = R.drawable.cannonball_flaming)

    val spriteRuneMjolnir = ImageBitmap.imageResource(id = R.drawable.rune_mjolnir)
    val spriteRuneMultiball = ImageBitmap.imageResource(id = R.drawable.rune_multiball)
    val spriteRuneFire = ImageBitmap.imageResource(id = R.drawable.rune_fire)
    val spriteRuneBallistas = ImageBitmap.imageResource(id = R.drawable.rune_ballistas)
    val spriteRuneShield = ImageBitmap.imageResource(id = R.drawable.rune_shield)
    val spriteRuneValkyrie = ImageBitmap.imageResource(id = R.drawable.rune_valkyrie)
    val spriteRuneFrost = ImageBitmap.imageResource(id = R.drawable.rune_frost)
    val spriteRuneSpear = ImageBitmap.imageResource(id = R.drawable.rune_spear)
    val spriteRuneVortex = ImageBitmap.imageResource(id = R.drawable.rune_vortex)
    val spriteRuneLoki = ImageBitmap.imageResource(id = R.drawable.rune_loki)
    val spriteRuneFrozenKeel = ImageBitmap.imageResource(id = R.drawable.rune_frozen_keel)
    val spriteRunePowderKeg = ImageBitmap.imageResource(id = R.drawable.rune_powder_keg)

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    engine.dragPaddle(dragAmount.x)
                }
            }
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    engine.handleScreenTap(offset)
                }
            }
    ) {
        val currentTick = frameTick
        val timeSec = currentTick / 60.0f

        val w = size.width
        val h = size.height

        val shake = engine.collisionEngine.screenShakeAmount
        val shakeX = if (shake > 0f) (Random.nextFloat() - 0.5f) * shake * 2f else 0f
        val shakeY = if (shake > 0f) (Random.nextFloat() - 0.5f) * shake * 2f else 0f

        translate(left = shakeX, top = shakeY) {
            // 1. Procedural Map & Oceanic Sea
            drawProceduralBattleMap(w, h, engine.battleMap.value, timeSec, engine.floatingScenery)

            // 1b. Dynamic Ocean Swells & Projectile-Blocking Crests
            drawOceanSwells(w, engine.oceanSwells, engine.battleMap.value, timeSec)

            // 2. Water Ripples
            drawWaterRipples(engine.collisionEngine.waterRipples)

            // 3. Enemy Viking Longships
            for (ship in engine.enemyShips) {
                val sprite = when (ship.type) {
                    ShipType.SCOUT -> spriteScout
                    ShipType.ARMORED_DRAKKAR -> spriteDrakkar
                    ShipType.FROST_BRIG -> spriteFrost
                    ShipType.FIRE_DREADNOUGHT -> spriteFire
                    ShipType.BOSS_JORMUNGANDR -> spriteDrakkar
                    ShipType.BOSS_SURTR -> spriteFire
                    ShipType.BOSS_NIDHOGGR -> spriteFrost
                    ShipType.BOSS_NAGLFAR -> spriteNaglfar
                }
                drawVikingLongship(ship, sprite, spriteEnemy, timeSec, engine.frostNovaTimer > 0f)
            }

            // 4. Enemy Flaming Cannonballs (High Visibility with Fire Glow & Smoke)
            for (cb in engine.cannonBalls) {
                // Flaming Outer Corona
                drawCircle(
                    color = Color(0xFFFF3D00).copy(alpha = 0.5f),
                    radius = cb.radius * 2.4f,
                    center = Offset(cb.x, cb.y)
                )
                drawCircle(
                    color = Color(0xFFFFD600).copy(alpha = 0.8f),
                    radius = cb.radius * 1.5f,
                    center = Offset(cb.x, cb.y)
                )

                // Cannonball Sprite
                val cbSize = (cb.radius * 3.2f).toInt()
                drawImage(
                    image = spriteCannonball,
                    dstOffset = IntOffset((cb.x - cbSize / 2f).toInt(), (cb.y - cbSize / 2f).toInt()),
                    dstSize = IntSize(cbSize, cbSize)
                )

                // Smoke / Fire Trail behind cannonball
                drawCircle(
                    color = Color(0x66FF5722),
                    radius = cb.radius * 1.1f,
                    center = Offset(cb.x, cb.y - 12f)
                )
                drawCircle(
                    color = Color(0x334E342E),
                    radius = cb.radius * 0.9f,
                    center = Offset(cb.x, cb.y - 22f)
                )
            }

            // 5. Laser Projectiles (High-Visibility Radiant Electric Cyan & Gold)
            for (proj in engine.projectiles) {
                // Outer Cyan Energy Bloom
                drawCircle(
                    color = Color(0xFF00E5FF).copy(alpha = 0.45f),
                    radius = 11f,
                    center = Offset(proj.x, proj.y)
                )
                // Middle Golden Core
                drawCircle(
                    color = HeimdallGold,
                    radius = 6f,
                    center = Offset(proj.x, proj.y)
                )
                // Inner White Flash
                drawCircle(
                    color = Color.White,
                    radius = 3.5f,
                    center = Offset(proj.x, proj.y)
                )

                // Dual Energy Beam Trail
                drawLine(
                    color = Color(0xFF00E5FF).copy(alpha = 0.85f),
                    start = Offset(proj.x, proj.y),
                    end = Offset(proj.x, proj.y + 24f),
                    strokeWidth = 6f
                )
                drawLine(
                    color = Color.White,
                    start = Offset(proj.x, proj.y),
                    end = Offset(proj.x, proj.y + 18f),
                    strokeWidth = 2.5f
                )
            }

            // 6. Falling Power-Up Runes (Beneficial Runes & Loki Curses)
            for (powerUp in engine.powerUps) {
                val runeSprite = when (powerUp.type) {
                    PowerUpType.MJOLNIR -> spriteRuneMjolnir
                    PowerUpType.MULTIBALL -> spriteRuneMultiball
                    PowerUpType.SURTR_FIRE -> spriteRuneFire
                    PowerUpType.BALLISTAS -> spriteRuneBallistas
                    PowerUpType.SHIELD_EXPAND -> spriteRuneShield
                    PowerUpType.REPAIR_LIFE -> spriteRuneValkyrie
                    PowerUpType.FROST_NOVA -> spriteRuneFrost
                    PowerUpType.GUNGNIR_SPEAR -> spriteRuneSpear
                    PowerUpType.VORTEX_MAGNET -> spriteRuneVortex
                    PowerUpType.LOKI_CURSE -> spriteRuneLoki
                    PowerUpType.FROZEN_KEEL -> spriteRuneFrozenKeel
                    PowerUpType.POWDER_KEG -> spriteRunePowderKeg
                }
                drawPowerUpRune(powerUp, runeSprite, timeSec)
            }

            // 7. Player Ship Flagship Paddle (with Evolution Tiers 1-4 & Visual Effects)
            val paddleSprite = when (engine.paddle.tier) {
                1 -> spritePaddleTier1
                2 -> spritePaddleTier2
                3 -> spritePaddleTier3
                else -> spritePaddleTier4
            }
            drawAsgardianPaddle(engine.paddle, paddleSprite, spriteJarl, engine.jarlCheerAnim.floatValue, timeSec)

            // 8. Viking Einherjar Boarding Warriors
            for (warrior in engine.vikingWarriors) {
                drawVikingWarrior(warrior, spriteWarrior, timeSec)
            }

            // 9. Rune Spheres (Balls)
            for (ball in engine.activeBalls) {
                drawRuneBall(ball, timeSec)
            }

            // 10. Ambient Weather Particles
            drawAmbientParticles(engine.ambientParticles)

            // 11. Wood Splinters & Explosions
            for (splinter in engine.collisionEngine.splinters) {
                drawCircle(
                    color = splinter.color.copy(alpha = splinter.alpha),
                    radius = splinter.size,
                    center = Offset(splinter.x, splinter.y)
                )
            }

            // 12. Floating Combat Text
            drawContext.canvas.nativeCanvas.apply {
                val paint = Paint().apply {
                    textSize = 26f
                    textAlign = Paint.Align.CENTER
                    typeface = Typeface.DEFAULT_BOLD
                    isAntiAlias = true
                }
                for (ft in engine.collisionEngine.floatingTexts) {
                    paint.color = ft.color.copy(alpha = ft.alpha).toArgb()
                    drawText(ft.text, ft.x, ft.y, paint)
                }
            }
        }
    }
}

private fun DrawScope.drawProceduralBattleMap(
    w: Float,
    h: Float,
    biome: MapBiome,
    timeSec: Float,
    sceneryList: List<FloatingScenery>
) {
    val horizonY = h * 0.10f

    // 1. Distant Scandinavian Coastal Horizon & Morning Sky
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                biome.skyColors.first(),
                biome.skyColors.last(),
                Color(0xFF2E5B82)
            ),
            startY = 0f,
            endY = horizonY
        ),
        topLeft = Offset.Zero,
        size = Size(w, horizonY)
    )

    // Layer 1: Distant Misty Fjord Mountain Peaks
    val mountainFar = Path()
    mountainFar.moveTo(0f, horizonY)
    mountainFar.lineTo(w * 0.10f, horizonY - 34f)
    mountainFar.lineTo(w * 0.22f, horizonY - 18f)
    mountainFar.lineTo(w * 0.38f, horizonY - 42f)
    mountainFar.lineTo(w * 0.55f, horizonY - 20f)
    mountainFar.lineTo(w * 0.72f, horizonY - 48f)
    mountainFar.lineTo(w * 0.88f, horizonY - 24f)
    mountainFar.lineTo(w, horizonY - 30f)
    mountainFar.lineTo(w, horizonY)
    mountainFar.close()
    drawPath(path = mountainFar, color = Color(0xFF1B3B5A).copy(alpha = 0.7f))

    // Layer 2: Near Fjord Cliffs with Forest/Rock Silhouette
    val mountainNear = Path()
    mountainNear.moveTo(0f, horizonY)
    mountainNear.lineTo(w * 0.16f, horizonY - 20f)
    mountainNear.lineTo(w * 0.32f, horizonY - 8f)
    mountainNear.lineTo(w * 0.48f, horizonY - 26f)
    mountainNear.lineTo(w * 0.65f, horizonY - 12f)
    mountainNear.lineTo(w * 0.85f, horizonY - 28f)
    mountainNear.lineTo(w, horizonY - 10f)
    mountainNear.lineTo(w, horizonY)
    mountainNear.close()
    drawPath(path = mountainNear, color = Color(0xFF0F263D).copy(alpha = 0.9f))

    // Horizon Sea Foam Surf line
    drawLine(
        color = Color(0x99E0F7FA),
        start = Offset(0f, horizonY),
        end = Offset(w, horizonY),
        strokeWidth = 3f
    )

    // 2. Realistic Open Oceanic Waters (from horizon down to bottom)
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFF0D3B66), // Translucent coastal azure
                Color(0xFF092E52), // Deep sapphire
                Color(0xFF05203A), // Abyssal naval blue
                Color(0xFF062A4A)  // Clear surface near player
            ),
            startY = horizonY,
            endY = h
        ),
        topLeft = Offset(0f, horizonY),
        size = Size(w, h - horizonY)
    )

    // Water Surface Caustics (Sunlight glistening on moving water)
    for (row in 0 until 6) {
        val cy = horizonY + (h - horizonY) * (0.15f + row * 0.14f)
        val phase = timeSec * 1.5f + row * 1.8f
        for (col in 0 until 5) {
            val cx = w * (0.1f + col * 0.2f) + sin(phase + col) * 16f
            drawCircle(
                color = Color(0x18FFFFFF),
                radius = 18f + sin(phase * 1.5f + col) * 6f,
                center = Offset(cx, cy)
            )
        }
    }

    // Floating Scenery (Icebergs, volcanic rocks, ghost wrecks)
    for (scenery in sceneryList) {
        val scColor = when (scenery.type) {
            SceneryType.ICEBERG -> Color(0xD0E0F7FA)
            SceneryType.VOLCANIC_ROCK -> Color(0xE03E2723)
            SceneryType.GHOST_WRECK -> Color(0xB04A148C)
            SceneryType.RUNIC_BUOY -> Color(0xE000B0FF)
        }
        rotate(degrees = scenery.rotation, pivot = Offset(scenery.x, scenery.y)) {
            // Water displacement shadow
            drawRoundRect(
                color = Color(0x33000000),
                topLeft = Offset(scenery.x - scenery.width * 0.45f, scenery.y - scenery.height * 0.35f + 4f),
                size = Size(scenery.width, scenery.height),
                cornerRadius = CornerRadius(8f, 8f)
            )
            drawRoundRect(
                color = scColor,
                topLeft = Offset(scenery.x - scenery.width / 2f, scenery.y - scenery.height / 2f),
                size = Size(scenery.width, scenery.height),
                cornerRadius = CornerRadius(8f, 8f)
            )
            drawCircle(
                color = Color(0x66FFFFFF),
                radius = scenery.width * 0.55f,
                center = Offset(scenery.x, scenery.y + scenery.height * 0.35f),
                style = Stroke(1.5f)
            )
        }
    }

    // Realistic Multi-Layer Ocean Wave Swells (Gentle visual waves that never block)
    val numWaves = 8
    for (i in 0 until numWaves) {
        val baseY = horizonY + (h - horizonY) * ((i + 1) / (numWaves + 1.2f))
        val waveSpeed = (i + 1) * 1.1f
        val waveAmp = 5f + i * 0.8f
        val path = Path()
        path.moveTo(0f, baseY)
        var x = 0f
        while (x <= w) {
            val yOffset = sin(x * 0.014f + timeSec * waveSpeed) * waveAmp
            path.lineTo(x, baseY + yOffset)
            x += 16f
        }
        // Wave crest foam line
        drawPath(
            path = path,
            color = Color(0x38FFFFFF),
            style = Stroke(width = 1.4f + (i * 0.2f))
        )
    }
}

/**
 * Draws rolling Ocean Swells.
 * Surges only once every 16 seconds for a brief ~1.1s blocking window.
 */
private fun DrawScope.drawOceanSwells(
    w: Float,
    swells: List<OceanSwell>,
    biome: MapBiome,
    timeSec: Float
) {
    for (swell in swells) {
        val y = swell.y
        val isSurging = swell.isBlocking
        val isWarning = swell.isWarning

        // 1. Warning Froth (2 seconds before surge)
        if (isWarning) {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.Transparent, Color(0x22FFD600), Color.Transparent),
                    startY = y - 20f,
                    endY = y + 20f
                ),
                topLeft = Offset(0f, y - 20f),
                size = Size(w, 40f)
            )
        }

        // 2. Surging Breaker Wave (Brief 1.1s blocking surge)
        if (isSurging) {
            // Translucent water curtain
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color(0x6600E5FF),
                        Color(0x4400B0FF),
                        Color.Transparent
                    ),
                    startY = y - swell.height * 1.2f,
                    endY = y + swell.height * 1.2f
                ),
                topLeft = Offset(0f, y - swell.height * 1.2f),
                size = Size(w, swell.height * 2.4f)
            )
        }

        // Foaming crest curve
        val wavePath = Path()
        wavePath.moveTo(0f, y)
        var x = 0f
        val crestAmp = if (isSurging) 11f else 4f
        while (x <= w) {
            val yOff = sin(x * 0.020f + timeSec * (if (isSurging) 5f else 2f) + swell.id) * crestAmp
            wavePath.lineTo(x, y + yOff)
            x += 14f
        }

        drawPath(
            path = wavePath,
            color = if (isSurging) Color.White else Color(0x44FFFFFF),
            style = Stroke(width = if (isSurging) 6f else 2f)
        )

        // Surging water spray droplets
        if (isSurging) {
            for (step in 0 until (w / 35f).toInt()) {
                val dropX = step * 35f + sin(timeSec * 8f + step) * 10f
                val dropY = y - 12f + sin(timeSec * 10f + step * 2f) * 8f
                drawCircle(
                    color = Color.White.copy(alpha = 0.9f),
                    radius = 3.5f,
                    center = Offset(dropX, dropY)
                )
            }
        }
    }
}

private fun DrawScope.drawWaterRipples(ripples: List<WaterRipple>) {
    for (r in ripples) {
        drawCircle(
            color = r.color.copy(alpha = r.alpha),
            radius = r.radius,
            center = Offset(r.x, r.y),
            style = Stroke(width = 2.5f)
        )
    }
}

private fun DrawScope.drawVikingLongship(
    ship: EnemyShip,
    shipSprite: ImageBitmap,
    enemySprite: ImageBitmap,
    timeSec: Float,
    isFrozen: Boolean = false
) {
    val left = ship.left
    val top = ship.top
    val w = ship.width
    val h = ship.height

    if (!ship.isSinking) {
        val wakeW = w * 0.8f
        drawLine(
            color = Color(0x3300E5FF),
            start = Offset(ship.x - wakeW / 2f, ship.bottom + 2f),
            end = Offset(ship.x + wakeW / 2f, ship.bottom + 2f),
            strokeWidth = 2f
        )
    }

    val alpha = if (ship.isSinking) (1f - ship.sinkProgress).coerceIn(0f, 1f) else 1f

    rotate(
        degrees = if (ship.isSinking) ship.tiltAngle * ship.sinkProgress else sin(timeSec * 2f + ship.id) * 2.5f,
        pivot = Offset(ship.x, ship.y)
    ) {
        drawImage(
            image = shipSprite,
            dstOffset = IntOffset(left.toInt(), top.toInt()),
            dstSize = IntSize(w.toInt(), h.toInt()),
            alpha = alpha,
            colorFilter = when {
                ship.hitFlashAnim > 0f -> ColorFilter.tint(Color.White, BlendMode.SrcAtop)
                isFrozen -> ColorFilter.tint(Color(0xFF80DEEA), BlendMode.SrcAtop)
                else -> null
            }
        )

        // Frost Ice Coating during Frost Nova
        if (isFrozen) {
            drawRoundRect(
                color = Color(0x55E0F7FA),
                topLeft = Offset(left, top),
                size = Size(w, h),
                cornerRadius = CornerRadius(6f, 6f),
                style = Stroke(2.5f)
            )
        }

        if (!ship.isSinking && ship.type != ShipType.SCOUT) {
            val raiderSize = (h * 0.45f).coerceIn(16f, 28f).toInt()
            drawImage(
                image = enemySprite,
                dstOffset = IntOffset((ship.x - raiderSize / 2f).toInt(), (top + h * 0.15f).toInt()),
                dstSize = IntSize(raiderSize, raiderSize)
            )
        }
    }

    // Health Bar
    if (ship.type.maxHp > 1 && !ship.isSinking) {
        val hpBarW = w * 0.8f
        val hpBarH = if (ship.type.isBoss) 7f else 4.5f
        val hpLeft = ship.x - hpBarW / 2f
        val hpTop = top - (if (ship.type.isBoss) 12f else 8f)
        val hpRatio = ship.currentHp.toFloat() / ship.type.maxHp.toFloat()

        drawRoundRect(
            color = CosmosCard,
            topLeft = Offset(hpLeft, hpTop),
            size = Size(hpBarW, hpBarH),
            cornerRadius = CornerRadius(2f, 2f)
        )
        drawRoundRect(
            color = ship.type.primaryColor,
            topLeft = Offset(hpLeft, hpTop),
            size = Size(hpBarW * hpRatio, hpBarH),
            cornerRadius = CornerRadius(2f, 2f)
        )
        drawRoundRect(
            color = Color.Black.copy(alpha = 0.5f),
            topLeft = Offset(hpLeft, hpTop),
            size = Size(hpBarW, hpBarH),
            cornerRadius = CornerRadius(2f, 2f),
            style = Stroke(1f)
        )
    }

    if (ship.hitFlashAnim > 0f) {
        ship.hitFlashAnim = (ship.hitFlashAnim - 0.12f).coerceAtLeast(0f)
    }
}

private fun DrawScope.drawAsgardianPaddle(
    paddle: Paddle,
    paddleSprite: ImageBitmap,
    jarlSprite: ImageBitmap,
    jarlCheer: Float,
    timeSec: Float
) {
    val left = paddle.left
    val top = paddle.top
    val w = paddle.width
    val h = paddle.height

    // Water Foam Wake
    drawCircle(
        color = Color(0x4400E5FF),
        radius = 12f,
        center = Offset(left + 6f, top + h * 0.5f)
    )
    drawCircle(
        color = Color(0x4400E5FF),
        radius = 12f,
        center = Offset(paddle.right - 6f, top + h * 0.5f)
    )

    // Visual Status Effects under ship:
    // 1. Vortex Magnet Aura
    if (paddle.isMagnetActive) {
        drawCircle(
            color = Color(0x4400E676),
            radius = w * 0.65f,
            center = Offset(paddle.centerX, top + h * 0.5f),
            style = Stroke(2.5f)
        )
    }

    // 2. Frozen Keel Ice effect
    if (paddle.isKeelFrozen) {
        drawRoundRect(
            color = Color(0x7780DEEA),
            topLeft = Offset(left - 6f, top + h * 0.6f),
            size = Size(w + 12f, 14f),
            cornerRadius = CornerRadius(4f, 4f)
        )
    }

    // 3. Loki's Curse Aura
    if (paddle.isLokiCursed) {
        drawCircle(
            color = Color(0x66E040FB),
            radius = w * 0.55f,
            center = Offset(paddle.centerX, top + h * 0.5f),
            style = Stroke(3f)
        )
    }

    // Draw Ship Tier Sprite (with hit flash / curse tint)
    drawImage(
        image = paddleSprite,
        dstOffset = IntOffset(left.toInt(), top.toInt()),
        dstSize = IntSize(w.toInt(), h.toInt()),
        colorFilter = when {
            paddle.flashAnim > 0f -> ColorFilter.tint(Color.White, BlendMode.SrcAtop)
            paddle.isLokiCursed -> ColorFilter.tint(Color(0xFFEA80FC), BlendMode.SrcAtop)
            else -> null
        }
    )

    // Jarl Commander on deck
    val jarlSize = (h * 1.35f).toInt()
    val jarlBounce = sin(timeSec * 4f) * 2f - (jarlCheer * 8f)
    drawImage(
        image = jarlSprite,
        dstOffset = IntOffset((paddle.centerX - jarlSize / 2f).toInt(), (top - jarlSize * 0.65f + jarlBounce).toInt()),
        dstSize = IntSize(jarlSize, jarlSize)
    )

    // Mounted Ballistas
    if (paddle.hasBallistas) {
        val turretRadius = 8f
        drawCircle(
            color = ValkyrieViolet,
            radius = turretRadius,
            center = Offset(left + 14f, top)
        )
        drawCircle(
            color = Color.White,
            radius = 3.5f,
            center = Offset(left + 14f, top)
        )

        drawCircle(
            color = ValkyrieViolet,
            radius = turretRadius,
            center = Offset(paddle.right - 14f, top)
        )
        drawCircle(
            color = Color.White,
            radius = 3.5f,
            center = Offset(paddle.right - 14f, top)
        )
    }
}

private fun DrawScope.drawVikingWarrior(
    warrior: VikingWarrior,
    warriorSprite: ImageBitmap,
    timeSec: Float
) {
    val size = 46
    rotate(degrees = warrior.rotation, pivot = Offset(warrior.x, warrior.y)) {
        drawImage(
            image = warriorSprite,
            dstOffset = IntOffset((warrior.x - size / 2f).toInt(), (warrior.y - size / 2f).toInt()),
            dstSize = IntSize(size, size)
        )
    }

    if (warrior.state == WarriorState.SLASHING) {
        drawArc(
            color = HeimdallGold,
            startAngle = 180f,
            sweepAngle = 140f,
            useCenter = false,
            topLeft = Offset(warrior.x - 24f, warrior.y - 24f),
            size = Size(48f, 48f),
            style = Stroke(3.5f)
        )
    }
}

private fun DrawScope.drawPowerUpRune(
    p: PowerUpDrop,
    runeSprite: ImageBitmap,
    timeSec: Float
) {
    val size = (p.radius * 2.5f).toInt()
    val isCurse = p.type.isCurse

    if (isCurse) {
        // --- DOWNGRAVDE / MALEFICIO: MENACING SPIKED RED-PURPLE HAZARD ---
        val pulse = sin(timeSec * 7f) * 4f
        val spikeR = p.radius * 1.5f + pulse

        // Sinister toxic smoke
        drawCircle(
            color = Color(0x66D50000),
            radius = spikeR * 1.3f,
            center = Offset(p.x, p.y)
        )
        drawCircle(
            color = Color(0x559C27B0),
            radius = spikeR,
            center = Offset(p.x, p.y)
        )

        // Spiked warning diamond border
        val diamond = Path()
        diamond.moveTo(p.x, p.y - spikeR * 1.2f)
        diamond.lineTo(p.x + spikeR * 1.2f, p.y)
        diamond.lineTo(p.x, p.y + spikeR * 1.2f)
        diamond.lineTo(p.x - spikeR * 1.2f, p.y)
        diamond.close()
        drawPath(path = diamond, color = Color(0xFFFF1744), style = Stroke(3f))

        // Warning text banner above
        drawContext.canvas.nativeCanvas.apply {
            val paint = Paint().apply {
                textSize = 20f
                textAlign = Paint.Align.CENTER
                typeface = Typeface.DEFAULT_BOLD
                color = android.graphics.Color.parseColor("#FF1744")
                isAntiAlias = true
            }
            drawText("⚠️ PELIGRO", p.x, p.y - spikeR * 1.2f - 4f, paint)
        }
    } else {
        // --- UPGRADE / BENDICIÓN: CELESTIAL GOLDEN DIVINE MEDALLION ---
        val haloRadius = p.radius * (1.3f + sin(timeSec * 4f) * 0.15f)

        // Outer divine glow
        drawCircle(
            color = HeimdallGold.copy(alpha = 0.45f),
            radius = haloRadius * 1.3f,
            center = Offset(p.x, p.y)
        )
        drawCircle(
            color = p.type.color.copy(alpha = 0.5f),
            radius = haloRadius,
            center = Offset(p.x, p.y)
        )
        drawCircle(
            color = Color.White.copy(alpha = 0.7f),
            radius = haloRadius * 1.2f,
            center = Offset(p.x, p.y),
            style = Stroke(1.5f)
        )

        // Text banner above
        drawContext.canvas.nativeCanvas.apply {
            val paint = Paint().apply {
                textSize = 20f
                textAlign = Paint.Align.CENTER
                typeface = Typeface.DEFAULT_BOLD
                color = android.graphics.Color.parseColor("#FFD700")
                isAntiAlias = true
            }
            drawText("✨ MEJORA", p.x, p.y - haloRadius - 6f, paint)
        }
    }

    // Sprite image
    drawImage(
        image = runeSprite,
        dstOffset = IntOffset((p.x - size / 2f).toInt(), (p.y - size / 2f).toInt()),
        dstSize = IntSize(size, size)
    )
}

private fun DrawScope.drawRuneBall(ball: Ball, timeSec: Float) {
    val trail = ball.trailPoints
    for (i in trail.indices) {
        val pt = trail[i]
        val ratio = (i + 1).toFloat() / trail.size.toFloat()
        drawCircle(
            color = ball.type.color.copy(alpha = ratio * 0.45f),
            radius = ball.radius * ratio * 0.9f,
            center = Offset(pt.x, pt.y)
        )
    }

    drawCircle(
        color = ball.type.glowColor,
        radius = ball.radius * 2.4f,
        center = Offset(ball.x, ball.y)
    )

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color.White, ball.type.color),
            center = Offset(ball.x - 2f, ball.y - 2f),
            radius = ball.radius
        ),
        radius = ball.radius,
        center = Offset(ball.x, ball.y)
    )
}

private fun DrawScope.drawAmbientParticles(particles: List<AmbientParticle>) {
    for (p in particles) {
        drawCircle(
            color = p.color.copy(alpha = p.alpha),
            radius = p.size,
            center = Offset(p.x, p.y)
        )
    }
}
