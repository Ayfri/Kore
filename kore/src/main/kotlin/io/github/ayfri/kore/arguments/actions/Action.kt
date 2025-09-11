package io.github.ayfri.kore.arguments.actions

import io.github.ayfri.kore.serializers.InlineAutoSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

@Serializable(with = Action.Companion.ActionSerializer::class)
sealed class Action {
	companion object {
		data object ActionSerializer : NamespacedPolymorphicSerializer<Action>(
			kClass = Action::class,
			outputName = "type",
			useMinecraftPrefix = true
		)
	}
}

sealed interface ActionWrapper<out T : Action> {
	var action: @UnsafeVariance T?
}

@Serializable(with = ActionContainer.Companion.ActionContainerSerializer::class)
data class ActionContainer(override var action: Action? = null) : ActionWrapper<Action> {
	companion object {
		data object ActionContainerSerializer : InlineAutoSerializer<ActionContainer>(ActionContainer::class)
	}
}
