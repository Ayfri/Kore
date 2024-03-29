package io.github.ayfri.kore.arguments.components

import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.data.item.ItemStack
import io.github.ayfri.kore.generated.ComponentTypes
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

fun Components.chargedProjectiles(projectiles: List<ItemStack>) = apply {
	this[ComponentTypes.CHARGED_PROJECTILES] = ChargedProjectilesComponent(projectiles.toMutableList())
}

fun Components.chargedProjectiles(vararg projectiles: ItemStack) = apply {
	this[ComponentTypes.CHARGED_PROJECTILES] = ChargedProjectilesComponent(projectiles.toMutableList())
}

fun Components.chargedProjectiles(block: ChargedProjectilesComponent.() -> Unit) = apply {
	this[ComponentTypes.CHARGED_PROJECTILES] = ChargedProjectilesComponent(mutableListOf()).apply(block)
}

fun Components.chargedProjectile(id: ItemArgument, count: Short? = null, itemComponents: Components? = null) = apply {
	this[ComponentTypes.CHARGED_PROJECTILES] = ChargedProjectilesComponent(mutableListOf(ItemStack(id.asId(), count, itemComponents)))
}

fun Components.chargedProjectiles(vararg id: ItemArgument) = apply {
	this[ComponentTypes.CHARGED_PROJECTILES] = ChargedProjectilesComponent(id.map { ItemStack(it.asId()) }.toMutableList())
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
