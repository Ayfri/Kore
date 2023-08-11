package features.worldgen.configuredfeature

import data.block.BlockState
import data.block.blockStateStone
import features.worldgen.configuredfeature.ruletest.AlwaysTrue
import features.worldgen.configuredfeature.ruletest.RuleTest
import kotlinx.serialization.Serializable

@Serializable
data class Target(
	var target: RuleTest = AlwaysTrue,
	var state: BlockState = blockStateStone(),
)

fun target(target: RuleTest = AlwaysTrue, state: BlockState = blockStateStone()) = Target(target, state)
fun MutableList<Target>.target(target: RuleTest = AlwaysTrue, state: BlockState = blockStateStone()) {
	this += Target(target, state)
}
