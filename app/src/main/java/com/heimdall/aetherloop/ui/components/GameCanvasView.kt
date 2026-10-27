package com.heimdall.aetherloop.ui.components

import android.graphics.Paint
import android.graphics.PointF
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import com.heimdall.aetherloop.game.engine.GameLoopEngine
import com.heimdall.aetherloop.game.model.Conduit
import com.heimdall.aetherloop.game.model.SandArtMatrix
import com.heimdall.aetherloop.game.model.Vessel
import com.heimdall.aetherloop.game.physics.BifrostTrack
import com.heimdall.aetherloop.ui.theme.*
import kotlin.math.sin

/**
 * High-performance hardware-accelerated Game Canvas.
 *
 * Renders the Sand Art Mosaic, discharge hoppers, moving conveyor belt,
 * falling stardust particles, splash sparks, floating scores, and resonance vessels at 60 FPS.
 */
@Composable
fun GameCanvasView(
    engine: GameLoopEngine,
    frameTick: Long,
    onTapScreen: (Offset) -> Unit,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    onTapScreen(offset)
                }
            }
    ) {
        val currentTick = frameTick

        val w = size.width
        val h = size.height

        // 1. Draw Deep Cosmos Background & Parallax Nebulae
        drawCosmicBackground(
            w = w,
            h = h,
            isChronoActive = engine.isChronoActive.value,
            isGjallarhornActive = engine.isGjallarhornActive.value,
            shieldHealth = engine.shieldHealth.floatValue
        )

        // 2. Draw Sand Art Mosaic Matrix
        drawSandArtMatrix(engine.sandMatrix, w, h)

        // 3. Draw Laser Targeting Guide Beams from Funnels to Conveyor
        for (conduit in engine.conduits) {
            drawLaserGuide(conduit, w, h, engine.track.top)
        }

        // 4. Draw Funnels / Chutes
        for (conduit in engine.conduits) {
            drawConduitFunnel(conduit, w, h)
        }

        // 5. Draw Conveyor Belt Loop Circuit with Pulleys
        drawConveyorBelt(engine.track, engine.track.beltScrollOffset)

        // 6. Draw Active Falling Sand Particles
        val particleCount = engine.particleEngine.activeParticlesCount
        val particles = engine.particleEngine.getParticlesArray()
        for (i in 0 until particleCount) {
            val p = particles[i]
            if (p.isActive) {
                drawCircle(
                    color = p.color.glowColor.copy(alpha = 0.55f * p.alpha),
                    radius = p.radius * 2.2f,
                    center = Offset(p.x, p.y)
                )
                drawCircle(
                    color = p.color.color.copy(alpha = p.alpha),
                    radius = p.radius,
                    center = Offset(p.x, p.y)
                )
            }
        }

        // 7. Draw Splash Sparks
        val sparks = engine.particleEngine.splashSparks
        for (spark in sparks) {
            drawCircle(
                color = spark.color.color.copy(alpha = spark.alpha),
                radius = 2.4f,
                center = Offset(spark.x, spark.y)
            )
        }

        // 8. Draw Resonance Vessels on Conveyor
        val tempPoint = PointF()
        for (vessel in engine.activeVessels) {
            drawVessel(vessel, engine.track, tempPoint, w, h)
        }

        // 9. Draw Floating Combat / Score Text
        val texts = engine.particleEngine.floatingTexts
        drawContext.canvas.nativeCanvas.apply {
            val paint = Paint().apply {
                textSize = 24f
                textAlign = Paint.Align.CENTER
                typeface = Typeface.DEFAULT_BOLD
                isAntiAlias = true
            }
            for (ft in texts) {
                paint.color = ft.color.copy(alpha = ft.alpha).toArgb()
                drawText(ft.text, ft.x, ft.y, paint)
            }
        }
    }
}

private fun DrawScope.drawCosmicBackground(
    w: Float,
    h: Float,
    isChronoActive: Boolean,
    isGjallarhornActive: Boolean,
    shieldHealth: Float
) {
    val topColor = when {
        shieldHealth < 30f -> Color(0xFF260505)
        isChronoActive -> Color(0xFF021B29)
        isGjallarhornActive -> Color(0xFF291B02)
        else -> Color(0xFF04060E)
    }

    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                topColor,
                Color(0xFF090D1C),
                Color(0xFF050813)
            )
        )
    )

    // Critical Shield Warning Border (Flashing red if shield < 30%)
    if (shieldHealth < 30f) {
        drawRect(
            color = MuspelCrimson.copy(alpha = 0.25f),
            style = Stroke(width = 8f)
        )
    }

    // Starlight nebula
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                if (isGjallarhornActive) Color(0x33FFD700) else Color(0x2200E5FF),
                Color.Transparent
            ),
            center = Offset(w * 0.5f, h * 0.35f),
            radius = w * 0.7f
        )
    )
}

private fun DrawScope.drawLaserGuide(conduit: Conduit, w: Float, h: Float, trackTop: Float) {
    val cx = conduit.xNorm * w
    val startY = conduit.yNorm * h + 8f
    val endY = trackTop

    drawLine(
        color = if (conduit.isOpen) conduit.color.color.copy(alpha = 0.50f) else Color(0x1AFFFFFF),
        start = Offset(cx, startY),
        end = Offset(cx, endY),
        strokeWidth = if (conduit.isOpen) 2.5f else 1.2f,
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f), 0f)
    )
}

private fun DrawScope.drawSandArtMatrix(matrix: SandArtMatrix, w: Float, h: Float) {
    val matrixLeft = w * 0.14f
    val matrixRight = w * 0.86f
    val matrixTop = h * 0.16f
    val matrixBottom = h * 0.45f

    val matrixWidth = matrixRight - matrixLeft
    val matrixHeight = matrixBottom - matrixTop

    // Chassis Container Frame
    drawRoundRect(
        color = CosmosSurface.copy(alpha = 0.75f),
        topLeft = Offset(matrixLeft - 8f, matrixTop - 8f),
        size = Size(matrixWidth + 16f, matrixHeight + 16f),
        cornerRadius = CornerRadius(16f, 16f)
    )
    drawRoundRect(
        color = CosmosBorder,
        topLeft = Offset(matrixLeft - 8f, matrixTop - 8f),
        size = Size(matrixWidth + 16f, matrixHeight + 16f),
        cornerRadius = CornerRadius(16f, 16f),
        style = Stroke(width = 2.0f)
    )

    val cellW = matrixWidth / matrix.cols.toFloat()
    val cellH = matrixHeight / matrix.rows.toFloat()
    val grainRadius = (cellW * 0.42f).coerceAtMost(cellH * 0.42f)

    for (r in 0 until matrix.rows) {
        for (c in 0 until matrix.cols) {
            val grainColor = matrix.grid[r][c] ?: continue
            val cx = matrixLeft + (c + 0.5f) * cellW
            val cy = matrixTop + (r + 0.5f) * cellH

            drawCircle(
                color = grainColor.color,
                radius = grainRadius,
                center = Offset(cx, cy)
            )
        }
    }
}

private fun DrawScope.drawConduitFunnel(conduit: Conduit, w: Float, h: Float) {
    val cx = conduit.xNorm * w
    val cy = conduit.yNorm * h
    val funnelWidth = 36f
    val funnelHeight = 24f

    val path = Path().apply {
        moveTo(cx - funnelWidth / 2f, cy - funnelHeight)
        lineTo(cx + funnelWidth / 2f, cy - funnelHeight)
        lineTo(cx + 10f, cy)
        lineTo(cx - 10f, cy)
        close()
    }

    drawPath(
        path = path,
        brush = Brush.verticalGradient(
            colors = listOf(
                CosmosCard,
                if (conduit.isOpen) conduit.color.color else CosmosBorder
            )
        )
    )
    drawPath(
        path = path,
        color = if (conduit.isOpen) conduit.color.color else CosmosBorder,
        style = Stroke(width = 2f)
    )

    drawCircle(
        color = if (conduit.isOpen) conduit.color.color else Color(0xFF334155),
        radius = 8f,
        center = Offset(cx, cy + 2f)
    )
    if (conduit.isOpen) {
        drawCircle(
            color = conduit.color.glowColor,
            radius = 16f,
            center = Offset(cx, cy + 2f)
        )
    }
}

private fun DrawScope.drawConveyorBelt(track: BifrostTrack, beltOffset: Float) {
    val trackWidth = track.right - track.left
    val trackHeight = track.bottom - track.top
    val r = track.cornerRadius

    // 1. Conveyor Belt Bed
    drawRoundRect(
        color = Color(0xFF0F172A),
        topLeft = Offset(track.left - 16f, track.top - 16f),
        size = Size(trackWidth + 32f, trackHeight + 32f),
        cornerRadius = CornerRadius(r + 16f, r + 16f),
        style = Stroke(width = 30f)
    )

    // 2. Track Outer Glowing Rails
    drawRoundRect(
        color = Color(0x3300E5FF),
        topLeft = Offset(track.left - 26f, track.top - 26f),
        size = Size(trackWidth + 52f, trackHeight + 52f),
        cornerRadius = CornerRadius(r + 26f, r + 26f),
        style = Stroke(width = 2.5f)
    )
    drawRoundRect(
        color = Color(0x3300E5FF),
        topLeft = Offset(track.left - 6f, track.top - 6f),
        size = Size(trackWidth + 12f, trackHeight + 12f),
        cornerRadius = CornerRadius(r + 6f, r + 6f),
        style = Stroke(width = 2.5f)
    )

    // 3. Corner Pulleys
    val pulleyRadius = 14f
    val corners = listOf(
        Offset(track.left + r, track.top + r),
        Offset(track.right - r, track.top + r),
        Offset(track.right - r, track.bottom - r),
        Offset(track.left + r, track.bottom - r)
    )
    for (corner in corners) {
        drawCircle(color = Color(0xFF1E293B), radius = pulleyRadius, center = corner)
        drawCircle(color = BifrostCyan.copy(alpha = 0.5f), radius = pulleyRadius, center = corner, style = Stroke(2f))
    }

    // 4. Animated Moving Belt Treads
    val numTreads = 36
    val tempPoint = PointF()
    for (i in 0 until numTreads) {
        val treadProgress = ((i.toFloat() / numTreads.toFloat()) + (beltOffset * 0.1f)) % 1.0f
        track.getPointAtProgress(treadProgress, tempPoint)
        drawCircle(
            color = BifrostCyan.copy(alpha = 0.45f),
            radius = 3.0f,
            center = Offset(tempPoint.x, tempPoint.y)
        )
    }
}

private fun DrawScope.drawVessel(
    vessel: Vessel,
    track: BifrostTrack,
    tempPoint: PointF,
    w: Float,
    h: Float
) {
    track.getPointAtProgress(vessel.trackProgress, tempPoint)
    val angle = track.getOrientationAtProgress(vessel.trackProgress)

    val vesselWidth = w * 0.16f
    val vesselHeight = h * 0.054f

    rotate(degrees = angle, pivot = Offset(tempPoint.x, tempPoint.y)) {
        val left = tempPoint.x - vesselWidth / 2f
        val top = tempPoint.y - vesselHeight / 2f

        // Overdrive Rocket Boost Flame
        if (vessel.isBoosted) {
            val flamePath = Path().apply {
                moveTo(left - 16f, top + vesselHeight * 0.5f)
                lineTo(left + 2f, top + 4f)
                lineTo(left + 2f, top + vesselHeight - 4f)
                close()
            }
            drawPath(
                path = flamePath,
                brush = Brush.horizontalGradient(listOf(Color.Transparent, BifrostCyan))
            )
        }

        // Full vessel radiant aura
        if (vessel.fullFlashAnim > 0f) {
            drawRoundRect(
                color = HeimdallGold.copy(alpha = vessel.fullFlashAnim * 0.8f),
                topLeft = Offset(left - 8f, top - 8f),
                size = Size(vesselWidth + 16f, vesselHeight + 16f),
                cornerRadius = CornerRadius(16f, 16f)
            )
        }

        // Vessel Background Hull
        drawRoundRect(
            color = CosmosCard,
            topLeft = Offset(left, top),
            size = Size(vesselWidth, vesselHeight),
            cornerRadius = CornerRadius(12f, 12f)
        )

        // Internal Fluid/Sand Fill Level
        if (vessel.currentAmount > 0) {
            val fillRatio = vessel.fillRatio
            val fillHeight = (vesselHeight - 4f) * fillRatio
            val fillTop = (top + vesselHeight - 2f) - fillHeight

            val wavePath = Path().apply {
                moveTo(left + 2f, top + vesselHeight - 2f)
                lineTo(left + 2f, fillTop)
                val waveAmplitude = 3.0f * (1f - fillRatio.coerceAtMost(0.95f))
                val waveCount = 2
                val segWidth = (vesselWidth - 4f) / waveCount
                for (i in 0 until waveCount) {
                    val x1 = left + 2f + (i * segWidth) + segWidth * 0.5f
                    val y1 = fillTop + sin(vessel.liquidWaveOffset + i.toFloat()) * waveAmplitude
                    val x2 = left + 2f + ((i + 1) * segWidth)
                    val y2 = fillTop
                    quadraticTo(x1, y1, x2, y2)
                }
                lineTo(left + vesselWidth - 2f, top + vesselHeight - 2f)
                close()
            }

            drawPath(
                path = wavePath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        vessel.color.color,
                        vessel.color.color.copy(alpha = 0.8f)
                    )
                )
            )
        }

        // Hull Edge Border
        drawRoundRect(
            color = if (vessel.isBoosted) BifrostCyan else if (vessel.isFull) HeimdallGold else vessel.color.color,
            topLeft = Offset(left, top),
            size = Size(vesselWidth, vesselHeight),
            cornerRadius = CornerRadius(12f, 12f),
            style = Stroke(width = if (vessel.isFull || vessel.isBoosted) 3.5f else 2.2f)
        )

        // Wheels
        drawCircle(
            color = Color(0xFF64748B),
            radius = 4f,
            center = Offset(left + 8f, top + vesselHeight + 2f)
        )
        drawCircle(
            color = Color(0xFF64748B),
            radius = 4f,
            center = Offset(left + vesselWidth - 8f, top + vesselHeight + 2f)
        )

        // Vessel Rune & Fill text
        drawContext.canvas.nativeCanvas.apply {
            val paint = Paint().apply {
                color = android.graphics.Color.WHITE
                textSize = 21f
                textAlign = Paint.Align.CENTER
                typeface = Typeface.DEFAULT_BOLD
                isAntiAlias = true
            }
            val text = "${vessel.color.runeSymbol} ${vessel.currentAmount}/${vessel.capacity}"
            drawText(text, tempPoint.x, tempPoint.y + 7f, paint)
        }
    }
}
