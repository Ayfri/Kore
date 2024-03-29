package io.github.ayfri.kore.features.worldgen.blockpredicate

import io.github.ayfri.kore.features.worldgen.configuredfeature.Direction
import io.github.ayfri.kore.serializers.TripleAsArray
import kotlinx.serialization.Serializable

@Serializable
data class HasSturdyFace(
	var offset: TripleAsArray<Int, Int, Int>? = null,
	var direction: Direction,
) : BlockPredicate()

fun hasSturdyFace(offset: TripleAsArray<Int, Int, Int>? = null, direction: Direction) = HasSturdyFace(offset, direction)

fun MutableList<BlockPredicate>.hasSturdyFace(offset: TripleAsArray<Int, Int, Int>? = null, direction: Direction) {
	this += HasSturdyFace(offset, direction)
}
