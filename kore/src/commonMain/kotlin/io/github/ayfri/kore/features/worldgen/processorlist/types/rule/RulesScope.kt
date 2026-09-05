package io.github.ayfri.kore.features.worldgen.processorlist.types.rule

/**
 * Builder scope for declaring the rules of a [io.github.ayfri.kore.features.worldgen.processorlist.types.Rule]
 * processor via [io.github.ayfri.kore.features.worldgen.processorlist.types.rules].
 *
 * [rule] is an extension on this class, so it only resolves inside a `rules { }` block.
 *
 * @property rules The rules appended so far, in evaluation order.
 */
class RulesScope {
	val rules = mutableListOf<ProcessorRule>()
}

/** Collects the rules appended in [block] into a list. */
internal fun buildRules(block: RulesScope.() -> Unit) = RulesScope().apply(block).rules
