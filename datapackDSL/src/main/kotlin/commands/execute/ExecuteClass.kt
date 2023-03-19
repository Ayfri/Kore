package commands.execute

import arguments.*
import arguments.enums.Dimension
import arguments.selector.Sort
import commands.asArg
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
		val values = values()

		object RelationSerializer : LowercaseSerializer<Relation>(values)
	}
}

class Execute {
	private val array = mutableListOf<Argument>()

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

	fun ifCondition(block: ExecuteCondition.() -> Unit) = array.addAll(ExecuteCondition(this, false).apply(block).arguments)
	fun unlessCondition(block: ExecuteCondition.() -> Unit) = array.addAll(ExecuteCondition(this, true).apply(block).arguments)

	fun on(relation: Relation) = array.addAll(literal("on"), literal(relation.asArg()))

	fun positionedOver(heightMap: HeightMap) = array.addAll(literal("positioned"), literal("over"), literal(heightMap.asArg()))

	fun storeResult(block: ExecuteStore.() -> List<Argument>) =
		array.addAll(literal("store"), literal("result"), *ExecuteStore(this).block().toTypedArray())

	fun storeValue(block: ExecuteStore.() -> List<Argument>) =
		array.addAll(literal("store"), literal("value"), *ExecuteStore(this).block().toTypedArray())

	fun summon(entity: Argument.EntitySummon) = array.addAll(literal("summon"), entity)
}
