package io.github.ayfri.kore.commands.particle.types

import io.github.ayfri.kore.arguments.maths.Vec3
import io.github.ayfri.kore.arguments.types.EntityArgument
import io.github.ayfri.kore.arguments.types.literals.float
import io.github.ayfri.kore.arguments.types.literals.int
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.commands.command
import io.github.ayfri.kore.commands.particle.ParticleMode
import io.github.ayfri.kore.commands.particle.ParticleType
import io.github.ayfri.kore.commands.particle.Particles
import io.github.ayfri.kore.commands.particle.asParticleArg
import io.github.ayfri.kore.data.item.ItemStack
import io.github.ayfri.kore.utils.asArg
import kotlinx.serialization.Serializable

@Serializable
data class ItemParticleType(
	var item: ItemStack,
) : ParticleData()

fun Particles.item(item: ItemArgument, pos: Vec3? = null) =
	fn.addLine(command("particle", ParticleType("item", ItemParticleType(ItemStack(item))).asParticleArg(), pos))

fun Particles.item(
	item: ItemArgument,
	pos: Vec3,
	delta: Vec3,
	speed: Double,
	count: Int,
	mode: ParticleMode? = null,
	viewers: EntityArgument? = null,
) =
	fn.addLine(
		command(
			"particle",
			ParticleType("item", ItemParticleType(ItemStack(item))).asParticleArg(),
			pos,
			delta,
			float(speed),
			int(count),
			literal(mode?.asArg()),
			viewers
		)
	)
