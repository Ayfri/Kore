package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.foliageplacer

import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.Tree
import io.github.ayfri.kore.features.worldgen.intproviders.ConstantIntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import kotlinx.serialization.Serializable

/**
 * Places the wide, hanging leaf canopy of the cherry trees.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 *
 * @property radius Horizontal radius of the leaves, `0` to `16`, `4` for vanilla cherry trees.
 * @property offset Vertical offset from the top of the trunk to the foliage, `0` to `16`.
 * @property height Height of the canopy, `4` to `16`, `5` for vanilla cherry trees.
 * @property wideBottomLayerHoleChance Chance of a leaf missing from the wide bottom layer, `0.0` to `1.0`, `0.25` in vanilla.
 * @property cornerHoleChance Chance of a corner leaf missing, `0.0` to `1.0`, `0.25` in vanilla.
 * @property hangingLeavesChance Chance of a leaf hanging below the canopy, `0.0` to `1.0`, `1/6` in vanilla.
 * @property hangingLeavesExtensionChance Chance of a hanging leaf being extended by another one below it, `0.0` to
 * `1.0`, `1/3` in vanilla.
 */
@Serializable
data class CherryFoliagePlacer(
	override var radius: IntProvider = ConstantIntProvider(0),
	override var offset: IntProvider = ConstantIntProvider(0),
	var height: IntProvider = ConstantIntProvider(0),
	var wideBottomLayerHoleChance: Double = 0.0,
	var cornerHoleChance: Double = 0.0,
	var hangingLeavesChance: Double = 0.0,
	var hangingLeavesExtensionChance: Double = 0.0,
) : FoliagePlacer()

/**
 * Sets [Tree.foliagePlacer] to a [CherryFoliagePlacer] configured in [block].
 *
 * ```kotlin
 * tree("cherry") {
 *     cherryFoliagePlacer {
 *         radius(4)
 *         height(5)
 *         wideBottomLayerHoleChance = 0.25
 *         cornerHoleChance = 0.25
 *     }
 * }
 * ```
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 */
fun Tree.cherryFoliagePlacer(
	block: CherryFoliagePlacer.() -> Unit = {},
) {
	foliagePlacer = CherryFoliagePlacer().apply(block)
}

/**
 * Sets [CherryFoliagePlacer.height] to [value], the height of the canopy.
 *
 * ```kotlin
 * cherryFoliagePlacer { height(uniform(4, 5)) }
 * ```
 */
fun CherryFoliagePlacer.height(value: IntProvider) {
	height = value
}

/** Sets [CherryFoliagePlacer.height] to the constant [value]. */
fun CherryFoliagePlacer.height(value: Int) = height(ConstantIntProvider(value))
