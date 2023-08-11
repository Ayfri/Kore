package features.worldgen.configuredfeature.blockpredicate

import arguments.types.TaggedResourceLocationArgument
import arguments.types.resources.tagged.BlockTagArgument
import serializers.TripleAsArray
import kotlinx.serialization.Serializable

@Serializable
data class MatchingBlockTag(
	var offset: TripleAsArray<Int, Int, Int> = TripleAsArray(0, 0, 0),
	@Serializable(TaggedResourceLocationArgument.TaggedResourceLocationWithoutPrefixSerializer::class)
	var tag: BlockTagArgument,
) : BlockPredicate()

fun matchingBlockTag(offset: TripleAsArray<Int, Int, Int> = TripleAsArray(0, 0, 0), tag: BlockTagArgument) = MatchingBlockTag(offset, tag)

fun MutableList<BlockPredicate>.matchingBlockTag(offset: TripleAsArray<Int, Int, Int> = TripleAsArray(0, 0, 0), tag: BlockTagArgument) {
	this += MatchingBlockTag(offset, tag)
}
