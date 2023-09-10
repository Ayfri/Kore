package io.github.ayfri.kore.features.predicates.conditions

import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

@Serializable(with = PredicateCondition.Companion.PredicateConditionSerializer::class)
sealed class PredicateCondition {
	companion object {
		data object PredicateConditionSerializer : NamespacedPolymorphicSerializer<PredicateCondition>(
			PredicateCondition::class,
			outputName = "condition"
		)
	}
}
