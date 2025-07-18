package io.github.ayfri.kore.features.enchantments.effects.entity

import io.github.ayfri.kore.features.enchantments.values.LevelBased
import io.github.ayfri.kore.features.enchantments.values.constantLevelBased
import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicate
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProvider
import io.github.ayfri.kore.generated.arguments.types.GameEventArgument
import io.github.ayfri.kore.serializers.TripleAsArray
import kotlinx.serialization.Serializable

@Serializable
data class ReplaceDisk(
	var blockState: BlockStateProvider,
	var radius: LevelBased = constantLevelBased(0),
	var height: LevelBased = constantLevelBased(0),
	var offset: TripleAsArray<Int, Int, Int>? = null,
	var predicate: BlockPredicate? = null,
	var triggerGameEvent: GameEventArgument? = null,
) : EntityEffect()

fun ReplaceDisk.radius(value: Int) {
	radius = constantLevelBased(value)
}

fun ReplaceDisk.height(value: Int) {
	height = constantLevelBased(value)
}

fun ReplaceDisk.offset(x: Int, y: Int, z: Int) {
	offset = Triple(x, y, z)
}

fun ReplaceDisk.predicate(predicate: BlockPredicate) {
	this.predicate = predicate
}
