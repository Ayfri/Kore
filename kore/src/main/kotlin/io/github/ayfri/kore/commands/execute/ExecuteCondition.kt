package io.github.ayfri.kore.commands.execute

import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.arguments.ItemSlot
import io.github.ayfri.kore.arguments.components.ItemPredicate
import io.github.ayfri.kore.arguments.enums.Relation
import io.github.ayfri.kore.arguments.maths.Vec3
import io.github.ayfri.kore.arguments.numbers.ranges.IntRangeOrInt
import io.github.ayfri.kore.arguments.scores.ExecuteScore
import io.github.ayfri.kore.arguments.scores.Scores
import io.github.ayfri.kore.arguments.types.ContainerArgument
import io.github.ayfri.kore.arguments.types.DataArgument
import io.github.ayfri.kore.arguments.types.EntityArgument
import io.github.ayfri.kore.arguments.types.ScoreHolderArgument
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.arguments.types.resources.BlockArgument
import io.github.ayfri.kore.arguments.types.resources.FunctionArgument
import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.generated.arguments.types.DimensionArgument
import io.github.ayfri.kore.generated.arguments.types.PredicateArgument
import io.github.ayfri.kore.generated.arguments.worldgen.BiomeOrTagArgument
import io.github.ayfri.kore.serializers.LowercaseSerializer
import io.github.ayfri.kore.utils.asArg
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

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

	fun biome(pos: Vec3, biome: BiomeOrTagArgument) = addArguments(listOf(literal("biome"), pos, biome))

	fun block(pos: Vec3, block: BlockArgument) = addArguments(listOf(literal("block"), pos, block))

	fun blocks(start: Vec3, end: Vec3, destination: Vec3, mode: BlocksTestMode) =
		addArguments(listOf(literal("blocks"), start, end, destination, literal(mode.asArg())))

	fun data(target: DataArgument, path: String) = addArguments(
		listOf(
			literal("data"),
			literal(target.literalName), ex.targetArg(target),
			literal(path)
		)
	)

	fun dimension(dimension: DimensionArgument) = addArguments(listOf(literal("dimension"), dimension))

	fun entity(target: EntityArgument) = addArguments(listOf(literal("entity"), ex.targetArg(target)))

	fun items(source: ContainerArgument, slots: ItemSlot, itemPredicate: ItemPredicate) =
		addArguments(listOf(literal("items"), source, slots, literal(itemPredicate.toString())))

	fun function(function: FunctionArgument) = addArguments(listOf(literal("function"), function))

	fun loaded(pos: Vec3) = addArguments(listOf(literal("loaded"), pos))

	/** References a predicate. */
	fun predicate(predicate: PredicateArgument) = addArguments(listOf(literal("predicate"), predicate))
	/** References a predicate. */
	fun predicate(predicate: String) = addArguments(listOf(literal("predicate"), literal(predicate)))
	/** Generates an inline predicate in the execute command. */
	fun predicate(block: Predicate.() -> Unit) = addArguments(
		listOf(
			literal("predicate"),
			literal(
				Json.encodeToString(Predicate().apply(block))
			),
		)
	)

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
