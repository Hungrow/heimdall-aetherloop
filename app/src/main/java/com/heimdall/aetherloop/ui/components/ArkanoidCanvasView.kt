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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import com.heimdall.aetherloop.game.engine.VikingArkanoidEngine
import com.heimdall.aetherloop.game.model.*
import com.heimdall.aetherloop.ui.theme.*
import kotlin.math.sin

/**
 * High-performance hardware-accelerated Arkanoid Canvas for Viking Naval Combat.
 */
@Composable
fun ArkanoidCanvasView(
    engine: VikingArkanoidEngine,
    frameTick: Long,
    modifier: Modifier = Modifier
) {
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

        val w = size.width
        val h = size.height

        // 1. Draw Northern Fjord Waters & Aurora Sky
        drawFjordBackground(w, h)

        // 2. Draw Enemy Viking Longships
        for (ship in engine.enemyShips) {
            drawVikingLongship(ship)
        }

        // 3. Draw Laser Projectiles
        for (proj in engine.projectiles) {
            drawCircle(
                color = ValkyrieViolet,
                radius = 4f,
                center = Offset(proj.x, proj.y)
            )
            drawLine(
                color = ValkyrieViolet.copy(alpha = 0.6f),
                start = Offset(proj.x, proj.y),
                end = Offset(proj.x, proj.y + 16f),
                strokeWidth = 3f
            )
        }

        // 4. Draw Falling Power-Up Runes
        for (powerUp in engine.powerUps) {
            drawPowerUpRune(powerUp)
        }

        // 5. Draw Asgardian Warship Paddle
        drawAsgardianPaddle(engine.paddle)

        // 6. Draw Rune Spheres (Balls) with Comet Stardust Trails
        for (ball in engine.activeBalls) {
            drawRuneBall(ball)
        }

        // 7. Draw Wood Splinters & Explosions
        for (splinter in engine.collisionEngine.splinters) {
            drawCircle(
                color = splinter.color.copy(alpha = splinter.alpha),
                radius = splinter.size,
                center = Offset(splinter.x, splinter.y)
            )
        }

        // 8. Draw Floating Score Text
        drawContext.canvas.nativeCanvas.apply {
            val paint = Paint().apply {
                textSize = 24f
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

private fun DrawScope.drawFjordBackground(w: Float, h: Float) {
    // Night sky to ocean gradient
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFF020914),
                Color(0xFF08182B),
                Color(0xFF0B253D),
                Color(0xFF071B2C)
            )
        )
    )

    // Aurora Borealis Shimmer in Upper Sky
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0x2400E5FF), Color.Transparent),
            center = Offset(w * 0.35f, h * 0.12f),
            radius = w * 0.6f
        )
    )
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0x1AD500F9), Color.Transparent),
            center = Offset(w * 0.75f, h * 0.18f),
            radius = w * 0.55f
        )
    )

    // Fjord Sea Waves (Distant horizontal shimmer lines)
    val waveColor = Color(0x1F00E5FF)
    for (i in 0 until 6) {
        val y = h * (0.65f + i * 0.05f)
        drawLine(
            color = waveColor,
            start = Offset(0f, y),
            end = Offset(w, y),
            strokeWidth = 1.5f
        )
    }
}

private fun DrawScope.drawVikingLongship(ship: EnemyShip) {
    val left = ship.left
    val top = ship.top
    val w = ship.width
    val h = ship.height

    val hullColor = if (ship.hitFlashAnim > 0f) Color.White else Color(0xFF4E342E)
    val trimColor = if (ship.hitFlashAnim > 0f) Color.White else Color(0xFF2E1C14)

    // 1. Longship Wooden Hull (Pointed bow and stern)
    val hullPath = Path().apply {
        moveTo(left, top + h * 0.3f)
        // Upcurved dragon prow (left)
        quadraticTo(left - 8f, top - 6f, left - 4f, top - 12f)
        lineTo(left + 2f, top + h * 0.4f)
        // Keel curve to right
        lineTo(ship.right - 2f, top + h * 0.4f)
        // Upcurved dragon stern (right)
        lineTo(ship.right + 4f, top - 12f)
        quadraticTo(ship.right + 8f, top - 6f, ship.right, top + h * 0.3f)
        // Bottom hull curve
        quadraticTo(ship.x, top + h + 4f, left, top + h * 0.3f)
        close()
    }

    drawPath(path = hullPath, color = hullColor)
    drawPath(path = hullPath, color = trimColor, style = Stroke(2f))

    // 2. Striped Viking Sail in Center
    val sailW = w * 0.52f
    val sailH = h * 0.55f
    val sailLeft = ship.x - sailW / 2f
    val sailTop = top - sailH + 4f

    drawRect(
        brush = Brush.horizontalGradient(
            colors = listOf(
                ship.type.primaryColor,
                Color.White,
                ship.type.primaryColor,
                Color.White
            )
        ),
        topLeft = Offset(sailLeft, sailTop),
        size = Size(sailW, sailH)
    )
    drawRect(
        color = trimColor,
        topLeft = Offset(sailLeft, sailTop),
        size = Size(sailW, sailH),
        style = Stroke(1.5f)
    )

    // Mast
    drawLine(
        color = Color(0xFF3E2723),
        start = Offset(ship.x, top - sailH - 2f),
        end = Offset(ship.x, top + h * 0.4f),
        strokeWidth = 2.5f
    )

    // 3. Round Shields along the Gunwale
    val numShields = 4
    val shieldSpacing = w * 0.75f / (numShields - 1)
    val shieldStartX = left + w * 0.12f
    val shieldY = top + h * 0.35f
    val shieldRadius = 5.5f

    for (i in 0 until numShields) {
        val sx = shieldStartX + i * shieldSpacing
        drawCircle(
            color = if (i % 2 == 0) ship.type.primaryColor else HeimdallGold,
            radius = shieldRadius,
            center = Offset(sx, shieldY)
        )
        drawCircle(
            color = Color(0xFF263238),
            radius = shieldRadius,
            center = Offset(sx, shieldY),
            style = Stroke(1.2f)
        )
    }

    // 4. HP Health Gauge (if maxHp > 1)
    if (ship.type.maxHp > 1) {
        val hpBarW = w * 0.75f
        val hpBarH = 4f
        val hpLeft = ship.x - hpBarW / 2f
        val hpTop = top - sailH - 8f
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
    }

    // Decay hit flash
    if (ship.hitFlashAnim > 0f) {
        ship.hitFlashAnim = (ship.hitFlashAnim - 0.12f).coerceAtLeast(0f)
    }
}

private fun DrawScope.drawAsgardianPaddle(paddle: Paddle) {
    val left = paddle.left
    val top = paddle.top
    val w = paddle.width
    val h = paddle.height

    // 1. Paddle Hull Body (Bifrost War Vessel)
    drawRoundRect(
        brush = Brush.horizontalGradient(
            colors = listOf(
                BifrostCyan.copy(alpha = 0.8f),
                HeimdallGold,
                BifrostCyan.copy(alpha = 0.8f)
            )
        ),
        topLeft = Offset(left, top),
        size = Size(w, h),
        cornerRadius = CornerRadius(12f, 12f)
    )

    // Outer Glow / Border
    drawRoundRect(
        color = HeimdallGold,
        topLeft = Offset(left, top),
        size = Size(w, h),
        cornerRadius = CornerRadius(12f, 12f),
        style = Stroke(width = 2.5f)
    )

    // Center Valkyrie Rune Core
    drawCircle(
        color = CosmosDark,
        radius = 8f,
        center = Offset(paddle.centerX, paddle.y)
    )
    drawCircle(
        color = HeimdallGold,
        radius = 5f,
        center = Offset(paddle.centerX, paddle.y)
    )

    // 2. Mounted Ballistas (if active)
    if (paddle.hasBallistas) {
        // Left Ballista Turret
        drawRoundRect(
            color = ValkyrieViolet,
            topLeft = Offset(left + 6f, top - 8f),
            size = Size(10f, 12f),
            cornerRadius = CornerRadius(3f, 3f)
        )
        // Right Ballista Turret
        drawRoundRect(
            color = ValkyrieViolet,
            topLeft = Offset(paddle.right - 16f, top - 8f),
            size = Size(10f, 12f),
            cornerRadius = CornerRadius(3f, 3f)
        )
    }
}

private fun DrawScope.drawRuneBall(ball: Ball) {
    // 1. Comet Stardust Trail
    val trail = ball.trailPoints
    for (i in trail.indices) {
        val pt = trail[i]
        val ratio = (i + 1).toFloat() / trail.size.toFloat()
        drawCircle(
            color = ball.type.color.copy(alpha = ratio * 0.35f),
            radius = ball.radius * ratio * 0.85f,
            center = Offset(pt.x, pt.y)
        )
    }

    // 2. Outer Glow Halo
    drawCircle(
        color = ball.type.glowColor,
        radius = ball.radius * 2.2f,
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

private fun DrawScope.drawPowerUpRune(p: PowerUpDrop) {
    // Outer shield circle
    drawCircle(
        color = CosmosCard,
        radius = p.radius,
        center = Offset(p.x, p.y)
    )
    drawCircle(
        color = p.type.color,
        radius = p.radius,
        center = Offset(p.x, p.y),
        style = Stroke(width = 2.5f)
    )

    // Rune Glyph inside
    drawContext.canvas.nativeCanvas.apply {
        val paint = Paint().apply {
            color = p.type.color.toArgb()
            textSize = 22f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.DEFAULT_BOLD
            isAntiAlias = true
        }
        drawText(p.type.runeSymbol, p.x, p.y + 7f, paint)
    }
}
