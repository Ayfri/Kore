package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.arguments.components.item.FireworkExplosionComponent
import io.github.ayfri.kore.arguments.components.item.FireworkExplosionShape
import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.itemmodifiers.types.Mode
import io.github.ayfri.kore.features.itemmodifiers.types.ModeHandler
import io.github.ayfri.kore.features.predicates.PredicateAsList
import kotlinx.serialization.Serializable

@Serializable
data class SetFireworksExplosions(
	var values: List<FireworkExplosionComponent> = emptyList(),
) : ModeHandler {
	@Serializable
	override var mode: Mode = Mode.REPLACE_ALL

	@Serializable
	override var offset: Int? = null

	@Serializable
	override var size: Int? = null
}

@Serializable
data class SetFireworks(
	override var conditions: PredicateAsList? = null,
	var flightDuration: Int? = null,
	var explosions: SetFireworksExplosions? = null,
) : ItemFunction()

fun ItemModifier.setFireworks(
	flightDuration: Int? = null,
	explosions: List<FireworkExplosionComponent>? = null,
	block: SetFireworks.() -> Unit = {},
) = SetFireworks(flightDuration = flightDuration, explosions = explosions?.let(::SetFireworksExplosions)).apply(block).also {
	modifiers += it.apply(block)
}

fun SetFireworks.explosions(block: SetFireworksExplosions.() -> Unit) = apply {
	explosions = SetFireworksExplosions().apply(block)
}

fun SetFireworksExplosions.explosion(shape: FireworkExplosionShape, block: FireworkExplosionComponent.() -> Unit) = apply {
	values += FireworkExplosionComponent(shape).apply(block)
}
