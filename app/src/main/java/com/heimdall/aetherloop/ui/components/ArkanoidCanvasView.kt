package com.heimdall.aetherloop.ui.components

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * High-performance hardware-accelerated Arkanoid Canvas with Custom Sprites,
 * Procedural Maps, Viking Einherjar Boarding Parties, and Dynamic Juice.
 */
@Composable
fun ArkanoidCanvasView(
    engine: VikingArkanoidEngine,
    frameTick: Long,
    modifier: Modifier = Modifier
) {
    // Cache high-detail custom sprites
    val spriteScout = ImageBitmap.imageResource(id = R.drawable.ship_scout)
    val spriteDrakkar = ImageBitmap.imageResource(id = R.drawable.ship_drakkar)
    val spriteFrost = ImageBitmap.imageResource(id = R.drawable.ship_frost)
    val spriteFire = ImageBitmap.imageResource(id = R.drawable.ship_fire)
    val spriteNaglfar = ImageBitmap.imageResource(id = R.drawable.ship_naglfar)
    val spriteFlagship = ImageBitmap.imageResource(id = R.drawable.paddle_flagship)
    val spriteJarl = ImageBitmap.imageResource(id = R.drawable.viking_jarl)
    val spriteWarrior = ImageBitmap.imageResource(id = R.drawable.viking_warrior)
    val spriteEnemy = ImageBitmap.imageResource(id = R.drawable.viking_enemy)

    val spriteRuneMjolnir = ImageBitmap.imageResource(id = R.drawable.rune_mjolnir)
    val spriteRuneMultiball = ImageBitmap.imageResource(id = R.drawable.rune_multiball)
    val spriteRuneFire = ImageBitmap.imageResource(id = R.drawable.rune_fire)
    val spriteRuneBallistas = ImageBitmap.imageResource(id = R.drawable.rune_ballistas)
    val spriteRuneShield = ImageBitmap.imageResource(id = R.drawable.rune_shield)
    val spriteRuneValkyrie = ImageBitmap.imageResource(id = R.drawable.rune_valkyrie)

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    engine.dragPaddle(engine.paddle.centerX + dragAmount.x)
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

        // Calculate Screen Shake
        val shake = engine.collisionEngine.screenShakeAmount
        val shakeX = if (shake > 0f) (Random.nextFloat() - 0.5f) * shake * 2f else 0f
        val shakeY = if (shake > 0f) (Random.nextFloat() - 0.5f) * shake * 2f else 0f

        translate(left = shakeX, top = shakeY) {
            // 1. Draw Procedural Map & Atmospheric Ocean/Sky
            drawProceduralBattleMap(w, h, engine.battleMap.value, timeSec, engine.floatingScenery)

            // 2. Draw Water Ripples
            drawWaterRipples(engine.collisionEngine.waterRipples)

            // 3. Draw Enemy Viking Longships with custom sprites
            for (ship in engine.enemyShips) {
                val sprite = when (ship.type) {
                    ShipType.SCOUT -> spriteScout
                    ShipType.ARMORED_DRAKKAR -> spriteDrakkar
                    ShipType.FROST_BRIG -> spriteFrost
                    ShipType.FIRE_DREADNOUGHT -> spriteFire
                    ShipType.NAGLFAR_BOSS -> spriteNaglfar
                }
                drawVikingLongship(ship, sprite, spriteEnemy, timeSec)
            }

            // 4. Draw Laser Projectiles
            for (proj in engine.projectiles) {
                drawCircle(
                    color = ValkyrieViolet,
                    radius = 5f,
                    center = Offset(proj.x, proj.y)
                )
                drawLine(
                    color = ValkyrieViolet.copy(alpha = 0.8f),
                    start = Offset(proj.x, proj.y),
                    end = Offset(proj.x, proj.y + 18f),
                    strokeWidth = 3.5f
                )
            }

            // 5. Draw Falling Power-Up Runes
            for (powerUp in engine.powerUps) {
                val runeSprite = when (powerUp.type) {
                    PowerUpType.MJOLNIR -> spriteRuneMjolnir
                    PowerUpType.MULTIBALL -> spriteRuneMultiball
                    PowerUpType.SURTR_FIRE -> spriteRuneFire
                    PowerUpType.BALLISTAS -> spriteRuneBallistas
                    PowerUpType.SHIELD_EXPAND -> spriteRuneShield
                    PowerUpType.REPAIR_LIFE -> spriteRuneValkyrie
                }
                drawPowerUpRune(powerUp, runeSprite, timeSec)
            }

            // 6. Draw Asgardian Warship Flagship Paddle with Viking Jarl
            drawAsgardianPaddle(engine.paddle, spriteFlagship, spriteJarl, engine.jarlCheerAnim.floatValue, timeSec)

            // 7. Draw Viking Einherjar Boarding Warriors
            for (warrior in engine.vikingWarriors) {
                drawVikingWarrior(warrior, spriteWarrior, timeSec)
            }

            // 8. Draw Rune Spheres (Balls) with Comet Stardust Trails
            for (ball in engine.activeBalls) {
                drawRuneBall(ball, timeSec)
            }

            // 9. Draw Ambient Weather Particles (Snow, Embers, Stardust)
            drawAmbientParticles(engine.ambientParticles)

            // 10. Draw Wood Splinters & Explosions
            for (splinter in engine.collisionEngine.splinters) {
                drawCircle(
                    color = splinter.color.copy(alpha = splinter.alpha),
                    radius = splinter.size,
                    center = Offset(splinter.x, splinter.y)
                )
            }

            // 11. Draw Floating Combat Text
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
    // 1. Sky Gradient
    drawRect(
        brush = Brush.verticalGradient(
            colors = biome.skyColors,
            startY = 0f,
            endY = h * 0.45f
        ),
        topLeft = Offset.Zero,
        size = Size(w, h * 0.45f)
    )

    // 2. Aurora Borealis / Cosmic Ribbon
    val auroraAlpha = (sin(timeSec * 0.8f) * 0.15f + 0.25f).coerceIn(0.1f, 0.4f)
    when (biome) {
        MapBiome.NIFLHEIM_ICE -> {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF00E5FF).copy(alpha = auroraAlpha), Color.Transparent),
                    center = Offset(w * 0.4f, h * 0.10f),
                    radius = w * 0.7f
                )
            )
        }
        MapBiome.MUSPELHEIM_MAGMA -> {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFFF5722).copy(alpha = auroraAlpha), Color.Transparent),
                    center = Offset(w * 0.5f, h * 0.08f),
                    radius = w * 0.75f
                )
            )
        }
        MapBiome.HELHEIM_ABYSS -> {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFD500F9).copy(alpha = auroraAlpha), Color.Transparent),
                    center = Offset(w * 0.6f, h * 0.12f),
                    radius = w * 0.65f
                )
            )
        }
        MapBiome.BIFROST_ASTRAL -> {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFFFD700).copy(alpha = auroraAlpha), Color.Transparent),
                    center = Offset(w * 0.35f, h * 0.08f),
                    radius = w * 0.8f
                )
            )
        }
        else -> {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF00E5FF).copy(alpha = auroraAlpha), Color.Transparent),
                    center = Offset(w * 0.35f, h * 0.12f),
                    radius = w * 0.6f
                )
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFD500F9).copy(alpha = auroraAlpha * 0.8f), Color.Transparent),
                    center = Offset(w * 0.75f, h * 0.16f),
                    radius = w * 0.5f
                )
            )
        }
    }

    // 3. Water Deep Gradient
    drawRect(
        brush = Brush.verticalGradient(
            colors = biome.waterColors,
            startY = h * 0.45f,
            endY = h
        ),
        topLeft = Offset(0f, h * 0.45f),
        size = Size(w, h * 0.55f)
    )

    // 4. Floating Procedural Scenery (Icebergs, Volcanic rocks)
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
            // Ripple around scenery
            drawCircle(
                color = biome.waveFoamColor,
                radius = scenery.width * 0.6f,
                center = Offset(scenery.x, scenery.y + scenery.height * 0.4f),
                style = Stroke(1.2f)
            )
        }
    }

    // 5. Sinusoidal Animated Wave Lines
    val numWaves = 7
    for (i in 0 until numWaves) {
        val baseY = h * (0.48f + i * 0.075f)
        val waveSpeed = (i + 1) * 1.4f
        val waveAmplitude = 4f + i * 0.8f
        val path = Path()
        path.moveTo(0f, baseY)
        var x = 0f
        while (x <= w) {
            val yOffset = sin(x * 0.015f + timeSec * waveSpeed) * waveAmplitude
            path.lineTo(x, baseY + yOffset)
            x += 16f
        }
        drawPath(
            path = path,
            color = biome.waveFoamColor,
            style = Stroke(width = 1.5f + (i * 0.2f))
        )
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
    timeSec: Float
) {
    val left = ship.left
    val top = ship.top
    val w = ship.width
    val h = ship.height

    // Water wake behind active ships
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
        // Draw High-Detail Custom Ship Sprite
        drawImage(
            image = shipSprite,
            dstOffset = IntOffset(left.toInt(), top.toInt()),
            dstSize = IntSize(w.toInt(), h.toInt()),
            alpha = alpha,
            colorFilter = if (ship.hitFlashAnim > 0f) ColorFilter.tint(Color.White, BlendMode.SrcAtop) else null
        )

        // Draw small enemy Viking raider on deck
        if (!ship.isSinking && ship.type != ShipType.SCOUT) {
            val raiderSize = (h * 0.45f).coerceIn(16f, 28f).toInt()
            drawImage(
                image = enemySprite,
                dstOffset = IntOffset((ship.x - raiderSize / 2f).toInt(), (top + h * 0.15f).toInt()),
                dstSize = IntSize(raiderSize, raiderSize)
            )
        }
    }

    // Health Bar (if maxHp > 1 and not sinking)
    if (ship.type.maxHp > 1 && !ship.isSinking) {
        val hpBarW = w * 0.8f
        val hpBarH = 4.5f
        val hpLeft = ship.x - hpBarW / 2f
        val hpTop = top - 8f
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

    // Decay hit flash
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

    // Water Foam Wake at paddle ends
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

    // Draw Asgardian Battleship Paddle Sprite
    drawImage(
        image = paddleSprite,
        dstOffset = IntOffset(left.toInt(), top.toInt()),
        dstSize = IntSize(w.toInt(), h.toInt())
    )

    // Draw Viking Jarl Commander standing proudly at center of flagship deck
    val jarlSize = (h * 1.35f).toInt()
    val jarlBounce = sin(timeSec * 4f) * 2f - (jarlCheer * 8f)
    drawImage(
        image = jarlSprite,
        dstOffset = IntOffset((paddle.centerX - jarlSize / 2f).toInt(), (top - jarlSize * 0.65f + jarlBounce).toInt()),
        dstSize = IntSize(jarlSize, jarlSize)
    )

    // Mounted Ballista Turrets glow
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

    // If slashing, draw golden axe slash arc
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

    // Outer pulsating halo
    val haloRadius = p.radius * (1.2f + sin(timeSec * 5f) * 0.15f)
    drawCircle(
        color = p.type.color.copy(alpha = 0.35f),
        radius = haloRadius,
        center = Offset(p.x, p.y)
    )

    // Draw Stone Tablet Sprite
    drawImage(
        image = runeSprite,
        dstOffset = IntOffset((p.x - size / 2f).toInt(), (p.y - size / 2f).toInt()),
        dstSize = IntSize(size, size)
    )
}

private fun DrawScope.drawRuneBall(ball: Ball, timeSec: Float) {
    // 1. Comet Stardust Trail
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

    // 2. Outer Glow Halo
    drawCircle(
        color = ball.type.glowColor,
        radius = ball.radius * 2.4f,
        center = Offset(ball.x, ball.y)
    )

    // 3. Core Sphere
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
