package io.github.ayfri.kore.arguments.components.types

import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.types.resources.BlockArgument
import io.github.ayfri.kore.generated.ComponentTypes
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

fun ComponentsScope.debugStickState(map: Map<BlockArgument, String>) = apply {
	this[ComponentTypes.DEBUG_STICK_STATE] = DebugStickStateComponent(map.toMutableMap())
}

fun ComponentsScope.debugStickState(block: DebugStickStateComponent.() -> Unit) = apply {
	this[ComponentTypes.DEBUG_STICK_STATE] = DebugStickStateComponent(mutableMapOf()).apply(block)
}

fun DebugStickStateComponent.state(block: BlockArgument, state: String) {
	map[block] = state
}

operator fun DebugStickStateComponent.set(block: BlockArgument, state: String) {
	map[block] = state
}

operator fun DebugStickStateComponent.get(block: BlockArgument) = map[block]
