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
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import io.github.ayfri.kore.generated.Particles as ParticlesNames

@Serializable
data class DustColorTransition(
	@SerialName("from_color")
	var fromColor: @Serializable(ColorAsDoubleArraySerializer::class) Color,
	@SerialName("to_color")
	var toColor: @Serializable(ColorAsDoubleArraySerializer::class) Color,
	var scale: Double,
) : ParticleData()

fun Particles.dustColorTransition(fromColor: Color, size: Double, toColor: Color, pos: Vec3? = null) =
	fn.addLine(
		command(
			"particle",
			ParticleType(ParticlesNames.DUST_COLOR_TRANSITION, DustColorTransition(fromColor, toColor, size)).asParticleArg(),
			pos
		)
	)

fun Particles.dustColorTransition(
	fromColor: Color,
	scale: Double,
	toColor: Color,
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
			ParticleType(ParticlesNames.DUST_COLOR_TRANSITION, DustColorTransition(fromColor, toColor, scale)).asParticleArg(),
			pos,
			delta,
			float(speed),
			int(count),
			literal(mode?.asArg()),
			viewers
		)
	)
