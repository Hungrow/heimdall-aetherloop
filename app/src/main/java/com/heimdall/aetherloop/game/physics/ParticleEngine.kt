package com.heimdall.aetherloop.game.physics

import android.graphics.PointF
import com.heimdall.aetherloop.core.HapticManager
import com.heimdall.aetherloop.core.SoundSynthesizer
import com.heimdall.aetherloop.game.model.*
import kotlin.math.hypot
import kotlin.random.Random

/**
 * Floating Combat/Score Text particle for visual punch and juice.
 */
data class FloatingText(
    var x: Float,
    var y: Float,
    val text: String,
    val color: androidx.compose.ui.graphics.Color,
    var alpha: Float = 1.0f,
    var life: Float = 0.8f // seconds
)

/**
 * Splash particle emitted when a sand grain impacts a vessel.
 */
data class SplashSpark(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    val color: AetherColor,
    var alpha: Float = 1.0f,
    var life: Float = 0.35f
)

/**
 * High-performance Granular Physics and Particle Collision Engine.
 *
 * Couples the deconstruction of the SandArtMatrix with free-falling stardust grains,
 * container absorption, magnetic relic pulls, and juice splash effects.
 */
class ParticleEngine(
    private val pool: ParticlePool = ParticlePool(650),
    private val soundSynthesizer: SoundSynthesizer,
    private val hapticManager: HapticManager
) {
    private val tempPoint = PointF()
    private val baseGravity = 1250f

    val activeParticlesCount: Int get() = pool.activeCount
    fun getParticlesArray(): Array<Particle> = pool.getAll()

    val floatingTexts = mutableListOf<FloatingText>()
    val splashSparks = mutableListOf<SplashSpark>()

    var onVesselCompleted: ((Vessel) -> Unit)? = null
    var onArtworkCleared: (() -> Unit)? = null
    var onParticleCaptured: ((points: Int) -> Unit)? = null

    var isChronoStasisActive: Boolean = false
    var isGjallarhornActive: Boolean = false

    fun update(
        dt: Float,
        width: Float,
        height: Float,
        sandMatrix: SandArtMatrix,
        conduits: List<Conduit>,
        vessels: List<Vessel>,
        track: BifrostTrack,
        flowRatePerSec: Float,
        comboMultiplier: Int
    ) {
        val safeDt = dt.coerceIn(0.001f, 0.05f)
        val simDt = if (isChronoStasisActive) safeDt * 0.45f else safeDt

        val vesselWidth = width * 0.17f
        val vesselHeight = height * 0.058f

        // 1. Check which conduits have matching vessels directly underneath
        for (conduit in conduits) {
            val funnelX = conduit.xNorm * width
            var hasMatchingVessel = false
            var candidateVessel: Vessel? = null

            for (vessel in vessels) {
                if (vessel.state == VesselState.ON_TRACK && !vessel.isFull) {
                    track.getPointAtProgress(vessel.trackProgress, tempPoint)
                    if (kotlin.math.abs(tempPoint.y - track.top) < 32f) {
                        val dist = kotlin.math.abs(tempPoint.x - funnelX)
                        if (dist < (vesselWidth * 0.60f)) {
                            candidateVessel = vessel
                            if (vessel.color == conduit.color || isGjallarhornActive) {
                                hasMatchingVessel = true
                            }
                            break
                        }
                    }
                }
            }

            conduit.isOpen = hasMatchingVessel

            if (hasMatchingVessel && candidateVessel != null) {
                conduit.flowAccumulator += simDt * flowRatePerSec
                while (conduit.flowAccumulator >= 1.0f) {
                    conduit.flowAccumulator -= 1.0f

                    val col = Random.nextInt(conduit.colRangeStart, conduit.colRangeEnd + 1)
                    val targetColor = if (isGjallarhornActive) null else conduit.color
                    val drainedColor = sandMatrix.drainGrainAtColumn(col, targetColor)

                    if (drainedColor != null) {
                        val particle = pool.obtain()
                        if (particle != null) {
                            val startX = funnelX + (Random.nextFloat() - 0.5f) * 16f
                            val startY = conduit.yNorm * height + 10f
                            val vx = (Random.nextFloat() - 0.5f) * 24f
                            val vy = 110f + Random.nextFloat() * 70f

                            particle.reset(
                                startX = startX,
                                startY = startY,
                                initialVx = vx,
                                initialVy = vy,
                                particleColor = drainedColor,
                                conduitId = conduit.id,
                                particleRadius = 4.3f
                            )
                        }
                    } else {
                        if (sandMatrix.remainingGrainCount <= 0) {
                            onArtworkCleared?.invoke()
                        }
                        break
                    }
                }
            }
        }

        // 2. Pre-calculate active vessel capture hitboxes
        val vesselHitboxes = ArrayList<VesselHitbox>(vessels.size)
        for (vessel in vessels) {
            if (vessel.state == VesselState.ON_TRACK) {
                track.getPointAtProgress(vessel.trackProgress, tempPoint)
                val boundsMinX = tempPoint.x - (vesselWidth * 0.56f)
                val boundsMaxX = tempPoint.x + (vesselWidth * 0.56f)
                val boundsMinY = tempPoint.y - (vesselHeight * 0.65f)
                val boundsMaxY = tempPoint.y + (vesselHeight * 0.65f)

                vesselHitboxes.add(
                    VesselHitbox(
                        vessel = vessel,
                        minX = boundsMinX,
                        maxX = boundsMaxX,
                        minY = boundsMinY,
                        maxY = boundsMaxY,
                        centerX = tempPoint.x,
                        centerY = tempPoint.y
                    )
                )
            }
        }

        // 3. Update falling particles & collision
        val particles = pool.getAll()
        val count = pool.activeCount
        for (i in 0 until count) {
            val p = particles[i]
            if (!p.isActive) continue

            // If Gjallarhorn is active, attract particles magnetically towards matching vessel
            if (isGjallarhornActive) {
                val matchingBox = vesselHitboxes.firstOrNull { it.vessel.color == p.color }
                if (matchingBox != null) {
                    val dx = matchingBox.centerX - p.x
                    val dy = matchingBox.centerY - p.y
                    val dist = hypot(dx, dy).coerceAtLeast(10f)
                    p.vx += (dx / dist) * 1600f * simDt
                    p.vy += (dy / dist) * 1200f * simDt
                }
            }

            p.vy += baseGravity * simDt
            p.x += p.vx * simDt
            p.y += p.vy * simDt

            var handled = false

            for (hb in vesselHitboxes) {
                if (p.x in hb.minX..hb.maxX && p.y in hb.minY..hb.maxY) {
                    val target = hb.vessel
                    if (target.color == p.color || isGjallarhornActive) {
                        val wasFull = target.isFull
                        val becameFull = target.addParticle()

                        val earnedPoints = 10 * comboMultiplier
                        onParticleCaptured?.invoke(earnedPoints)

                        // Spawn splash sparks
                        for (s in 0 until 3) {
                            splashSparks.add(
                                SplashSpark(
                                    x = p.x,
                                    y = p.y,
                                    vx = (Random.nextFloat() - 0.5f) * 140f,
                                    vy = -60f - Random.nextFloat() * 100f,
                                    color = p.color
                                )
                            )
                        }

                        if (becameFull) {
                            soundSynthesizer.playVesselFull()
                            hapticManager.onVesselFilled()
                            onVesselCompleted?.invoke(target)

                            // Floating bonus label
                            floatingTexts.add(
                                FloatingText(
                                    x = hb.centerX,
                                    y = hb.centerY - 20f,
                                    text = "¡SELLADO +${100 * comboMultiplier}!",
                                    color = androidx.compose.ui.graphics.Color(0xFFFFD700)
                                )
                            )
                        } else if (!wasFull) {
                            soundSynthesizer.playParticleCatch(p.color.id)
                            hapticManager.onParticleCaptured()

                            if (comboMultiplier > 1 && Random.nextFloat() < 0.25f) {
                                floatingTexts.add(
                                    FloatingText(
                                        x = p.x,
                                        y = p.y - 12f,
                                        text = "+$earnedPoints (x$comboMultiplier)",
                                        color = p.color.color
                                    )
                                )
                            }
                        }

                        pool.release(p)
                        handled = true
                        break
                    } else {
                        // Deflect particle sideways
                        p.vx = if (p.x < (hb.minX + hb.maxX) / 2f) -160f else 160f
                        p.vy = -60f
                        p.alpha *= 0.7f
                    }
                }
            }

            if (handled) continue

            // Bottom bounds release
            if (p.y > height * 0.85f || p.x < 0 || p.x > width) {
                pool.release(p)
            }
        }

        // 4. Update Splash Sparks
        val sparkIter = splashSparks.iterator()
        while (sparkIter.hasNext()) {
            val spark = sparkIter.next()
            spark.vy += 800f * safeDt
            spark.x += spark.vx * safeDt
            spark.y += spark.vy * safeDt
            spark.life -= safeDt
            spark.alpha = (spark.life / 0.35f).coerceIn(0f, 1f)
            if (spark.life <= 0f) {
                sparkIter.remove()
            }
        }

        // 5. Update Floating Texts
        val textIter = floatingTexts.iterator()
        while (textIter.hasNext()) {
            val text = textIter.next()
            text.y -= 45f * safeDt
            text.life -= safeDt
            text.alpha = (text.life / 0.8f).coerceIn(0f, 1f)
            if (text.life <= 0f) {
                textIter.remove()
            }
        }

        if (sandMatrix.remainingGrainCount <= 0 && pool.activeCount == 0) {
            onArtworkCleared?.invoke()
        }
    }

    private data class VesselHitbox(
        val vessel: Vessel,
        val minX: Float,
        val maxX: Float,
        val minY: Float,
        val maxY: Float,
        val centerX: Float,
        val centerY: Float
    )
}
