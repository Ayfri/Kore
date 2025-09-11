package io.github.ayfri.kore.arguments.actions

import io.github.ayfri.kore.serializers.InlineAutoSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

@Serializable(with = DialogAction.Companion.DialogActionSerializer::class)
sealed class DialogAction : Action() {
	companion object {
		data object DialogActionSerializer : NamespacedPolymorphicSerializer<DialogAction>(DialogAction::class)
	}
}

@Serializable(with = DialogActionContainer.Companion.DialogActionContainerSerializer::class)
data class DialogActionContainer(override var action: Action? = null) : ActionWrapper() {
	companion object {
		data object DialogActionContainerSerializer : InlineAutoSerializer<DialogActionContainer>(DialogActionContainer::class)
	}
}
