package io.github.ayfri.kore.features.predicates.conditions

import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@GeneratedSealedSerializer
@Serializable(with = PredicateCondition.Companion.PredicateConditionSerializer::class)
sealed class PredicateCondition {
	companion object {
		@OptIn(InternalSerializationApi::class)
		data object PredicateConditionSerializer : NamespacedPolymorphicSerializer<PredicateCondition>(
			predicateConditionSealedSerializer(),
			outputName = "condition"
		)
	}
}
