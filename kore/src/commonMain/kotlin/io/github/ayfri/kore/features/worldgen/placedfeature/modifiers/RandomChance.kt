package io.github.ayfri.kore.features.worldgen.placedfeature.modifiers

import kotlinx.serialization.Serializable

/**
 * Placement modifier keeping the position with a probability of [chance], and dropping it otherwise.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Java_Edition_26.3-snapshot-3
 *
 * @property chance The probability of keeping the position, `0` to `1`: `0.25` keeps one position out of four.
 */
@Serializable
data class RandomChance(
	var chance: Float,
) : PlacementModifier()

/**
 * Appends a `random_chance` placement modifier keeping the position with a probability of [chance].
 *
 * ```kotlin
 * placedFeature("my_feature", Features.ACACIA) {
 *     randomChance(0.25f)
 * }
 * ```
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Java_Edition_26.3-snapshot-3
 */
fun PlacementModifiersScope.randomChance(chance: Float) {
	placementModifiers += RandomChance(chance)
}
