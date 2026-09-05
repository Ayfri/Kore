package io.github.ayfri.kore.features.enchantments.values

import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

/**
 * A number computed from the level of the enchantment it belongs to.
 *
 * Every value is a float, so fractional results like a `3.5` explosion radius are expressible. A [Constant] is
 * serialized as the bare number, every other variant as an object carrying its `type`.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#Level-based_values
 */
@GeneratedSealedSerializer
@Serializable(with = LevelBased.Companion.LevelBasedSerializer::class)
sealed class LevelBased {
	companion object : LevelBasedScope {
		@OptIn(InternalSerializationApi::class)
		data object LevelBasedSerializer : NamespacedPolymorphicSerializer<LevelBased>(levelBasedSealedSerializer())
	}
}
