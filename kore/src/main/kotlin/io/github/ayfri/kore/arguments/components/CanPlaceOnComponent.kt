package io.github.ayfri.kore.arguments.components

import io.github.ayfri.kore.arguments.components.data.BlockPredicate
import io.github.ayfri.kore.arguments.types.BlockOrTagArgument
import net.benwoodworth.knbt.NbtCompound
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CanPlaceOnComponent(
	var predicates: List<BlockPredicate>,
	@SerialName("show_in_tooltip")
	var showInTooltip: Boolean? = null,
) : Component()

fun Components.canPlaceOn(
	predicates: List<BlockPredicate>,
	showInTooltip: Boolean? = null,
) = apply { components["can_place_on"] = CanPlaceOnComponent(predicates.toMutableList(), showInTooltip) }

fun Components.canPlaceOn(
	vararg predicates: BlockPredicate,
	showInTooltip: Boolean? = null,
) = canPlaceOn(predicates.toList(), showInTooltip)

fun Components.canPlaceOn(
	block: BlockOrTagArgument,
	nbt: NbtCompound? = null,
	state: Map<String, String>? = null,
	showInTooltip: Boolean? = null,
) = canPlaceOn(listOf(BlockPredicate(mutableListOf(block), nbt, state?.toMutableMap())), showInTooltip)

fun Components.canPlaceOn(block: CanPlaceOnComponent.() -> Unit) =
	CanPlaceOnComponent(mutableListOf()).apply(block).let { components["can_place_on"] = it }

fun CanPlaceOnComponent.predicate(vararg block: BlockOrTagArgument, nbt: NbtCompound? = null, state: Map<String, String>? = null) =
	apply { predicates += BlockPredicate(mutableListOf(*block), nbt, state?.toMutableMap()) }

fun CanPlaceOnComponent.predicate(predicate: BlockPredicate) = apply { predicates += predicate }
