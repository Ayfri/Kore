package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.types.resources.BlockArgument
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

@Serializable(with = DebugStickStateComponent.Companion.DebugStickStateSerializer::class)
data class DebugStickStateComponent(var map: MutableMap<BlockArgument, String>) : Component() {
	companion object {
		data object DebugStickStateSerializer : InlineAutoSerializer<DebugStickStateComponent>(
			DebugStickStateComponent::class
		)
	}
}

fun ComponentsScope.debugStickState(map: Map<BlockArgument, String>) = apply {
	this[ItemComponentTypes.DEBUG_STICK_STATE] = DebugStickStateComponent(map.toMutableMap())
}

fun ComponentsScope.debugStickState(block: DebugStickStateComponent.() -> Unit) = apply {
	this[ItemComponentTypes.DEBUG_STICK_STATE] = DebugStickStateComponent(mutableMapOf()).apply(block)
}

fun DebugStickStateComponent.state(block: BlockArgument, state: String) {
	map[block] = state
}

operator fun DebugStickStateComponent.set(block: BlockArgument, state: String) {
	map[block] = state
}

operator fun DebugStickStateComponent.get(block: BlockArgument) = map[block]
