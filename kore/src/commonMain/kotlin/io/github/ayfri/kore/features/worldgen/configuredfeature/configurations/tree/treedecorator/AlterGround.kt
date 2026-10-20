package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.treedecorator

import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProviderScope
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.RuleBasedStateProvider
import kotlinx.serialization.Serializable

/**
 * Replaces the ground around the base of the tree, such as the podzol under the mega pine and mega spruce trees.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 *
 * @property provider The rule-based state provider choosing the blocks replacing the ground.
 */
@Serializable
data class AlterGround(
	var provider: BlockStateProvider = RuleBasedStateProvider(),
) : TreeDecorator(), BlockStateProviderScope

/**
 * Creates an [AlterGround] replacing the ground with [provider].
 *
 * ```kotlin
 * tree("mega_pine") {
 *     val podzol = ruleBasedStateProvider { rule(simpleStateProvider(Blocks.PODZOL)) { solid() } }
 *     decorators(alterGround(podzol))
 * }
 * ```
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 */
fun BlockStateProviderScope.alterGround(provider: BlockStateProvider) = AlterGround(provider)

/**
 * Creates an [AlterGround] replacing the ground with the [RuleBasedStateProvider] built in [block].
 *
 * ```kotlin
 * tree("mega_pine") {
 *     decorators(alterGround { rule(simpleStateProvider(Blocks.PODZOL)) { solid() } })
 * }
 * ```
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 */
fun BlockStateProviderScope.alterGround(block: RuleBasedStateProvider.() -> Unit) =
	AlterGround(RuleBasedStateProvider().apply(block))

/**
 * Appends an [AlterGround] replacing the ground with [provider].
 *
 * ```kotlin
 * tree("mega_pine") {
 *     val podzol = ruleBasedStateProvider { rule(simpleStateProvider(Blocks.PODZOL)) { solid() } }
 *     decorators { alterGround(podzol) }
 * }
 * ```
 */
fun MutableList<TreeDecorator>.alterGround(provider: BlockStateProvider) {
	this += AlterGround(provider)
}

/**
 * Appends an [AlterGround] replacing the ground with the [RuleBasedStateProvider] built in [block].
 *
 * ```kotlin
 * tree("mega_pine") {
 *     decorators {
 *         alterGround {
 *             rule(simpleStateProvider(Blocks.PODZOL)) { solid() }
 *         }
 *     }
 * }
 * ```
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 */
fun MutableList<TreeDecorator>.alterGround(block: RuleBasedStateProvider.() -> Unit) {
	this += AlterGround(RuleBasedStateProvider().apply(block))
}
