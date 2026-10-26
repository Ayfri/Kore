package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.foliageplacer

import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.Tree
import io.github.ayfri.kore.features.worldgen.intproviders.ConstantIntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import kotlinx.serialization.Serializable

/**
 * Places the tall leaf crown of the mega pine and mega spruce trees, the giant 2x2 conifers of the old growth taigas.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 *
 * @property radius Horizontal radius of the leaves, `0` to `16`, `0` for vanilla trees.
 * @property offset Vertical offset from the top of the trunk to the foliage, `0` to `16`.
 * @property crownHeight Height of the crown, `0` to `24`, `3` to `7` for vanilla mega pines and `13` to `17` for vanilla
 * mega spruces.
 */
@Serializable
data class MegaPineFoliagePlacer(
	override var radius: IntProvider = ConstantIntProvider(0),
	override var offset: IntProvider = ConstantIntProvider(0),
	var crownHeight: IntProvider = ConstantIntProvider(0),
) : FoliagePlacer()

/**
 * Sets [Tree.foliagePlacer] to a [MegaPineFoliagePlacer].
 *
 * ```kotlin
 * tree("mega_pine") {
 *     megaPineFoliagePlacer(crownHeight = uniform(3, 7))
 * }
 * ```
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 */
fun Tree.megaPineFoliagePlacer(
	radius: IntProvider = ConstantIntProvider(0),
	offset: IntProvider = ConstantIntProvider(0),
	crownHeight: IntProvider = ConstantIntProvider(0),
	block: MegaPineFoliagePlacer.() -> Unit = {},
) {
	foliagePlacer = MegaPineFoliagePlacer(radius, offset, crownHeight).apply(block)
}

/**
 * Sets [Tree.foliagePlacer] to a [MegaPineFoliagePlacer] of the constant [radius], [offset] and [crownHeight].
 *
 * ```kotlin
 * tree("mega_pine") {
 *     megaPineFoliagePlacer(radius = 1, crownHeight = 5)
 * }
 * ```
 */
fun Tree.megaPineFoliagePlacer(
	radius: Int,
	offset: Int = 0,
	crownHeight: Int = 0,
	block: MegaPineFoliagePlacer.() -> Unit = {},
) = megaPineFoliagePlacer(ConstantIntProvider(radius), ConstantIntProvider(offset), ConstantIntProvider(crownHeight), block)

/**
 * Sets [MegaPineFoliagePlacer.crownHeight] to [value], the height of the crown.
 *
 * ```kotlin
 * megaPineFoliagePlacer(radius = 1) { crownHeight(uniform(3, 7)) }
 * ```
 */
fun MegaPineFoliagePlacer.crownHeight(value: IntProvider) {
	crownHeight = value
}

/** Sets [MegaPineFoliagePlacer.crownHeight] to the constant [value]. */
fun MegaPineFoliagePlacer.crownHeight(value: Int) = crownHeight(ConstantIntProvider(value))
