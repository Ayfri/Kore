package io.github.ayfri.kore.features.enchantments.values

import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@GeneratedSealedSerializer
@Serializable(with = LevelBased.Companion.LevelBasedSerializer::class)
sealed class LevelBased {
	companion object {
		@OptIn(InternalSerializationApi::class)
		data object LevelBasedSerializer : NamespacedPolymorphicSerializer<LevelBased>(levelBasedSealedSerializer())
	}
}
