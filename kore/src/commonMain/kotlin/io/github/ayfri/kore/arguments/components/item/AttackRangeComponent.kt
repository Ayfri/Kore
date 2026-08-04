package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents the `minecraft:attack_range` item component, which configures the reach of an item when attacking (min/max reach, hitbox margin, mob factor).
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#attack_range
 */
@Serializable
data class AttackRangeComponent(
	@SerialName("hitbox_margin")
	var hitboxMargin: Float? = null,
	@SerialName("max_creative_reach")
	var maxCreativeReach: Float? = null,
	@SerialName("max_reach")
	var maxReach: Float? = null,
	@SerialName("min_creative_reach")
	var minCreativeReach: Float? = null,
	@SerialName("min_reach")
	var minReach: Float? = null,
	@SerialName("mob_factor")
	var mobFactor: Float? = null,
) : Component()

/** Configures the attack range of an item (min/max reach, hitbox margin, mob factor). */
fun ComponentsScope.attackRange(
	hitboxMargin: Float? = null,
	maxCreativeReach: Float? = null,
	maxReach: Float? = null,
	minCreativeReach: Float? = null,
	minReach: Float? = null,
	mobFactor: Float? = null,
	block: AttackRangeComponent.() -> Unit = {},
) = apply {
	this[ItemComponentTypes.ATTACK_RANGE] = AttackRangeComponent(
		hitboxMargin,
		maxCreativeReach = maxCreativeReach,
		maxReach = maxReach,
		minCreativeReach = minCreativeReach,
		minReach = minReach,
		mobFactor = mobFactor
	).apply(block)
}
