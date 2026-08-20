package io.github.ayfri.kore.features.enchantments.effects.entity.spawnparticles.types

import io.github.ayfri.kore.arguments.types.resources.BlockArgument
import io.github.ayfri.kore.data.block.BlockState
import io.github.ayfri.kore.generated.arguments.types.ParticleTypeArgument
import kotlinx.serialization.Serializable

/**
 * A particle textured after [blockState], such as `block`, `block_marker`, `falling_dust` or `dust_pillar`.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Particle#block
 *
 * @property type The id of the particle.
 * @property blockState The block the particle takes its texture from.
 */
@Serializable
data class BlockParticleType(
	override var type: ParticleTypeArgument,
	var blockState: BlockState,
) : ParticleType()

/** Creates the options of a block-textured particle. */
fun ParticleTypeScope.blockParticleType(type: ParticleTypeArgument, block: BlockArgument, properties: Map<String, String>? = null) =
	BlockParticleType(type, BlockState(block, properties))

/** Creates the options of a block-textured particle. */
fun ParticleTypeScope.blockParticleType(type: ParticleTypeArgument, blockState: BlockState) = BlockParticleType(type, blockState)
