package io.github.ayfri.kore.features.worldgen.processorlist.types

import io.github.ayfri.kore.features.worldgen.processorlist.types.rule.ProcessorRule
import io.github.ayfri.kore.features.worldgen.processorlist.types.rule.RulesScope
import io.github.ayfri.kore.features.worldgen.processorlist.types.rule.buildRules
import kotlinx.serialization.Serializable

/**
 * Replaces blocks of the template using a list of rules, the first matching rule wins.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Processor_list
 *
 * @property rules The rules to test, in evaluation order.
 */
@Serializable
data class Rule(
	var rules: List<ProcessorRule> = emptyList(),
) : ProcessorType()

/**
 * Appends a `rule` processor with the rules declared in [block].
 *
 * ```kotlin
 * rules {
 *     rule {
 *         inputPredicate = blockMatch(Blocks.STONE_BRICKS)
 *         outputState = blockState(Blocks.MOSSY_STONE_BRICKS)
 *     }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Processor_list
 */
fun ProcessorsScope.rules(block: RulesScope.() -> Unit) = apply { processors += Rule(buildRules(block)) }

/**
 * Appends a `rule` processor with the given [rules].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Processor_list
 */
fun ProcessorsScope.rules(vararg rules: ProcessorRule) = apply { processors += Rule(rules.toList()) }
