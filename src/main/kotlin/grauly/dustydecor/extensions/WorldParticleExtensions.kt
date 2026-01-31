package grauly.dustydecor.extensions

import net.minecraft.core.particles.ParticleOptions
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.phys.Vec3

fun <T : ParticleOptions> ServerLevel.spawnParticle(effect: T, pos: Vec3, vel: Vec3 = Vec3(0.0, 0.0, 0.0), speed: Double = 0.0) {
    this.sendParticles(effect, pos.x, pos.y, pos.z, 0, vel.x, vel.y, vel.z, speed)
}