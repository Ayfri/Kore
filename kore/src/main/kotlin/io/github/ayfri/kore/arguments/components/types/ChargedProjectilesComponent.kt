package io.github.ayfri.kore.arguments.components.types

import io.github.ayfri.kore.arguments.components.Components
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.data.item.ItemStack
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.serializers.InlineSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer

@Serializable(with = ChargedProjectilesComponent.Companion.ChargedProjectilesSerializer::class)
data class ChargedProjectilesComponent(
	val projectiles: MutableList<ItemStack>,
) : Component() {
	companion object {
		object ChargedProjectilesSerializer : InlineSerializer<ChargedProjectilesComponent, List<ItemStack>>(
			ListSerializer(ItemStack.serializer()),
			ChargedProjectilesComponent::projectiles
		)
	}
}

fun ComponentsScope.chargedProjectiles(projectiles: List<ItemStack>) = apply {
	this[ItemComponentTypes.CHARGED_PROJECTILES] = ChargedProjectilesComponent(projectiles.toMutableList())
}

fun ComponentsScope.chargedProjectiles(vararg projectiles: ItemStack) = apply {
	this[ItemComponentTypes.CHARGED_PROJECTILES] = ChargedProjectilesComponent(projectiles.toMutableList())
}

fun ComponentsScope.chargedProjectiles(block: ChargedProjectilesComponent.() -> Unit) = apply {
	this[ItemComponentTypes.CHARGED_PROJECTILES] = ChargedProjectilesComponent(mutableListOf()).apply(block)
}

fun ComponentsScope.chargedProjectile(id: ItemArgument, count: Short? = null, itemComponents: Components? = null) = apply {
	this[ItemComponentTypes.CHARGED_PROJECTILES] = ChargedProjectilesComponent(mutableListOf(ItemStack(id.asId(), count, itemComponents)))
}

fun ComponentsScope.chargedProjectiles(vararg id: ItemArgument) = apply {
	this[ItemComponentTypes.CHARGED_PROJECTILES] = ChargedProjectilesComponent(id.map { ItemStack(it.asId()) }.toMutableList())
}

fun ChargedProjectilesComponent.projectile(projectile: ItemStack) = apply {
	projectiles += projectile
}

fun ChargedProjectilesComponent.projectile(
	id: ItemArgument,
	count: Short? = null,
	components: Components? = null,
) = apply {
	projectiles += ItemStack(id.asId(), count, components)
}

fun ChargedProjectilesComponent.projectile(
	id: ItemArgument,
	count: Short? = null,
	block: Components.() -> Unit,
) = apply {
	projectiles += ItemStack(id.asId(), count, Components().apply(block))
}
