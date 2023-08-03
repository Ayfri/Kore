package commands.execute

import arguments.Argument
import arguments.enums.Relation
import arguments.maths.Vec3
import arguments.numbers.ranges.IntRangeOrInt
import arguments.scores.ExecuteScore
import arguments.scores.Scores
import arguments.types.BiomeOrTagArgument
import arguments.types.DataArgument
import arguments.types.EntityArgument
import arguments.types.ScoreHolderArgument
import arguments.types.literals.literal
import arguments.types.resources.BlockArgument
import arguments.types.resources.DimensionArgument
import arguments.types.resources.FunctionArgument
import arguments.types.resources.PredicateArgument
import serializers.LowercaseSerializer
import utils.asArg
import kotlinx.serialization.Serializable

@Serializable(BlocksTestMode.Companion.FillModeSerializer::class)
enum class BlocksTestMode {
	ALL,
	MASKED;

	companion object {
		data object FillModeSerializer : LowercaseSerializer<BlocksTestMode>(entries)
	}
}

class ExecuteCondition(private val ex: Execute, isUnless: Boolean) : Scores<ExecuteScore>() {
	private val prefix = if (isUnless) "unless" else "if"
	val arguments = mutableListOf<Argument>()

	private fun addArguments(arguments: List<Argument>) {
		this.arguments += listOf(literal(prefix), *arguments.toTypedArray())
	}

	fun block(pos: Vec3, block: BlockArgument) = addArguments(listOf(literal("block"), pos, block))

	fun blocks(start: Vec3, end: Vec3, destination: Vec3, mode: BlocksTestMode) =
		addArguments(listOf(literal("blocks"), start, end, destination, literal(mode.asArg())))

	fun biome(pos: Vec3, biome: BiomeOrTagArgument) = addArguments(listOf(literal("biome"), pos, biome))

	fun data(target: DataArgument, path: String) = addArguments(
		listOf(
			literal("data"),
			literal(target.literalName), ex.targetArg(target),
			literal(path)
		)
	)

	fun dimension(dimension: DimensionArgument) = addArguments(listOf(literal("dimension"), dimension))

	fun entity(target: EntityArgument) = addArguments(listOf(literal("entity"), ex.targetArg(target)))

	fun function(function: FunctionArgument) = addArguments(listOf(literal("function"), function))

	fun loaded(pos: Vec3) = addArguments(listOf(literal("loaded"), pos))

	fun predicate(predicate: PredicateArgument) = addArguments(listOf(literal("predicate"), predicate))
	fun predicate(predicate: String) = addArguments(listOf(literal("predicate"), literal(predicate)))

	fun score(
		target: ScoreHolderArgument,
		objective: String,
		source: ScoreHolderArgument,
		sourceObjective: String,
		relation: Relation,
	) = addArguments(
		listOf(
			literal("score"), ex.targetArg(target),
			literal(objective), relation, source,
			literal(sourceObjective)
		)
	)

	override fun addScore(string: String) = addArguments(listOf(literal("score"), literal(string)))

	fun score(target: ScoreHolderArgument, objective: String, range: IntRangeOrInt) =
		addArguments(listOf(literal("score"), ex.targetArg(target), literal(objective), literal("matches"), literal(range.asArg())))
}
