package features.worldgen.configuredfeature.ruletest

import arguments.types.resources.BlockArgument
import kotlinx.serialization.Serializable

@Serializable
data class BlockMatch(
	var block: BlockArgument,
) : RuleTest()

fun blockMatch(block: BlockArgument) = BlockMatch(block)
