package io.github.ayfri.kore.commands.execute

import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.arguments.enums.HeightMap
import io.github.ayfri.kore.arguments.maths.Axes
import io.github.ayfri.kore.arguments.maths.Vec3
import io.github.ayfri.kore.arguments.selector.Sort
import io.github.ayfri.kore.arguments.types.EntityArgument
import io.github.ayfri.kore.arguments.types.literals.*
import io.github.ayfri.kore.arguments.types.resources.FunctionArgument
import io.github.ayfri.kore.commands.Command
import io.github.ayfri.kore.commands.function
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.emptyFunction
import io.github.ayfri.kore.functions.generatedFunction
import io.github.ayfri.kore.generated.arguments.types.DimensionArgument
import io.github.ayfri.kore.generated.arguments.types.EntityTypeArgument
import io.github.ayfri.kore.generated.arguments.types.PredicateArgument
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

	/** Returns the accumulated execute arguments as an array. */
	fun getArguments() = array.toTypedArray()

	/** Aligns the execution position to the given axes, optionally offsetting the result by [offset] on each aligned axis. */
	fun align(axis: Axes, offset: Int? = null) = array.addAll(literal("align"), axis, int(offset))

	/** Sets the execution anchor (`FEET` or `EYES`), affecting `^ ^ ^` local coordinates and `facing` behavior. */
	fun anchored(anchor: Anchor) = array.addAll(literal("anchored"), literal(anchor.asArg()))

	/** Executes as the given target, changing the `@s` selector and the executor of the command. */
	fun asTarget(target: EntityArgument) = array.addAll(literal("as"), target).also { asArg = target }

	/** Executes at the given target, changing the position, rotation and dimension to the target's. */
	fun at(target: EntityArgument) = array.addAll(literal("at"), targetArg(target))

	/** Rotates the execution to face the given absolute position. */
	fun facing(target: Vec3) = array.addAll(literal("facing"), target)

	/** Rotates the execution to face the given entity, at its [anchor] (`FEET` or `EYES`). */
	fun facingEntity(target: EntityArgument, anchor: Anchor) =
		array.addAll(literal("facing"), literal("entity"), targetArg(target), literal(anchor.asArg()))

	/** Sets the execution dimension to the given one. */
	fun inDimension(dimension: DimensionArgument) = array.addAll(literal("in"), dimension)

	/** Sets the execution dimension to a custom dimension, identified by [customDimension] and [namespace]. */
	fun inDimension(customDimension: String, namespace: String = "minecraft") =
		array.addAll(literal("in"), DimensionArgument(customDimension, namespace))

	/** Sets the execution position to the given absolute position. */
	fun positioned(pos: Vec3) = array.addAll(literal("positioned"), pos)

	/** Sets the execution position to the given target's position. */
	fun positionedAs(target: EntityArgument) = array.addAll(literal("positioned"), literal("as"), targetArg(target))

	/** Sets the execution rotation to the given rotation. */
	fun rotated(rotation: RotationArgument) = array.addAll(literal("rotated"), rotation)

	/** Sets the execution rotation to the given target's rotation. */
	fun rotatedAs(target: EntityArgument) = array.addAll(literal("rotated"), literal("as"), targetArg(target))

	/** Runs the chained command only if the conditions defined in [block] match. */
	fun ifCondition(block: ExecuteCondition.() -> Unit) = array.addAll(ExecuteCondition(this, false).apply(block).arguments)

	/** Runs the chained command only if all the given [predicates] match. */
	fun ifCondition(vararg predicates: PredicateArgument) =
		array.addAll(ExecuteCondition(this, false).apply { predicates.forEach(::predicate) }.arguments)

	/** Runs the chained command only if the conditions defined in [block] do NOT match. */
	fun unlessCondition(block: ExecuteCondition.() -> Unit) = array.addAll(ExecuteCondition(this, true).apply(block).arguments)

	/** Runs the chained command only if none of the given [predicates] match. */
	fun unlessCondition(vararg predicates: PredicateArgument) =
		array.addAll(ExecuteCondition(this, true).apply { predicates.forEach(::predicate) }.arguments)

	/** Executes the command as/at an entity derived from the current executor via the given [relation] (e.g. `OWNER`, `VEHICLE`, `TARGET`). */
	fun on(relation: Relation) = array.addAll(literal("on"), literal(relation.asArg()))

	/** Sets the execution Y position to the top block matching the given heightmap at the current X/Z. */
	fun positionedOver(heightMap: HeightMap) = array.addAll(literal("positioned"), literal("over"), literal(heightMap.asArg()))

	/** Stores the return value of the chained command into the destination defined in [block]. */
	fun storeResult(block: ExecuteStore.() -> List<Argument>) =
		array.addAll(literal("store"), literal("result"), *ExecuteStore(this).block().toTypedArray())

	/** Stores the success value (0 or 1) of the chained command into the destination defined in [block]. */
	fun storeSuccess(block: ExecuteStore.() -> List<Argument>) =
		array.addAll(literal("store"), literal("success"), *ExecuteStore(this).block().toTypedArray())

	/** Summons a new entity of the given type and executes the chained command as/at it. */
	fun summon(entity: EntityTypeArgument) = array.addAll(literal("summon"), entity)

	/**
	 * Runs the given [block] as the final command of the execute chain.
	 *
	 * If the block produces a single inlinable command, it is inlined directly; otherwise a generated function is created
	 * and invoked. Entity arguments in the block are rewritten through [targetArg] to keep `@s` semantics consistent.
	 */
	context(fn:Function)
	fun run(block: Function.() -> Command): FunctionArgument {
		val function = Function("", "", datapack = fn.datapack).apply { block() }

		if (function.isInlinable) return emptyFunction(fn.datapack) {
			block().apply {
				arguments.replaceAll {
					when (it) {
						is EntityArgument -> targetArg(it)
						else -> it
					}
				}
			}
			run = this
			lines.removeAll { it.startsWith("#") || it.isBlank() || it.isEmpty() }
		}

		val name = "generated_${this.hashCode()}"
		val generatedFunction = fn.datapack.generatedFunction(name) { block() }
		if (generatedFunction.name == name && fn.datapack.configuration.generateCommentOfGeneratedFunctionCall) fn.comment("Generated function ${fn.asString()}")
		run = generatedFunction

		return generatedFunction
	}

	/** Runs a newly generated function with the given [name], [namespace] and [directory] as the final command of the execute chain. */
	context(fn: Function)
	fun run(name: String, namespace: String = fn.datapack.name, directory: String = "", block: Function.() -> Unit) =
		fn.datapack.generatedFunction(name, namespace, directory, block)

	/** Runs the given existing [function] as the final command of the execute chain. */
	fun run(function: FunctionArgument) {
		run = emptyFunction { function(function) }
	}
}
