package io.github.ayfri.kore.features.worldgen.intproviders

import io.github.ayfri.kore.serializers.ProviderSerializer
import kotlinx.serialization.Serializable

/**
 * Picks an integer, either a fixed one or a random one drawn from a distribution, used by the counts and the sizes
 * of the configured features, the placed feature modifiers, the processors and the enchantment providers.
 *
 * A [ConstantIntProvider] is inlined to its value, so `constant(5)` serializes as `5` instead of an object with a
 * `type` field. Every other type keeps its `type` field.
 *
 * Every builder is an extension on [IntProviderScope], so they only resolve inside a block that actually accepts an
 * int provider, such as `ore("...") { }` or `placedFeature("...", ...) { }`.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Custom_world_generation/int_provider
 */
typealias IntProvider = @Serializable(IntProviderSurrogate.Companion.IntProviderSerializer::class) IntProviderSurrogate

/** Surrogate sealed interface backing [IntProvider], only needed because a typealias cannot carry a serializer on its own. */
@Serializable
sealed interface IntProviderSurrogate {
	companion object {
		data object IntProviderSerializer : ProviderSerializer<IntProviderSurrogate>(serializer())
	}
}
