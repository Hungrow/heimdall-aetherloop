package com.heimdall.aetherloop.game.physics

import android.graphics.PointF
import com.heimdall.aetherloop.core.HapticManager
import com.heimdall.aetherloop.core.SoundSynthesizer
import com.heimdall.aetherloop.game.model.*
import kotlin.math.abs
import kotlin.random.Random

/**
 * High-performance Granular Physics and Particle Collision Engine.
 *
 * Simulates fluid stardust physics at 60 FPS, container interception,
 * ASMR audio cues, and void spill tracking.
 */
class ParticleEngine(
    private val pool: ParticlePool = ParticlePool(500),
    private val soundSynthesizer: SoundSynthesizer,
    private val hapticManager: HapticManager
) {
    private val tempPoint = PointF()
    private val gravity = 880f // dp/s^2 downwards acceleration

    // Direct access to particle pool for zero-allocation rendering
    val activeParticlesCount: Int get() = pool.activeCount
    fun getParticlesArray(): Array<Particle> = pool.getAll()

    // Callbacks to notify game loop
    var onVesselCompleted: ((Vessel) -> Unit)? = null
    var onConduitProgress: ((conduitId: Int, harvested: Int, total: Int) -> Unit)? = null
    var onParticleSpilled: (() -> Unit)? = null

    /**
     * Updates physics for all active particles and manages conduit emissions.
     */
    fun update(
        dt: Float,
        width: Float,
        height: Float,
        conduits: List<Conduit>,
        vessels: List<Vessel>,
        track: BifrostTrack,
        flowRatePerSec: Float
    ) {
        val safeDt = dt.coerceIn(0.001f, 0.05f)

        // 1. Manage conduit particle emission
        for (conduit in conduits) {
            if (conduit.emittedCount < conduit.totalRequired) {
                conduit.emissionAccumulator += safeDt * flowRatePerSec
                while (conduit.emissionAccumulator >= 1.0f && conduit.emittedCount < conduit.totalRequired) {
                    conduit.emissionAccumulator -= 1.0f
                    conduit.emittedCount++

                    val particle = pool.obtain()
                    if (particle != null) {
                        val startX = conduit.xNorm * width + (Random.nextFloat() - 0.5f) * 16f
                        val startY = conduit.yNorm * height
                        val vx = (Random.nextFloat() - 0.5f) * 28f
                        val vy = 40f + Random.nextFloat() * 60f

                        particle.reset(
                            startX = startX,
                            startY = startY,
                            initialVx = vx,
                            initialVy = vy,
                            particleColor = conduit.color,
                            conduitId = conduit.id
                        )
                    }
                }
            }
        }

        // 2. Pre-calculate active vessel capture hitboxes
        val vesselHitboxes = ArrayList<VesselHitbox>(vessels.size)
        val vesselWidth = width * 0.16f
        val vesselHeight = height * 0.055f

        for (vessel in vessels) {
            if (vessel.state == VesselState.ON_TRACK) {
                track.getPointAtProgress(vessel.trackProgress, tempPoint)
                // We test particles near the top half of the loop (track.top)
                val boundsMinX = tempPoint.x - (vesselWidth * 0.55f)
                val boundsMaxX = tempPoint.x + (vesselWidth * 0.55f)
                val boundsMinY = tempPoint.y - (vesselHeight * 0.6f)
                val boundsMaxY = tempPoint.y + (vesselHeight * 0.6f)

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

        // 3. Update active particles and check collisions
        val particles = pool.getAll()
        for (i in 0 until pool.activeCount) {
            val p = particles[i]
            if (!p.isActive) continue

            // Integrate motion
            p.vy += gravity * safeDt
            p.x += p.vx * safeDt
            p.y += p.vy * safeDt

            var handled = false

            // Check collision with vessels
            for (hb in vesselHitboxes) {
                if (p.x in hb.minX..hb.maxX && p.y in hb.minY..hb.maxY) {
                    val targetVessel = hb.vessel

                    if (targetVessel.color == p.color) {
                        // MATCH! Particle harvested into vessel
                        val wasFullBefore = targetVessel.isFull
                        val becameFull = targetVessel.addParticle()

                        // Update corresponding conduit harvest counter
                        val conduit = conduits.firstOrNull { it.id == p.sourceConduitId }
                        if (conduit != null && !conduit.isHarvestComplete) {
                            conduit.harvestedCount++
                            onConduitProgress?.invoke(conduit.id, conduit.harvestedCount, conduit.totalRequired)
                        }

                        // Audio & Haptic ASMR feedback
                        if (becameFull) {
                            soundSynthesizer.playVesselFull()
                            hapticManager.onVesselFilled()
                            onVesselCompleted?.invoke(targetVessel)
                        } else if (!wasFullBefore) {
                            soundSynthesizer.playParticleCatch(p.color.id)
                            hapticManager.onParticleCaptured()
                        }

                        pool.release(p)
                        handled = true
                        break
                    } else {
                        // MISMATCH! Deflect particle laterally
                        p.vx = if (p.x < (hb.minX + hb.maxX) / 2f) -120f else 120f
                        p.vy = -60f // Bounce upward slightly
                        p.alpha *= 0.6f
                    }
                }
            }

            if (handled) continue

            // Void / boundary check: if particle falls below bottom of track or screen
            if (p.y > height * 0.85f || p.x < 0 || p.x > width) {
                pool.release(p)
                onParticleSpilled?.invoke()
            }
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
