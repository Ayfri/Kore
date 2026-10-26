package io.github.ayfri.kore.features.worldgen.placedfeature.modifiers

import kotlinx.serialization.Serializable

/**
 * Placement modifier picking one of [placements] at random for each position and delegating the placement to it.
 *
 * Vanilla uses it in the `desert_well` configured feature, to place the suspicious sand of the well at one of five offsets.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Java_Edition_26.3-snapshot-8
 *
 * @property placements The placement modifiers one of which is picked for each position, at least one is required.
 */
@Serializable
data class RandomlySelected(
	var placements: List<PlacementModifier>,
) : PlacementModifier()

/**
 * Appends a `randomly_selected` placement modifier picking one of the modifiers appended in [block] for each position.
 *
 * The modifiers of [block] are not appended to the enclosing list, only [RandomlySelected] is. [block] has to append at
 * least one modifier, the game rejects an empty list.
 *
 * ```kotlin
 * placedFeature("my_feature", Features.ACACIA) {
 *     randomlySelected {
 *         offset(y = -1)
 *         offset(x = 1, y = -1)
 *     }
 * }
 * ```
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Java_Edition_26.3-snapshot-8
 */
fun PlacementModifiersScope.randomlySelected(block: PlacementModifiersScope.() -> Unit) {
	val placements = buildPlacementModifiers(block)
	require(placements.isNotEmpty()) { "randomlySelected needs at least one placement modifier." }
	placementModifiers += RandomlySelected(placements)
}
