package io.github.ayfri.kore.helpers.maths

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.DataPackStateKey
import io.github.ayfri.kore.arguments.enums.DataType
import io.github.ayfri.kore.arguments.maths.vec2
import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrIntEnd
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.arguments.types.literals.rotation
import io.github.ayfri.kore.arguments.types.literals.uuid
import io.github.ayfri.kore.arguments.types.resources.FunctionArgument
import io.github.ayfri.kore.arguments.types.resources.storage
import io.github.ayfri.kore.commands.*
import io.github.ayfri.kore.commands.execute.Anchor
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.scoreboard.scoreboard
import io.github.ayfri.kore.entities.Entity
import io.github.ayfri.kore.entities.FakePlayer
import io.github.ayfri.kore.features.predicates.conditions.intValueCheck
import io.github.ayfri.kore.features.predicates.providers.ContextFloatProvider
import io.github.ayfri.kore.features.predicates.providers.ContextIntProvider
import io.github.ayfri.kore.features.predicates.providers.NumberProviderScope
import io.github.ayfri.kore.features.predicates.providers.add
import io.github.ayfri.kore.features.predicates.providers.conditional
import io.github.ayfri.kore.features.predicates.providers.constant
import io.github.ayfri.kore.features.predicates.providers.cos
import io.github.ayfri.kore.features.predicates.providers.floorDiv
import io.github.ayfri.kore.features.predicates.providers.fromFloat
import io.github.ayfri.kore.features.predicates.providers.fromInt
import io.github.ayfri.kore.features.predicates.providers.max
import io.github.ayfri.kore.features.predicates.providers.mod
import io.github.ayfri.kore.features.predicates.providers.mul
import io.github.ayfri.kore.features.predicates.providers.round
import io.github.ayfri.kore.features.predicates.providers.sin
import io.github.ayfri.kore.features.predicates.providers.sqrt
import io.github.ayfri.kore.features.predicates.providers.sub
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

/** Fixed-point scale of trigonometric results and the default [ScoreVector] scale: `1000` means `1.0`. */
const val MATH_SCALE = 1000

/** Past this, `360 × angleScale` isn't exact as a float (`2^24 / 360`), and the angle loses precision. */
private const val TRIG_MAX_ANGLE_SCALE = (1 shl 24) / 360

private val handles = DataPackStateKey<MutableMap<String, MathHandle>>("helpers.math")

/** `fromFloat(sqrt)` never undershoots an Int's root, it only overshoots by one past `16785407`, which `root² > input` detects. */
private fun NumberProviderScope.integerSquareRoot(input: ContextIntProvider): ContextIntProvider {
	val value = max(input, 0)
	val root = fromFloat(sqrt(fromInt(value)))
	return sub(root, conditional(1) { intValueCheck(sub(value, mul(root, root)), max = constant(-1)) })
}

private fun NumberProviderScope.projectileHeight(time: ContextIntProvider, initialVelocity: ContextIntProvider, gravity: ContextIntProvider) =
	sub(mul(initialVelocity, time), floorDiv(mul(gravity, time, time), constant(2)))

private fun NumberProviderScope.squaredDistance(from: List<ContextIntProvider>, to: List<ContextIntProvider>) =
	add(from.zip(to) { start, end -> sub(end, start).let { mul(it, it) } })

private fun NumberProviderScope.trigonometry(cosine: Boolean, angle: ContextIntProvider, angleScale: Int): ContextFloatProvider {
	val radians = mul(fromInt(mod(angle, constant(360 * angleScale))), constant((PI / 180 / angleScale).toFloat()))
	return round(mul(if (cosine) cos(radians) else sin(radians), constant(MATH_SCALE.toFloat())))
}

/**
 * Runtime math engine of a datapack, returned by [registerMath].
 *
 * `sin`, `cos` and `sqrt` are one `execute store result score ... run compute ...` at the call site. `atan2` lives in
 * one shared generated function under `kore_math/`, called after copying the input. Constants and helper entities are
 * emitted into the `kore_math_init` load function only when an operation needs them.
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

	/** Marker teleported around `0 0 0` to turn rotations into directions and back. */
	internal val marker = uuid(HelpersConstants.mathMarkerUuid)
	internal val storage = storage(HelpersConstants.mathStorage, datapack.name)
	internal val home = vec3(HelpersConstants.mathEntitiesX.toDouble(), 0.0, HelpersConstants.mathEntitiesZ.toDouble())

	/** Returns the fake player holding [value] on [objective], set once in the init function. */
	fun constant(value: Int): ScoreboardEntity {
		if (constants.add(value)) init.scoreboard.players.set(literal("#$value"), objective, value)
		return temp("$value")
	}

	/** Returns the scratch fake player `#name` on [objective]. */
	fun temp(name: String) = ScoreboardEntity(objective, FakePlayer("#$name"))

	/**
	 * Returns [inputs] in a form one `compute` reads. A `compute` targets one real entity, so the scores of every other real
	 * entity are first copied into the scratch fake players `#s<index>`.
	 */
	context(fn: Function)
	internal fun stage(inputs: List<ScoreboardEntity>): List<ScoreboardEntity> {
		var target: String? = null
		return inputs.mapIndexed { index, input ->
			if (input.entity is FakePlayer) return@mapIndexed input
			val selector = input.entity.asSelector().asString()
			if (target == null) target = selector
			if (selector == target) input else temp("s$index").also { it setTo input }
		}
	}

	internal fun ensureObjective(name: String) {
		if (objectives.add(name)) init.scoreboard.objectives.add(name)
	}

	/** Forceloads a far chunk and summons the [marker] once, tagged `smithed.entity` so other packs skip it. */
	internal fun ensureEntities() {
		if (entitiesReady) return
		entitiesReady = true

		init.apply {
			forceLoad { add(vec2(HelpersConstants.mathEntitiesX, HelpersConstants.mathEntitiesZ)) }
			execute {
				unlessCondition { entity(marker) }
				run {
					summon(EntityTypes.MARKER, home) {
						this["Tags"] = nbtListOf("kore.math", "smithed.entity", "smithed.strict")
						this["UUID"] = marker.toIntArray()
					}
				}
			}
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

	private fun checkAngleScale(angleScale: Int) =
		require(angleScale in 1..TRIG_MAX_ANGLE_SCALE) { "angleScale $angleScale must be in 1..$TRIG_MAX_ANGLE_SCALE." }

	/**
	 * Cosine of [input] degrees, scaled by [angleScale] (`100` means the input is in hundredths of a degree).
	 * One `compute` reads the sine table of the game: the result is `cos × 1000`, within 1 unit of the exact value, for any
	 * input including negative ones. [angleScale] goes up to `46603`, past it the angle isn't exact as a float. The entity of
	 * [input] must be a single entity (`limitToOne`).
	 *
	 * ```kotlin
	 * math.cos(angle, result) // angle = 60 -> result = 500
	 * ```
	 */
	context(fn: Function)
	fun cos(input: ScoreboardEntity, output: ScoreboardEntity, angleScale: Int = 1) {
		checkAngleScale(angleScale)
		output.setTo { trigonometry(true, score(input), angleScale) }
	}

	context(fn: Function)
	fun cos(entity: Entity, inputScore: String, outputScore: String, angleScale: Int = 1) =
		cos(ScoreboardEntity(inputScore, entity), ScoreboardEntity(outputScore, entity), angleScale)

	context(fn: Function)
	fun cos(input: ScoreboardDelegate, output: ScoreboardDelegate, angleScale: Int = 1) =
		cos(input.scoreboardEntity(), output.scoreboardEntity(), angleScale)

	/**
	 * Sine of [input] degrees, scaled by [angleScale]. The result is `sin × 1000`, within 1 unit of the exact value, and the
	 * entity of [input] must be a single entity (`limitToOne`), see [cos].
	 *
	 * ```kotlin
	 * math.sin(angle, result) // angle = -90 -> result = -1000
	 * ```
	 */
	context(fn: Function)
	fun sin(input: ScoreboardEntity, output: ScoreboardEntity, angleScale: Int = 1) {
		checkAngleScale(angleScale)
		output.setTo { trigonometry(false, score(input), angleScale) }
	}

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
	 * One `compute` takes the game's float square root, then subtracts `1` when its square exceeds the input, which
	 * happens from `16785407`. Verified against all 2³² inputs. The entity of [input] must be a single entity (`limitToOne`).
	 *
	 * ```kotlin
	 * math.sqrt(input, result) // input = 1000000 -> result = 1000
	 * ```
	 */
	context(fn: Function)
	fun sqrt(input: ScoreboardEntity, output: ScoreboardEntity) {
		output.setTo { integerSquareRoot(score(input)) }
	}

	context(fn: Function)
	fun sqrt(entity: Entity, inputScore: String, outputScore: String) =
		sqrt(ScoreboardEntity(inputScore, entity), ScoreboardEntity(outputScore, entity))

	context(fn: Function)
	fun sqrt(input: ScoreboardDelegate, output: ScoreboardDelegate) = sqrt(input.scoreboardEntity(), output.scoreboardEntity())

	/**
	 * Squared distance between (x1, y1, z1) and (x2, y2, z2), all scores of [entity], in one `compute`. A result that
	 * doesn't fit an `Int` (past `46340` per axis) is `0`, use [ScoreVector.distanceTo] for fixed-point coordinates. The
	 * [entity] must be a single entity (`limitToOne`).
	 */
	context(fn: Function)
	fun distanceSquared(
		entity: Entity,
		x1: String, y1: String, z1: String,
		x2: String, y2: String, z2: String,
		outputScore: String,
	) = distanceSquared(
		listOf(x1, y1, z1).map { ScoreboardEntity(it, entity) },
		listOf(x2, y2, z2).map { ScoreboardEntity(it, entity) },
		ScoreboardEntity(outputScore, entity),
	)

	context(fn: Function)
	fun distanceSquared(
		first: Triple<ScoreboardDelegate, ScoreboardDelegate, ScoreboardDelegate>,
		second: Triple<ScoreboardDelegate, ScoreboardDelegate, ScoreboardDelegate>,
		output: ScoreboardDelegate,
	) = distanceSquared(
		first.toList().map { it.scoreboardEntity() },
		second.toList().map { it.scoreboardEntity() },
		output.scoreboardEntity(),
	)

	context(fn: Function)
	private fun distanceSquared(first: List<ScoreboardEntity>, second: List<ScoreboardEntity>, output: ScoreboardEntity) {
		val scores = stage(first + second)
		output.setTo { squaredDistance(scores.take(3).map { score(it) }, scores.drop(3).map { score(it) }) }
	}

	/**
	 * Height of a projectile, `v0 × t - g × t² / 2` with a floored division, all four values being scores, in one
	 * `compute`. A product or a result that doesn't fit an `Int` is `0`, except a product past 64 bits, which wraps first
	 * (`g = 4`, `t = -2^31` gives `g × t² = 0`). The entities read must be single entities (`limitToOne`).
	 *
	 * ```kotlin
	 * math.parabola(time, launchSpeed, gravity, height) // t = 10, v0 = 500, g = 10 -> 4500
	 * ```
	 */
	context(fn: Function)
	fun parabola(time: ScoreboardEntity, initialVelocity: ScoreboardEntity, gravity: ScoreboardEntity, output: ScoreboardEntity) {
		val (t, v0, g) = stage(listOf(time, initialVelocity, gravity))
		output.setTo { projectileHeight(score(t), score(v0), score(g)) }
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
	state(handles) { mutableMapOf() }.getOrPut(objective) { MathHandle(this, objective) }
