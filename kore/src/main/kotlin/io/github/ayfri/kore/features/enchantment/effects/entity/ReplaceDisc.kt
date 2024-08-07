package io.github.ayfri.kore.features.enchantment.effects.entity

import io.github.ayfri.kore.features.enchantment.values.LevelBased
import io.github.ayfri.kore.features.enchantment.values.constantLevelBased
import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicate
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProvider
import io.github.ayfri.kore.serializers.TripleAsArray
import kotlinx.serialization.Serializable

@Serializable
data class ReplaceDisc(
	var blockState: BlockStateProvider,
	var radius: LevelBased = constantLevelBased(0),
	var height: LevelBased = constantLevelBased(0),
	var offset: TripleAsArray<Int, Int, Int>? = null,
	var predicate: BlockPredicate? = null,
) : EntityEffect()

fun ReplaceDisc.radius(value: Int) {
	radius = constantLevelBased(value)
}

fun ReplaceDisc.height(value: Int) {
	height = constantLevelBased(value)
}

fun ReplaceDisc.offset(x: Int, y: Int, z: Int) {
	offset = Triple(x, y, z)
}

fun ReplaceDisc.predicate(predicate: BlockPredicate) {
	this.predicate = predicate
}
