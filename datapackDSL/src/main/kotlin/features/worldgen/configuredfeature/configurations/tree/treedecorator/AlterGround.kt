package features.worldgen.configuredfeature.configurations.tree.treedecorator

import features.worldgen.configuredfeature.blockstateprovider.BlockStateProvider
import features.worldgen.configuredfeature.blockstateprovider.simpleStateProvider
import kotlinx.serialization.Serializable

@Serializable
data class AlterGround(
	var provider: BlockStateProvider,
) : TreeDecorator()

fun alterGround(provider: BlockStateProvider = simpleStateProvider()) = AlterGround(provider)

fun MutableList<TreeDecorator>.alterGround(provider: BlockStateProvider = simpleStateProvider()) {
	this += AlterGround(provider)
}
