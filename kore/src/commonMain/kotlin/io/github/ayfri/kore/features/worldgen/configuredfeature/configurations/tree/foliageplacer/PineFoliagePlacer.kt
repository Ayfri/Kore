package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.foliageplacer

import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.Tree
import io.github.ayfri.kore.features.worldgen.intproviders.ConstantIntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import kotlinx.serialization.Serializable

/**
 * Places the narrow, cone-shaped leaves of the pine trees.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 *
 * @property radius Horizontal radius of the leaves, `0` to `16`, `1` for vanilla pine trees.
 * @property offset Vertical offset from the top of the trunk to the foliage, `0` to `16`, `1` for vanilla pine trees.
 * @property height Height of the leaves, `0` to `24`, `3` to `4` for vanilla pine trees.
 */
@Serializable
data class PineFoliagePlacer(
	override var radius: IntProvider = ConstantIntProvider(0),
	override var offset: IntProvider = ConstantIntProvider(0),
	var height: IntProvider = ConstantIntProvider(0),
) : FoliagePlacer()

/**
 * Sets [Tree.foliagePlacer] to a [PineFoliagePlacer].
 *
 * ```kotlin
 * tree("pine") {
 *     pineFoliagePlacer(radius = uniform(1, 2), offset = uniform(0, 1), height = uniform(3, 4))
 * }
 * ```
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 */
fun Tree.pineFoliagePlacer(
	radius: IntProvider = ConstantIntProvider(0),
	offset: IntProvider = ConstantIntProvider(0),
	height: IntProvider = ConstantIntProvider(0),
	block: PineFoliagePlacer.() -> Unit = {},
) {
	foliagePlacer = PineFoliagePlacer(radius, offset, height).apply(block)
}

/**
 * Sets [Tree.foliagePlacer] to a [PineFoliagePlacer] of the constant [radius], [offset] and [height].
 *
 * ```kotlin
 * tree("pine") {
 *     pineFoliagePlacer(radius = 1, offset = 1, height = 3)
 * }
 * ```
 */
fun Tree.pineFoliagePlacer(radius: Int, offset: Int = 0, height: Int = 0, block: PineFoliagePlacer.() -> Unit = {}) =
	pineFoliagePlacer(ConstantIntProvider(radius), ConstantIntProvider(offset), ConstantIntProvider(height), block)

/**
 * Sets [PineFoliagePlacer.height] to [value], the height of the leaves.
 *
 * ```kotlin
 * pineFoliagePlacer(radius = 1) { height(uniform(3, 4)) }
 * ```
 */
fun PineFoliagePlacer.height(value: IntProvider) {
	height = value
}

/** Sets [PineFoliagePlacer.height] to the constant [value]. */
fun PineFoliagePlacer.height(value: Int) = height(ConstantIntProvider(value))
