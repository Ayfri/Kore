package io.github.ayfri.kore.arguments.components.types

import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeaponComponent(
	@SerialName("damage_per_attack")
	var damagePerAttack: Int = 1,
	@SerialName("can_disable_blocking")
	var canDisableBlocking: Boolean = false,
) : Component()

fun ComponentsScope.weapon(
	damagePerAttack: Int = 1,
	canDisableBlocking: Boolean = false,
) = apply {
	this[ItemComponentTypes.WEAPON] = WeaponComponent(damagePerAttack, canDisableBlocking)
}
