package io.github.ayfri.kore.commands.particle

import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.arguments.types.resources.ParticleArgument
import io.github.ayfri.kore.commands.particle.types.ParticleData
import net.benwoodworth.knbt.StringifiedNbt

private val nbtSerializer = StringifiedNbt {
	nameRootClasses = false
}

data class ParticleType<T : ParticleData>(var particle: ParticleArgument, var data: T) {
	override fun toString() = "$particle${nbtSerializer.encodeToString(ParticleData.serializer(), data)}"
}

fun ParticleType<*>.asParticleArg() = literal(toString())
