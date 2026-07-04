package io.github.ayfri.kore.features.enchantments.effects.entity.spawnparticles.types

import io.github.ayfri.kore.arguments.types.resources.BlockArgument
import io.github.ayfri.kore.data.block.BlockState
import io.github.ayfri.kore.generated.arguments.types.ParticleTypeArgument
import kotlinx.serialization.Serializable

@Serializable
data class BlockParticleType(
	override var type: ParticleTypeArgument,
	var blockState: BlockState,
) : ParticleType()

fun blockParticleType(type: ParticleTypeArgument, block: BlockArgument, properties: Map<String, String>? = null) =
	BlockParticleType(type, BlockState(block, properties))

fun blockParticleType(type: ParticleTypeArgument, blockState: BlockState) = BlockParticleType(type, blockState)
