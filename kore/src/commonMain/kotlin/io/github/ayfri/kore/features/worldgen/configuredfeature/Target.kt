package io.github.ayfri.kore.features.worldgen.configuredfeature

import io.github.ayfri.kore.data.block.BlockState
import io.github.ayfri.kore.features.worldgen.ruletest.AlwaysTrue
import io.github.ayfri.kore.features.worldgen.ruletest.RuleTest
import io.github.ayfri.kore.features.worldgen.ruletest.RuleTestScope
import kotlinx.serialization.Serializable

/**
 * A replacement target of an ore-like configured feature: the blocks matching [target] become [state].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Ore_(feature)
 *
 * @property target The rule test matching the terrain blocks to replace.
 * @property state The block state placed instead.
 */
@Serializable
data class Target(
	var target: RuleTest = AlwaysTrue,
	var state: BlockState,
) : RuleTestScope

/**
 * Builder scope for declaring the targets of an ore-like configured feature via [targets].
 *
 * [target] is an extension on this class, so it only resolves inside a `targets { }` block.
 *
 * @property targets The targets appended so far.
 */
class TargetsScope {
	val targets = mutableListOf<Target>()
}

/** Collects the targets appended in [block] into a list. */
internal fun buildTargets(block: TargetsScope.() -> Unit) = TargetsScope().apply(block).targets

/**
 * Appends a target replacing the blocks matching [Target.target] by [state].
 *
 * The rule test builders are scoped to this block.
 *
 * ```kotlin
 * targets {
 *     target(blockState(Blocks.DEEPSLATE_IRON_ORE)) {
 *         target = tagMatch(Tags.Block.DEEPSLATE_ORE_REPLACEABLES)
 *     }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Ore_(feature)
 */
fun TargetsScope.target(state: BlockState, block: Target.() -> Unit = {}) = apply {
	targets += Target(state = state).apply(block)
}

/**
 * A configured feature configuration replacing terrain blocks through a list of [Target]s.
 *
 * @property targets The replacement targets, the first matching one wins.
 */
interface TargetsHolder {
	var targets: List<Target>

	/**
	 * Sets the replacement targets to the ones declared in [block].
	 *
	 * ```kotlin
	 * ore("iron_ore", size = 9) {
	 *     targets {
	 *         target(blockState(Blocks.IRON_ORE)) {
	 *             target = tagMatch(Tags.Block.STONE_ORE_REPLACEABLES)
	 *         }
	 *     }
	 * }
	 * ```
	 *
	 * Minecraft Wiki: https://minecraft.wiki/w/Ore_(feature)
	 */
	fun targets(block: TargetsScope.() -> Unit) {
		targets = buildTargets(block)
	}
}
