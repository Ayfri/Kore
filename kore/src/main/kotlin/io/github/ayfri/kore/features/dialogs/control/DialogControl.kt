package io.github.ayfri.kore.features.dialogs.control

import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@GeneratedSealedSerializer
@Serializable(with = DialogControl.Companion.DialogControlSerializer::class)
sealed class DialogControl {
	abstract var key: String

	companion object {
		@OptIn(InternalSerializationApi::class)
		data object DialogControlSerializer :
			NamespacedPolymorphicSerializer<DialogControl>(dialogControlSealedSerializer())
	}
}
