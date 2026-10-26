package com.heimdall.aetherloop.game.model

/**
 * Granular Aether Particle.
 *
 * Represents a single falling grain of celestial stardust simulated with fluid physics.
 */
class Particle {
    var x: Float = 0f
    var y: Float = 0f
    var vx: Float = 0f
    var vy: Float = 0f
    var radius: Float = 4f
    var color: AetherColor = AetherColor.SOLAR_GOLD
    var alpha: Float = 1.0f
    var life: Float = 1.0f
    var isActive: Boolean = false
    var sourceConduitId: Int = 0

    fun reset(
        startX: Float,
        startY: Float,
        initialVx: Float,
        initialVy: Float,
        particleColor: AetherColor,
        conduitId: Int,
        particleRadius: Float = 4.5f
    ) {
        x = startX
        y = startY
        vx = initialVx
        vy = initialVy
        radius = particleRadius
        color = particleColor
        alpha = 1.0f
        life = 1.0f
        isActive = true
        sourceConduitId = conduitId
    }

    fun markInactive() {
        isActive = false
    }
}

/**
 * Reusable memory pool for stardust particles to prevent Garbage Collector pauses.
 */
class ParticlePool(capacity: Int = 600) {
    private val pool = Array(capacity) { Particle() }
    private var freeIndex = 0

    fun obtain(): Particle? {
        if (freeIndex < pool.size) {
            val p = pool[freeIndex]
            freeIndex++
            return p
        }
        return null
    }

    fun release(particle: Particle) {
        particle.markInactive()
        if (freeIndex > 0) {
            freeIndex--
            // Swap back to maintain pool array
            pool[freeIndex] = particle
        }
    }

    val activeCount: Int
        get() = freeIndex

    fun getAll(): Array<Particle> = pool
}
