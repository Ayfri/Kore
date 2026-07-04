package io.github.ayfri.kore.features.worldgen.ruletest

import io.github.ayfri.kore.arguments.types.resources.BlockArgument
import kotlinx.serialization.Serializable

@Serializable
data class BlockMatch(
	var block: BlockArgument,
) : RuleTest()

fun blockMatch(block: BlockArgument) = BlockMatch(block)
