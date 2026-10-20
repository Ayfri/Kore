package io.github.ayfri.kore.features.worldgen.placedfeature.modifiers

import io.github.ayfri.kore.features.worldgen.placedfeature.PlacedFeature
import kotlinx.serialization.Serializable

/**
 * Placement modifier keeping the position with a probability of `1 / chance`, and dropping it otherwise.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Placed_feature#rarity_filter
 *
 * @property chance The inverse of the probability, a positive integer: `4` keeps one position out of four.
 */
@Serializable
data class RarityFilter(
	var chance: Int,
) : PlacementModifier()

/**
 * Appends a `rarity_filter` placement modifier keeping one position out of [chance].
 *
 * ```kotlin
 * placedFeature("my_feature", Features.ACACIA) {
 *     rarityFilter(4)
 * }
 * ```
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Placed_feature#rarity_filter
 */
fun PlacedFeature.rarityFilter(chance: Int) {
	placementModifiers += RarityFilter(chance)
}
