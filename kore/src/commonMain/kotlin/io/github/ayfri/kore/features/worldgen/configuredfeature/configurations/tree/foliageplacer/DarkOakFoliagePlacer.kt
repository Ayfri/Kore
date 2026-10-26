package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.foliageplacer

import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.Tree
import io.github.ayfri.kore.features.worldgen.intproviders.ConstantIntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import kotlinx.serialization.Serializable

/**
 * Places the leaves of the dark oak and pale oak trees, around the top of their 2x2 trunk and of its branches.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 *
 * @property radius Horizontal radius of the leaves, `0` to `16`, `0` for vanilla trees.
 * @property offset Vertical offset from the top of the trunk to the foliage, `0` to `16`.
 */
@Serializable
data class DarkOakFoliagePlacer(
	override var radius: IntProvider = ConstantIntProvider(0),
	override var offset: IntProvider = ConstantIntProvider(0),
) : FoliagePlacer()

/**
 * Sets [Tree.foliagePlacer] to a [DarkOakFoliagePlacer].
 *
 * ```kotlin
 * tree("dark_oak") {
 *     darkOakFoliagePlacer()
 * }
 * ```
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 */
fun Tree.darkOakFoliagePlacer(
	radius: IntProvider = ConstantIntProvider(0),
	offset: IntProvider = ConstantIntProvider(0),
	block: DarkOakFoliagePlacer.() -> Unit = {},
) {
	foliagePlacer = DarkOakFoliagePlacer(radius, offset).apply(block)
}

/**
 * Sets [Tree.foliagePlacer] to a [DarkOakFoliagePlacer] of the constant [radius] and [offset].
 *
 * ```kotlin
 * tree("dark_oak") {
 *     darkOakFoliagePlacer(radius = 0, offset = 0)
 * }
 * ```
 */
fun Tree.darkOakFoliagePlacer(radius: Int, offset: Int = 0, block: DarkOakFoliagePlacer.() -> Unit = {}) =
	darkOakFoliagePlacer(ConstantIntProvider(radius), ConstantIntProvider(offset), block)
