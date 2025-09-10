package io.github.ayfri.kore.features.dialogs.body

import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

@Serializable(with = DialogBody.Companion.DialogBodySerializer::class)
sealed class DialogBody {
	companion object {
		data object DialogBodySerializer : NamespacedPolymorphicSerializer<DialogBody>(DialogBody::class)
	}
}
