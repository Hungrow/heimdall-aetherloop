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
    val horizonY = h * 0.11f

    // 1. Distant Nordic Fjord Skyline (11% top of screen)
    drawRect(
        brush = Brush.verticalGradient(
            colors = biome.skyColors,
            startY = 0f,
            endY = horizonY
        ),
        topLeft = Offset.Zero,
        size = Size(w, horizonY)
    )

    // Distant mountain / cliff silhouettes on the horizon
    val mountainPath = Path()
    mountainPath.moveTo(0f, horizonY)
    mountainPath.lineTo(w * 0.12f, horizonY - 26f)
    mountainPath.lineTo(w * 0.28f, horizonY - 14f)
    mountainPath.lineTo(w * 0.44f, horizonY - 32f)
    mountainPath.lineTo(w * 0.62f, horizonY - 16f)
    mountainPath.lineTo(w * 0.82f, horizonY - 38f)
    mountainPath.lineTo(w, horizonY - 18f)
    mountainPath.lineTo(w, horizonY)
    mountainPath.close()
    drawPath(
        path = mountainPath,
        color = biome.skyColors.first().copy(alpha = 0.85f)
    )

    // Horizon mist light
    drawLine(
        color = biome.waveFoamColor.copy(alpha = 0.6f),
        start = Offset(0f, horizonY),
        end = Offset(w, horizonY),
        strokeWidth = 2.5f
    )

    // 2. Realistic Oceanic Sea (covering from horizon down to bottom 89%)
    drawRect(
        brush = Brush.verticalGradient(
            colors = biome.waterColors,
            startY = horizonY,
            endY = h
        ),
        topLeft = Offset(0f, horizonY),
        size = Size(w, h - horizonY)
    )

    // Floating Scenery (Icebergs, rocks, buoys)
    for (scenery in sceneryList) {
        val scColor = when (scenery.type) {
            SceneryType.ICEBERG -> Color(0xD0E0F7FA)
            SceneryType.VOLCANIC_ROCK -> Color(0xE03E2723)
            SceneryType.GHOST_WRECK -> Color(0xB04A148C)
            SceneryType.RUNIC_BUOY -> Color(0xE000B0FF)
        }
        rotate(degrees = scenery.rotation, pivot = Offset(scenery.x, scenery.y)) {
            drawRoundRect(
                color = scColor,
                topLeft = Offset(scenery.x - scenery.width / 2f, scenery.y - scenery.height / 2f),
                size = Size(scenery.width, scenery.height),
                cornerRadius = CornerRadius(8f, 8f)
            )
            drawCircle(
                color = biome.waveFoamColor,
                radius = scenery.width * 0.6f,
                center = Offset(scenery.x, scenery.y + scenery.height * 0.4f),
                style = Stroke(1.2f)
            )
        }
    }

    // Dynamic Ocean Sea Wavelets
    val numWaves = 9
    for (i in 0 until numWaves) {
        val baseY = horizonY + (h - horizonY) * ((i + 1) / (numWaves + 1.0f))
        val waveSpeed = (i + 1) * 1.3f
        val waveAmplitude = 5f + i * 0.9f
        val path = Path()
        path.moveTo(0f, baseY)
        var x = 0f
        while (x <= w) {
            val yOffset = sin(x * 0.016f + timeSec * waveSpeed) * waveAmplitude
            path.lineTo(x, baseY + yOffset)
            x += 16f
        }
        drawPath(
            path = path,
            color = biome.waveFoamColor.copy(alpha = 0.45f),
            style = Stroke(width = 1.6f + (i * 0.25f))
        )
    }
}

/**
 * Draws rolling Ocean Swells. When surging at peak crest (isBlocking = true),
 * it displays prominent whitecap crests and water spray that absorb projectiles!
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
        val crestColor = if (isSurging) Color.White else biome.waveFoamColor
        val alpha = if (isSurging) 0.92f else swell.crestAlpha * 0.5f

        // Surging water curtain band
        if (isSurging) {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color(0x5500E5FF),
                        Color(0x3300B0FF),
                        Color.Transparent
                    ),
                    startY = y - swell.height,
                    endY = y + swell.height
                ),
                topLeft = Offset(0f, y - swell.height),
                size = Size(w, swell.height * 2f)
            )
        }

        // Foaming wave crest line
        val wavePath = Path()
        wavePath.moveTo(0f, y)
        var x = 0f
        val crestAmp = if (isSurging) 9f else 5f
        while (x <= w) {
            val yOff = sin(x * 0.022f + timeSec * 4f + swell.id) * crestAmp
            wavePath.lineTo(x, y + yOff)
            x += 14f
        }
        drawPath(
            path = wavePath,
            color = crestColor.copy(alpha = alpha),
            style = Stroke(width = if (isSurging) 5.5f else 2.2f)
        )

        // Surging crest water spray droplets
        if (isSurging) {
            for (step in 0 until (w / 45f).toInt()) {
                val dropX = step * 45f + sin(timeSec * 6f + step) * 12f
                val dropY = y - 10f + sin(timeSec * 8f + step * 2f) * 6f
                drawCircle(
                    color = Color.White.copy(alpha = 0.85f),
                    radius = 3f,
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
    val size = (p.radius * 2.3f).toInt()
    val haloRadius = p.radius * (1.2f + sin(timeSec * 5f) * 0.15f)
    drawCircle(
        color = p.type.color.copy(alpha = 0.35f),
        radius = haloRadius,
        center = Offset(p.x, p.y)
    )

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
