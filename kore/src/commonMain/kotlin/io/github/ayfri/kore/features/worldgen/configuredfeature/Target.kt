package io.github.ayfri.kore.features.worldgen.configuredfeature

import io.github.ayfri.kore.data.block.BlockState
import io.github.ayfri.kore.data.block.blockStateStone
import io.github.ayfri.kore.features.worldgen.ruletest.AlwaysTrue
import io.github.ayfri.kore.features.worldgen.ruletest.RuleTest
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
