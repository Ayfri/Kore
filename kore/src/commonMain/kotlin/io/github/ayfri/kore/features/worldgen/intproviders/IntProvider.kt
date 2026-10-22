package io.github.ayfri.kore.features.worldgen.intproviders

import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

/**
 * Picks an integer, either a fixed one or a random one drawn from a distribution, used by the counts and the sizes
 * of the configured features, the placed feature modifiers, the processors and the enchantment providers.
 *
 * A [ConstantIntProvider] is inlined to its value, so `constant(5)` serializes as `5` instead of an object with a
 * `type` field, and a bare number is decoded as a constant. Every other type keeps its `type` field.
 *
 * Every builder is an extension on [IntProviderScope], so they only resolve inside a block that actually accepts an
 * int provider, such as `ore("...") { }` or `placedFeature("...", ...) { }`.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Custom_world_generation/int_provider
 */
@GeneratedSealedSerializer
@Serializable(with = IntProvider.Companion.IntProviderSerializer::class)
sealed interface IntProvider {
	companion object {
		data object IntProviderSerializer : NamespacedPolymorphicSerializer<IntProvider>(
			intProviderSealedSerializer(),
			inlinedSubtype = "constant",
			suffix = "IntProvider",
		)
	}
}
