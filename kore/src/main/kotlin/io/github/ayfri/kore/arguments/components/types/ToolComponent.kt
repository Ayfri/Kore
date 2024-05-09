package io.github.ayfri.kore.arguments.components.types

import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.types.BlockOrTagArgument
import io.github.ayfri.kore.generated.ComponentTypes
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
) : Component()

fun ComponentsScope.tool(
	rules: List<ToolRule> = emptyList(),
	defaultMiningSpeed: Float? = null,
	damagePerBlock: Int? = null,
	block: ToolComponent.() -> Unit = {},
) = apply { this[ComponentTypes.TOOL] = ToolComponent(rules, defaultMiningSpeed, damagePerBlock).apply(block) }

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
