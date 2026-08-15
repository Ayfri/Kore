package io.github.ayfri.kore.helpers.vfx

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.maths.Vec3
import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.arguments.numbers.PosNumber
import io.github.ayfri.kore.commands.particle.particle
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.generatedFunction
import io.github.ayfri.kore.generated.arguments.types.ParticleTypeArgument
import io.github.ayfri.kore.helpers.HelpersConstants
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/** Geometric shapes supported by the VFX engine. */
enum class Shape {
	/** Flat closed circle on the XZ plane, using [VfxShape.radius]. */
	CIRCLE,

	/** Fixed-radius coil rising along Y, using [VfxShape.radius], [VfxShape.height] and [VfxShape.turns]. */
	HELIX,

	/** Straight segment along the `dx`/`dy`/`dz` direction, using [VfxShape.length]. */
	LINE,

	/** Points spread over a sphere surface, using [VfxShape.radius]. */
	SPHERE,

	/** Coil rising along Y whose radius grows from `0` to [VfxShape.radius], using [VfxShape.turns]. */
	SPIRAL,
}

/**
 * Configuration for a geometric particle shape emitted by [drawShape].
 *
 * Geometry is computed once at generation time around [origin], then every point is written in [positionType]'s
 * coordinate space. Which properties are read depends on [shape] - see each [Shape] constant.
 */
class VfxShape {
	/** X component of the [Shape.LINE] direction vector. Normalized with [dy] and [dz], so only the ratio matters. */
	var dx: Double = 1.0

	/** Y component of the [Shape.LINE] direction vector. Normalized with [dx] and [dz], so only the ratio matters. */
	var dy: Double = 0.0

	/** Z component of the [Shape.LINE] direction vector. Normalized with [dx] and [dy], so only the ratio matters. */
	var dz: Double = 0.0

	/** Total rise along Y for [Shape.SPIRAL] and [Shape.HELIX]. */
	var height: Double = 3.0

	/** Total length of a [Shape.LINE]. */
	var length: Double = 5.0

	/**
	 * Offset added to every generated point, before [positionType] is applied.
	 *
	 * Only the three numeric values are used: any `~` / `^` marker carried by this [Vec3] is discarded, since the
	 * coordinate space of the output is decided by [positionType] alone.
	 */
	var origin: Vec3 = vec3(0, 0, 0)

	/** Particle type used for every generated point. */
	lateinit var particle: ParticleTypeArgument

	/** Number of points to generate along the shape. Must be strictly positive. */
	var points: Int = 20

	/**
	 * Coordinate space every generated `particle` command is written in.
	 *
	 * - [PosNumber.Type.RELATIVE] (default) writes `~x ~y ~z`, so the shape is centered on wherever the generated
	 *   function is executed, typically through `execute at <target> run function ...`.
	 * - [PosNumber.Type.LOCAL] writes `^x ^y ^z`, so the shape additionally rotates with the executing entity's
	 *   facing direction.
	 * - [PosNumber.Type.WORLD] writes absolute coordinates, so the shape always lands at the same place in the world
	 *   regardless of where the function is executed.
	 */
	var positionType: PosNumber.Type = PosNumber.Type.RELATIVE

	/** Radius used by [Shape.CIRCLE], [Shape.SPHERE], [Shape.SPIRAL] and [Shape.HELIX]. */
	var radius: Double = 1.0

	/** Which geometry to generate. */
	var shape: Shape = Shape.CIRCLE

	/** Number of full revolutions for [Shape.SPIRAL] and [Shape.HELIX]. */
	var turns: Int = 3
}

/** Offsets a computed point by [VfxShape.origin] and rewrites it in [VfxShape.positionType]'s coordinate space. */
private fun VfxShape.at(x: Double, y: Double, z: Double) = (origin + vec3(x, y, z)).let {
	when (positionType) {
		PosNumber.Type.LOCAL -> it.local
		PosNumber.Type.RELATIVE -> it.relative
		PosNumber.Type.WORLD -> it.world
	}
}

/**
 * Generates a function holding one pre-computed `particle` command per point of the configured shape.
 *
 * The positions are baked at generation time, so with the default [VfxShape.positionType] the resulting function
 * follows whoever runs it:
 *
 * ```kotlin
 * val ring = drawShape("fire_ring") {
 * 	shape = Shape.CIRCLE
 * 	particle = Particles.FLAME
 * 	radius = 5.0
 * 	points = 16
 * }
 *
 * function("cast_ring") {
 * 	execute {
 * 		at(self())
 * 		run { function(ring) }
 * 	}
 * }
 * ```
 *
 * @param name shape name, turned into the function name by [HelpersConstants.vfxShapeFunctionName]
 * @param block configures the [VfxShape] to generate
 * @return the generated [Function], to be called with the `function` command
 * @throws IllegalArgumentException if [VfxShape.points] is not strictly positive
 */
fun DataPack.drawShape(name: String, block: VfxShape.() -> Unit) =
	VfxShape().apply(block).let { cfg ->
		require(cfg.points > 0) { "VfxShape.points must be strictly positive, got ${cfg.points}." }

		generatedFunction(HelpersConstants.vfxShapeFunctionName(name)) {
			when (cfg.shape) {
				Shape.CIRCLE -> drawCircle(cfg)
				Shape.LINE -> drawLine(cfg)
				Shape.SPHERE -> drawSphere(cfg)
				Shape.SPIRAL -> drawSpiral(cfg)
				Shape.HELIX -> drawHelix(cfg)
			}
		}
	}

/**
 * Shorthand for [drawShape] with [Shape.CIRCLE].
 *
 * @param name shape name, turned into the function name by [HelpersConstants.vfxShapeFunctionName]
 * @param particle particle type used for every generated point
 * @param radius circle radius
 * @param points number of points to generate around the circle
 * @param positionType coordinate space the generated `particle` commands are written in
 * @return the generated [Function], to be called with the `function` command
 */
fun DataPack.drawCircle(
	name: String,
	particle: ParticleTypeArgument,
	radius: Double = 1.0,
	points: Int = 20,
	positionType: PosNumber.Type = PosNumber.Type.RELATIVE,
) = drawShape(name) {
	shape = Shape.CIRCLE
	this.particle = particle
	this.radius = radius
	this.points = points
	this.positionType = positionType
}

/** Emits [VfxShape.points] particles evenly spaced around a [Shape.CIRCLE] on the XZ plane. */
private fun Function.drawCircle(cfg: VfxShape) {
	for (i in 0 until cfg.points) {
		val angle = 2.0 * PI * i / cfg.points
		val x = cos(angle) * cfg.radius
		val z = sin(angle) * cfg.radius
		particle(cfg.particle, cfg.at(x, 0.0, z))
	}
}

/**
 * Emits [VfxShape.points] particles evenly spaced along a [Shape.LINE] in the normalized `dx`/`dy`/`dz` direction.
 * Emits nothing when that direction is the zero vector, since it has no orientation to follow.
 */
private fun Function.drawLine(cfg: VfxShape) {
	val mag = sqrt(cfg.dx * cfg.dx + cfg.dy * cfg.dy + cfg.dz * cfg.dz)
	if (mag == 0.0) return
	val nx = cfg.dx / mag
	val ny = cfg.dy / mag
	val nz = cfg.dz / mag
	for (i in 0 until cfg.points) {
		val t = cfg.length * i / (cfg.points - 1).coerceAtLeast(1)
		particle(cfg.particle, cfg.at(nx * t, ny * t, nz * t))
	}
}

/** Emits [VfxShape.points] particles distributed on a [Shape.SPHERE] surface using a golden-angle spiral. */
private fun Function.drawSphere(cfg: VfxShape) {
	val goldenAngle = PI * (3.0 - sqrt(5.0))
	for (i in 0 until cfg.points) {
		val y = 1.0 - 2.0 * i / (cfg.points - 1).coerceAtLeast(1)
		val radiusAtY = sqrt(1.0 - y * y)
		val theta = goldenAngle * i
		val x = cos(theta) * radiusAtY * cfg.radius
		val z = sin(theta) * radiusAtY * cfg.radius
		particle(cfg.particle, cfg.at(x, y * cfg.radius, z))
	}
}

/** Emits [VfxShape.points] particles along a [Shape.SPIRAL] that expands in radius while rising along Y. */
private fun Function.drawSpiral(cfg: VfxShape) {
	for (i in 0 until cfg.points) {
		val t = i.toDouble() / cfg.points
		val angle = 2.0 * PI * cfg.turns * t
		val x = cos(angle) * cfg.radius * t
		val z = sin(angle) * cfg.radius * t
		val y = cfg.height * t
		particle(cfg.particle, cfg.at(x, y, z))
	}
}

/** Emits [VfxShape.points] particles along a fixed-radius [Shape.HELIX] that rises along Y. */
private fun Function.drawHelix(cfg: VfxShape) {
	for (i in 0 until cfg.points) {
		val t = i.toDouble() / cfg.points
		val angle = 2.0 * PI * cfg.turns * t
		val x = cos(angle) * cfg.radius
		val z = sin(angle) * cfg.radius
		val y = cfg.height * t
		particle(cfg.particle, cfg.at(x, y, z))
	}
}
