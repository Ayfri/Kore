package commands

import DataPack.Companion.GENERATED_FUNCTIONS_FOLDER
import arguments.*
import arguments.enums.DataType
import arguments.enums.Dimension
import arguments.numbers.IntRangeOrInt
import arguments.selector.Sort
import functions.Function
import functions.function
import functions.generatedFunction
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

class ExecuteCondition(private val ex: Execute) {
	fun block(pos: Vec3, block: Argument.Block) = listOf(literal("block"), pos, block)

	fun blocks(start: Vec3, end: Vec3, destination: Vec3, mode: BlocksTestMode) = listOf(
		literal("blocks"), start, end, destination, literal(mode.asArg())
	)

	fun biome(biome: Argument.Biome) = listOf(literal("biome"), biome)

	fun data(target: Argument.Data, path: String) = listOf(
		literal("data"), literal(target.literalName), ex.targetArg(target), literal(path)
	)

	fun entity(target: Argument.Entity) = listOf(literal("entity"), ex.targetArg(target))

	fun predicate(predicate: Argument.Predicate) = listOf(literal("predicate"), predicate)
	fun predicate(predicate: String) = listOf(literal("predicate"), literal(predicate))

	fun score(
		target: Argument.ScoreHolder,
		objective: String,
		source: Argument.ScoreHolder,
		sourceObjective: String,
		relation: RelationBlock.(Number, Number) -> Relation
	) = listOf(
		literal("score"), ex.targetArg(target), literal(objective), relation(RelationBlock(), 0.0, 0.0), source, literal(sourceObjective)
	)

	fun score(target: Argument.ScoreHolder, objective: String, range: IntRangeOrInt) = listOf(
		literal("score"), ex.targetArg(target), literal(objective), literal("matches"), literal(range.asArg())
	)
}

class ExecuteStore(private val ex: Execute) {
	fun block(
		pos: Vec3,
		path: String,
		type: DataType,
		scale: Double,
	) = listOf(literal("store"), literal("block"), pos, literal(path), literal(type.asArg()), float(scale))

	fun bossBarMax(id: String) = listOf(literal("bossbar"), literal(id), literal("max"))
	fun bossBarValue(id: String) = listOf(literal("bossbar"), literal(id), literal("value"))

	fun entity(target: Argument.Entity, path: String, type: DataType, scale: Double) = listOf(literal("entity"), ex.targetArg(target), literal(path), literal(type.asArg()), float(scale))

	fun score(target: Argument.ScoreHolder, objective: String) = listOf(literal("score"), ex.targetArg(target), literal(objective))

	fun storage(target: Argument.Storage, path: String, type: DataType, scale: Double) = listOf(literal("storage"), target, literal(path), literal(type.asArg()), float(scale))
}

class Execute {
	private val array = mutableListOf<Argument>()
	private var command: Command? = null

	private fun <T> MutableList<T>.addAll(vararg args: T?) = addAll(args.filterNotNull())

	private var asArg: Argument.Entity? = null

	internal fun targetArg(arg: Argument) = when {
		asArg is Argument.UUID || (asArg as? Argument.Selector)?.selector?.let {
			val nbtData = it.nbtData
			val otherSel = (arg as? Argument.Selector)?.selector
			val otherNbtData = otherSel?.nbtData
			nbtData.limit == 1 && nbtData.sort != Sort.RANDOM && nbtData == otherNbtData
		} == true -> self()

		asArg == arg -> self()
		else -> arg
	}

	fun getArguments() = array.toTypedArray()

	fun align(axis: Axes, offset: Int? = null) = array.addAll(literal("align"), axis, int(offset))
	fun anchored(anchor: Anchor) = array.addAll(literal("anchored"), literal(anchor.asArg()))
	fun asTarget(target: Argument.Entity) = array.addAll(literal("as"), target).also { asArg = target }
	fun at(target: Argument.Entity) = array.addAll(literal("at"), targetArg(target))
	fun facing(target: Vec3) = array.addAll(literal("facing"), target)
	fun facingEntity(target: Argument.Entity, anchor: Anchor) =
		array.addAll(literal("facing"), literal("entity"), targetArg(target), literal(anchor.asArg()))

	fun inDimension(dimension: Dimension) = array.addAll(literal("in"), dimension)
	fun inDimension(customDimension: String, namespace: String = "minecraft") =
		array.addAll(literal("in"), dimension(customDimension, namespace))

	fun positioned(pos: Vec3) = array.addAll(literal("positioned"), pos)
	fun positionedAs(target: Argument.Entity) = array.addAll(literal("positioned"), literal("as"), targetArg(target))
	fun rotated(rotation: Argument.Rotation) = array.addAll(literal("rotated"), rotation)
	fun rotatedAs(target: Argument.Entity) = array.addAll(literal("rotated"), literal("as"), targetArg(target))

	fun ifCondition(block: ExecuteCondition.() -> List<Argument>) =
		array.addAll(literal("if"), *ExecuteCondition(this).block().toTypedArray())

	fun unlessCondition(block: ExecuteCondition.() -> List<Argument>) =
		array.addAll(literal("unless"), *ExecuteCondition(this).block().toTypedArray())

	fun storeResult(block: ExecuteStore.() -> List<Argument>) =
		array.addAll(literal("store"), literal("result"), *ExecuteStore(this).block().toTypedArray())

	fun storeValue(block: ExecuteStore.() -> List<Argument>) =
		array.addAll(literal("store"), literal("value"), *ExecuteStore(this).block().toTypedArray())

}


context(Function)
fun Execute.run(name: String, namespace: String = datapack.name, block: Function.() -> Unit): Command {
	datapack.function(name, namespace, block = block)

	return function(namespace, "$GENERATED_FUNCTIONS_FOLDER/$name")
}

context(Function)
fun Execute.run(block: Function.() -> Command): Command {
	val function = Function("", "", "", datapack).apply { block() }

	if (function.lines.size == 1) return Function.EMPTY.block().apply {
		arguments.replaceAll {
			when (it) {
				is Argument.Entity -> this@run.targetArg(it)
				else -> it
			}
		}
	}

	val name = "generated_${hashCode()}"
	val namespace = datapack.name

	val finalName = datapack.generatedFunction(name) { block() }
	val usageName = "$GENERATED_FUNCTIONS_FOLDER/$finalName"

	if (finalName == name) comment("Generated function $namespace:$usageName")

	return Function.EMPTY.function(namespace, usageName)
}

fun Function.execute(block: Execute.() -> Command): Command {
	val execute = Execute()
	val run = execute.block()
	return addLine(command("execute", *execute.getArguments(), literal("run"), literal(run.toString())))
}
