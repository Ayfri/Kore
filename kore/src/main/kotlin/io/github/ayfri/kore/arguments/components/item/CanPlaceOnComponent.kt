package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.components.data.BlockPredicate
import io.github.ayfri.kore.arguments.types.BlockOrTagArgument
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.NbtCompound

@Serializable(with = CanPlaceOnComponent.Companion.CanPlaceOnComponentSerializer::class)
data class CanPlaceOnComponent(var predicates: List<BlockPredicate>) : Component() {
	companion object {
		data object CanPlaceOnComponentSerializer : InlineAutoSerializer<CanPlaceOnComponent>(CanPlaceOnComponent::class)
	}
}

fun ComponentsScope.canPlaceOn(predicates: List<BlockPredicate>) = apply {
	this[ItemComponentTypes.CAN_PLACE_ON] = CanPlaceOnComponent(predicates.toMutableList())
}

fun ComponentsScope.canPlaceOn(vararg predicates: BlockPredicate) = canPlaceOn(predicates.toList())

fun ComponentsScope.canPlaceOn(
	block: BlockOrTagArgument,
	nbt: NbtCompound? = null,
	state: Map<String, String>? = null,
) = canPlaceOn(listOf(BlockPredicate(mutableListOf(block), nbt, state?.toMutableMap())))

fun ComponentsScope.canPlaceOn(block: CanPlaceOnComponent.() -> Unit) =
	CanPlaceOnComponent(mutableListOf()).apply(block).let { this[ItemComponentTypes.CAN_PLACE_ON] = it }

fun CanPlaceOnComponent.predicate(vararg block: BlockOrTagArgument, nbt: NbtCompound? = null, state: Map<String, String>? = null) =
	apply { predicates += BlockPredicate(mutableListOf(*block), nbt, state?.toMutableMap()) }

fun CanPlaceOnComponent.predicate(predicate: BlockPredicate) = apply { predicates += predicate }
