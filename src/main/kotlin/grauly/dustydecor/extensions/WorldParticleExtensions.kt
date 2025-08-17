package grauly.dustydecor.extensions

import net.minecraft.particle.ParticleEffect
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.Vec3d

fun <T : ParticleEffect> ServerWorld.spawnParticle(effect: T, pos: Vec3d) {
    this.spawnParticles(effect, pos.x, pos.y, pos.z, 0, 0.0, 0.0, 0.0, 0.0)
}