package io.github.ayfri.kore.features.worldgen.ruletest

import io.github.ayfri.kore.arguments.types.TaggedResourceLocationArgument
import io.github.ayfri.kore.arguments.types.resources.tagged.BlockTagArgument
import kotlinx.serialization.Serializable

/**
 * Matches every block of a block tag.
 *
 * The tag is written without its `#` prefix, as Minecraft expects it here.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Processor_list#Rule_test
 *
 * @property tag The block tag to match.
 */
@Serializable
data class TagMatch(
	@Serializable(TaggedResourceLocationArgument.TaggedResourceLocationUnPrefixedSerializer::class)
	var tag: BlockTagArgument,
) : RuleTest()

/**
 * Creates a `tag_match` rule test, matching every block of [tag].
 *
 * ```kotlin
 * rule {
 *     locationPredicate = tagMatch(Tags.Block.DIRT)
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Processor_list#Rule_test
 */
fun RuleTestScope.tagMatch(tag: BlockTagArgument) = TagMatch(tag)
