package io.github.ayfri.kore.helpers.vfx

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.maths.vec3
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
	var dx: Double = 1.0
	var dy: Double = 0.0
	var dz: Double = 0.0
    var height: Double = 3.0
    var length: Double = 5.0
    lateinit var particle: ParticleTypeArgument
    var points: Int = 20
    var radius: Double = 1.0
    var shape: Shape = Shape.CIRCLE
	var turns: Int = 3
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

/** Shorthand for [drawShape] with [Shape.CIRCLE]. */
fun DataPack.drawCircle(name: String, particle: ParticleTypeArgument, radius: Double = 1.0, points: Int = 20) =
	drawShape(name) {
		shape = Shape.CIRCLE
		this.particle = particle
		this.radius = radius
		this.points = points
	}

private fun Function.drawCircle(cfg: VfxShape) {
	for (i in 0 until cfg.points) {
		val angle = 2.0 * PI * i / cfg.points
		val x = cos(angle) * cfg.radius
		val z = sin(angle) * cfg.radius
		particle(cfg.particle, vec3(x, 0, z))
	}
}

private fun Function.drawLine(cfg: VfxShape) {
	val mag = sqrt(cfg.dx * cfg.dx + cfg.dy * cfg.dy + cfg.dz * cfg.dz)
	if (mag == 0.0) return
	val nx = cfg.dx / mag
	val ny = cfg.dy / mag
	val nz = cfg.dz / mag
	for (i in 0 until cfg.points) {
		val t = cfg.length * i / (cfg.points - 1).coerceAtLeast(1)
		particle(cfg.particle, vec3(nx * t, ny * t, nz * t))
	}
}

private fun Function.drawSphere(cfg: VfxShape) {
	val goldenAngle = PI * (3.0 - sqrt(5.0))
	for (i in 0 until cfg.points) {
		val y = 1.0 - 2.0 * i / (cfg.points - 1).coerceAtLeast(1)
		val radiusAtY = sqrt(1.0 - y * y)
		val theta = goldenAngle * i
		val x = cos(theta) * radiusAtY * cfg.radius
		val z = sin(theta) * radiusAtY * cfg.radius
		particle(cfg.particle, vec3(x, y * cfg.radius, z))
	}
}

private fun Function.drawSpiral(cfg: VfxShape) {
	for (i in 0 until cfg.points) {
		val t = i.toDouble() / cfg.points
		val angle = 2.0 * PI * cfg.turns * t
		val x = cos(angle) * cfg.radius * t
		val z = sin(angle) * cfg.radius * t
		val y = cfg.height * t
		particle(cfg.particle, vec3(x, y, z))
	}
}

private fun Function.drawHelix(cfg: VfxShape) {
	for (i in 0 until cfg.points) {
		val t = i.toDouble() / cfg.points
		val angle = 2.0 * PI * cfg.turns * t
		val x = cos(angle) * cfg.radius
		val z = sin(angle) * cfg.radius
		val y = cfg.height * t
		particle(cfg.particle, vec3(x, y, z))
	}
}
