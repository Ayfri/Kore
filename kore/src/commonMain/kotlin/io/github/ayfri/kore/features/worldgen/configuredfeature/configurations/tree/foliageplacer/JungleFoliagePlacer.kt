package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.foliageplacer

import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.Tree
import io.github.ayfri.kore.features.worldgen.intproviders.ConstantIntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import kotlinx.serialization.Serializable

/**
 * Places the leaves of the mega jungle trees, on top of the trunk and at the end of its branches.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 *
 * @property radius Horizontal radius of the leaves, `0` to `16`, `2` for vanilla mega jungle trees.
 * @property offset Vertical offset from the top of the trunk to the foliage, `0` to `16`.
 * @property height Height of the leaves, `0` to `16`, `2` for vanilla mega jungle trees.
 */
@Serializable
data class JungleFoliagePlacer(
	override var radius: IntProvider = ConstantIntProvider(0),
	override var offset: IntProvider = ConstantIntProvider(0),
	var height: Int = 0,
) : FoliagePlacer()

/**
 * Sets [Tree.foliagePlacer] to a [JungleFoliagePlacer].
 *
 * ```kotlin
 * tree("mega_jungle") {
 *     jungleFoliagePlacer(radius = uniform(2, 3), height = 2)
 * }
 * ```
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 */
fun Tree.jungleFoliagePlacer(
	radius: IntProvider = ConstantIntProvider(0),
	offset: IntProvider = ConstantIntProvider(0),
	height: Int = 0,
	block: JungleFoliagePlacer.() -> Unit = {},
) {
	foliagePlacer = JungleFoliagePlacer(radius, offset, height).apply(block)
}

/**
 * Sets [Tree.foliagePlacer] to a [JungleFoliagePlacer] of the constant [radius] and [offset].
 *
 * ```kotlin
 * tree("mega_jungle") {
 *     jungleFoliagePlacer(radius = 2, height = 2)
 * }
 * ```
 */
fun Tree.jungleFoliagePlacer(radius: Int, offset: Int = 0, height: Int = 0, block: JungleFoliagePlacer.() -> Unit = {}) =
	jungleFoliagePlacer(ConstantIntProvider(radius), ConstantIntProvider(offset), height, block)
