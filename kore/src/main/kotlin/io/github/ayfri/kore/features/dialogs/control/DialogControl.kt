package io.github.ayfri.kore.features.dialogs.control

import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

@Serializable(with = DialogControl.Companion.DialogControlSerializer::class)
sealed class DialogControl {
	abstract var key: String

	companion object {
		data object DialogControlSerializer : NamespacedPolymorphicSerializer<DialogControl>(DialogControl::class)
	}
}
