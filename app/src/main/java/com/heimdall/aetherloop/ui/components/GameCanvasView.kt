package com.heimdall.aetherloop.ui.components

import android.graphics.Paint
import android.graphics.PointF
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
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
import com.heimdall.aetherloop.game.engine.GameLoopEngine
import com.heimdall.aetherloop.game.model.Conduit
import com.heimdall.aetherloop.game.model.Vessel
import com.heimdall.aetherloop.game.physics.BifrostTrack
import com.heimdall.aetherloop.ui.theme.*
import kotlin.math.sin

/**
 * High-performance hardware-accelerated Game Canvas.
 *
 * Renders the cosmic starfield, the Bifröst loop track, active stardust particles,
 * celestial conduits, and resonance vessels with real-time fluid animations.
 */
@Composable
fun GameCanvasView(
    engine: GameLoopEngine,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // 1. Draw Deep Cosmos Background with Nebulae
        drawCosmicBackground(w, h)

        // 2. Draw Bifrost Loop Track Circuit
        drawTrack(engine.track)

        // 3. Draw Celestial Conduits (Emitters)
        for (conduit in engine.conduits) {
            drawConduit(conduit, w, h)
        }

        // 4. Draw Active Stardust Particles
        val particleCount = engine.particleEngine.activeParticlesCount
        val particles = engine.particleEngine.getParticlesArray()

        for (i in 0 until particleCount) {
            val p = particles[i]
            if (p.isActive) {
                // Glow halo
                drawCircle(
                    color = p.color.glowColor.copy(alpha = 0.45f * p.alpha),
                    radius = p.radius * 2.2f,
                    center = Offset(p.x, p.y)
                )
                // Core particle
                drawCircle(
                    color = p.color.color.copy(alpha = p.alpha),
                    radius = p.radius,
                    center = Offset(p.x, p.y)
                )
            }
        }

        // 5. Draw Active Vessels on Track
        val tempPoint = PointF()
        for (vessel in engine.activeVessels) {
            drawVessel(vessel, engine.track, tempPoint, w, h)
        }
    }
}

private fun DrawScope.drawCosmicBackground(w: Float, h: Float) {
    // Deep celestial background gradient
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFF04060C),
                Color(0xFF0A0F1F),
                Color(0xFF060914)
            )
        )
    )

    // Subtle ambient cosmic nebula glow
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0x1F00E5FF), Color.Transparent),
            center = Offset(w * 0.3f, h * 0.25f),
            radius = w * 0.6f
        )
    )
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0x1AD500F9), Color.Transparent),
            center = Offset(w * 0.75f, h * 0.65f),
            radius = w * 0.7f
        )
    )
}

private fun DrawScope.drawTrack(track: BifrostTrack) {
    val trackWidth = track.right - track.left
    val trackHeight = track.bottom - track.top
    val r = track.cornerRadius

    // Outer Track Glow
    drawRoundRect(
        color = Color(0x3300E5FF),
        topLeft = Offset(track.left - 4f, track.top - 4f),
        size = Size(trackWidth + 8f, trackHeight + 8f),
        cornerRadius = CornerRadius(r + 4f, r + 4f),
        style = Stroke(width = 9f)
    )

    // Inner Track Conduit Line
    drawRoundRect(
        brush = Brush.horizontalGradient(
            colors = listOf(
                HeimdallGold.copy(alpha = 0.8f),
                BifrostCyan.copy(alpha = 0.9f),
                ValkyrieViolet.copy(alpha = 0.8f),
                HeimdallGold.copy(alpha = 0.8f)
            )
        ),
        topLeft = Offset(track.left, track.top),
        size = Size(trackWidth, trackHeight),
        cornerRadius = CornerRadius(r, r),
        style = Stroke(width = 3.5f)
    )
}

private fun DrawScope.drawConduit(conduit: Conduit, w: Float, h: Float) {
    val cx = conduit.xNorm * w
    val cy = conduit.yNorm * h
    val radius = 24f

    // Harvest progress arc around conduit
    drawArc(
        color = Color(0x33FFFFFF),
        startAngle = -90f,
        sweepAngle = 360f,
        useCenter = false,
        topLeft = Offset(cx - radius - 8f, cy - radius - 8f),
        size = Size((radius + 8f) * 2f, (radius + 8f) * 2f),
        style = Stroke(width = 3f)
    )
    drawArc(
        color = conduit.color.color,
        startAngle = -90f,
        sweepAngle = 360f * conduit.harvestProgress,
        useCenter = false,
        topLeft = Offset(cx - radius - 8f, cy - radius - 8f),
        size = Size((radius + 8f) * 2f, (radius + 8f) * 2f),
        style = Stroke(width = 3.5f)
    )

    // Outer emitter body
    drawCircle(
        color = CosmosCard,
        radius = radius,
        center = Offset(cx, cy)
    )
    drawCircle(
        color = conduit.color.color,
        radius = radius,
        center = Offset(cx, cy),
        style = Stroke(width = 2.5f)
    )

    // Inner glowing crystal aperture
    drawCircle(
        color = conduit.color.glowColor,
        radius = radius * 0.65f,
        center = Offset(cx, cy)
    )

    // Rune Glyph inside conduit
    drawContext.canvas.nativeCanvas.apply {
        val paint = Paint().apply {
            color = conduit.color.color.toArgb()
            textSize = 28f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.DEFAULT_BOLD
            isAntiAlias = true
        }
        drawText(conduit.color.runeSymbol, cx, cy + 10f, paint)
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

    val vesselWidth = w * 0.15f
    val vesselHeight = h * 0.052f

    rotate(degrees = angle, pivot = Offset(tempPoint.x, tempPoint.y)) {
        val left = tempPoint.x - vesselWidth / 2f
        val top = tempPoint.y - vesselHeight / 2f

        // Full vessel radiant aura
        if (vessel.fullFlashAnim > 0f) {
            drawRoundRect(
                color = HeimdallGold.copy(alpha = vessel.fullFlashAnim * 0.7f),
                topLeft = Offset(left - 6f, top - 6f),
                size = Size(vesselWidth + 12f, vesselHeight + 12f),
                cornerRadius = CornerRadius(14f, 14f)
            )
        }

        // Vessel Background Hull
        drawRoundRect(
            color = CosmosCard,
            topLeft = Offset(left, top),
            size = Size(vesselWidth, vesselHeight),
            cornerRadius = CornerRadius(10f, 10f)
        )

        // Internal Fluid/Sand Fill Level
        if (vessel.currentAmount > 0) {
            val fillRatio = vessel.fillRatio
            val fillHeight = vesselHeight * fillRatio
            val fillTop = top + (vesselHeight - fillHeight)

            // Animated wave contour
            val wavePath = Path().apply {
                moveTo(left + 2f, top + vesselHeight - 2f)
                lineTo(left + 2f, fillTop)
                val waveAmplitude = 3.5f * (1f - fillRatio.coerceAtMost(0.95f))
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
                        vessel.color.color.copy(alpha = 0.75f)
                    )
                )
            )
        }

        // Hull Edge Border
        drawRoundRect(
            color = if (vessel.isFull) HeimdallGold else vessel.color.color,
            topLeft = Offset(left, top),
            size = Size(vesselWidth, vesselHeight),
            cornerRadius = CornerRadius(10f, 10f),
            style = Stroke(width = if (vessel.isFull) 3.5f else 2.0f)
        )

        // Vessel Rune and Fill Count Text
        drawContext.canvas.nativeCanvas.apply {
            val paint = Paint().apply {
                color = android.graphics.Color.WHITE
                textSize = 22f
                textAlign = Paint.Align.CENTER
                typeface = Typeface.DEFAULT_BOLD
                isAntiAlias = true
            }
            val text = "${vessel.currentAmount}/${vessel.capacity}"
            drawText(text, tempPoint.x, tempPoint.y + 7f, paint)
        }
    }
}
