package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.foliageplacer

import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.Tree
import io.github.ayfri.kore.features.worldgen.intproviders.ConstantIntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import kotlinx.serialization.Serializable

/**
 * Places the leaves of the poplar trees, the 26.3 orange, red and yellow poplars.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 *
 * @property radius Horizontal radius of the leaves, `0` to `16`, `5` to `8` for vanilla poplars.
 * @property offset Vertical offset from the top of the trunk to the foliage, `0` to `16`.
 * @property height Height of the leaves, `5` to `16`, `5` to `6` for vanilla poplars.
 * @property sideHoleChance Chance of a leaf missing on the sides of the foliage, `0.0` to `1.0`, `0.15` in vanilla.
 */
@Serializable
data class PoplarFoliagePlacer(
	override var radius: IntProvider = ConstantIntProvider(0),
	override var offset: IntProvider = ConstantIntProvider(0),
	var height: IntProvider = ConstantIntProvider(5),
	var sideHoleChance: Double = 0.0,
) : FoliagePlacer()

/**
 * Sets [Tree.foliagePlacer] to a [PoplarFoliagePlacer] configured in [block].
 *
 * ```kotlin
 * tree("poplar") {
 *     poplarFoliagePlacer {
 *         radius = constant(6)
 *         height = uniform(5, 6)
 *         sideHoleChance = 0.15
 *     }
 * }
 * ```
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 */
fun Tree.poplarFoliagePlacer(block: PoplarFoliagePlacer.() -> Unit = {}) {
	foliagePlacer = PoplarFoliagePlacer().apply(block)
}
