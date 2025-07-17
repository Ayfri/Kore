package io.github.ayfri.kore.commands.particle

import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.maths.Vec3
import io.github.ayfri.kore.arguments.types.EntityArgument
import io.github.ayfri.kore.arguments.types.literals.float
import io.github.ayfri.kore.arguments.types.literals.int
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.commands.command
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.generated.arguments.types.ParticleTypeArgument
import io.github.ayfri.kore.serializers.LowercaseSerializer
import io.github.ayfri.kore.utils.asArg
import kotlinx.serialization.Serializable
import io.github.ayfri.kore.generated.Particles as ParticlesNames

@Serializable(ParticleMode.Companion.ParticleModeSerializer::class)
enum class ParticleMode {
	NORMAL,
	FORCE;

	companion object {
		data object ParticleModeSerializer : LowercaseSerializer<ParticleMode>(entries)
	}
}

private fun Color.asArgs() = toRGB().normalizedArray.map { float(it) }.toTypedArray()

class Particles(internal val fn: Function) {
	fun particle(particle: ParticleTypeArgument, pos: Vec3? = null) = fn.addLine(command("particle", particle, pos))
	fun particle(
		particle: ParticleTypeArgument,
		pos: Vec3,
		delta: Vec3,
		speed: Double,
		count: Int,
		mode: ParticleMode? = null,
		viewers: EntityArgument? = null,
	) =
		fn.addLine(command("particle", particle, pos, delta, float(speed), int(count), literal(mode?.asArg()), viewers))

	fun sculkCharge(angle: Double, pos: Vec3? = null) =
		fn.addLine(command("particle", literal(ParticlesNames.SCULK_CHARGE.name.lowercase()), float(angle), pos))

	fun sculkCharge(
		angle: Double,
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
				ParticlesNames.SCULK_CHARGE,
				float(angle),
				pos,
				delta,
				float(speed),
				int(count),
				literal(mode?.asArg()),
				viewers
			)
		)

	fun shriek(delay: Int, pos: Vec3? = null) =
		fn.addLine(command("particle", literal(ParticlesNames.SHRIEK.name.lowercase()), int(delay), pos))

	fun shriek(
		delay: Int,
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
				ParticlesNames.SHRIEK,
				int(delay),
				pos,
				delta,
				float(speed),
				int(count),
				literal(mode?.asArg()),
				viewers
			)
		)

	fun vibration(destination: Vec3, duration: Int, pos: Vec3? = null) =
		fn.addLine(command("particle", literal(ParticlesNames.VIBRATION.name.lowercase()), destination, int(duration), pos))

	fun vibration(
		destination: Vec3,
		duration: Int,
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
				literal(ParticlesNames.VIBRATION.name.lowercase()),
				destination,
				int(duration),
				pos,
				delta,
				float(speed),
				int(count),
				literal(mode?.asArg()),
				viewers
			)
		)
}

fun Function.particle(particle: ParticleTypeArgument, pos: Vec3? = null) = addLine(command("particle", particle, pos))

fun Function.particle(
	particle: ParticleTypeArgument,
	pos: Vec3,
	delta: Vec3,
	speed: Double,
	count: Int,
	mode: ParticleMode? = null,
	viewers: EntityArgument? = null,
) = addLine(command("particle", particle, pos, delta, float(speed), int(count), literal(mode?.asArg()), viewers))

val Function.particles get() = Particles(this)
fun Function.particles(block: Particles.() -> Unit) = Particles(this).block()
