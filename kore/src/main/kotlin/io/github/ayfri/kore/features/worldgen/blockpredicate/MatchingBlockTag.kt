package io.github.ayfri.kore.features.worldgen.blockpredicate

import io.github.ayfri.kore.arguments.types.TaggedResourceLocationArgument
import io.github.ayfri.kore.arguments.types.resources.tagged.BlockTagArgument
import io.github.ayfri.kore.serializers.TripleAsArray
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
