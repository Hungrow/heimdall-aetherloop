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
 * falling stardust particles, and resonance vessels at 60 FPS.
 */
@Composable
fun GameCanvasView(
    engine: GameLoopEngine,
    frameTick: Long,
    onTapScreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures {
                    onTapScreen()
                }
            }
    ) {
        // Essential: register frameTick state read so Compose redraws every frame!
        val currentTick = frameTick

        val w = size.width
        val h = size.height

        // 1. Draw Deep Cosmos Background
        drawCosmicBackground(w, h)

        // 2. Draw the Sacred Sand Art Mosaic Matrix (Relic being deconstructed)
        drawSandArtMatrix(engine.sandMatrix, w, h)

        // 3. Draw Discharge Funnels beneath the Sand Art
        for (conduit in engine.conduits) {
            drawConduitFunnel(conduit, w, h)
        }

        // 4. Draw Conveyor Belt Loop Circuit with moving tread indicators
        drawConveyorBelt(engine.track, engine.track.beltScrollOffset)

        // 5. Draw Active Falling Sand Particles
        val particleCount = engine.particleEngine.activeParticlesCount
        val particles = engine.particleEngine.getParticlesArray()
        for (i in 0 until particleCount) {
            val p = particles[i]
            if (p.isActive) {
                // Outer glow
                drawCircle(
                    color = p.color.glowColor.copy(alpha = 0.55f * p.alpha),
                    radius = p.radius * 2.2f,
                    center = Offset(p.x, p.y)
                )
                // Particle Core
                drawCircle(
                    color = p.color.color.copy(alpha = p.alpha),
                    radius = p.radius,
                    center = Offset(p.x, p.y)
                )
            }
        }

        // 6. Draw Resonance Vessels on the Conveyor Loop
        val tempPoint = PointF()
        for (vessel in engine.activeVessels) {
            drawVessel(vessel, engine.track, tempPoint, w, h)
        }
    }
}

private fun DrawScope.drawCosmicBackground(w: Float, h: Float) {
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFF04060E),
                Color(0xFF090D1C),
                Color(0xFF050813)
            )
        )
    )

    // Ambient starlight nebula
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0x2200E5FF), Color.Transparent),
            center = Offset(w * 0.5f, h * 0.35f),
            radius = w * 0.7f
        )
    )
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0x1AD500F9), Color.Transparent),
            center = Offset(w * 0.8f, h * 0.70f),
            radius = w * 0.6f
        )
    )
}

/**
 * Draws the Sand Art Mosaic grid. Each grain is rendered as a vibrant pixel.
 */
private fun DrawScope.drawSandArtMatrix(matrix: SandArtMatrix, w: Float, h: Float) {
    val matrixLeft = w * 0.14f
    val matrixRight = w * 0.86f
    val matrixTop = h * 0.16f
    val matrixBottom = h * 0.45f

    val matrixWidth = matrixRight - matrixLeft
    val matrixHeight = matrixBottom - matrixTop

    // Chassis Container Frame
    drawRoundRect(
        color = CosmosSurface.copy(alpha = 0.7f),
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

/**
 * Draws the Asgardian discharge funnels directly below the sand art frame.
 */
private fun DrawScope.drawConduitFunnel(conduit: Conduit, w: Float, h: Float) {
    val cx = conduit.xNorm * w
    val cy = conduit.yNorm * h
    val funnelWidth = 36f
    val funnelHeight = 24f

    // Funnel geometry (wedge shape pointing down)
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

    // Glowing aperture nozzle
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

/**
 * Draws the conveyor belt with moving directional indicators.
 */
private fun DrawScope.drawConveyorBelt(track: BifrostTrack, beltOffset: Float) {
    val trackWidth = track.right - track.left
    val trackHeight = track.bottom - track.top
    val r = track.cornerRadius

    // 1. Conveyor Belt Bed (Dark Rubber/Nanotech Track)
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

    // 3. Animated Moving Belt Treads / Light Pulses
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

/**
 * Draws a resonance vessel traveling along the conveyor.
 */
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
            color = if (vessel.isFull) HeimdallGold else vessel.color.color,
            topLeft = Offset(left, top),
            size = Size(vesselWidth, vesselHeight),
            cornerRadius = CornerRadius(12f, 12f),
            style = Stroke(width = if (vessel.isFull) 3.5f else 2.2f)
        )

        // Container Wheels / Track Bearings
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

        // Vessel Fill Count Text & Rune
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
