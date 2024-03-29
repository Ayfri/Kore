package io.github.ayfri.kore.arguments.components

import io.github.ayfri.kore.arguments.types.resources.BlockArgument
import io.github.ayfri.kore.serializers.InlineSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer

@Serializable(with = DebugStickStateComponent.Companion.DebugStickStateSerializer::class)
data class DebugStickStateComponent(
	var map: MutableMap<BlockArgument, String>,
) : Component() {
	companion object {
		object DebugStickStateSerializer : InlineSerializer<DebugStickStateComponent, Map<BlockArgument, String>>(
			MapSerializer(BlockArgument.serializer(), String.serializer()),
			DebugStickStateComponent::map
		)
	}
}

fun Components.debugStickState(map: Map<BlockArgument, String>) = apply {
	components["debug_stick_state"] = DebugStickStateComponent(map.toMutableMap())
}

fun Components.debugStickState(block: DebugStickStateComponent.() -> Unit) = apply {
	components["debug_stick_state"] = DebugStickStateComponent(mutableMapOf()).apply(block)
}

fun DebugStickStateComponent.state(block: BlockArgument, state: String) {
	map[block] = state
}

operator fun DebugStickStateComponent.set(block: BlockArgument, state: String) {
	map[block] = state
}

operator fun DebugStickStateComponent.get(block: BlockArgument) = map[block]
