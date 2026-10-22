package io.github.ayfri.kore.features.worldgen.placedfeature.modifiers

import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProviderScope
import io.github.ayfri.kore.features.worldgen.intproviders.constant
import kotlinx.serialization.Serializable

/**
 * Placement modifier repeating the position [count] times, so the feature is attempted that many times per chunk.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Placed_feature#count
 *
 * @property count How many times the position is repeated, `0` to `4096`, a constant is written as a bare integer.
 */
@Serializable
data class Count(
	var count: IntProvider,
) : PlacementModifier(), IntProviderScope

/**
 * Appends a `count` placement modifier repeating the position as many times as [count] draws.
 *
 * ```kotlin
 * placedFeature("my_feature", Features.ACACIA) {
 *     count(uniform(1, 5))
 * }
 * ```
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Placed_feature#count
 */
fun PlacementModifiersScope.count(count: IntProvider) {
	placementModifiers += Count(count)
}

/**
 * Appends a `count` placement modifier repeating the position exactly [count] times.
 *
 * ```kotlin
 * placedFeature("my_feature", Features.ACACIA) {
 *     count(10)
 * }
 * ```
 */
fun PlacementModifiersScope.count(count: Int) = count(constant(count))
