package commands

import arguments.*
import arguments.numbers.IntRangeOrInt
import functions.Function
import kotlinx.serialization.Serializable
import serializers.LowercaseSerializer

@Serializable(Anchor.Companion.AnchorSerializer::class)
enum class Anchor {
	FEET,
	EYES;
	
	companion object {
		val values = values()
		
		object AnchorSerializer : LowercaseSerializer<Anchor>(values)
	}
}

@Serializable(BlocksTestMode.Companion.FillModeSerializer::class)
enum class BlocksTestMode {
	ALL,
	MASKED;
	
	companion object {
		val values = values()
		
		object FillModeSerializer : LowercaseSerializer<BlocksTestMode>(values)
	}
}

object ExecuteCondition {
	fun block(pos: Argument.Coordinate, block: Argument.Block) = listOf(literal("block"), pos, block)
	
	fun blocks(start: Argument.Coordinate, end: Argument.Coordinate, destination: Argument.Coordinate, mode: BlocksTestMode) = listOf(
		literal("blocks"), start, end, destination, literal(mode.asArg())
	)
	
	fun data(target: Argument.Data, path: String) = listOf(
		literal("data"), literal(target.literalName), target, literal(path)
	)
	
	fun entity(target: Argument.Entity) = listOf(literal("entity"), target)
	
	fun predicate(predicate: String) = listOf(literal("predicate"), literal(predicate))
	
	fun score(target: Argument.ScoreHolder, objective: String, source: Argument.ScoreHolder, sourceObjective: String, relation: RelationBlock.(Number, Number) -> Relation) = listOf(
		literal("score"), target, literal(objective), literal(relation(RelationBlock(), 0.0, 0.0).asArg()), source, literal(sourceObjective)
	)
	
	fun score(target: Argument.ScoreHolder, objective: String, range: IntRangeOrInt) = listOf(
		literal("score"), target, literal(objective), literal(range.asArg())
	)
}

object ExecuteStore {
	fun block(
		pos: Argument.Coordinate,
		path: String,
		type: DataType,
		scale: Double,
	) = listOf(literal("store"), literal("block"), pos, literal(path), literal(type.asArg()), float(scale))
	
	fun bossbarMax(id: String) = listOf(literal("bossbar"), literal(id), literal("max"))
	fun bossbarValue(id: String) = listOf(literal("bossbar"), literal(id), literal("value"))
	
	fun entity(target: Argument.Entity, path: String, type: DataType, scale: Double) = listOf(literal("entity"), target, literal(path), literal(type.asArg()), float(scale))
	
	fun score(target: Argument.ScoreHolder, objective: String) = listOf(literal("score"), target, literal(objective))
	
	fun storage(target: Argument.Storage, path: String, type: DataType, scale: Double) = listOf(literal("storage"), target, literal(path), literal(type.asArg()), float(scale))
}

class Execute {
	private val array = mutableListOf<Argument>()
	private var command: Command? = null
	
	private fun <T> MutableList<T>.addAll(vararg args: T?) = addAll(args.filterNotNull())
	
	fun getFinalCommand() = (array + literal("run") + literal(command?.toString())).toTypedArray()
	
	fun align(axis: Axes, offset: Int? = null) = array.addAll(literal("align"), literal(axis.asArg()), int(offset))
	fun anchored(anchor: Anchor) = array.addAll(literal("anchored"), literal(anchor.asArg()))
	fun asTarget(target: Argument.Entity) = array.addAll(literal("as"), target)
	fun at(target: Argument.Entity) = array.addAll(literal("at"), target)
	fun facing(target: Argument.Entity, anchor: Anchor? = null) = array.addAll(literal("facing"), target, literal(anchor?.asArg()))
	fun facingBlock(pos: Argument.Coordinate, anchor: Anchor? = null) = array.addAll(literal("facing"), literal("block"), pos, literal(anchor?.asArg()))
	fun facingEntity(target: Argument.Entity, anchor: Anchor? = null) = array.addAll(literal("facing"), target, literal("entity"), target, literal(anchor?.asArg()))
	
	fun facingPos(pos: Argument.Coordinate, anchor: Anchor? = null) = array.addAll(literal("facing"), literal("position"), pos, literal(anchor?.asArg()))
	fun inDimension(dimension: Dimension) = array.addAll(literal("in"), dimension(dimension))
	fun inDimension(customDimension: String, namespace: String? = null) = array.addAll(literal("in"), dimension(customDimension, namespace))
	fun positioned(pos: Argument.Coordinate) = array.addAll(literal("positioned"), pos)
	fun positionedAs(target: Argument.Entity) = array.addAll(literal("positioned"), target, literal("as"), target)
	fun rotated(rotation: Argument.Rotation) = array.addAll(literal("rotated"), rotation)
	fun rotatedAs(target: Argument.Entity) = array.addAll(literal("rotated"), target, literal("as"), target)
	
	fun ifCondition(block: ExecuteCondition.() -> List<Argument>) = array.addAll(literal("if"), *ExecuteCondition.block().toTypedArray())
	fun unlessCondition(block: ExecuteCondition.() -> List<Argument>) = array.addAll(literal("unless"), *ExecuteCondition.block().toTypedArray())
	
	fun storeResult(block: ExecuteStore.() -> List<Argument>) = array.addAll(literal("store"), literal("result"), *ExecuteStore.block().toTypedArray())
	fun storeValue(block: ExecuteStore.() -> List<Argument>) = array.addAll(literal("store"), literal("value"), *ExecuteStore.block().toTypedArray())
	
	fun run(block: Function.() -> Command) {
		val function = Function.EMPTY
		command = function.block()
	}
}

fun Function.execute(block: Execute.() -> Unit) = addLine(command("execute", *Execute().apply(block).getFinalCommand()))
