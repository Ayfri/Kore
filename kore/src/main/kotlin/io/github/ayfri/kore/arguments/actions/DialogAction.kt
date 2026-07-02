package io.github.ayfri.kore.arguments.actions

import io.github.ayfri.kore.serializers.InlineAutoSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

/**
 * Sealed interface for actions available inside dialog definitions.
 * Concrete types: [OpenUrl], [RunCommand], [SuggestCommand], [CopyToClipboard], [ChangePage], [ShowDialog], [Custom], [DynamicCustom], [DynamicRunCommand].
 */
@Serializable(with = DialogAction.Companion.DialogActionSerializer::class)
sealed interface DialogAction : ActionType {
	companion object {
		data object DialogActionSerializer : NamespacedPolymorphicSerializer<DialogAction>(DialogAction::class)
	}
}

/**
 * Mutable wrapper that accumulates a [DialogAction] from the dialog action builder DSL.
 * Access the built action via [action] after calling one of the builder extensions.
 */
@Serializable(with = DialogActionContainer.Companion.DialogActionContainerSerializer::class)
data class DialogActionContainer(override var action: DialogAction? = null) : ActionWrapper<DialogAction>() {
	companion object {
		data object DialogActionContainerSerializer : InlineAutoSerializer<DialogActionContainer, DialogAction?>(
			serializer<DialogAction?>(),
			DialogActionContainer::action,
			::DialogActionContainer
		)
	}
}
