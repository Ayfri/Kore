package features.worldgen.configuredfeature.blockpredicate

import features.worldgen.configuredfeature.Direction
import serializers.TripleAsArray
import kotlinx.serialization.Serializable

@Serializable
data class HasSturdyFace(
	var offset: TripleAsArray<Int, Int, Int> = TripleAsArray(0, 0, 0),
	var direction: Direction,
) : BlockPredicate()

fun hasSturdyFace(offset: TripleAsArray<Int, Int, Int> = TripleAsArray(0, 0, 0), direction: Direction) = HasSturdyFace(offset, direction)

fun MutableList<BlockPredicate>.hasSturdyFace(offset: TripleAsArray<Int, Int, Int> = TripleAsArray(0, 0, 0), direction: Direction) {
	this += HasSturdyFace(offset, direction)
}
