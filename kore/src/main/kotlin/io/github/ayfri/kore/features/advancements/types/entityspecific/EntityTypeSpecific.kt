package io.github.ayfri.kore.features.advancements.types.entityspecific

import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

@Serializable(with = EntityTypeSpecific.Companion.EntityTypeSpecificSerializer::class)
sealed class EntityTypeSpecific {
	companion object {
		data object EntityTypeSpecificSerializer : NamespacedPolymorphicSerializer<EntityTypeSpecific>(EntityTypeSpecific::class)
	}
}
