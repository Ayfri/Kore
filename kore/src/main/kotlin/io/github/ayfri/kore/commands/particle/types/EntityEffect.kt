@file:JvmName("ParticleCommand")
package io.github.ayfri.kore.commands.particle.types

import io.github.ayfri.kore.arguments.colors.ARGB
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
data class DragonBreath(
	var power: Float? = null,
) : ParticleData()

@Serializable
data class EntityEffect(
	var color: @Serializable(ColorAsDoubleArraySerializer::class) Color? = null,
	var power: Float? = null,
) : ParticleData()

@Serializable
data class Flash(
	var color: ARGB,
) : ParticleData()

@Serializable
data class InstantEffect(
	var color: @Serializable(ColorAsDoubleArraySerializer::class) Color? = null,
	var power: Float? = null,
) : ParticleData()

fun Particles.dragonBreath(
	power: Float? = null,
	pos: Vec3? = null,
	delta: Vec3? = null,
	speed: Double? = null,
	count: Int? = null,
	mode: ParticleMode? = null,
	viewers: EntityArgument? = null,
) = fn.addLine(
	command(
		"particle",
		ParticleType(ParticlesNames.DRAGON_BREATH, DragonBreath(power)).asParticleArg(),
		pos,
		delta,
		float(speed),
		int(count),
		literal(mode?.asArg()),
		viewers
	)
)

fun Particles.entityEffect(
	color: Color? = null,
	power: Float? = null,
	pos: Vec3? = null,
	delta: Vec3? = null,
	speed: Double? = null,
	count: Int? = null,
	mode: ParticleMode? = null,
	viewers: EntityArgument? = null,
) = fn.addLine(
	command(
		"particle",
		ParticleType(ParticlesNames.ENTITY_EFFECT, EntityEffect(color, power)).asParticleArg(),
		pos,
		delta,
		float(speed),
		int(count),
		literal(mode?.asArg()),
		viewers
	)
)

fun Particles.flash(
	color: ARGB,
	pos: Vec3? = null,
	delta: Vec3? = null,
	speed: Double? = null,
	count: Int? = null,
	mode: ParticleMode? = null,
	viewers: EntityArgument? = null,
) = fn.addLine(
	command(
		"particle",
		ParticleType(ParticlesNames.FLASH, Flash(color)).asParticleArg(),
		pos,
		delta,
		float(speed),
		int(count),
		literal(mode?.asArg()),
		viewers
	)
)

fun Particles.instantEffect(
	color: Color? = null,
	power: Float? = null,
	pos: Vec3? = null,
	delta: Vec3? = null,
	speed: Double? = null,
	count: Int? = null,
	mode: ParticleMode? = null,
	viewers: EntityArgument? = null,
) = fn.addLine(
	command(
		"particle",
		ParticleType(ParticlesNames.INSTANT_EFFECT, InstantEffect(color, power)).asParticleArg(),
		pos,
		delta,
		float(speed),
		int(count),
		literal(mode?.asArg()),
		viewers
	)
)
