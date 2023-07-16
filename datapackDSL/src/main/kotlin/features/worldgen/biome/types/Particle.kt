package features.worldgen.biome.types

import arguments.types.resources.ParticleArgument
import kotlinx.serialization.Serializable

@Serializable
data class Particle(
	var options: ParticleOptions,
	var probability: Float
)

@Serializable
data class ParticleOptions(
	var type: ParticleArgument
)
