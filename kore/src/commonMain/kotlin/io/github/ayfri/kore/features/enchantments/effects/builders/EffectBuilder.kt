package io.github.ayfri.kore.features.enchantments.effects.builders

import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

/**
 * The value of one effect component of an enchantment, shaped after what that component expects: a list of
 * conditional effects, a list of sounds, or nothing at all.
 *
 * Serialized without a `type` field, since the component id already says which shape to read. The sentinel
 * [outputName][io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer] keeps the `type` a subtype writes
 * itself, such as the effect held by a [SingleValueEffectBuilder].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#Effect_components
 */
@GeneratedSealedSerializer
@Serializable(with = EffectBuilder.Companion.EffectBuilderSerializer::class)
sealed class EffectBuilder {
	companion object {
		@OptIn(InternalSerializationApi::class)
		data object EffectBuilderSerializer : NamespacedPolymorphicSerializer<EffectBuilder>(
			effectBuilderSealedSerializer(),
			outputName = "__type__",
			skipOutputName = true,
		)
	}
}
