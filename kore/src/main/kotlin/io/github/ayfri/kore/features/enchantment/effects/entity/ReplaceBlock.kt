package io.github.ayfri.kore.features.enchantment.effects.entity

import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicate
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProvider
import io.github.ayfri.kore.serializers.TripleAsArray
import kotlinx.serialization.Serializable

@Serializable
data class ReplaceBlock(
	var blockState: BlockStateProvider,
	var offset: TripleAsArray<Int, Int, Int>? = null,
	var predicate: BlockPredicate? = null,
) : EntityEffect()

fun ReplaceBlock.offset(x: Int, y: Int, z: Int) {
	offset = Triple(x, y, z)
}
