package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.generated.arguments.types.SoundEventArgument
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PiercingWeaponComponent(
	@SerialName("deals_knockback")
	var dealsKnockback: Boolean? = null,
	var dismounts: Boolean? = null,
	@SerialName("hit_sound")
	var hitSound: SoundEventArgument? = null,
	var sound: SoundEventArgument? = null,
) : Component()

fun ComponentsScope.piercingWeapon(block: PiercingWeaponComponent.() -> Unit = {}) = apply {
	this[ItemComponentTypes.PIERCING_WEAPON] = PiercingWeaponComponent().apply(block)
}
