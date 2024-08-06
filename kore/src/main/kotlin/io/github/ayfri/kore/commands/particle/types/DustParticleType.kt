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
import io.github.ayfri.kore.generated.Particles as ParticlesNames

@Serializable
data class Dust(
	var color: @Serializable(ColorAsDoubleArraySerializer::class) Color,
	var scale: Double,
) : ParticleData()

fun Particles.dust(color: Color, size: Double = 1.0, pos: Vec3? = null) =
	fn.addLine(command("particle", ParticleType(ParticlesNames.DUST, Dust(color, size)).asParticleArg(), pos))

fun Particles.dust(
	color: Color,
	size: Double = 1.0,
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
			ParticleType(ParticlesNames.DUST, Dust(color, size)).asParticleArg(),
			pos,
			delta,
			float(speed),
			int(count),
			literal(mode?.asArg()),
			viewers
		)
	)
