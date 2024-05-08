package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.arguments.components.FireworkExplosionComponent
import io.github.ayfri.kore.arguments.components.FireworkExplosionShape
import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.itemmodifiers.types.Mode
import io.github.ayfri.kore.features.itemmodifiers.types.ModeHandler
import io.github.ayfri.kore.features.predicates.PredicateAsList
import kotlinx.serialization.Serializable

@Serializable
data class SetFireworks(
	override var conditions: PredicateAsList? = null,
	var flightDuration: Int? = null,
	var explosions: List<FireworkExplosionComponent> = emptyList(),
) : ItemFunction(), ModeHandler {
	@Serializable
	override lateinit var mode: Mode

	@Serializable
	override var offset: Int? = null

	@Serializable
	override var size: Int? = null
}

fun ItemModifier.setFireworks(
	flightDuration: Int? = null,
	explosions: List<FireworkExplosionComponent> = emptyList(),
	block: SetFireworks.() -> Unit = {},
) = SetFireworks(flightDuration = flightDuration, explosions = explosions).also {
	this.modifiers += it.apply(block)
}

fun SetFireworks.explosion(shape: FireworkExplosionShape, block: FireworkExplosionComponent.() -> Unit) = apply {
	explosions += FireworkExplosionComponent(shape).apply(block)
}
