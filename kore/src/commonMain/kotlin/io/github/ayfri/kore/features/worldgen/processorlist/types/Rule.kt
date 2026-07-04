package io.github.ayfri.kore.features.worldgen.processorlist.types

import io.github.ayfri.kore.features.worldgen.processorlist.ProcessorList
import io.github.ayfri.kore.features.worldgen.processorlist.types.rule.ProcessorRule
import kotlinx.serialization.Serializable

@Serializable
data class Rule(
	var rules: List<ProcessorRule> = listOf(),
) : ProcessorType()

fun ProcessorList.rules(rules: List<ProcessorRule> = listOf(), block: Rule.() -> Unit = {}) {
	processors += Rule(rules).apply(block)
}

fun ProcessorList.rules(vararg rules: ProcessorRule) {
	processors += Rule(rules.toList())
}
