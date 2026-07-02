package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.components.data.BlockPredicate
import io.github.ayfri.kore.arguments.types.BlockOrTagArgument
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer
import net.benwoodworth.knbt.NbtCompound

/**
 * Represents the `minecraft:can_break` item component, which restricts which blocks this item can break in Adventure mode.
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#can_break
 */
@Serializable(with = CanBreakComponent.Companion.CanBreakComponentSerializer::class)
data class CanBreakComponent(var predicates: List<BlockPredicate>) : Component() {
	companion object {
		data object CanBreakComponentSerializer : InlineAutoSerializer<CanBreakComponent, List<BlockPredicate>>(
			serializer<List<BlockPredicate>>(),
			CanBreakComponent::predicates,
			::CanBreakComponent
		)
	}
}

/** Restricts which blocks this item can break in Adventure mode. */
fun ComponentsScope.canBreak(predicates: List<BlockPredicate>) = apply {
	this[ItemComponentTypes.CAN_BREAK] = CanBreakComponent(predicates.toMutableList())
}

fun ComponentsScope.canBreak(vararg predicates: BlockPredicate) = canBreak(predicates.toList())

fun ComponentsScope.canBreak(
	block: BlockOrTagArgument,
	nbt: NbtCompound? = null,
	state: Map<String, String>? = null,
) = canBreak(listOf(BlockPredicate(mutableListOf(block), nbt, state?.toMutableMap())))

fun ComponentsScope.canBreak(block: CanBreakComponent.() -> Unit) =
	CanBreakComponent(mutableListOf()).apply(block).let { this[ItemComponentTypes.CAN_BREAK] = it }

fun CanBreakComponent.predicate(vararg block: BlockOrTagArgument, nbt: NbtCompound? = null, state: Map<String, String>? = null) =
	apply { predicates += BlockPredicate(mutableListOf(*block), nbt, state?.toMutableMap()) }

fun CanBreakComponent.predicate(predicate: BlockPredicate) = apply { predicates += predicate }
