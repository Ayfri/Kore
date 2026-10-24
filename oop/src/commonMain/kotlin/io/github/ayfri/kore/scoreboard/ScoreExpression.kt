package io.github.ayfri.kore.scoreboard

import io.github.ayfri.kore.arguments.types.EntityArgument
import io.github.ayfri.kore.commands.Command
import io.github.ayfri.kore.commands.Data
import io.github.ayfri.kore.commands.compute
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.entities.Entity
import io.github.ayfri.kore.entities.FakePlayer
import io.github.ayfri.kore.features.predicates.providers.ContextFloatProvider
import io.github.ayfri.kore.features.predicates.providers.ContextIntProvider
import io.github.ayfri.kore.features.predicates.providers.NumberProviderScope
import io.github.ayfri.kore.features.predicates.providers.div
import io.github.ayfri.kore.features.predicates.providers.score
import io.github.ayfri.kore.features.predicates.providers.times
import io.github.ayfri.kore.features.predicates.providers.toFloat
import io.github.ayfri.kore.features.predicates.types.EntityTarget
import io.github.ayfri.kore.functions.Function
import kotlin.experimental.ExperimentalTypeInference

/**
 * The scope of the [setTo] expressions: every number provider builder and operator of the core DSL, plus [score] to read
 * a [ScoreboardEntity].
 *
 * A fake player is read by name. A real entity is read through the `compute entity` target, so one expression can read
 * the scores of a single real entity, in any number of objectives, and of any number of fake players.
 */
class ScoreExpressionScope internal constructor() : NumberProviderScope {
	internal var contextEntity: Entity? = null

	/**
	 * Reads [entry] as an int provider. Reading two different real entities in one expression isn't possible because
	 * `compute` has one target: copy one of the scores into a fake player first.
	 *
	 * ```
	 * score(kills)    // kills of a player: {type:"minecraft:score",score:kills,target:{type:"minecraft:context",target:target_entity}}
	 * score(total)    // total of fake player #total: {type:"minecraft:score",score:total,target:{type:"minecraft:fixed",name:"#total"}}
	 * ```
	 */
	fun score(entry: ScoreboardEntity): ContextIntProvider {
		val holder = entry.entity
		if (holder is FakePlayer) return score(entry.name, holder.holderName)

		check(holder.limitToOne) { "A score expression reads one entity at most, but '${holder.asSelector().asString()}' can match several." }
		val current = contextEntity
		check(current == null || current.asSelector().asString() == holder.asSelector().asString()) {
			"A score expression reads one real entity at most, copy one of the scores into a fake player first."
		}

		contextEntity = holder
		return score(entry.name, EntityTarget.TARGET_ENTITY)
	}
}

context(fn: Function)
private fun ScoreboardEntity.storeCompute(scope: ScoreExpressionScope, emit: Function.(EntityArgument?) -> Unit) =
	fn.execute {
		storeResult { score(entity.asScoreHolder(), name) }
		run { emit(scope.contextEntity?.asSelector()) }
	}

/**
 * Sets this score to an int expression of other scores, as one `execute store result score ... run compute ...`.
 *
 * Inside the lambda, `score(entry)` reads a [ScoreboardEntity] and the operators of the core DSL build the tree. The
 * `compute` runs on the single real entity the expression reads, or with no target when it only reads fake players. A
 * failing expression (a division by zero, an overflow) stores `0`.
 *
 * ```
 * val sum = fakePlayer("sum").getScoreEntity("game")
 * sum setTo { score(a) + score(b) * 2 }
 * // execute store result score #sum game run compute default integer {type:"minecraft:add",inputs:[...]}
 * ```
 *
 * A lambda returning a float provider picks the [float overload][setTo].
 */
@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
context(fn: Function)
infix fun ScoreboardEntity.setTo(expression: ScoreExpressionScope.() -> ContextIntProvider) {
	val scope = ScoreExpressionScope()
	val provider = scope.expression()
	storeCompute(scope) { target ->
		if (target == null) compute { provider } else compute(target) { provider }
	}
}

/**
 * Sets this score to a float expression of other scores in `float` mode, multiplied by [scale] and rounded down, as one
 * `execute store result score ... run compute ...`. A result that isn't finite or a failing expression stores `0`, a
 * result beyond the int range saturates, and a float provider is exact up to 2^24 only.
 *
 * ```
 * chance.setTo(1000f) { uniform(0.5f, 2f) }                   // compute default float {...} 1000, between 500 and 1999
 * ratio.setTo(100f) { score(kills).toFloat() / score(deaths).toFloat() }  // 2.5 kills per death gives 250
 * ```
 */
@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
context(fn: Function)
fun ScoreboardEntity.setTo(scale: Float? = null, expression: ScoreExpressionScope.() -> ContextFloatProvider) {
	val scope = ScoreExpressionScope()
	val provider = scope.expression()
	storeCompute(scope) { target ->
		if (target == null) compute(scale) { provider } else compute(target, scale) { provider }
	}
}

/**
 * Writes a float expression of scores into [path] as one `data modify ... set compute ...`, reading scores like
 * [setTo]: the `compute` runs on the single real entity the expression reads, or with no target when it only reads fake
 * players. The stored float is never scaled.
 *
 * ```
 * data(storage("foo")).computeScores("len") { score(x).toFloat() }
 * // data modify storage minecraft:foo len set compute entity @e[limit=1,name=Ayfri] float {...}
 * ```
 */
fun Data.computeScores(path: String, expression: ScoreExpressionScope.() -> ContextFloatProvider): Command {
	val scope = ScoreExpressionScope()
	val provider = scope.expression()
	val entity = scope.contextEntity?.asSelector()
	return if (entity == null) compute(path) { provider } else compute(path, entity) { provider }
}

/**
 * Multiplies this score by the float [factor] in one `compute`, rounding the result down: `-3 * 1.5` gives `-5`. An
 * `Int` factor keeps the single `scoreboard players operation` of [timesAssign]. The score is read as a float, so it is
 * exact up to 2^24, and a result beyond the int range saturates.
 *
 * ```
 * score *= 1.5f   // execute store result score ... run compute default float {type:"minecraft:mul",inputs:[...]}
 * ```
 */
context(fn: Function)
operator fun ScoreboardEntity.timesAssign(factor: Float) = setTo { score(this@timesAssign).toFloat() * factor }

/**
 * Divides this score by the float [divisor] in one `compute`, rounding the result down: `-3 / 2.5` gives `-2`. A
 * division by `0f` fails the `compute` and stores `0`. See [timesAssign] for the precision.
 *
 * ```
 * score /= 2.5f   // execute store result score ... run compute default float {type:"minecraft:div",...}
 * ```
 */
context(fn: Function)
operator fun ScoreboardEntity.divAssign(divisor: Float) = setTo { score(this@divAssign).toFloat() / divisor }
