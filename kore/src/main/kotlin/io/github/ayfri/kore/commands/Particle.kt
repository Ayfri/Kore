package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.maths.Vec3
import io.github.ayfri.kore.arguments.types.EntityArgument
import io.github.ayfri.kore.arguments.types.literals.float
import io.github.ayfri.kore.arguments.types.literals.int
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.arguments.types.resources.BlockArgument
import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.arguments.types.resources.ParticleArgument
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.serializers.LowercaseSerializer
import io.github.ayfri.kore.utils.asArg
import kotlinx.serialization.Serializable

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
	fun block(block: BlockArgument, pos: Vec3? = null) = fn.addLine(command("particle", literal("block"), block, pos))
	fun block(
		block: BlockArgument,
		pos: Vec3,
		delta: Vec3,
		speed: Double,
		count: Int,
		mode: ParticleMode? = null,
		viewers: EntityArgument? = null,
	) =
		fn.addLine(command("particle", literal("block"), block, pos, delta, float(speed), int(count), literal(mode?.asArg()), viewers))

	fun blockMarker(block: BlockArgument, pos: Vec3? = null) = fn.addLine(command("particle", literal("block_marker"), block, pos))
	fun blockMarker(
		block: BlockArgument,
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
		viewers: EntityArgument? = null,
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
		viewers: EntityArgument? = null,
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

	fun fallingDust(block: BlockArgument, pos: Vec3? = null) = fn.addLine(command("particle", literal("falling_dust"), block, pos))
	fun fallingDust(
		block: BlockArgument,
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

	fun item(item: ItemArgument, pos: Vec3? = null) = fn.addLine(command("particle", literal("item"), item, pos))
	fun item(
		item: ItemArgument,
		pos: Vec3,
		delta: Vec3,
		speed: Double,
		count: Int,
		mode: ParticleMode? = null,
		viewers: EntityArgument? = null,
	) =
		fn.addLine(command("particle", literal("item"), item, pos, delta, float(speed), int(count), literal(mode?.asArg()), viewers))

	fun particle(particle: ParticleArgument, pos: Vec3? = null) = fn.addLine(command("particle", particle, pos))
	fun particle(
		particle: ParticleArgument,
		pos: Vec3,
		delta: Vec3,
		speed: Double,
		count: Int,
		mode: ParticleMode? = null,
		viewers: EntityArgument? = null,
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
		viewers: EntityArgument? = null,
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
		viewers: EntityArgument? = null,
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
		viewers: EntityArgument? = null,
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

fun Function.particle(particle: ParticleArgument, pos: Vec3? = null) = addLine(command("particle", particle, pos))

fun Function.particle(
	particle: ParticleArgument,
	pos: Vec3,
	delta: Vec3,
	speed: Double,
	count: Int,
	mode: ParticleMode? = null,
	viewers: EntityArgument? = null,
) = addLine(command("particle", particle, pos, delta, float(speed), int(count), literal(mode?.asArg()), viewers))

val Function.particles get() = Particles(this)
fun Function.particles(block: Particles.() -> Unit) = Particles(this).block()
