package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.foliageplacer

import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.Tree
import io.github.ayfri.kore.features.worldgen.intproviders.ConstantIntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import kotlinx.serialization.Serializable

/**
 * Places the flat, wide leaf canopies of the acacia trees, one on the top of each fork of the trunk.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 *
 * @property radius Horizontal radius of the leaves, `0` to `16`, `2` for vanilla acacia trees.
 * @property offset Vertical offset from the top of the trunk to the foliage, `0` to `16`.
 */
@Serializable
data class AcaciaFoliagePlacer(
	override var radius: IntProvider = ConstantIntProvider(0),
	override var offset: IntProvider = ConstantIntProvider(0),
) : FoliagePlacer()

/**
 * Sets [Tree.foliagePlacer] to an [AcaciaFoliagePlacer].
 *
 * ```kotlin
 * tree("acacia") {
 *     acaciaFoliagePlacer(radius = constant(2))
 * }
 * ```
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 */
fun Tree.acaciaFoliagePlacer(
	radius: IntProvider = ConstantIntProvider(0),
	offset: IntProvider = ConstantIntProvider(0),
	block: AcaciaFoliagePlacer.() -> Unit = {},
) {
	foliagePlacer = AcaciaFoliagePlacer(radius, offset).apply(block)
}
