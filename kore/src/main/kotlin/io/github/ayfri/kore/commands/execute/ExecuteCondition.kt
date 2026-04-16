package io.github.ayfri.kore.commands.execute

import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.arguments.ItemSlot
import io.github.ayfri.kore.arguments.components.ItemPredicate
import io.github.ayfri.kore.arguments.enums.Relation
import io.github.ayfri.kore.arguments.maths.Vec3
import io.github.ayfri.kore.arguments.numbers.ranges.IntRangeOrInt
import io.github.ayfri.kore.arguments.scores.ExecuteScore
import io.github.ayfri.kore.arguments.scores.Scores
import io.github.ayfri.kore.arguments.types.*
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.arguments.types.resources.BlockArgument
import io.github.ayfri.kore.arguments.types.resources.FunctionArgument
import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.generated.arguments.types.DimensionArgument
import io.github.ayfri.kore.generated.arguments.types.PredicateArgument
import io.github.ayfri.kore.generated.arguments.types.StopwatchArgument
import io.github.ayfri.kore.generated.arguments.worldgen.BiomeOrTagArgument
import io.github.ayfri.kore.serializers.LowercaseSerializer
import io.github.ayfri.kore.utils.asArg
import io.github.ayfri.kore.utils.snbtSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString

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

	/** Checks if the biome at the given position matches the given biome or biome tag. */
	fun biome(pos: Vec3, biome: BiomeOrTagArgument) =
		addArguments(listOf(literal("biome"), literal(pos.toStringTruncated()), biome))

	/** Checks if the block at the given position matches the given block. */
	fun block(pos: Vec3, block: BlockArgument) =
		addArguments(listOf(literal("block"), literal(pos.toStringTruncated()), block))

	/** Checks if the region between [start] and [end] matches the region starting at [destination], using the given [mode]. */
	fun blocks(start: Vec3, end: Vec3, destination: Vec3, mode: BlocksTestMode) =
		addArguments(
			listOf(
				literal("blocks"),
				literal(start.toStringTruncated()),
				literal(end.toStringTruncated()),
				literal(destination.toStringTruncated()),
				literal(mode.asArg())
			)
		)

	/** Checks if the given data [target] contains something at the given NBT [path]. */
	fun data(target: DataArgument, path: String) = addArguments(
		listOf(
			literal("data"),
			literal(target.literalName), ex.targetArg(target),
			literal(path)
		)
	)

	/** Checks if the execution is currently in the given dimension. */
	fun dimension(dimension: DimensionArgument) = addArguments(listOf(literal("dimension"), dimension))

	/** Checks if the given entity selector matches at least one entity. */
	fun entity(target: EntityArgument) = addArguments(listOf(literal("entity"), ex.targetArg(target)))

	/** Checks if the items in the given [slots] of the [source] container match the given [itemPredicate]. */
	fun items(source: ContainerArgument, slots: ItemSlot, itemPredicate: ItemPredicate) =
		addArguments(listOf(literal("items"), literal(source.literalName), source, slots, literal(itemPredicate.toString())))

	/** Checks if the given function returns a non-zero value (its return value is used as the condition). */
	fun function(function: FunctionArgument) = addArguments(listOf(literal("function"), function))

	/** Checks if the chunk at the given position is fully loaded (entity ticking). */
	fun loaded(pos: Vec3) = addArguments(listOf(literal("loaded"), literal(pos.toStringTruncatedIfZero())))

	/** References a predicate. */
	fun predicate(predicate: PredicateArgument) = addArguments(listOf(literal("predicate"), predicate))
	/** References a predicate. */
	fun predicate(predicate: String) = addArguments(listOf(literal("predicate"), literal(predicate)))
	/** Generates an inline predicate in the execute command. */
	fun predicate(block: Predicate.() -> Unit) = addArguments(
		listOf(
			literal("predicate"),
			literal(
				snbtSerializer.encodeToString(Predicate().apply(block))
			),
		)
	)

	/** Compares the score of [target] on [objective] with the score of [source] on [sourceObjective] using the given [relation]. */
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

	/** Checks if the score of [target] on [objective] matches the given [range]. */
	fun score(target: ScoreHolderArgument, objective: String, range: IntRangeOrInt) =
		addArguments(listOf(literal("score"), ex.targetArg(target), literal(objective), literal("matches"), literal(range.asArg())))

	/** Checks if the given stopwatch is running and has elapsed the given range, the range is in milliseconds. */
	fun stopwatch(id: StopwatchArgument, range: IntRangeOrInt) =
		addArguments(listOf(literal("stopwatch"), id, literal(range.asArg())))
}
