package io.github.ayfri.kore.features.worldgen.placedfeature.modifiers

import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProviderScope
import io.github.ayfri.kore.features.worldgen.intproviders.constant
import io.github.ayfri.kore.features.worldgen.placedfeature.PlacedFeature
import kotlinx.serialization.Serializable

/**
 * Placement modifier repeating the position [count] times on every layer of the column with open space above solid
 * ground, such as the surface and each cave floor below it, instead of only once for the whole column.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Placed_feature#count_on_every_layer
 *
 * @property count How many times the position is repeated per layer, `0` to `256`, a constant is written as a bare
 * integer.
 */
@Serializable
data class CountOnEveryLayer(
	var count: IntProvider,
) : PlacementModifier(), IntProviderScope

/**
 * Appends a `count_on_every_layer` placement modifier repeating the position as many times as [count] draws on every
 * layer.
 *
 * ```kotlin
 * placedFeature("my_feature", Features.ACACIA) {
 *     countOnEveryLayer(uniform(1, 3))
 * }
 * ```
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Placed_feature#count_on_every_layer
 */
fun PlacedFeature.countOnEveryLayer(count: IntProvider) {
	placementModifiers += CountOnEveryLayer(count)
}

/**
 * Appends a `count_on_every_layer` placement modifier repeating the position exactly [count] times on every layer.
 *
 * ```kotlin
 * placedFeature("my_feature", Features.ACACIA) {
 *     countOnEveryLayer(4)
 * }
 * ```
 */
fun PlacedFeature.countOnEveryLayer(count: Int) = countOnEveryLayer(constant(count))
