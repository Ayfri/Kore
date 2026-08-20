package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.treedecorator

import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProviderScope
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.RuleBasedStateProvider
import kotlinx.serialization.Serializable

/**
 * Replaces the ground blocks beneath a placed tree, such as the podzol under the spruce trees.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#alter_ground
 *
 * @property provider The block states replacing the ground.
 */
@Serializable
data class AlterGround(
	var provider: BlockStateProvider = RuleBasedStateProvider(),
) : TreeDecorator(), BlockStateProviderScope

/**
 * Creates an `alter_ground` tree decorator replacing the ground with [provider].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#alter_ground
 */
fun BlockStateProviderScope.alterGround(provider: BlockStateProvider) = AlterGround(provider)

/**
 * Creates an `alter_ground` tree decorator replacing the ground with the [RuleBasedStateProvider] built in [block].
 *
 * ```kotlin
 * alterGround {
 *     rule(simpleStateProvider(Blocks.PODZOL)) { solid() }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#alter_ground
 */
fun BlockStateProviderScope.alterGround(block: RuleBasedStateProvider.() -> Unit) =
	AlterGround(RuleBasedStateProvider().apply(block))

/** Appends an `alter_ground` tree decorator replacing the ground with [provider]. */
fun MutableList<TreeDecorator>.alterGround(provider: BlockStateProvider) {
	this += AlterGround(provider)
}

/**
 * Appends an `alter_ground` tree decorator replacing the ground with the [RuleBasedStateProvider] built in [block].
 *
 * ```kotlin
 * decorators {
 *     alterGround {
 *         rule(simpleStateProvider(Blocks.PODZOL)) { solid() }
 *     }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#alter_ground
 */
fun MutableList<TreeDecorator>.alterGround(block: RuleBasedStateProvider.() -> Unit) {
	this += AlterGround(RuleBasedStateProvider().apply(block))
}
