package io.github.ayfri.kore.features.enchantments.effects.entity.spawnparticles.types

import io.github.ayfri.kore.generated.arguments.types.ParticleTypeArgument
import kotlinx.serialization.Serializable

/** Options for the `minecraft:geyser` particle, spawned above a column of [waterBlocks] water blocks. */
@Serializable
data class GeyserParticleType(
	override var type: ParticleTypeArgument,
	var waterBlocks: Int,
) : ParticleType()

fun geyserParticleType(type: ParticleTypeArgument, waterBlocks: Int) = GeyserParticleType(type, waterBlocks)
