package io.github.ayfri.kore.commands.particle

import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.commands.particle.types.ParticleData
import io.github.ayfri.kore.generated.arguments.types.ParticleTypeArgument
import io.github.ayfri.kore.utils.encodeToSnbt
import io.github.ayfri.kore.utils.snbtSerializer

data class ParticleType<T : ParticleData>(var particle: ParticleTypeArgument, var data: T) {
	override fun toString() = "${particle.name.lowercase()}${snbtSerializer.encodeToSnbt(ParticleData.serializer(), data)}"
}

fun ParticleType<*>.asParticleArg() = literal(toString())
