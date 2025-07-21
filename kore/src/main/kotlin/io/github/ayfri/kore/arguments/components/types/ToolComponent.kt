package io.github.ayfri.kore.arguments.components.types

import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.types.BlockOrTagArgument
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ToolRule(
	var blocks: InlinableList<BlockOrTagArgument>,
	var speed: Float? = null,
	@SerialName("correct_for_drops")
	var correctForDrops: Boolean? = null,
)

@Serializable
data class ToolComponent(
	var rules: List<ToolRule>,
	@SerialName("default_mining_speed")
	var defaultMiningSpeed: Float? = null,
	@SerialName("damage_per_block")
	var damagePerBlock: Int? = null,
	@SerialName("can_destroy_blocks_in_creative")
	var canDestroyBlocksInCreative: Boolean? = null,
) : Component()

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
