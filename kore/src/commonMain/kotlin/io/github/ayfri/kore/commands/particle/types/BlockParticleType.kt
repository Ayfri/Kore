package io.github.ayfri.kore.commands.particle.types

import io.github.ayfri.kore.arguments.maths.Vec3
import io.github.ayfri.kore.arguments.types.EntityArgument
import io.github.ayfri.kore.arguments.types.literals.float
import io.github.ayfri.kore.arguments.types.literals.int
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.arguments.types.resources.BlockArgument
import io.github.ayfri.kore.commands.command
import io.github.ayfri.kore.commands.particle.ParticleMode
import io.github.ayfri.kore.commands.particle.ParticleType
import io.github.ayfri.kore.commands.particle.Particles
import io.github.ayfri.kore.commands.particle.asParticleArg
import io.github.ayfri.kore.data.block.BlockState
import io.github.ayfri.kore.generated.arguments.types.ParticleTypeArgument
import io.github.ayfri.kore.utils.asArg
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import io.github.ayfri.kore.generated.Particles as ParticlesNames

@Serializable
data class BlockParticleType(
	@SerialName("block_state")
	var blockState: BlockState,
) : ParticleData()

fun BlockParticleType.blockState(block: BlockArgument, properties: Map<String, String>? = null) = apply {
	blockState = BlockState(name = block, properties = properties)
}

private fun Particles.blockParticleType(
	name: ParticleTypeArgument,
	blockState: BlockState,
	pos: Vec3? = null,
	delta: Vec3? = null,
	speed: Double? = null,
	count: Int? = null,
	mode: ParticleMode? = null,
	viewers: EntityArgument? = null,
) =
	fn.addLine(
		command(
			"particle",
			ParticleType(name, BlockParticleType(blockState)).asParticleArg(),
			pos,
			delta,
			float(speed),
			int(count),
			literal(mode?.asArg()),
			viewers
		)
	)

fun Particles.block(block: BlockState, pos: Vec3? = null) = blockParticleType(ParticlesNames.BLOCK, block, pos)
fun Particles.block(block: BlockArgument, pos: Vec3? = null) = block(BlockState(block), pos)

fun Particles.block(
	block: BlockState,
	pos: Vec3,
	delta: Vec3,
	speed: Double,
	count: Int,
	mode: ParticleMode? = null,
	viewers: EntityArgument? = null,
) = blockParticleType(ParticlesNames.BLOCK, block, pos, delta, speed, count, mode, viewers)

fun Particles.block(
	block: BlockArgument,
	pos: Vec3,
	delta: Vec3,
	speed: Double,
	count: Int,
	mode: ParticleMode? = null,
	viewers: EntityArgument? = null,
) = block(BlockState(block), pos, delta, speed, count, mode, viewers)

fun Particles.blockCrumble(block: BlockState, pos: Vec3? = null) = blockParticleType(ParticlesNames.BLOCK_CRUMBLE, block, pos)
fun Particles.blockCrumble(block: BlockArgument, pos: Vec3? = null) = blockCrumble(BlockState(block), pos)

fun Particles.blockCrumble(
	block: BlockState,
	pos: Vec3,
	delta: Vec3,
	speed: Double,
	count: Int,
	mode: ParticleMode? = null,
	viewers: EntityArgument? = null,
) = blockParticleType(ParticlesNames.BLOCK_CRUMBLE, block, pos, delta, speed, count, mode, viewers)


fun Particles.blockMarker(block: BlockState, pos: Vec3? = null) = blockParticleType(ParticlesNames.BLOCK_MARKER, block, pos)
fun Particles.blockMarker(block: BlockArgument, pos: Vec3? = null) = blockMarker(BlockState(block), pos)

fun Particles.blockMarker(
	block: BlockState,
	pos: Vec3,
	delta: Vec3,
	speed: Double,
	count: Int,
	mode: ParticleMode? = null,
	viewers: EntityArgument? = null,
) = blockParticleType(ParticlesNames.BLOCK_MARKER, block, pos, delta, speed, count, mode, viewers)

fun Particles.blockMarker(
	block: BlockArgument,
	pos: Vec3,
	delta: Vec3,
	speed: Double,
	count: Int,
	mode: ParticleMode? = null,
	viewers: EntityArgument? = null,
) = blockMarker(BlockState(block), pos, delta, speed, count, mode, viewers)


fun Particles.dustPillar(block: BlockState, pos: Vec3? = null) = blockParticleType(ParticlesNames.DUST_PILLAR, block, pos)
fun Particles.dustPillar(block: BlockArgument, pos: Vec3? = null) = dustPillar(BlockState(block), pos)

fun Particles.dustPillar(
	block: BlockState,
	pos: Vec3,
	delta: Vec3,
	speed: Double,
	count: Int,
	mode: ParticleMode? = null,
	viewers: EntityArgument? = null,
) = blockParticleType(ParticlesNames.DUST_PILLAR, block, pos, delta, speed, count, mode, viewers)

fun Particles.dustPillar(
	block: BlockArgument,
	pos: Vec3,
	delta: Vec3,
	speed: Double,
	count: Int,
	mode: ParticleMode? = null,
	viewers: EntityArgument? = null,
) = dustPillar(BlockState(block), pos, delta, speed, count, mode, viewers)


fun Particles.fallingDust(block: BlockState, pos: Vec3? = null) = blockParticleType(ParticlesNames.FALLING_DUST, block, pos)
fun Particles.fallingDust(block: BlockArgument, pos: Vec3? = null) = fallingDust(BlockState(block), pos)

fun Particles.fallingDust(
	block: BlockState,
	pos: Vec3,
	delta: Vec3,
	speed: Double,
	count: Int,
	mode: ParticleMode? = null,
	viewers: EntityArgument? = null,
) = blockParticleType(ParticlesNames.FALLING_DUST, block, pos, delta, speed, count, mode, viewers)

fun Particles.fallingDust(
	block: BlockArgument,
	pos: Vec3,
	delta: Vec3,
	speed: Double,
	count: Int,
	mode: ParticleMode? = null,
	viewers: EntityArgument? = null,
) = fallingDust(BlockState(block), pos, delta, speed, count, mode, viewers)
