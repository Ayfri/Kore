package io.github.ayfri.kore.features.worldgen.processorlist.types.rule

import io.github.ayfri.kore.data.block.BlockState
import io.github.ayfri.kore.data.block.blockStateStone
import io.github.ayfri.kore.features.worldgen.processorlist.types.rule.blockentitymodifier.BlockEntityModifier
import io.github.ayfri.kore.features.worldgen.processorlist.types.rule.positionpredicate.PositionPredicate
import io.github.ayfri.kore.features.worldgen.ruletest.AlwaysTrue
import io.github.ayfri.kore.features.worldgen.ruletest.RuleTest
import io.github.ayfri.kore.features.worldgen.ruletest.RuleTestScope
import kotlinx.serialization.Serializable

/**
 * A single rule of a [io.github.ayfri.kore.features.worldgen.processorlist.types.Rule] processor: when the three
 * predicates pass, the template block is replaced by [outputState].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Processor_list
 *
 * @property positionPredicate The test on the position inside the structure piece, always passing when `null`.
 * @property locationPredicate The test on the block already in the world at that position.
 * @property inputPredicate The test on the block of the structure template.
 * @property outputState The block state placed when every predicate passes.
 * @property blockEntityModifier What to do with the block entity data of the placed block, `passthrough` when `null`.
 */
@Serializable
data class ProcessorRule(
	var positionPredicate: PositionPredicate? = null,
	var locationPredicate: RuleTest = AlwaysTrue,
	var inputPredicate: RuleTest = AlwaysTrue,
	var outputState: BlockState = blockStateStone(),
	var blockEntityModifier: BlockEntityModifier? = null,
) : RuleTestScope

/**
 * Appends a rule replacing the blocks matching its predicates by [ProcessorRule.outputState].
 *
 * The rule test, position predicate and block entity modifier builders are all scoped to this block.
 *
 * ```kotlin
 * rule {
 *     inputPredicate = blockMatch(Blocks.STONE_BRICKS)
 *     locationPredicate = tagMatch(Tags.Block.DIRT)
 *     outputState = blockState(Blocks.MOSSY_STONE_BRICKS)
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Processor_list
 */
fun RulesScope.rule(block: ProcessorRule.() -> Unit = {}) = apply { rules += ProcessorRule().apply(block) }
