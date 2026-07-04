package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.treedecorator

import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.RuleBasedStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.ruleBasedStateProvider
import kotlinx.serialization.Serializable

/**
 * Tree decorator that replaces ground blocks beneath a placed tree using a [BlockStateProvider].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#alter_ground
 */
@Serializable
data class AlterGround(
	var provider: BlockStateProvider,
) : TreeDecorator()

/** Creates an [AlterGround] decorator with the given [provider]. */
fun alterGround(provider: BlockStateProvider) = AlterGround(provider)

/** Creates an [AlterGround] decorator configured via a [RuleBasedStateProvider] builder block. */
fun alterGround(block: RuleBasedStateProvider.() -> Unit) = AlterGround(ruleBasedStateProvider(block))

/** Adds an [AlterGround] decorator to this list with the given [provider]. */
fun MutableList<TreeDecorator>.alterGround(provider: BlockStateProvider) {
	this += AlterGround(provider)
}

/** Adds an [AlterGround] decorator to this list configured via a [RuleBasedStateProvider] builder block. */
fun MutableList<TreeDecorator>.alterGround(block: RuleBasedStateProvider.() -> Unit) {
	this += AlterGround(ruleBasedStateProvider(block))
}
