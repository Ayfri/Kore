package io.github.ayfri.kore.data.spawncondition.types

import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

@Serializable(with = VariantCondition.Companion.VariantConditionSerializer::class)
sealed interface VariantCondition {
	companion object {
		data object VariantConditionSerializer : NamespacedPolymorphicSerializer<VariantCondition>(VariantCondition::class)
	}
}
