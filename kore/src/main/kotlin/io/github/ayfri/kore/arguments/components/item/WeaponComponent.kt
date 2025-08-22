package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeaponComponent(
	@SerialName("item_damage_per_attack")
	var itemDamagePerAttack: Int = 1,
	@SerialName("disable_blocking_for_seconds")
	var disableBlockingForSeconds: Float = 0f,
) : Component()

fun ComponentsScope.weapon(
	itemDamagePerAttack: Int = 1,
	disableBlockingForSeconds: Float = 0f,
) = apply {
	this[ItemComponentTypes.WEAPON] = WeaponComponent(itemDamagePerAttack, disableBlockingForSeconds)
}
