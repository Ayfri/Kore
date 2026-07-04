package io.github.ayfri.kore.data.spawncondition.types

import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@GeneratedSealedSerializer
@Serializable(with = VariantCondition.Companion.VariantConditionSerializer::class)
sealed interface VariantCondition {
	companion object {
		@OptIn(InternalSerializationApi::class)
		data object VariantConditionSerializer :
			NamespacedPolymorphicSerializer<VariantCondition>(variantConditionSealedSerializer())
	}
}
