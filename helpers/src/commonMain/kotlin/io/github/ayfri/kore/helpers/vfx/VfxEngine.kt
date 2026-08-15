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
	CIRCLE,
	HELIX,
	LINE,
	SPHERE,
	SPIRAL,
}

/** Configuration for a geometric particle shape emitted by [drawShape]. */
class VfxShape {
	/** X component of the [Shape.LINE] direction vector. */
	var dx: Double = 1.0

	/** Y component of the [Shape.LINE] direction vector. */
	var dy: Double = 0.0

	/** Z component of the [Shape.LINE] direction vector. */
	var dz: Double = 0.0

	/** Total rise along Y for [Shape.SPIRAL] and [Shape.HELIX]. */
	var height: Double = 3.0

	/** Total length of a [Shape.LINE]. */
	var length: Double = 5.0

	/** Numeric offset applied to every generated point, in the same coordinate space as [positionType]. */
	var origin: Vec3 = vec3(0, 0, 0)

	/** Particle type used for every generated point. */
	lateinit var particle: ParticleTypeArgument

	/** Number of points to generate along the shape. */
	var points: Int = 20

	/** Coordinate space the generated `particle` commands are emitted in. Defaults to execution-relative (`~`). */
	var positionType: PosNumber.Type = PosNumber.Type.RELATIVE

	/** Radius used by [Shape.CIRCLE], [Shape.SPHERE], [Shape.SPIRAL] and [Shape.HELIX]. */
	var radius: Double = 1.0

	/** Which geometry to generate. */
	var shape: Shape = Shape.CIRCLE

	/** Number of full revolutions for [Shape.SPIRAL] and [Shape.HELIX]. */
	var turns: Int = 3
}

/** Combines [VfxShape.origin] with a point and converts it to [VfxShape.positionType]'s coordinate space. */
private fun VfxShape.at(x: Double, y: Double, z: Double) = (origin + vec3(x, y, z)).let {
	when (positionType) {
		PosNumber.Type.LOCAL -> it.local
		PosNumber.Type.RELATIVE -> it.relative
		PosNumber.Type.WORLD -> it.world
	}
}

/** Generates a function containing pre-computed `particle` commands for the configured shape. */
fun DataPack.drawShape(name: String, block: VfxShape.() -> Unit) =
	VfxShape().apply(block).let { cfg ->
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
 * @param name generated function name, see [HelpersConstants.vfxShapeFunctionName]
 * @param particle particle type used for every generated point
 * @param radius circle radius
 * @param points number of points to generate around the circle
 * @param positionType coordinate space the generated `particle` commands are emitted in
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

/** Emits [VfxShape.points] particles evenly spaced along a [Shape.LINE] in the `dx/dy/dz` direction. */
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
