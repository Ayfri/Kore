package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.treedecorator

import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProvider
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
fun alterGround(provider: BlockStateProvider = ruleBasedStateProvider()) = AlterGround(provider)

/** Adds an [AlterGround] decorator to this list. */
fun MutableList<TreeDecorator>.alterGround(provider: BlockStateProvider = ruleBasedStateProvider()) {
	this += AlterGround(provider)
}
