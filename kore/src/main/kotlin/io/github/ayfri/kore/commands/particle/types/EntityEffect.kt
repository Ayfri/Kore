package io.github.ayfri.kore.commands.particle.types

import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.colors.ColorAsDoubleArraySerializer
import io.github.ayfri.kore.arguments.maths.Vec3
import io.github.ayfri.kore.arguments.types.EntityArgument
import io.github.ayfri.kore.arguments.types.literals.float
import io.github.ayfri.kore.arguments.types.literals.int
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.commands.command
import io.github.ayfri.kore.commands.particle.ParticleMode
import io.github.ayfri.kore.commands.particle.ParticleType
import io.github.ayfri.kore.commands.particle.Particles
import io.github.ayfri.kore.commands.particle.asParticleArg
import io.github.ayfri.kore.utils.asArg
import kotlinx.serialization.Serializable

@Serializable
data class EntityEffect(
	var color: @Serializable(ColorAsDoubleArraySerializer::class) Color,
) : ParticleData()

fun Particles.entityEffect(
	color: Color,
	pos: Vec3? = null,
	delta: Vec3? = null,
	speed: Double? = null,
	count: Int? = null,
	mode: ParticleMode? = null,
	viewers: EntityArgument? = null,
) = fn.addLine(
	command(
		"particle",
		ParticleType("entity_effect", EntityEffect(color)).asParticleArg(),
		pos,
		delta,
		float(speed),
		int(count),
		literal(mode?.asArg()),
		viewers
	)
)
