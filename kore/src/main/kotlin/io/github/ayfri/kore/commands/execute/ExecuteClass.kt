package io.github.ayfri.kore.commands.execute

import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.arguments.enums.HeightMap
import io.github.ayfri.kore.arguments.maths.Axes
import io.github.ayfri.kore.arguments.maths.Vec3
import io.github.ayfri.kore.arguments.selector.Sort
import io.github.ayfri.kore.arguments.types.EntityArgument
import io.github.ayfri.kore.arguments.types.literals.*
import io.github.ayfri.kore.arguments.types.resources.EntityTypeArgument
import io.github.ayfri.kore.arguments.types.resources.FunctionArgument
import io.github.ayfri.kore.arguments.types.resources.PredicateArgument
import io.github.ayfri.kore.arguments.types.resources.worldgen.DimensionArgument
import io.github.ayfri.kore.commands.Command
import io.github.ayfri.kore.commands.function
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.emptyFunction
import io.github.ayfri.kore.functions.generatedFunction
import io.github.ayfri.kore.serializers.LowercaseSerializer
import io.github.ayfri.kore.utils.asArg
import kotlinx.serialization.Serializable

@Serializable(Anchor.Companion.AnchorSerializer::class)
enum class Anchor {
	FEET,
	EYES;

	companion object {
		data object AnchorSerializer : LowercaseSerializer<Anchor>(entries)
	}
}

@Serializable(Relation.Companion.RelationSerializer::class)
enum class Relation {
	ATTACKER,
	CONTROLLER,
	LEASHER,
	OWNER,
	PASSENGERS,
	TARGET,
	VEHICLE;

	companion object {
		data object RelationSerializer : LowercaseSerializer<Relation>(entries)
	}
}

class Execute {
	private val array = mutableListOf<Argument>()
	internal var run: FunctionArgument = emptyFunction()

	private fun <T> MutableList<T>.addAll(vararg args: T?) = addAll(args.filterNotNull())

	private var asArg: EntityArgument? = null

	internal fun targetArg(arg: Argument) = when {
		asArg is UUIDArgument || (asArg as? SelectorArgument)?.selector?.let {
			val nbtData = it.nbtData
			val otherSel = (arg as? SelectorArgument)?.selector
			val otherNbtData = otherSel?.nbtData
			nbtData.limit == 1 && nbtData.sort != Sort.RANDOM && nbtData == otherNbtData
		} == true -> self()

		asArg == arg -> self()
		else -> arg
	}

	fun getArguments() = array.toTypedArray()

	fun align(axis: Axes, offset: Int? = null) = array.addAll(literal("align"), axis, int(offset))
	fun anchored(anchor: Anchor) = array.addAll(literal("anchored"), literal(anchor.asArg()))
	fun asTarget(target: EntityArgument) = array.addAll(literal("as"), target).also { asArg = target }
	fun at(target: EntityArgument) = array.addAll(literal("at"), targetArg(target))
	fun facing(target: Vec3) = array.addAll(literal("facing"), target)
	fun facingEntity(target: EntityArgument, anchor: Anchor) =
		array.addAll(literal("facing"), literal("entity"), targetArg(target), literal(anchor.asArg()))

	fun inDimension(dimension: DimensionArgument) = array.addAll(literal("in"), dimension)
	fun inDimension(customDimension: String, namespace: String = "minecraft") =
		array.addAll(literal("in"), DimensionArgument(customDimension, namespace))

	fun positioned(pos: Vec3) = array.addAll(literal("positioned"), pos)
	fun positionedAs(target: EntityArgument) = array.addAll(literal("positioned"), literal("as"), targetArg(target))
	fun rotated(rotation: RotationArgument) = array.addAll(literal("rotated"), rotation)
	fun rotatedAs(target: EntityArgument) = array.addAll(literal("rotated"), literal("as"), targetArg(target))

	fun ifCondition(block: ExecuteCondition.() -> Unit) = array.addAll(ExecuteCondition(this, false).apply(block).arguments)
	fun ifCondition(vararg predicates: PredicateArgument) =
		array.addAll(ExecuteCondition(this, false).apply { predicates.forEach(::predicate) }.arguments)

	fun unlessCondition(block: ExecuteCondition.() -> Unit) = array.addAll(ExecuteCondition(this, true).apply(block).arguments)
	fun unlessCondition(vararg predicates: PredicateArgument) =
		array.addAll(ExecuteCondition(this, true).apply { predicates.forEach(::predicate) }.arguments)

	fun on(relation: Relation) = array.addAll(literal("on"), literal(relation.asArg()))

	fun positionedOver(heightMap: HeightMap) = array.addAll(literal("positioned"), literal("over"), literal(heightMap.asArg()))

	fun storeResult(block: ExecuteStore.() -> List<Argument>) =
		array.addAll(literal("store"), literal("result"), *ExecuteStore(this).block().toTypedArray())

	fun storeSuccess(block: ExecuteStore.() -> List<Argument>) =
		array.addAll(literal("store"), literal("success"), *ExecuteStore(this).block().toTypedArray())

	fun summon(entity: EntityTypeArgument) = array.addAll(literal("summon"), entity)

	context(Function)
	fun run(block: Function.() -> Command): FunctionArgument {
		val function = Function("", "", "", datapack).apply { block() }
		val nonCommentedLines = function.lines.filter { !it.startsWith("#") }

		if (nonCommentedLines.size == 1) return emptyFunction(datapack) {
			block().apply {
				arguments.replaceAll {
					when (it) {
						is EntityArgument -> targetArg(it)
						else -> it
					}
				}
			}
			run = this
			lines.removeAll { it.startsWith("#") }
		}

		val name = "generated_${hashCode()}"
		val generatedFunction = datapack.generatedFunction(name) { block() }
		if (generatedFunction.name == name && datapack.configuration.generateCommentOfGeneratedFunctions) comment("Generated function ${asString()}")
		run = generatedFunction

		return generatedFunction
	}

	context(Function)
	fun run(name: String, namespace: String = datapack.name, directory: String = "", block: Function.() -> Unit) =
		datapack.generatedFunction(name, namespace, directory, block)

	fun run(function: FunctionArgument) {
		run = emptyFunction { function(function) }
	}
}
