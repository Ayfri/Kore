package io.github.ayfri.kore.features.worldgen.floatproviders

import io.github.ayfri.kore.serializers.ProviderSerializer
import kotlinx.serialization.Serializable

/**
 * Picks a float, either a fixed one or a random one drawn from a distribution, used by the radii and the scales of
 * the carvers and the configured features, and by the enchantment effects.
 *
 * A [ConstantFloatProvider] is inlined to its value, so `constant(1.5f)` serializes as `1.5` instead of an object
 * with a `type` field. Every other type keeps its `type` field.
 *
 * Every builder is an extension on [FloatProviderScope], so they only resolve inside a block that actually accepts a
 * float provider, such as `cave("...") { }` or `largeDripstone("...") { }`.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Custom_world_generation/float_provider
 */
typealias FloatProvider = @Serializable(with = FloatProviderSurrogate.Companion.FloatProviderSerializer::class) FloatProviderSurrogate

/** Surrogate sealed interface backing [FloatProvider], only needed because a typealias cannot carry a serializer on its own. */
@Serializable
sealed interface FloatProviderSurrogate {
	companion object {
		data object FloatProviderSerializer : ProviderSerializer<FloatProviderSurrogate>(serializer())
	}
}
