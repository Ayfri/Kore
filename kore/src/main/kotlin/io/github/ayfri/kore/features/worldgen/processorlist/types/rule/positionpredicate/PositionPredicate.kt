package io.github.ayfri.kore.features.worldgen.processorlist.types.rule.positionpredicate

import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

@Serializable(with = PositionPredicate.Companion.PositionPredicateSerializer::class)
sealed class PositionPredicate {
	companion object {
		data object PositionPredicateSerializer : NamespacedPolymorphicSerializer<PositionPredicate>(
			PositionPredicate::class,
			"predicate_type"
		)
	}
}
