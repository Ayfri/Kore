package io.github.ayfri.kore.features.worldgen.processorlist.types.rule

import io.github.ayfri.kore.data.block.BlockState
import io.github.ayfri.kore.data.block.blockStateStone
import io.github.ayfri.kore.features.worldgen.processorlist.types.Rule
import io.github.ayfri.kore.features.worldgen.processorlist.types.rule.blockentitymodifier.BlockEntityModifier
import io.github.ayfri.kore.features.worldgen.processorlist.types.rule.positionpredicate.PositionPredicate
import io.github.ayfri.kore.features.worldgen.ruletest.AlwaysTrue
import io.github.ayfri.kore.features.worldgen.ruletest.RuleTest
import kotlinx.serialization.Serializable

@Serializable
data class ProcessorRule(
	var positionPredicate: PositionPredicate? = null,
	var locationPredicate: RuleTest = AlwaysTrue,
	var inputPredicate: RuleTest = AlwaysTrue,
	var outputState: BlockState = blockStateStone(),
	var blockEntityModifier: BlockEntityModifier? = null,
)

fun Rule.rule(
	positionPredicate: PositionPredicate? = null,
	locationPredicate: RuleTest = AlwaysTrue,
	inputPredicate: RuleTest = AlwaysTrue,
	outputState: BlockState = blockStateStone(),
	blockEntityModifier: BlockEntityModifier? = null,
	block: ProcessorRule.() -> Unit = {},
) {
	rules += ProcessorRule(positionPredicate, locationPredicate, inputPredicate, outputState, blockEntityModifier).apply(block)
}
