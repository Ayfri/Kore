package io.github.ayfri.kore.arguments.components

import io.github.ayfri.kore.generated.ComponentTypes
import io.github.ayfri.kore.serializers.InlineSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer

@Serializable(with = BlockStateComponent.Companion.BlockStateComponentSerializer::class)
data class BlockStateComponent(var states: Map<String, String>) : Component() {
	companion object {
		object BlockStateComponentSerializer : InlineSerializer<BlockStateComponent, Map<String, String>>(
			MapSerializer(String.serializer(), String.serializer()),
			BlockStateComponent::states
		)
	}
}

fun Components.blockState(states: Map<String, String>) = apply {
	this[ComponentTypes.BLOCK_STATE] = BlockStateComponent(states)
}

fun Components.blockState(block: MutableMap<String, String>.() -> Unit) = apply {
	this[ComponentTypes.BLOCK_STATE] = BlockStateComponent(buildMap(block))
}
