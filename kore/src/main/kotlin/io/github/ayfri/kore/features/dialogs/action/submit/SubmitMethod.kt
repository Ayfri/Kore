package io.github.ayfri.kore.features.dialogs.action.submit

import io.github.ayfri.kore.serializers.InlineAutoSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

@Serializable(with = SubmitMethod.Companion.SubmitMethodSerializer::class)
sealed class SubmitMethod {
	companion object {
		data object SubmitMethodSerializer : NamespacedPolymorphicSerializer<SubmitMethod>(SubmitMethod::class)
	}
}

@Serializable(with = SubmitMethodContainer.Companion.SubmitMethodContainerSerializer::class)
data class SubmitMethodContainer(var method: SubmitMethod? = null) {
	companion object {
		data object SubmitMethodContainerSerializer : InlineAutoSerializer<SubmitMethodContainer>(SubmitMethodContainer::class)
	}
}
