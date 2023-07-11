package features.worldgen.biome.types

import arguments.Argument
import kotlinx.serialization.Serializable

@Serializable
data class Particle(
	var options: ParticleOptions,
	var probability: Float
)

@Serializable
data class ParticleOptions(
	var type: Argument.Particle
)
