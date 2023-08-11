package features.worldgen.configuredfeature.ruletest

import arguments.types.resources.tagged.BlockTagArgument
import kotlinx.serialization.Serializable

@Serializable
data class TagMatch(
	var tag: BlockTagArgument,
) : RuleTest()

fun tagMatch(tag: BlockTagArgument) = TagMatch(tag)
