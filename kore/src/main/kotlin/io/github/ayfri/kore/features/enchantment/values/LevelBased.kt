package io.github.ayfri.kore.features.enchantment.values

import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

@Serializable(with = LevelBased.Companion.LevelBasedSerializer::class)
sealed class LevelBased {
	companion object {
		data object LevelBasedSerializer : NamespacedPolymorphicSerializer<LevelBased>(LevelBased::class)
	}
}
