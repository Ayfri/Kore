package io.github.ayfri.kore.features.worldgen.ruletest

/**
 * Builder scope for rule tests, the block predicates shared by the structure processors and the ore-like configured
 * features.
 *
 * Every rule test builder (e.g. [blockMatch], [tagMatch]) is an extension on this interface, so they only resolve
 * inside a block that actually accepts a rule test, such as `rule { }` or `target { }`.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Processor_list#Rule_test
 */
interface RuleTestScope
