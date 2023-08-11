package features.predicates.conditions

import serializers.NamespacedPolymorphicSerializer
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
