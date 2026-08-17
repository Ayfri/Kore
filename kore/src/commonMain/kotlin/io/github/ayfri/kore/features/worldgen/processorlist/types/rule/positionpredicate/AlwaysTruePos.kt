package io.github.ayfri.kore.features.worldgen.processorlist.types.rule.positionpredicate

import io.github.ayfri.kore.features.worldgen.processorlist.types.rule.ProcessorRule
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Passes at every position. Same behavior as leaving [ProcessorRule.positionPredicate] to `null`.
 *
 * Named after its `Pos` siblings so it does not clash with the [io.github.ayfri.kore.features.worldgen.ruletest.AlwaysTrue]
 * rule test.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Processor_list
 */
@Serializable
@SerialName("always_true")
data object AlwaysTruePos : PositionPredicate()

/**
 * Creates an `always_true` position predicate, passing at every position.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Processor_list
 */
fun ProcessorRule.alwaysTruePos() = AlwaysTruePos
