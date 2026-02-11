package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AttackRangeComponent(
	@SerialName("hitbox_margin")
	var hitboxMargin: Float? = null,
	@SerialName("max_creative_reach")
	var maxCreativeReach: Float? = null,
	@SerialName("max_range")
	var maxRange: Float? = null,
	@SerialName("min_creative_reach")
	var minCreativeReach: Float? = null,
	@SerialName("min_range")
	var minRange: Float? = null,
	@SerialName("mob_factor")
	var mobFactor: Float? = null,
) : Component()

fun ComponentsScope.attackRange(
	hitboxMargin: Float? = null,
	maxCreativeReach: Float? = null,
	maxRange: Float? = null,
	minCreativeReach: Float? = null,
	minRange: Float? = null,
	mobFactor: Float? = null,
	block: AttackRangeComponent.() -> Unit = {},
) = apply {
	this[ItemComponentTypes.ATTACK_RANGE] = AttackRangeComponent(
		hitboxMargin,
		maxCreativeReach = maxCreativeReach,
		maxRange = maxRange,
		minCreativeReach = minCreativeReach,
		minRange = minRange,
		mobFactor = mobFactor
	).apply(block)
}
