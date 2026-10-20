package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.generated.arguments.types.SoundEventArgument
import kotlinx.serialization.Serializable

/** Speed and timing conditions for when a [KineticWeaponComponent] effect (damage/knockback/dismount) triggers. */
@Serializable
data class KineticWeaponEffectCondition(
	var maxDurationTicks: Int,
	var minSpeed: Float? = null,
	var minRelativeSpeed: Float? = null,
)

/**
 * Represents the `minecraft:kinetic_weapon` item component, which configures kinetic weapon properties for mounted combat (damage multiplier, conditions).
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#kinetic_weapon
 */
@Serializable
data class KineticWeaponComponent(
	var damageConditions: KineticWeaponEffectCondition? = null,
	var damageMultiplier: Float? = null,
	var delayTicks: Int? = null,
	var dismountConditions: KineticWeaponEffectCondition? = null,
	var forwardMovement: Float? = null,
	var hitSound: SoundEventArgument? = null,
	var knockbackConditions: KineticWeaponEffectCondition? = null,
	var sound: SoundEventArgument? = null,
) : Component()

/** Configures kinetic weapon properties for mounted combat (damage multiplier, conditions). */
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
