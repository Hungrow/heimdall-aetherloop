package com.heimdall.aetherloop.game.physics

import android.graphics.PointF
import com.heimdall.aetherloop.core.HapticManager
import com.heimdall.aetherloop.core.SoundSynthesizer
import com.heimdall.aetherloop.game.model.*
import kotlin.random.Random

/**
 * High-performance Granular Physics and Particle Collision Engine.
 *
 * Couples the deconstruction of the SandArtMatrix with free-falling stardust grains
 * and container absorption at 60 FPS.
 */
class ParticleEngine(
    private val pool: ParticlePool = ParticlePool(600),
    private val soundSynthesizer: SoundSynthesizer,
    private val hapticManager: HapticManager
) {
    private val tempPoint = PointF()
    private val gravity = 1200f // Crisp, fast falling sand gravity

    val activeParticlesCount: Int get() = pool.activeCount
    fun getParticlesArray(): Array<Particle> = pool.getAll()

    var onVesselCompleted: ((Vessel) -> Unit)? = null
    var onArtworkCleared: (() -> Unit)? = null

    /**
     * Updates falling particles and drains grains from the SandArtMatrix when matching vessels pass beneath funnels.
     */
    fun update(
        dt: Float,
        width: Float,
        height: Float,
        sandMatrix: SandArtMatrix,
        conduits: List<Conduit>,
        vessels: List<Vessel>,
        track: BifrostTrack,
        flowRatePerSec: Float
    ) {
        val safeDt = dt.coerceIn(0.001f, 0.05f)

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
                    // If vessel is on the top straight near this funnel's X
                    if (kotlin.math.abs(tempPoint.y - track.top) < 30f) {
                        val dist = kotlin.math.abs(tempPoint.x - funnelX)
                        if (dist < (vesselWidth * 0.58f)) {
                            candidateVessel = vessel
                            if (vessel.color == conduit.color) {
                                hasMatchingVessel = true
                            }
                            break
                        }
                    }
                }
            }

            conduit.isOpen = hasMatchingVessel

            // If a matching vessel is under the funnel, drain sand grains into falling particles
            if (hasMatchingVessel && candidateVessel != null) {
                conduit.flowAccumulator += safeDt * flowRatePerSec
                while (conduit.flowAccumulator >= 1.0f) {
                    conduit.flowAccumulator -= 1.0f

                    // Pick a column in this funnel's range and drain a grain
                    val col = Random.nextInt(conduit.colRangeStart, conduit.colRangeEnd + 1)
                    val drainedColor = sandMatrix.drainGrainAtColumn(col, conduit.color)

                    if (drainedColor != null) {
                        val particle = pool.obtain()
                        if (particle != null) {
                            val startX = funnelX + (Random.nextFloat() - 0.5f) * 14f
                            val startY = conduit.yNorm * height + 10f
                            val vx = (Random.nextFloat() - 0.5f) * 20f
                            val vy = 120f + Random.nextFloat() * 80f

                            particle.reset(
                                startX = startX,
                                startY = startY,
                                initialVx = vx,
                                initialVy = vy,
                                particleColor = drainedColor,
                                conduitId = conduit.id,
                                particleRadius = 4.2f
                            )
                        }
                    } else {
                        // Check if any other grains remain in the whole matrix
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
                val boundsMinX = tempPoint.x - (vesselWidth * 0.55f)
                val boundsMaxX = tempPoint.x + (vesselWidth * 0.55f)
                val boundsMinY = tempPoint.y - (vesselHeight * 0.65f)
                val boundsMaxY = tempPoint.y + (vesselHeight * 0.65f)

                vesselHitboxes.add(
                    VesselHitbox(
                        vessel = vessel,
                        minX = boundsMinX,
                        maxX = boundsMaxX,
                        minY = boundsMinY,
                        maxY = boundsMaxY
                    )
                )
            }
        }

        // 3. Update falling particles
        val particles = pool.getAll()
        val count = pool.activeCount
        for (i in 0 until count) {
            val p = particles[i]
            if (!p.isActive) continue

            p.vy += gravity * safeDt
            p.x += p.vx * safeDt
            p.y += p.vy * safeDt

            var handled = false

            for (hb in vesselHitboxes) {
                if (p.x in hb.minX..hb.maxX && p.y in hb.minY..hb.maxY) {
                    val target = hb.vessel
                    if (target.color == p.color) {
                        val wasFull = target.isFull
                        val becameFull = target.addParticle()

                        if (becameFull) {
                            soundSynthesizer.playVesselFull()
                            hapticManager.onVesselFilled()
                            onVesselCompleted?.invoke(target)
                        } else if (!wasFull) {
                            soundSynthesizer.playParticleCatch(p.color.id)
                            hapticManager.onParticleCaptured()
                        }

                        pool.release(p)
                        handled = true
                        break
                    } else {
                        // Deflect particle sideways
                        p.vx = if (p.x < (hb.minX + hb.maxX) / 2f) -140f else 140f
                        p.vy = -50f
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

        if (sandMatrix.remainingGrainCount <= 0 && pool.activeCount == 0) {
            onArtworkCleared?.invoke()
        }
    }

    private data class VesselHitbox(
        val vessel: Vessel,
        val minX: Float,
        val maxX: Float,
        val minY: Float,
        val maxY: Float
    )
}
