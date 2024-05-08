package io.github.ayfri.kore.arguments.components.types

import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.components.data.BlockPredicate
import io.github.ayfri.kore.arguments.types.BlockOrTagArgument
import io.github.ayfri.kore.generated.ComponentTypes
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.NbtCompound

@Serializable
data class CanBreakComponent(
	var predicates: List<BlockPredicate>,
	@SerialName("show_in_tooltip")
	var showInTooltip: Boolean? = null,
) : Component()

fun ComponentsScope.canBreak(
	predicates: List<BlockPredicate>,
	showInTooltip: Boolean? = null,
) = apply { this[ComponentTypes.CAN_BREAK] = CanBreakComponent(predicates.toMutableList(), showInTooltip) }

fun ComponentsScope.canBreak(
	vararg predicates: BlockPredicate,
	showInTooltip: Boolean? = null,
) = canBreak(predicates.toList(), showInTooltip)

fun ComponentsScope.canBreak(
	block: BlockOrTagArgument,
	nbt: NbtCompound? = null,
	state: Map<String, String>? = null,
	showInTooltip: Boolean? = null,
) = canBreak(listOf(BlockPredicate(mutableListOf(block), nbt, state?.toMutableMap())), showInTooltip)

fun ComponentsScope.canBreak(block: CanBreakComponent.() -> Unit) =
	CanBreakComponent(mutableListOf()).apply(block).let { this[ComponentTypes.CAN_BREAK] = it }

fun CanBreakComponent.predicate(vararg block: BlockOrTagArgument, nbt: NbtCompound? = null, state: Map<String, String>? = null) =
	apply { predicates += BlockPredicate(mutableListOf(*block), nbt, state?.toMutableMap()) }

fun CanBreakComponent.predicate(predicate: BlockPredicate) = apply { predicates += predicate }
