package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.generated.arguments.types.SoundEventArgument
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KineticWeaponEffectCondition(
	@SerialName("max_duration_ticks")
	var maxDurationTicks: Int,
	@SerialName("min_speed")
	var minSpeed: Float? = null,
	@SerialName("min_relative_speed")
	var minRelativeSpeed: Float? = null,
)

@Serializable
data class KineticWeaponComponent(
	@SerialName("contact_cooldown_ticks")
	var contactCooldownTicks: Int? = null,
	@SerialName("damage_conditions")
	var damageConditions: KineticWeaponEffectCondition? = null,
	@SerialName("damage_multiplier")
	var damageMultiplier: Float? = null,
	@SerialName("delay_ticks")
	var delayTicks: Int? = null,
	@SerialName("dismount_conditions")
	var dismountConditions: KineticWeaponEffectCondition? = null,
	@SerialName("forward_movement")
	var forwardMovement: Float? = null,
	@SerialName("hit_sound")
	var hitSound: SoundEventArgument? = null,
	@SerialName("knockback_conditions")
	var knockbackConditions: KineticWeaponEffectCondition? = null,
	var sound: SoundEventArgument? = null,
) : Component()

fun ComponentsScope.kineticWeapon(block: KineticWeaponComponent.() -> Unit = {}) = apply {
	this[ItemComponentTypes.KINETIC_WEAPON] = KineticWeaponComponent().apply(block)
}

fun KineticWeaponComponent.damageConditions(
	maxDurationTicks: Int,
	minRelativeSpeed: Float? = null,
	minSpeed: Float? = null,
) = apply {
	damageConditions = KineticWeaponEffectCondition(maxDurationTicks, minSpeed, minRelativeSpeed)
}

fun KineticWeaponComponent.dismountConditions(
	maxDurationTicks: Int,
	minRelativeSpeed: Float? = null,
	minSpeed: Float? = null,
) = apply {
	dismountConditions = KineticWeaponEffectCondition(maxDurationTicks, minSpeed, minRelativeSpeed)
}

fun KineticWeaponComponent.knockbackConditions(
	maxDurationTicks: Int,
	minRelativeSpeed: Float? = null,
	minSpeed: Float? = null,
) = apply {
	knockbackConditions = KineticWeaponEffectCondition(maxDurationTicks, minSpeed, minRelativeSpeed)
}
