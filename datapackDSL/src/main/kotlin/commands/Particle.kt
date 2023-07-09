package commands

import arguments.*
import functions.Function
import kotlinx.serialization.Serializable
import serializers.LowercaseSerializer
import utils.asArg


@Serializable(ParticleMode.Companion.ParticleModeSerializer::class)
enum class ParticleMode {
	NORMAL,
	FORCE;

	companion object {
		data object ParticleModeSerializer : LowercaseSerializer<ParticleMode>(entries)
	}
}

private fun Color.asArgs() = toRGB().normalizedArray.map { float(it) }.toTypedArray()

class Particles(private val fn: Function) {
	fun block(block: Argument.Block, pos: Vec3? = null) = fn.addLine(command("particle", literal("block"), block, pos))
	fun block(
		block: Argument.Block,
		pos: Vec3,
		delta: Vec3,
		speed: Double,
		count: Int,
		mode: ParticleMode? = null,
		viewers: Argument.Entity? = null
	) =
		fn.addLine(command("particle", literal("block"), block, pos, delta, float(speed), int(count), literal(mode?.asArg()), viewers))

	fun blockMarker(block: Argument.Block, pos: Vec3? = null) = fn.addLine(command("particle", literal("block_marker"), block, pos))
	fun blockMarker(
		block: Argument.Block,
		pos: Vec3,
		delta: Vec3,
		speed: Double,
		count: Int,
		mode: ParticleMode? = null,
		viewers: Argument.Entity? = null
	) =
		fn.addLine(
			command(
				"particle",
				literal("block_marker"),
				block,
				pos,
				delta,
				float(speed),
				int(count),
				literal(mode?.asArg()),
				viewers
			)
		)

	fun fallingDust(block: Argument.Block, pos: Vec3? = null) = fn.addLine(command("particle", literal("falling_dust"), block, pos))
	fun fallingDust(
		block: Argument.Block,
		pos: Vec3,
		delta: Vec3,
		speed: Double,
		count: Int,
		mode: ParticleMode? = null,
		viewers: Argument.Entity? = null
	) =
		fn.addLine(
			command(
				"particle",
				literal("falling_dust"),
				block,
				pos,
				delta,
				float(speed),
				int(count),
				literal(mode?.asArg()),
				viewers
			)
		)

	fun dust(color: Color, size: Double = 1.0, pos: Vec3? = null) =
		fn.addLine(command("particle", literal("dust"), *color.asArgs(), float(size), pos))

	fun dust(
		color: Color,
		size: Double = 1.0,
		pos: Vec3,
		delta: Vec3,
		speed: Double,
		count: Int,
		mode: ParticleMode? = null,
		viewers: Argument.Entity? = null
	) =
		fn.addLine(
			command(
				"particle",
				literal("dust"),
				*color.asArgs(),
				float(size),
				pos,
				delta,
				float(speed),
				int(count),
				literal(mode?.asArg()),
				viewers
			)
		)

	fun dustColorTransition(color1: Color, size: Double, color2: Color, pos: Vec3? = null) =
		fn.addLine(command("particle", literal("dust_color_transition"), *color1.asArgs(), float(size), *color2.asArgs(), pos))

	fun dustColorTransition(
		color1: Color,
		size: Double,
		color2: Color,
		pos: Vec3,
		delta: Vec3,
		speed: Double,
		count: Int,
		mode: ParticleMode? = null,
		viewers: Argument.Entity? = null
	) =
		fn.addLine(
			command(
				"particle",
				literal("dust_color_transition"),
				*color1.asArgs(),
				float(size),
				*color2.asArgs(),
				pos,
				delta,
				float(speed),
				int(count),
				literal(mode?.asArg()),
				viewers
			)
		)

	fun item(item: Argument.Item, pos: Vec3? = null) = fn.addLine(command("particle", literal("item"), item, pos))
	fun item(
		item: Argument.Item,
		pos: Vec3,
		delta: Vec3,
		speed: Double,
		count: Int,
		mode: ParticleMode? = null,
		viewers: Argument.Entity? = null
	) =
		fn.addLine(command("particle", literal("item"), item, pos, delta, float(speed), int(count), literal(mode?.asArg()), viewers))

	fun particle(particle: Argument.Particle, pos: Vec3? = null) = fn.addLine(command("particle", particle, pos))
	fun particle(
		particle: Argument.Particle,
		pos: Vec3,
		delta: Vec3,
		speed: Double,
		count: Int,
		mode: ParticleMode? = null,
		viewers: Argument.Entity? = null
	) =
		fn.addLine(command("particle", particle, pos, delta, float(speed), int(count), literal(mode?.asArg()), viewers))

	fun sculkCharge(angle: Double, pos: Vec3? = null) = fn.addLine(command("particle", literal("sculk_charge"), float(angle), pos))
	fun sculkCharge(
		angle: Double,
		pos: Vec3,
		delta: Vec3,
		speed: Double,
		count: Int,
		mode: ParticleMode? = null,
		viewers: Argument.Entity? = null
	) =
		fn.addLine(
			command(
				"particle",
				literal("sculk_charge"),
				float(angle),
				pos,
				delta,
				float(speed),
				int(count),
				literal(mode?.asArg()),
				viewers
			)
		)

	fun shriek(delay: Int, pos: Vec3? = null) = fn.addLine(command("particle", literal("shriek"), int(delay), pos))
	fun shriek(
		delay: Int,
		pos: Vec3,
		delta: Vec3,
		speed: Double,
		count: Int,
		mode: ParticleMode? = null,
		viewers: Argument.Entity? = null
	) =
		fn.addLine(
			command(
				"particle",
				literal("shriek"),
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
		fn.addLine(command("particle", literal("vibration"), destination, int(duration), pos))

	fun vibration(
		destination: Vec3,
		duration: Int,
		pos: Vec3,
		delta: Vec3,
		speed: Double,
		count: Int,
		mode: ParticleMode? = null,
		viewers: Argument.Entity? = null
	) =
		fn.addLine(
			command(
				"particle",
				literal("vibration"),
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

fun Function.particle(particle: Argument.Particle, pos: Vec3? = null) = addLine(command("particle", particle, pos))

fun Function.particle(
	particle: Argument.Particle,
	pos: Vec3,
	delta: Vec3,
	speed: Double,
	count: Int,
	mode: ParticleMode? = null,
	viewers: Argument.Entity? = null,
) = addLine(command("particle", particle, pos, delta, float(speed), int(count), literal(mode?.asArg()), viewers))

val Function.particles get() = Particles(this)
fun Function.particles(block: Particles.() -> Unit) = Particles(this).block()
