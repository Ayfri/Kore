package io.github.ayfri.kore.helpers.maths

import io.github.ayfri.kore.arguments.enums.DataType
import io.github.ayfri.kore.arguments.types.EntityArgument
import io.github.ayfri.kore.arguments.types.literals.rotation
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.arguments.types.resources.FunctionArgument
import io.github.ayfri.kore.commands.data
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.function
import io.github.ayfri.kore.commands.scoreboard.scoreboard
import io.github.ayfri.kore.commands.teleport
import io.github.ayfri.kore.entities.Entity
import io.github.ayfri.kore.features.predicates.providers.ContextFloatProvider
import io.github.ayfri.kore.features.predicates.providers.ContextIntProvider
import io.github.ayfri.kore.features.predicates.providers.NumberProviderScope
import io.github.ayfri.kore.features.predicates.providers.add
import io.github.ayfri.kore.features.predicates.providers.constant
import io.github.ayfri.kore.features.predicates.providers.cos
import io.github.ayfri.kore.features.predicates.providers.div
import io.github.ayfri.kore.features.predicates.providers.floatStorage
import io.github.ayfri.kore.features.predicates.providers.floorDiv
import io.github.ayfri.kore.features.predicates.providers.fromInt
import io.github.ayfri.kore.features.predicates.providers.length
import io.github.ayfri.kore.features.predicates.providers.mul
import io.github.ayfri.kore.features.predicates.providers.negate
import io.github.ayfri.kore.features.predicates.providers.sin
import io.github.ayfri.kore.features.predicates.providers.sub
import io.github.ayfri.kore.features.predicates.providers.truncate
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.generated.Dimensions
import io.github.ayfri.kore.scoreboard.*
import kotlin.math.PI
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

private fun NumberProviderScope.crossComponent(
	first: List<ContextIntProvider>,
	second: List<ContextIntProvider>,
	left: Int,
	right: Int,
	scale: Int,
) = floorDiv(sub(mul(first[left], second[right]), mul(first[right], second[left])), constant(scale))

/** The view vector of the game, `(-sin(yaw) cos(pitch), -sin(pitch), cos(yaw) cos(pitch))`, from the degrees in `<storage>:rotation`. */
private fun NumberProviderScope.viewDirection(axis: Int, scale: Int, storage: String): ContextFloatProvider {
	val toRadians = constant((PI / 180).toFloat())
	val yaw = mul(floatStorage(storage, "rotation[0]"), toRadians)
	val pitch = mul(floatStorage(storage, "rotation[1]"), toRadians)
	val direction = when (axis) {
		0 -> mul(negate(sin(yaw)), cos(pitch))
		1 -> negate(sin(pitch))
		else -> mul(cos(yaw), cos(pitch))
	}
	return truncate(mul(direction, constant(scale.toFloat())))
}

private fun NumberProviderScope.normalizedComponent(value: ContextIntProvider, scale: Int, storage: String) =
	truncate(div(mul(fromInt(value), constant(scale.toFloat())), floatStorage(storage, "len")))

private fun NumberProviderScope.vectorLength(components: List<ContextIntProvider>) = length(components.map { fromInt(it) })

private fun NumberProviderScope.vectorLength(from: List<ContextIntProvider>, to: List<ContextIntProvider>) =
	length(from.zip(to) { start, end -> fromInt(sub(start, end)) })

private fun NumberProviderScope.dotProduct(first: List<ContextIntProvider>, second: List<ContextIntProvider>, scale: Int) =
	floorDiv(add(first.zip(second) { a, b -> mul(a, b) }), constant(scale))

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

	/**
	 * Stores the components as doubles in the math storage at `<path>.x/y/z`, then runs the macro [function] as and at [target] with them,
	 * except for a zero vector when [skipZero] is set.
	 */
	context(fn: Function)
	private fun runMacroAs(target: EntityArgument, path: String, macro: FunctionArgument, skipZero: Boolean = false) {
		listOf("x", "y", "z").zip(components).forEach { (key, component) ->
			fn.execute {
				storeResult { storage(math.storage, "$path.$key", DataType.DOUBLE, inverseScale) }
				run { scoreboard.players.get(component.entity.asScoreHolder(), component.name) }
			}
		}
		fn.execute {
			if (skipZero) unlessCondition { data(math.storage, "$path{x:0.0d,y:0.0d,z:0.0d}") }
			asTarget(target)
			at(self())
			run { function(macro, math.storage, path) }
		}
	}

	/** Sets the components from plain numbers, `set(0.5, 1.0, 0.0)` stores `500 1000 0`. */
	context(fn: Function)
	fun set(x: Double, y: Double, z: Double) = components.zip(listOf(x, y, z)).forEach { (component, value) ->
		component.set((value * scale).roundToInt())
	}

	/** Copies [other] into this vector. */
	context(fn: Function)
	infix fun setTo(other: ScoreVector) = components.zip(other.components).forEach { (component, source) -> component setTo source }

	/** Reads the `Motion` of [source], in blocks per tick, copying it to storage once instead of reading the entity three times. */
	context(fn: Function)
	fun setToMotion(source: EntityArgument) {
		fn.data(math.storage) { modify("read", source, "Motion") }
		components.forEachIndexed { index, component ->
			fn.execute {
				storeResult { score(component.entity.asScoreHolder(), component.name) }
				run { data(math.storage) { get("read[$index]", scale.toDouble()) } }
			}
		}
	}

	/**
	 * Reads the position of [source] off the math marker teleported onto it, since reading any field of a player's NBT
	 * serializes the whole player, inventory included.
	 */
	context(fn: Function)
	fun setToPosition(source: EntityArgument) {
		math.ensureEntities()
		fn.teleport(math.marker, source)
		readInto(math.marker, "Pos")
		math.parkMarker()
	}

	/**
	 * Sets this vector to the unit direction [source] looks at, the same direction as `^ ^ ^1`, truncated toward zero.
	 * The math marker copies the rotation of [source] without moving, since reading a player's NBT serializes the whole
	 * player, then one `compute` per component applies the game's own sine table to it.
	 *
	 * ```
	 * dash.setToLookDirection(self()) // looking south (yaw 0, pitch 0) -> (0, 0, 1000)
	 * ```
	 */
	context(fn: Function)
	fun setToLookDirection(source: EntityArgument) {
		math.ensureEntities()
		fn.execute {
			inDimension(Dimensions.OVERWORLD)
			rotatedAs(source)
			run { teleport(math.marker, math.home, rotation()) }
		}
		fn.data(math.storage) { modify("rotation", math.marker, "Rotation") }
		components.forEachIndexed { axis, component ->
			component.setTo { viewDirection(axis, scale, math.storage.asString()) }
		}
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

	/**
	 * Writes the fixed-point dot product of this vector and [other] into [output], in one `compute`. A product or a sum
	 * that doesn't fit an `Int` (past about `46` blocks per component at scale `1000`) writes `0`. The entity of each
	 * vector must be a single entity (`limitToOne`).
	 *
	 * ```
	 * a.dot(b, output) // a = (1000, 0, 0), b = (500, 700, 0) -> 500
	 * ```
	 */
	context(fn: Function)
	fun dot(other: ScoreVector, output: ScoreboardEntity) {
		val scores = math.stage(components + other.components)
		output.setTo { dotProduct(scores.take(3).map { score(it) }, scores.drop(3).map { score(it) }, scale) }
	}

	/**
	 * Writes the cross product `this × other` into [output], which must be a third vector, with one `compute` per
	 * component. A product that doesn't fit an `Int` writes `0`, and the entities must be single entities, see [dot].
	 */
	context(fn: Function)
	fun cross(other: ScoreVector, output: ScoreVector) {
		require(output !== this && output !== other) { "The cross product output must differ from both operands." }
		val scores = math.stage(components + other.components)
		listOf(output.x to (1 to 2), output.y to (2 to 0), output.z to (0 to 1)).forEach { (target, indices) ->
			target.setTo { crossComponent(scores.take(3).map { score(it) }, scores.drop(3).map { score(it) }, indices.first, indices.second, scale) }
		}
	}

	/** Writes the squared length into [output], in this vector's [scale], like [dot] of this vector with itself, entity rule included. */
	context(fn: Function)
	fun lengthSquared(output: ScoreboardEntity) = dot(this, output)

	/**
	 * Writes the length, rounded down, into [output] in this vector's [scale], in one `compute` through the `length`
	 * provider. It sums the squares as floats, so it never overflows, and it is within 1 unit of the exact length. The
	 * entity of the vector must be a single entity (`limitToOne`).
	 *
	 * ```
	 * a.length(output) // a = (3000, 4000, 0) -> 5000
	 * ```
	 */
	context(fn: Function)
	fun length(output: ScoreboardEntity) {
		output.setTo { vectorLength(components.map { score(it) }) }
	}

	/** Writes the distance between this point and [other] into [output], see [length] for the precision and the single entity rule. */
	context(fn: Function)
	fun distanceTo(other: ScoreVector, output: ScoreboardEntity) {
		val scores = math.stage(components + other.components)
		output.setTo { vectorLength(scores.take(3).map { score(it) }, scores.drop(3).map { score(it) }) }
	}

	/**
	 * Scales this vector to a length of `1.0` ([scale] units), keeping its direction, with the length computed once into
	 * the math storage and one `compute` per component, each truncated toward zero. A zero vector stays `0`. The entity of
	 * the vector must be a single entity (`limitToOne`).
	 *
	 * ```
	 * a.normalize() // a = (3000, 0, 4000) -> (600, 0, 800)
	 * ```
	 */
	context(fn: Function)
	fun normalize() {
		fn.data(math.storage).computeScores("len") { vectorLength(components.map { score(it) }) }
		components.forEach { component ->
			component.setTo { normalizedComponent(score(component), scale, math.storage.asString()) }
		}
	}

	/** Rotates [target] to look along this vector, through a macro `rotate`, so it also works on players. A zero vector keeps the rotation, where the game would snap it to yaw -90. */
	context(fn: Function)
	fun lookAlong(target: EntityArgument) =
		runMacroAs(target, "look", math.shared("look_along") { addLine($$"$rotate @s facing ~$(x) ~$(y) ~$(z)") }, skipZero = true)

	/** Writes this vector as the `Motion` of [target], in blocks per tick. Players ignore `Motion` changes. */
	context(fn: Function)
	fun applyAsMotion(target: EntityArgument) = writeInto(target, "Motion")

	/**
	 * Teleports [target] by this vector, interpreted through [mode]. Goes through a macro `tp`, so it works on players,
	 * keeps rotation, and crosses no chunk restriction.
	 */
	context(fn: Function)
	fun teleport(target: EntityArgument, mode: TeleportMode = TeleportMode.ABSOLUTE) {
		runMacroAs(target, "teleport", math.shared("teleport_${mode.name.lowercase()}") {
			addLine($$"$tp @s $${mode.prefix}$(x) $${mode.prefix}$(y) $${mode.prefix}$(z)")
		})
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
