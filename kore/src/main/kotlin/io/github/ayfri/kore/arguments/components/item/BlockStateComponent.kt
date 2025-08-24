package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

@Serializable(with = BlockStateComponent.Companion.BlockStateComponentSerializer::class)
data class BlockStateComponent(var states: Map<String, String>) : Component() {
	companion object {
		data object BlockStateComponentSerializer : InlineAutoSerializer<BlockStateComponent>(BlockStateComponent::class)
	}
}

fun ComponentsScope.blockState(states: Map<String, String>) = apply {
	this[ItemComponentTypes.BLOCK_STATE] = BlockStateComponent(states)
}

fun ComponentsScope.blockState(block: MutableMap<String, String>.() -> Unit) = apply {
	this[ItemComponentTypes.BLOCK_STATE] = BlockStateComponent(buildMap(block))
}
