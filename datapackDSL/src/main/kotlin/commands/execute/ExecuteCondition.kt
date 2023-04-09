package commands.execute

import arguments.*
import arguments.Relation
import arguments.numbers.IntRangeOrInt
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
		relation: RelationBlock.(Number, Number) -> Relation
	) = addArguments(
		listOf(
			literal("score"), ex.targetArg(target),
			literal(objective), relation(RelationBlock(), 0.0, 0.0), source,
			literal(sourceObjective)
		)
	)

	fun score(target: Argument.ScoreHolder, objective: String, range: IntRangeOrInt) =
		addArguments(listOf(literal("score"), ex.targetArg(target), literal(objective), literal("matches"), literal(range.asArg())))
}
