package features.worldgen.blockpredicate

import arguments.types.TaggedResourceLocationArgument
import arguments.types.resources.tagged.BlockTagArgument
import serializers.TripleAsArray
import kotlinx.serialization.Serializable

@Serializable
data class MatchingBlockTag(
	var offset: TripleAsArray<Int, Int, Int>? = null,
	@Serializable(TaggedResourceLocationArgument.TaggedResourceLocationWithoutPrefixSerializer::class)
	var tag: BlockTagArgument,
) : BlockPredicate()

fun matchingBlockTag(offset: TripleAsArray<Int, Int, Int>? = null, tag: BlockTagArgument) = MatchingBlockTag(offset, tag)

fun MutableList<BlockPredicate>.matchingBlockTag(offset: TripleAsArray<Int, Int, Int>? = null, tag: BlockTagArgument) {
	this += MatchingBlockTag(offset, tag)
}
