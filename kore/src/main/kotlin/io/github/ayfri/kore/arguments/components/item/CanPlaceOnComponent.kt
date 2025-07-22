package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.components.data.BlockPredicate
import io.github.ayfri.kore.arguments.types.BlockOrTagArgument
import io.github.ayfri.kore.generated.ItemComponentTypes
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.NbtCompound

@Serializable
data class CanPlaceOnComponent(
	var predicates: List<BlockPredicate>,
	@SerialName("show_in_tooltip")
	var showInTooltip: Boolean? = null,
) : Component()

fun ComponentsScope.canPlaceOn(
	predicates: List<BlockPredicate>,
	showInTooltip: Boolean? = null,
) = apply { this[ItemComponentTypes.CAN_PLACE_ON] = CanPlaceOnComponent(predicates.toMutableList(), showInTooltip) }

fun ComponentsScope.canPlaceOn(
	vararg predicates: BlockPredicate,
	showInTooltip: Boolean? = null,
) = canPlaceOn(predicates.toList(), showInTooltip)

fun ComponentsScope.canPlaceOn(
	block: BlockOrTagArgument,
	nbt: NbtCompound? = null,
	state: Map<String, String>? = null,
	showInTooltip: Boolean? = null,
) = canPlaceOn(listOf(BlockPredicate(mutableListOf(block), nbt, state?.toMutableMap())), showInTooltip)

fun ComponentsScope.canPlaceOn(block: CanPlaceOnComponent.() -> Unit) =
	CanPlaceOnComponent(mutableListOf()).apply(block).let { this[ItemComponentTypes.CAN_PLACE_ON] = it }

fun CanPlaceOnComponent.predicate(vararg block: BlockOrTagArgument, nbt: NbtCompound? = null, state: Map<String, String>? = null) =
	apply { predicates += BlockPredicate(mutableListOf(*block), nbt, state?.toMutableMap()) }

fun CanPlaceOnComponent.predicate(predicate: BlockPredicate) = apply { predicates += predicate }
