package io.github.ayfri.kore.features.enchantments.effects.entity.spawnparticles.types

import io.github.ayfri.kore.generated.arguments.types.ParticleTypeArgument
import kotlinx.serialization.Serializable

/**
 * Options for the `minecraft:geyser_base` particle, spawned above a column of [waterBlocks] water blocks and
 * pushed upwards by [burstImpulseBase].
 */
@Serializable
data class GeyserBaseParticleType(
	override var type: ParticleTypeArgument,
	var burstImpulseBase: Float,
	var waterBlocks: Int,
) : ParticleType()

fun geyserBaseParticleType(type: ParticleTypeArgument, burstImpulseBase: Float, waterBlocks: Int) =
	GeyserBaseParticleType(type, burstImpulseBase, waterBlocks)
