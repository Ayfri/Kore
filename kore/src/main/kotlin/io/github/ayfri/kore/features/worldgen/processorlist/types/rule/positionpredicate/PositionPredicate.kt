package io.github.ayfri.kore.features.worldgen.processorlist.types.rule.positionpredicate

import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@GeneratedSealedSerializer
@Serializable(with = PositionPredicate.Companion.PositionPredicateSerializer::class)
sealed class PositionPredicate {
	companion object {
		@OptIn(InternalSerializationApi::class)
		data object PositionPredicateSerializer : NamespacedPolymorphicSerializer<PositionPredicate>(
			positionPredicateSealedSerializer(),
			"predicate_type"
		)
	}
}
