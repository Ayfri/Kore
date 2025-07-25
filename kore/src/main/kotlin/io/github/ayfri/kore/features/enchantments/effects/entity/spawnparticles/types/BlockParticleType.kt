package io.github.ayfri.kore.features.enchantments.effects.entity.spawnparticles.types

import io.github.ayfri.kore.arguments.types.resources.BlockArgument
import io.github.ayfri.kore.commands.particle.types.BlockParticleType
import io.github.ayfri.kore.data.block.BlockState
import io.github.ayfri.kore.generated.arguments.types.ParticleTypeArgument
import kotlinx.serialization.Serializable

@Serializable
data class BlockParticleType(
	override var type: ParticleTypeArgument,
	var blockState: BlockState,
) : ParticleType()

fun BlockParticleType.blockState(block: BlockArgument, properties: Map<String, String>? = null) = apply {
	blockState = BlockState(name = block, properties = properties)
}
