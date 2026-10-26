package io.github.ayfri.kore.features.worldgen.carver

import io.github.ayfri.kore.features.worldgen.floatproviders.FloatProviderScope
import io.github.ayfri.kore.features.worldgen.heightproviders.ConstantHeightProvider
import io.github.ayfri.kore.features.worldgen.heightproviders.HeightProvider
import io.github.ayfri.kore.features.worldgen.heightproviders.HeightProviderScope
import io.github.ayfri.kore.features.worldgen.intproviders.IntProviderScope
import io.github.ayfri.kore.features.worldgen.verticalanchors.VerticalAnchor
import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

/**
 * The configuration of a [Carver], serialized as `{ "type": "minecraft:<carver>", ...fields }`.
 *
 * Every carver type shares the properties declared here and adds its own on top: see [Cave] and [Canyon].
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/carvers
 * Minecraft Wiki: https://minecraft.wiki/w/Carver_definition
 *
 * @property probability Chance for each chunk to attempt a carve, between `0` and `1`.
 * @property y The height at which the carve starts.
 */
@GeneratedSealedSerializer
@Serializable(with = Config.Companion.ConfigSerializer::class)
sealed class Config : FloatProviderScope, HeightProviderScope, IntProviderScope {
	abstract var probability: Double
	abstract var y: HeightProvider

	companion object {
		data object ConfigSerializer : NamespacedPolymorphicSerializer<Config>(configSealedSerializer())
	}
}

/**
 * Sets [Config.y] to [value], the height at which the carve starts.
 *
 * ```kotlin
 * y(uniformHeightProvider(aboveBottom(8), absolute(180)))
 * ```
 */
fun Config.y(value: HeightProvider) {
	y = value
}

/**
 * Sets [Config.y] to the constant [value], written as the bare anchor.
 *
 * ```kotlin
 * y(aboveBottom(8))  // "y": {"above_bottom": 8}
 * ```
 */
fun Config.y(value: VerticalAnchor) = y(ConstantHeightProvider(value))
