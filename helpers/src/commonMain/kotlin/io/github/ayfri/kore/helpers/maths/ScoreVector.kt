package io.github.ayfri.kore.helpers.maths

import io.github.ayfri.kore.arguments.enums.DataType
import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.arguments.types.EntityArgument
import io.github.ayfri.kore.arguments.types.literals.rotation
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.commands.data
import io.github.ayfri.kore.commands.execute.Anchor
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.function
import io.github.ayfri.kore.commands.scoreboard.Operation
import io.github.ayfri.kore.commands.scoreboard.scoreboard
import io.github.ayfri.kore.commands.teleport
import io.github.ayfri.kore.entities.Entity
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.generated.Dimensions
import io.github.ayfri.kore.scoreboard.*
import kotlin.math.roundToInt

/** How [ScoreVector.teleport] interprets the vector. */
enum class TeleportMode(internal val prefix: String) {
	/** World coordinates, `tp @s x y z`. */
	ABSOLUTE(""),

	/** Offset along the entity's view, `tp @s ^x ^y ^z` (x = left, y = up, z = forward). */
	LOCAL("^"),

	/** Offset from the entity, `tp @s ~x ~y ~z`. */
	RELATIVE("~"),
}

/**
 * A runtime 3D vector stored in three scores of [entity] (objectives `<name>_x`, `<name>_y`, `<name>_z`) in fixed
 * point: with the default [scale] of `1000`, a component of `1500` means `1.5`.
 *
 * It covers what datapacks usually hand-roll: reading positions, motion and look directions, arithmetic, dot and
 * cross products, length and normalization, then writing back as a teleport, a motion or a rotation.
 *
 * ```kotlin
 * val math = registerMath()
 * function("dash") {
 * 	val dash = math.vector("dash", player)
 * 	dash.setToLookDirection(self()) // unit vector, x1000
 * 	dash *= 3
 * 	dash.teleport(self(), TeleportMode.RELATIVE) // 3 blocks forward, works on players
 * }
 * ```
 *
 * Operations between two vectors assume both share the same [scale].
 */
class ScoreVector internal constructor(val math: MathHandle, val name: String, val entity: Entity, val scale: Int) {
	val x = ScoreboardEntity("${name}_x", entity)
	val y = ScoreboardEntity("${name}_y", entity)
	val z = ScoreboardEntity("${name}_z", entity)
	val components get() = listOf(x, y, z)

	private val inverseScale get() = 1.0 / scale

	context(fn: Function)
	private fun readInto(source: EntityArgument, path: String) = components.forEachIndexed { index, component ->
		fn.execute {
			storeResult { score(component.entity.asScoreHolder(), component.name) }
			run { data(source) { get("$path[$index]", scale.toDouble()) } }
		}
	}

	context(fn: Function)
	private fun writeInto(target: EntityArgument, path: String) = components.forEachIndexed { index, component ->
		fn.execute {
			storeResult { entity(target, "$path[$index]", DataType.DOUBLE, inverseScale) }
			run { scoreboard.players.get(component.entity.asScoreHolder(), component.name) }
		}
	}

	/** Runs [block] with the math marker moved to this vector's coordinates, then parks it again. */
	context(fn: Function)
	private fun withMarkerAtVector(block: Function.() -> Unit) {
		math.ensureEntities()
		writeInto(math.marker, "Pos")
		fn.block()
		math.parkMarker()
	}

	/** Sets the components from plain numbers, `set(0.5, 1.0, 0.0)` stores `500 1000 0`. */
	context(fn: Function)
	fun set(x: Double, y: Double, z: Double) = components.zip(listOf(x, y, z)).forEach { (component, value) ->
		component.set((value * scale).roundToInt())
	}

	/** Copies [other] into this vector. */
	context(fn: Function)
	infix fun setTo(other: ScoreVector) = components.zip(other.components).forEach { (component, source) -> component setTo source }

	/** Reads the `Motion` of [source], in blocks per tick. */
	context(fn: Function)
	fun setToMotion(source: EntityArgument) = readInto(source, "Motion")

	/** Reads the position of [source]. */
	context(fn: Function)
	fun setToPosition(source: EntityArgument) = readInto(source, "Pos")

	/** Sets this vector to the unit direction [source] looks at, the same direction as `^ ^ ^1`. */
	context(fn: Function)
	fun setToLookDirection(source: EntityArgument) {
		math.ensureEntities()
		fn.execute {
			inDimension(Dimensions.OVERWORLD)
			positioned(vec3(0, 0, 0))
			rotatedAs(source)
			run { teleport(math.marker, vec3(0, 0, 1).local) }
		}
		readInto(math.marker, "Pos")
		math.parkMarker()
	}

	context(fn: Function)
	operator fun plusAssign(other: ScoreVector) = components.zip(other.components).forEach { (component, source) -> component += source }

	context(fn: Function)
	operator fun minusAssign(other: ScoreVector) = components.zip(other.components).forEach { (component, source) -> component -= source }

	/** Multiplies every component by the plain number [factor]. */
	context(fn: Function)
	operator fun timesAssign(factor: Int) = components.forEach { it *= math.constant(factor) }

	/** Multiplies every component by the fixed-point [factor] sharing this vector's [scale]. */
	context(fn: Function)
	operator fun timesAssign(factor: ScoreboardEntity) = components.forEach {
		it *= factor
		it /= math.constant(scale)
	}

	/** Divides every component by the plain number [divisor], with Minecraft's floored division. */
	context(fn: Function)
	operator fun divAssign(divisor: Int) = components.forEach { it /= math.constant(divisor) }

	/** Writes the fixed-point dot product of this vector and [other] into [output]. */
	context(fn: Function)
	fun dot(other: ScoreVector, output: ScoreboardEntity) {
		val product = math.temp("t")
		output setTo x
		output *= other.x
		listOf(y to other.y, z to other.z).forEach { (component, source) ->
			product setTo component
			product *= source
			output += product
		}
		output /= math.constant(scale)
	}

	/** Writes the cross product `this × other` into [output], which must be a third vector. */
	context(fn: Function)
	fun cross(other: ScoreVector, output: ScoreVector) {
		require(output !== this && output !== other) { "The cross product output must differ from both operands." }
		val product = math.temp("t")
		listOf(Triple(output.x, y to z, other.z to other.y), Triple(output.y, z to x, other.x to other.z), Triple(output.z, x to y, other.y to other.x))
			.forEach { (target, left, right) ->
				target setTo left.first
				target *= right.first
				product setTo left.second
				product *= right.second
				target -= product
				target /= math.constant(scale)
			}
	}

	/** Writes the squared length into [output], in this vector's [scale]. Overflows past `46.34` blocks at scale `1000`. */
	context(fn: Function)
	fun lengthSquared(output: ScoreboardEntity) = dot(this, output)

	/**
	 * Writes the length into [output] in this vector's [scale], exact to the float and overflow-free. The components go
	 * into the first column of a `text_display` transformation matrix, whose decomposed `scale[0]` is the length.
	 */
	context(fn: Function)
	fun length(output: ScoreboardEntity) {
		math.ensureEntities()
		components.forEachIndexed { index, component ->
			fn.execute {
				storeResult { storage(math.storage, "matrix[${index * 4}]", DataType.FLOAT, 1.0) }
				run { scoreboard.players.get(component.entity.asScoreHolder(), component.name) }
			}
		}
		storeDisplayLength(output)
	}

	/** Writes the distance between this point and [other] into [output], see [length]. */
	context(fn: Function)
	fun distanceTo(other: ScoreVector, output: ScoreboardEntity) {
		math.ensureEntities()
		val delta = math.temp("d")
		components.zip(other.components).forEachIndexed { index, (component, source) ->
			delta setTo component
			fn.execute {
				storeResult { storage(math.storage, "matrix[${index * 4}]", DataType.FLOAT, 1.0) }
				run { delta.operation(Operation.REMOVE, source) }
			}
		}
		storeDisplayLength(output)
	}

	context(fn: Function)
	private fun storeDisplayLength(output: ScoreboardEntity) {
		fn.data(math.display) { modify("transformation", math.storage, "matrix") }
		fn.execute {
			storeResult { score(output.entity.asScoreHolder(), output.name) }
			run { data(math.display) { get("transformation.scale[0]") } }
		}
	}

	/** Scales this vector to a length of `1.0` ([scale] units), keeping its direction. A zero vector stays undefined. */
	context(fn: Function)
	fun normalize() = withMarkerAtVector {
		execute {
			inDimension(Dimensions.OVERWORLD)
			positioned(vec3(0, 0, 0))
			facingEntity(math.marker, Anchor.FEET)
			run { teleport(math.marker, vec3(0, 0, 1).local) }
		}
		readInto(math.marker, "Pos")
	}

	/** Rotates [target] to look along this vector, `tp`-based so it also works on players. */
	context(fn: Function)
	fun lookAlong(target: EntityArgument) = withMarkerAtVector {
		execute {
			asTarget(target)
			at(self())
			positioned(vec3(0, 0, 0))
			facingEntity(math.marker, Anchor.FEET)
			positionedAs(self())
			run { teleport(self(), vec3(), rotation()) }
		}
	}

	/** Writes this vector as the `Motion` of [target], in blocks per tick. Players ignore `Motion` changes. */
	context(fn: Function)
	fun applyAsMotion(target: EntityArgument) = writeInto(target, "Motion")

	/**
	 * Teleports [target] by this vector, interpreted through [mode]. Goes through a macro `tp`, so it works on players,
	 * keeps rotation, and crosses no chunk restriction.
	 */
	context(fn: Function)
	fun teleport(target: EntityArgument, mode: TeleportMode = TeleportMode.ABSOLUTE) {
		val teleportFunction = math.shared("teleport_${mode.name.lowercase()}") {
			addLine($$"$tp @s $${mode.prefix}$(x) $${mode.prefix}$(y) $${mode.prefix}$(z)")
		}
		listOf("x", "y", "z").zip(components).forEach { (key, component) ->
			fn.execute {
				storeResult { storage(math.storage, "teleport.$key", DataType.DOUBLE, inverseScale) }
				run { scoreboard.players.get(component.entity.asScoreHolder(), component.name) }
			}
		}
		fn.execute {
			asTarget(target)
			at(self())
			run { function(teleportFunction, math.storage, "teleport") }
		}
	}
}

/**
 * Creates a [ScoreVector] named [name] on [entity], registering its three objectives in the math init function.
 *
 * ```kotlin
 * val velocity = math.vector("velocity", player)
 * ```
 */
fun MathHandle.vector(name: String, entity: Entity, scale: Int = MATH_SCALE) = ScoreVector(this, name, entity, scale).also { vector ->
	vector.components.forEach { ensureObjective(it.name) }
}
