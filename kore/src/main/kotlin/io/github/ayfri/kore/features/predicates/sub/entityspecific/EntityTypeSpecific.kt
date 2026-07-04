package io.github.ayfri.kore.features.predicates.sub.entityspecific

import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@GeneratedSealedSerializer
@Serializable(with = EntityTypeSpecific.Companion.EntityTypeSpecificSerializer::class)
sealed class EntityTypeSpecific {
	companion object {
		@OptIn(InternalSerializationApi::class)
		data object EntityTypeSpecificSerializer :
			NamespacedPolymorphicSerializer<EntityTypeSpecific>(entityTypeSpecificSealedSerializer())
	}
}
