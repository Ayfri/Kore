package commands.execute

import arguments.Argument
import arguments.Relation
import arguments.Vec3
import arguments.literal
import arguments.numbers.*
import kotlinx.serialization.Serializable
import serializers.LowercaseSerializer
import utils.asArg

@Serializable(BlocksTestMode.Companion.FillModeSerializer::class)
enum class BlocksTestMode {
	ALL,
	MASKED;

	companion object {
		val values = values()

		object FillModeSerializer : LowercaseSerializer<BlocksTestMode>(values)
	}
}

data class ComparableScore(val target: Argument.ScoreHolder, val objective: String)

class ExecuteCondition(private val ex: Execute, isUnless: Boolean) {
	private val prefix = if (isUnless) "unless" else "if"
	val arguments = mutableListOf<Argument>()

	private fun addArguments(arguments: List<Argument>) {
		this.arguments += listOf(literal(prefix), *arguments.toTypedArray())
	}

	fun block(pos: Vec3, block: Argument.Block) = addArguments(listOf(literal("block"), pos, block))

	fun blocks(start: Vec3, end: Vec3, destination: Vec3, mode: BlocksTestMode) =
		addArguments(listOf(literal("blocks"), start, end, destination, literal(mode.asArg())))

	fun biome(pos: Vec3, biome: Argument.BiomeOrTag) = addArguments(listOf(literal("biome"), pos, biome))

	fun data(target: Argument.Data, path: String) = addArguments(
		listOf(
			literal("data"),
			literal(target.literalName), ex.targetArg(target),
			literal(path)
		)
	)

	fun dimension(dimension: Argument.Dimension) = addArguments(listOf(literal("dimension"), dimension))

	fun entity(target: Argument.Entity) = addArguments(listOf(literal("entity"), ex.targetArg(target)))

	fun loaded(pos: Vec3) = addArguments(listOf(literal("loaded"), pos))

	fun predicate(predicate: Argument.Predicate) = addArguments(listOf(literal("predicate"), predicate))
	fun predicate(predicate: String) = addArguments(listOf(literal("predicate"), literal(predicate)))

	fun score(
		target: Argument.ScoreHolder,
		objective: String,
		source: Argument.ScoreHolder,
		sourceObjective: String,
		relation: Relation
	) = addArguments(
		listOf(
			literal("score"), ex.targetArg(target),
			literal(objective), relation, source,
			literal(sourceObjective)
		)
	)

	fun score(target: Argument.ScoreHolder, objective: String, range: IntRangeOrInt) =
		addArguments(listOf(literal("score"), ex.targetArg(target), literal(objective), literal("matches"), literal(range.asArg())))

	fun score(target: Argument.ScoreHolder, objective: String) = ComparableScore(target, objective)

	infix fun ComparableScore.lessThan(source: Int) = score(target, objective, rangeOrIntEnd(source - 1))
	infix fun ComparableScore.lessThan(source: ComparableScore) =
		score(target, objective, source.target, source.objective, Relation.LESS_THAN)

	infix fun ComparableScore.lessThanOrEqualTo(source: Int) = score(target, objective, rangeOrIntEnd(source))
	infix fun ComparableScore.lessThanOrEqualTo(source: ComparableScore) =
		score(target, objective, source.target, source.objective, Relation.LESS_THAN_OR_EQUAL_TO)

	infix fun ComparableScore.equalTo(source: Int) = score(target, objective, rangeOrInt(source))
	infix fun ComparableScore.equalTo(source: ComparableScore) =
		score(target, objective, source.target, source.objective, Relation.EQUAL_TO)

	infix fun ComparableScore.greaterThanOrEqualTo(source: Int) = score(target, objective, rangeOrIntStart(source))
	infix fun ComparableScore.greaterThanOrEqualTo(source: ComparableScore) =
		score(target, objective, source.target, source.objective, Relation.GREATER_THAN_OR_EQUAL_TO)

	infix fun ComparableScore.greaterThan(source: Int) = score(target, objective, rangeOrIntStart(source + 1))
	infix fun ComparableScore.greaterThan(source: ComparableScore) =
		score(target, objective, source.target, source.objective, Relation.GREATER_THAN)

	infix fun ComparableScore.matches(range: IntRangeOrInt) = score(target, objective, range)
	infix fun ComparableScore.matches(source: IntRange) = score(target, objective, source.asRangeOrInt())
}
