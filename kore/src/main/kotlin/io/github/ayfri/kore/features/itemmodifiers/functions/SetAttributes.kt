package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.arguments.types.resources.AttributeArgument
import io.github.ayfri.kore.data.item.AttributeModifier
import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.PredicateAsList
import kotlinx.serialization.Serializable

@Serializable
data class SetAttributes(
	override var conditions: PredicateAsList? = null,
	val modifiers: List<AttributeModifier>,
) : ItemFunction()

fun ItemModifier.setAttributes(modifiers: MutableList<AttributeModifier>.() -> Unit = {}) =
	SetAttributes(modifiers = buildList(modifiers)).also { this.modifiers += it }

fun ItemModifier.setAttribute(attribute: AttributeArgument, modifier: AttributeModifier.() -> Unit = {}) =
	SetAttributes(modifiers = listOf(AttributeModifier(attribute).apply(modifier))).also { modifiers += it }
