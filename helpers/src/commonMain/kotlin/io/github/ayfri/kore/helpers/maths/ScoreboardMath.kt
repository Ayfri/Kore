package io.github.ayfri.kore.helpers.maths

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.enums.DataType
import io.github.ayfri.kore.arguments.enums.Relation
import io.github.ayfri.kore.arguments.maths.vec2
import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrIntEnd
import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrIntStart
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.arguments.types.literals.rotation
import io.github.ayfri.kore.arguments.types.literals.uuid
import io.github.ayfri.kore.arguments.types.resources.FunctionArgument
import io.github.ayfri.kore.arguments.types.resources.storage
import io.github.ayfri.kore.commands.*
import io.github.ayfri.kore.commands.execute.Anchor
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.scoreboard.Operation
import io.github.ayfri.kore.commands.scoreboard.scoreboard
import io.github.ayfri.kore.entities.Entity
import io.github.ayfri.kore.entities.FakePlayer
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.generatedFunction
import io.github.ayfri.kore.functions.load
import io.github.ayfri.kore.generated.Dimensions
import io.github.ayfri.kore.generated.EntityTypes
import io.github.ayfri.kore.helpers.HelpersConstants
import io.github.ayfri.kore.helpers.state.ScoreboardDelegate
import io.github.ayfri.kore.scoreboard.*
import io.github.ayfri.kore.utils.nbtListOf
import io.github.ayfri.kore.utils.set
import kotlin.math.PI
import kotlin.math.pow
import kotlin.math.roundToInt

/** Fixed-point scale of trigonometric results and the default [ScoreVector] scale: `1000` means `1.0`. */
const val MATH_SCALE = 1000

/** Odd degree-5 minimax fit of `sin` on `[-π/2, π/2]`, max error `6.8e-5`, so results are within 1 unit of `sin × 1000`. */
private val SIN_COEFFICIENTS = doubleArrayOf(0.99969693, -0.16567333, 0.00751446)
private const val TRIG_FIXED_POINT = 100_000
private const val TRIG_QUARTER_RANGE = 10_000

/** Past this, `h × z` (up to `TRIG_FIXED_POINT × range`) overflows an Int. */
private const val TRIG_MAX_RANGE = Int.MAX_VALUE / TRIG_FIXED_POINT

private val handles = mutableMapOf<Pair<DataPack, String>, MathHandle>()

/**
 * Runtime math engine of a datapack, returned by [registerMath].
 *
 * Every non-trivial operation (`sin`, `cos`, `sqrt`, `atan2`) lives in one shared generated function under
 * `kore_math/`, so a call site only costs two commands: copy the input, then
 * `execute store result score ... run function ...`. Constants and helper entities are emitted into the
 * `kore_math_init` load function only when an operation needs them.
 *
 * Temporaries are fake players (`#x`, `#q`...) on [objective], so they never need their own objective.
 */
class MathHandle internal constructor(val datapack: DataPack, val objective: String) {
	private val constants = mutableSetOf<Int>()
	private var entitiesReady = false
	private val functions = mutableMapOf<String, FunctionArgument>()
	private val init = datapack.load(HelpersConstants.mathInitFunction) {
		scoreboard.objectives.add(objective)
	} as Function
	private val objectives = mutableSetOf(objective)

	/** `text_display` whose `transformation` decomposition computes a vector length in one command. */
	internal val display = uuid(HelpersConstants.mathDisplayUuid)

	/** Marker teleported around `0 0 0` to turn rotations into directions and back. */
	internal val marker = uuid(HelpersConstants.mathMarkerUuid)
	internal val storage = storage(HelpersConstants.mathStorage, datapack.name)
	internal val home = vec3(HelpersConstants.mathEntitiesX, 0, HelpersConstants.mathEntitiesZ)

	/** Returns the fake player holding [value] on [objective], set once in the init function. */
	fun constant(value: Int): ScoreboardEntity {
		if (constants.add(value)) init.scoreboard.players.set(literal("#$value"), objective, value)
		return temp("$value")
	}

	/** Returns the scratch fake player `#name` on [objective]. */
	fun temp(name: String) = ScoreboardEntity(objective, FakePlayer("#$name"))

	internal fun ensureObjective(name: String) {
		if (objectives.add(name)) init.scoreboard.objectives.add(name)
	}

	/** Forceloads a far chunk and summons the [marker] and [display] once, both tagged `smithed.entity` so other packs skip them. */
	internal fun ensureEntities() {
		if (entitiesReady) return
		entitiesReady = true

		init.apply {
			forceLoad { add(vec2(HelpersConstants.mathEntitiesX, HelpersConstants.mathEntitiesZ)) }
			listOf(marker to EntityTypes.MARKER, display to EntityTypes.TEXT_DISPLAY).forEach { (id, type) ->
				execute {
					unlessCondition { entity(id) }
					run {
						summon(type, home) {
							this["Tags"] = nbtListOf("kore.math", "smithed.entity", "smithed.strict")
							this["UUID"] = id.toIntArray()
							if (type == EntityTypes.TEXT_DISPLAY) this["view_range"] = 0f
						}
					}
				}
			}
			data(storage) { modify("matrix", nbtListOf(*FloatArray(16) { if (it == 15) 1f else 0f })) }
		}
	}

	/** Moves the [marker] back into its forceloaded chunk, entities left near `0 0 0` unload at the end of the tick. */
	context(fn: Function)
	internal fun parkMarker() = fn.execute {
		inDimension(Dimensions.OVERWORLD)
		run { teleport(marker, home) }
	}

	internal fun shared(name: String, body: Function.() -> Unit) = functions.getOrPut(name) {
		datapack.generatedFunction(name, directory = HelpersConstants.mathFunctionsDirectory, block = body)
	}

	context(fn: Function)
	private fun call(function: FunctionArgument, output: ScoreboardEntity, vararg inputs: Pair<String, ScoreboardEntity>) {
		inputs.forEach { (name, source) -> temp(name) setTo source }
		fn.execute {
			storeResult { score(output.entity.asScoreHolder(), output.name) }
			run(function)
		}
	}

	/** `score += value` for any sign, `scoreboard players add` only accepts positive values. */
	context(fn: Function)
	private fun ScoreboardEntity.addSigned(value: Int) = if (value < 0) this -= -value else this += value

	private fun trigFunction(cosine: Boolean, angleScale: Int) = shared("${if (cosine) "cos" else "sin"}_$angleScale") {
		val turn = 360 * angleScale
		val quarter = turn / 4
		val factor = (TRIG_QUARTER_RANGE + quarter - 1) / quarter
		val range = quarter * factor
		require(range <= TRIG_MAX_RANGE) { "angleScale $angleScale is too fine for Int fixed-point, use at most ${TRIG_MAX_RANGE / 90}." }
		val divisor = range * (TRIG_FIXED_POINT / MATH_SCALE)
		val (a1, a3, a5) = SIN_COEFFICIENTS.mapIndexed { index, coefficient ->
			(coefficient * (PI / 2).pow(2 * index + 1) * TRIG_FIXED_POINT).roundToInt()
		}
		val x = temp("x")
		val q = temp("q")
		val h = temp("h")

		// Folds the angle to w in [-quarter, quarter] with sin(angle) = -sin(w), the negation lives in -factor.
		x += if (cosine) turn / 2 else turn / 2 - quarter
		x %= constant(turn)
		x -= turn / 2
		execute {
			ifCondition { score(x.entity.asScoreHolder(), objective, rangeOrIntEnd(-1)) }
			run { x *= constant(-1) }
		}
		x -= quarter
		execute {
			storeResult { score(q.entity.asScoreHolder(), objective) }
			run { x *= constant(-factor) }
		}
		q *= x
		q /= constant(range)
		h setTo q
		h *= constant(a5)
		h /= constant(range)
		h.addSigned(a3)
		h *= q
		h /= constant(range)
		h.addSigned(a1)
		h *= x
		h += divisor / 2
		returnRun { scoreboard.players.operation(h.entity.asScoreHolder(), objective, Operation.DIVIDE, constant(divisor).entity.asScoreHolder(), objective) }
	}

	/**
	 * Cosine of [input] degrees, scaled by [angleScale] (`100` means the input is in hundredths of a degree).
	 * The result is `cos × 1000`, within 1 unit of the exact value, for any input including negative ones.
	 *
	 * ```kotlin
	 * math.cos(angle, result) // angle = 60 -> result = 500
	 * ```
	 */
	context(fn: Function)
	fun cos(input: ScoreboardEntity, output: ScoreboardEntity, angleScale: Int = 1) =
		call(trigFunction(true, angleScale), output, "x" to input)

	context(fn: Function)
	fun cos(entity: Entity, inputScore: String, outputScore: String, angleScale: Int = 1) =
		cos(ScoreboardEntity(inputScore, entity), ScoreboardEntity(outputScore, entity), angleScale)

	context(fn: Function)
	fun cos(input: ScoreboardDelegate, output: ScoreboardDelegate, angleScale: Int = 1) =
		cos(input.scoreboardEntity(), output.scoreboardEntity(), angleScale)

	/**
	 * Sine of [input] degrees, scaled by [angleScale]. The result is `sin × 1000`, within 1 unit of the exact value.
	 *
	 * ```kotlin
	 * math.sin(angle, result) // angle = -90 -> result = -1000
	 * ```
	 */
	context(fn: Function)
	fun sin(input: ScoreboardEntity, output: ScoreboardEntity, angleScale: Int = 1) =
		call(trigFunction(false, angleScale), output, "x" to input)

	context(fn: Function)
	fun sin(entity: Entity, inputScore: String, outputScore: String, angleScale: Int = 1) =
		sin(ScoreboardEntity(inputScore, entity), ScoreboardEntity(outputScore, entity), angleScale)

	context(fn: Function)
	fun sin(input: ScoreboardDelegate, output: ScoreboardDelegate, angleScale: Int = 1) =
		sin(input.scoreboardEntity(), output.scoreboardEntity(), angleScale)

	/**
	 * Angle of the point ([x], [y]) in degrees scaled by [angleScale], in `(-180, 180]`, like `atan2(y, x)`.
	 * Both inputs only need to share a scale. Uses the math marker's `facing` rotation, exact to the float.
	 *
	 * ```kotlin
	 * math.atan2(y, x, angle) // y = 1000, x = 1000 -> angle = 45
	 * ```
	 */
	context(fn: Function)
	fun atan2(y: ScoreboardEntity, x: ScoreboardEntity, output: ScoreboardEntity, angleScale: Int = 1) {
		ensureEntities()
		val function = shared("atan2_$angleScale") {
			val result = temp("r")
			listOf("Pos[0]" to "y", "Pos[2]" to "x").forEach { (path, name) ->
				execute {
					storeResult { entity(marker, path, DataType.DOUBLE, 0.001) }
					run { scoreboard.players.get(temp(name).entity.asScoreHolder(), objective) }
				}
			}
			execute {
				inDimension(Dimensions.OVERWORLD)
				positioned(vec3(0, 0, 0))
				facingEntity(marker, Anchor.FEET)
				run { teleport(marker, home, rotation()) }
			}
			execute {
				storeResult { score(result.entity.asScoreHolder(), objective) }
				run { data(marker) { get("Rotation[0]", -angleScale.toDouble()) } }
			}
			execute {
				ifCondition { score(result.entity.asScoreHolder(), objective, rangeOrIntEnd(-180 * angleScale)) }
				run { result += 360 * angleScale }
			}
			returnRun { scoreboard.players.get(result.entity.asScoreHolder(), objective) }
		}
		call(function, output, "y" to y, "x" to x)
	}

	/**
	 * Exact integer square root, `floor(sqrt(input))`, for every non-negative `Int` (`0` for negative inputs).
	 * A piecewise linear first guess plus three Newton steps, verified against all 2³¹ inputs.
	 *
	 * ```kotlin
	 * math.sqrt(input, result) // input = 1000000 -> result = 1000
	 * ```
	 */
	context(fn: Function)
	fun sqrt(input: ScoreboardEntity, output: ScoreboardEntity) {
		val function = shared("sqrt") {
			val x = temp("x")
			val r = temp("r")
			val steps = listOf(temp("s"), temp("t"), temp("u"))
			val check = temp("v")

			returnIf(0) { score(x.entity.asScoreHolder(), objective, rangeOrIntEnd(0)) }
			execute {
				(steps + check).forEach { storeResult { score(it.entity.asScoreHolder(), objective) } }
				run { r setTo x }
			}
			for ((range, divisor, offset) in listOf(Triple(rangeOrIntEnd(1_515_359), 559, 15), Triple(rangeOrIntStart(1_515_360), 32_768, 2456))) {
				execute {
					ifCondition { score(x.entity.asScoreHolder(), objective, range) }
					run { r /= constant(divisor) }
				}
				execute {
					ifCondition { score(x.entity.asScoreHolder(), objective, range) }
					run { r += offset }
				}
			}
			steps.forEach {
				it /= r
				r += it
				r /= constant(2)
			}
			check /= r
			execute {
				ifCondition { score(r.entity.asScoreHolder(), objective, check.entity.asScoreHolder(), objective, Relation.GREATER_THAN) }
				run { r -= 1 }
			}
			returnRun { scoreboard.players.get(r.entity.asScoreHolder(), objective) }
		}
		call(function, output, "x" to input)
	}

	context(fn: Function)
	fun sqrt(entity: Entity, inputScore: String, outputScore: String) =
		sqrt(ScoreboardEntity(inputScore, entity), ScoreboardEntity(outputScore, entity))

	context(fn: Function)
	fun sqrt(input: ScoreboardDelegate, output: ScoreboardDelegate) = sqrt(input.scoreboardEntity(), output.scoreboardEntity())

	/**
	 * Squared distance between (x1, y1, z1) and (x2, y2, z2), all scores of [entity]. It overflows past `46340` per
	 * axis, use [ScoreVector.distanceTo] for fixed-point coordinates.
	 */
	context(fn: Function)
	fun distanceSquared(
		entity: Entity,
		x1: String, y1: String, z1: String,
		x2: String, y2: String, z2: String,
		outputScore: String,
	) {
		val output = ScoreboardEntity(outputScore, entity)
		val delta = temp("d")
		output.set(0)
		listOf(x1 to x2, y1 to y2, z1 to z2).forEach { (from, to) ->
			delta setTo ScoreboardEntity(to, entity)
			delta -= ScoreboardEntity(from, entity)
			delta *= delta
			output += delta
		}
	}

	context(fn: Function)
	fun distanceSquared(
		first: Triple<ScoreboardDelegate, ScoreboardDelegate, ScoreboardDelegate>,
		second: Triple<ScoreboardDelegate, ScoreboardDelegate, ScoreboardDelegate>,
		output: ScoreboardDelegate,
	) {
		val result = output.scoreboardEntity()
		val delta = temp("d")
		result.set(0)
		listOf(first.first to second.first, first.second to second.second, first.third to second.third).forEach { (from, to) ->
			delta setTo to.scoreboardEntity()
			delta -= from.scoreboardEntity()
			delta *= delta
			result += delta
		}
	}

	/** Height of a projectile, `v0 × t - g × t² / 2`, all four values being scores. */
	context(fn: Function)
	fun parabola(time: ScoreboardEntity, initialVelocity: ScoreboardEntity, gravity: ScoreboardEntity, output: ScoreboardEntity) {
		val drop = temp("p")
		drop setTo gravity
		drop *= time
		drop *= time
		drop /= constant(2)
		output setTo initialVelocity
		output *= time
		output -= drop
	}

	/** [parabola] where [initialVelocity] and [gravity] are fake player names on [objective], like `#v0`. */
	context(fn: Function)
	fun parabola(entity: Entity, tScore: String, initialVelocity: String, gravity: String, outputScore: String) = parabola(
		ScoreboardEntity(tScore, entity),
		ScoreboardEntity(objective, FakePlayer(initialVelocity)),
		ScoreboardEntity(objective, FakePlayer(gravity)),
		ScoreboardEntity(outputScore, entity),
	)

	context(fn: Function)
	fun parabola(time: ScoreboardDelegate, initialVelocity: ScoreboardDelegate, gravity: ScoreboardDelegate, output: ScoreboardDelegate) =
		parabola(time.scoreboardEntity(), initialVelocity.scoreboardEntity(), gravity.scoreboardEntity(), output.scoreboardEntity())
}

context(_: Function, math: MathHandle)
infix fun ScoreboardDelegate.cosTo(output: ScoreboardDelegate) = math.cos(this, output)

context(_: Function, math: MathHandle)
infix fun ScoreboardDelegate.sinTo(output: ScoreboardDelegate) = math.sin(this, output)

context(_: Function, math: MathHandle)
infix fun ScoreboardDelegate.sqrtTo(output: ScoreboardDelegate) = math.sqrt(this, output)

/**
 * Returns the [MathHandle] of this datapack for [objective], creating its `kore_math_init` load function the first time.
 * Calling it again returns the same handle.
 */
fun DataPack.registerMath(objective: String = HelpersConstants.mathObjective) =
	handles.getOrPut(this to objective) { MathHandle(this, objective) }
