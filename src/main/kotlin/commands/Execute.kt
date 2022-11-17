package commands

import arguments.Axes
import arguments.DataType
import arguments.Dimension
import arguments.RangeOrInt
import arguments.Relation
import arguments.RelationBlock
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

class ExecuteCondition(private val fn: Function) {
	fun block(pos: Argument.Coordinate, block: Argument.Block) = listOf(fn.literal("block"), pos, block)
	
	fun blocks(start: Argument.Coordinate, end: Argument.Coordinate, destination: Argument.Coordinate, mode: BlocksTestMode) = listOf(
		fn.literal("blocks"), start, end, destination, fn.literal(mode.asArg())
	)
	
	fun data(target: Argument.Data, path: String) = listOf(
		fn.literal("data"), fn.literal(target.literalName), target, fn.literal(path)
	)
	
	fun entity(target: Argument.Entity) = listOf(fn.literal("entity"), target)
	
	fun predicate(predicate: String) = listOf(fn.literal("predicate"), fn.literal(predicate))
	
	fun score(target: Argument.ScoreHolder, objective: String, source: Argument.ScoreHolder, sourceObjective: String, relation: RelationBlock.(Number, Number) -> Relation) = listOf(
		fn.literal("score"), target, fn.literal(objective), fn.literal(relation(RelationBlock(), 0.0, 0.0).asArg()), source, fn.literal(sourceObjective)
	)
	
	fun score(target: Argument.ScoreHolder, objective: String, range: RangeOrInt) = listOf(
		fn.literal("score"), target, fn.literal(objective), fn.literal(range.asArg())
	)
}

class ExecuteStore(private val fn: Function) {
	fun block(pos: Argument.Coordinate, path: String, type: DataType, scale: Double) =
		listOf(fn.literal("store"), fn.literal("block"), pos, fn.literal(path), fn.literal(type.asArg()), fn.float(scale))
	
	fun bossbarMax(id: String) = listOf(fn.literal("bossbar"), fn.literal(id), fn.literal("max"))
	fun bossbarValue(id: String) = listOf(fn.literal("bossbar"), fn.literal(id), fn.literal("value"))
	
	fun entity(target: Argument.Entity, path: String, type: DataType, scale: Double) = listOf(fn.literal("entity"), target, fn.literal(path), fn.literal(type.asArg()), fn.float(scale))
	
	fun score(target: Argument.ScoreHolder, objective: String) = listOf(fn.literal("score"), target, fn.literal(objective))
	
	fun storage(target: Argument.Storage, path: String, type: DataType, scale: Double) = listOf(fn.literal("storage"), target, fn.literal(path), fn.literal(type.asArg()), fn.float(scale))
}

class Execute(private val fn: Function) {
	private val array = mutableListOf<Argument>()
	private var command: Command? = null
	
	private fun <T> MutableList<T>.addAll(vararg args: T?) = addAll(args.filterNotNull())
	
	fun getFinalCommand() = (array + fn.literal("run") + fn.literal(command?.toString())).toTypedArray()
	
	fun align(axis: Axes, offset: Int? = null) = array.addAll(fn.literal("align"), fn.literal(axis.asArg()), fn.int(offset))
	fun anchored(anchor: Anchor) = array.addAll(fn.literal("anchored"), fn.literal(anchor.asArg()))
	fun asTarget(target: Argument.Entity) = array.addAll(fn.literal("as"), target)
	fun at(target: Argument.Entity) = array.addAll(fn.literal("at"), target)
	fun facing(target: Argument.Entity, anchor: Anchor? = null) = array.addAll(fn.literal("facing"), target, fn.literal(anchor?.asArg()))
	fun facingBlock(pos: Argument.Coordinate, anchor: Anchor? = null) = array.addAll(fn.literal("facing"), fn.literal("block"), pos, fn.literal(anchor?.asArg()))
	fun facingEntity(target: Argument.Entity, anchor: Anchor? = null) = array.addAll(fn.literal("facing"), target, fn.literal("entity"), target, fn.literal(anchor?.asArg()))
	
	fun facingPos(pos: Argument.Coordinate, anchor: Anchor? = null) = array.addAll(fn.literal("facing"), fn.literal("position"), pos, fn.literal(anchor?.asArg()))
	fun inDimension(dimension: Dimension) = array.addAll(fn.literal("in"), fn.dimension(dimension))
	fun inDimension(customDimension: String, namespace: String? = null) = array.addAll(fn.literal("in"), fn.dimension(customDimension, namespace))
	fun positioned(pos: Argument.Coordinate) = array.addAll(fn.literal("positioned"), pos)
	fun positionedAs(target: Argument.Entity) = array.addAll(fn.literal("positioned"), target, fn.literal("as"), target)
	fun rotated(rotation: Argument.Rotation) = array.addAll(fn.literal("rotated"), rotation)
	fun rotatedAs(target: Argument.Entity) = array.addAll(fn.literal("rotated"), target, fn.literal("as"), target)
	
	fun ifCondition(block: ExecuteCondition.() -> List<Argument>) = array.addAll(fn.literal("if"), *ExecuteCondition(fn).block().toTypedArray())
	fun unlessCondition(block: ExecuteCondition.() -> List<Argument>) = array.addAll(fn.literal("unless"), *ExecuteCondition(fn).block().toTypedArray())
	
	fun storeResult(block: ExecuteStore.() -> List<Argument>) = array.addAll(fn.literal("store"), fn.literal("result"), *ExecuteStore(fn).block().toTypedArray())
	fun storeValue(block: ExecuteStore.() -> List<Argument>) = array.addAll(fn.literal("store"), fn.literal("value"), *ExecuteStore(fn).block().toTypedArray())
	
	fun run(block: Function.() -> Command) {
		val function = Function.EMPTY
		command = function.block()
		function.clear()
	}
}

fun Function.execute(block: Execute.() -> Unit) = addLine(command("execute", *Execute(this).apply(block).getFinalCommand()))
