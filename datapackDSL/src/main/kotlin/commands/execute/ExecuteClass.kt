package commands.execute

import arguments.Argument
import arguments.enums.HeightMap
import arguments.maths.Axes
import arguments.maths.Vec3
import arguments.selector.Sort
import arguments.types.EntityArgument
import arguments.types.literals.*
import arguments.types.resources.EntityTypeArgument
import arguments.types.resources.PredicateArgument
import arguments.types.resources.worldgen.DimensionArgument
import serializers.LowercaseSerializer
import utils.asArg
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
}
