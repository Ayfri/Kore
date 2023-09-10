package io.github.ayfri.kore.features.worldgen.ruletest

import io.github.ayfri.kore.arguments.types.resources.BlockArgument
import kotlinx.serialization.Serializable

@Serializable
data class RandomBlockMatch(
	var block: BlockArgument,
	var probability: Double = 0.0,
) : RuleTest()

fun randomBlockMatch(block: BlockArgument, probability: Double = 0.0) = RandomBlockMatch(block, probability)
