package io.github.ayfri.kore.features.worldgen.ruletest

import io.github.ayfri.kore.arguments.types.resources.tagged.BlockTagArgument
import kotlinx.serialization.Serializable

@Serializable
data class TagMatch(
	var tag: BlockTagArgument,
) : RuleTest()

fun tagMatch(tag: BlockTagArgument) = TagMatch(tag)
