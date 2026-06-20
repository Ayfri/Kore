package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.treedecorator

import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.RuleBasedBlockStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.ruleBasedBlockStateProvider
import kotlinx.serialization.Serializable

/**
 * Tree decorator that replaces ground blocks beneath a placed tree using a [RuleBasedBlockStateProvider].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#alter_ground
 */
@Serializable
data class AlterGround(
	var provider: RuleBasedBlockStateProvider,
) : TreeDecorator()

/** Creates an [AlterGround] decorator with the given rule-based [provider]. */
fun alterGround(provider: RuleBasedBlockStateProvider = ruleBasedBlockStateProvider()) = AlterGround(provider)

/** Adds an [AlterGround] decorator to this list. */
fun MutableList<TreeDecorator>.alterGround(provider: RuleBasedBlockStateProvider = ruleBasedBlockStateProvider()) {
	this += AlterGround(provider)
}
