package io.github.ayfri.kore.features.worldgen.heightproviders

import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

/**
 * Picks a Y level, either a fixed one or a random one drawn from a distribution, used by the carvers, the
 * `height_range` placement modifier and the `jigsaw` / `nether_fossil` structures.
 *
 * A [ConstantHeightProvider] is inlined to its [vertical anchor][io.github.ayfri.kore.features.worldgen.verticalanchors.VerticalAnchor],
 * so `constantAbsolute(64)` serializes as `{ "absolute": 64 }` instead of an object with a `type` field, and an object
 * without a `type` is decoded as a constant, so it must be a vertical anchor. Every other type keeps its `type` field.
 *
 * Every builder is an extension on [HeightProviderScope], so they only resolve inside a block that actually accepts
 * a height provider, such as `cave("...") { }` or `placedFeature("...", ...) { }`.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Custom_world_generation/height_provider
 */
@GeneratedSealedSerializer
@Serializable(with = HeightProvider.Companion.HeightProviderSerializer::class)
sealed interface HeightProvider {
	companion object {
		data object HeightProviderSerializer : NamespacedPolymorphicSerializer<HeightProvider>(
			heightProviderSealedSerializer(),
			inlinedSubtype = "constant",
			suffix = "HeightProvider",
		)
	}
}
