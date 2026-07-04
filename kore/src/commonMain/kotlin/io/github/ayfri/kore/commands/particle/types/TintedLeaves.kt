package io.github.ayfri.kore.commands.particle.types

import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.colors.ColorAsDoubleArrayRGBASerializer
import io.github.ayfri.kore.arguments.maths.Vec3
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
import io.github.ayfri.kore.generated.Particles as ParticleNames

@Serializable
data class TintedLeaves(
	@Serializable(with = ColorAsDoubleArrayRGBASerializer::class) val color: Color,
) : ParticleData()

fun Particles.tintedLeaves(color: Color, pos: Vec3? = null) =
	fn.addLine(command(	"particle",	ParticleType(ParticleNames.TINTED_LEAVES, TintedLeaves(color)).asParticleArg(), pos))

fun Particles.tintedLeaves(
	color: Color,
	pos: Vec3,
	delta: Vec3,
	speed: Double,
	count: Int,
	mode: ParticleMode? = null,
	viewers: Int? = null,
) =
	fn.addLine(
		command(
			"particle",
			ParticleType(ParticleNames.TINTED_LEAVES, TintedLeaves(color)).asParticleArg(),
			pos,
			delta,
			float(speed),
			int(count),
			literal(mode?.asArg()),
			viewers?.let { int(it) }
		)
	)
