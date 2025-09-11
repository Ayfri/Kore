package io.github.ayfri.kore.arguments.actions

import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

@Serializable(with = Action.Companion.ActionSerializer::class)
sealed class Action {
	companion object {
		data object ActionSerializer : NamespacedPolymorphicSerializer<Action>(Action::class)
	}
}

sealed class ActionWrapper {
	abstract var action: Action?
}
