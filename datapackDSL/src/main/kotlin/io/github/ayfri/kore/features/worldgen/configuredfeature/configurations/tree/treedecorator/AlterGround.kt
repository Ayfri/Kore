package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.treedecorator

import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.simpleStateProvider
import kotlinx.serialization.Serializable

@Serializable
data class AlterGround(
	var provider: BlockStateProvider,
) : TreeDecorator()

fun alterGround(provider: BlockStateProvider = simpleStateProvider()) = AlterGround(provider)

fun MutableList<TreeDecorator>.alterGround(provider: BlockStateProvider = simpleStateProvider()) {
	this += AlterGround(provider)
}
