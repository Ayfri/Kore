package io.github.ayfri.kore.features.worldgen.processorlist.types.rule.positionpredicate

import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

/**
 * A test on the position of a block inside the structure piece, used by
 * [io.github.ayfri.kore.features.worldgen.processorlist.types.rule.ProcessorRule.positionPredicate].
 *
 * Every builder is an extension on [io.github.ayfri.kore.features.worldgen.processorlist.types.rule.ProcessorRule],
 * so they only resolve inside a `rule { }` block.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Processor_list
 */
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
