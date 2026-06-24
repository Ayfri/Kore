package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

/**
 * Represents the `minecraft:block_state` item component, which sets block state properties (e.g., facing, powered) applied when the item is placed.
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#block_state
 */
@Serializable(with = BlockStateComponent.Companion.BlockStateComponentSerializer::class)
data class BlockStateComponent(var states: Map<String, String>) : Component() {
	companion object {
		data object BlockStateComponentSerializer : InlineAutoSerializer<BlockStateComponent>(BlockStateComponent::class)
	}
}

/** Sets block state properties (e.g., facing, powered) when the item is placed. */
fun ComponentsScope.blockState(states: Map<String, String>) = apply {
	this[ItemComponentTypes.BLOCK_STATE] = BlockStateComponent(states)
}

fun ComponentsScope.blockState(block: MutableMap<String, String>.() -> Unit) = apply {
	this[ItemComponentTypes.BLOCK_STATE] = BlockStateComponent(buildMap(block))
}
