package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.types.BlockOrTagArgument
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/** A mining rule that applies a speed multiplier and/or correct-drops flag to specific blocks. */
@Serializable
data class ToolRule(
	var blocks: InlinableList<BlockOrTagArgument>,
	var speed: Float? = null,
	var correctForDrops: Boolean? = null,
)

/**
 * Represents the `minecraft:tool` item component, which defines mining speed rules and damage per block broken.
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#tool
 */
@Serializable
data class ToolComponent(
	var rules: List<ToolRule>,
	var defaultMiningSpeed: Float? = null,
	var damagePerBlock: Int? = null,
	var canDestroyBlocksInCreative: Boolean? = null,
) : Component()

/** Defines mining speed rules and damage per block broken for this tool. */
fun ComponentsScope.tool(
	rules: List<ToolRule> = emptyList(),
	defaultMiningSpeed: Float? = null,
	damagePerBlock: Int? = null,
	canDestroyBlocksInCreative: Boolean? = null,
	block: ToolComponent.() -> Unit = {},
) = apply { this[ItemComponentTypes.TOOL] = ToolComponent(rules, defaultMiningSpeed, damagePerBlock, canDestroyBlocksInCreative).apply(block) }

fun ToolComponent.rule(
	blocks: List<BlockOrTagArgument>,
	speed: Float? = null,
	correctForDrops: Boolean? = null,
) = apply { rules += ToolRule(blocks, speed, correctForDrops) }

fun ToolComponent.rule(
	speed: Float? = null,
	correctForDrops: Boolean? = null,
	vararg blocks: BlockOrTagArgument,
) = rule(blocks.toList(), speed, correctForDrops)
