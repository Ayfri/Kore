package io.github.ayfri.kore.features.worldgen.ruletest

import kotlinx.serialization.Serializable

/**
 * Matches every block.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Processor_list#Rule_test
 */
@Serializable
data object AlwaysTrue : RuleTest()

/**
 * Creates an `always_true` rule test, matching every block.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Processor_list#Rule_test
 */
fun RuleTestScope.alwaysTrue() = AlwaysTrue
