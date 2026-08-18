package io.github.ayfri.kore.features.enchantments.effects.entity.spawnparticles.types

import io.github.ayfri.kore.generated.arguments.types.ParticleTypeArgument
import kotlinx.serialization.Serializable

/** Options for the `minecraft:geyser_plume` particle, spawned above a column of [waterBlocks] water blocks. */
@Serializable
data class GeyserPlumeParticleType(
	override var type: ParticleTypeArgument,
	var waterBlocks: Int,
) : ParticleType()

fun geyserPlumeParticleType(type: ParticleTypeArgument, waterBlocks: Int) = GeyserPlumeParticleType(type, waterBlocks)
