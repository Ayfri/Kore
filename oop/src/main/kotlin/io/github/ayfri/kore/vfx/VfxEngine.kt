package io.github.ayfri.kore.vfx

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.OopConstants
import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.commands.particle.particle
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.generatedFunction
import io.github.ayfri.kore.generated.arguments.types.ParticleTypeArgument
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/** Geometric shapes supported by the VFX engine. */
enum class Shape {
	CIRCLE,
	LINE,
	SPHERE,
	SPIRAL,
	HELIX,
}

/** Configuration for a geometric particle shape emitted by [drawShape]. */
class VfxShape {
	var shape: Shape = Shape.CIRCLE
	lateinit var particle: ParticleTypeArgument
	var radius: Double = 1.0
	var points: Int = 20
	var height: Double = 3.0
	var length: Double = 5.0
	var dx: Double = 1.0
	var dy: Double = 0.0
	var dz: Double = 0.0
	var turns: Int = 3
}

/** Generates a function containing pre-computed `particle` commands for the configured shape. */
fun DataPack.drawShape(name: String, block: VfxShape.() -> Unit) =
	VfxShape().apply(block).let { cfg ->
		generatedFunction(OopConstants.Vfx.shapeFunctionName(name)) {
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
	val mag = kotlin.math.sqrt(cfg.dx * cfg.dx + cfg.dy * cfg.dy + cfg.dz * cfg.dz)
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
	val goldenAngle = PI * (3.0 - kotlin.math.sqrt(5.0))
	for (i in 0 until cfg.points) {
		val y = 1.0 - 2.0 * i / (cfg.points - 1).coerceAtLeast(1)
		val radiusAtY = kotlin.math.sqrt(1.0 - y * y)
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
