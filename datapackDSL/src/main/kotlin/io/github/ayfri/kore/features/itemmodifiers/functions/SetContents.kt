package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.arguments.types.resources.BlockEntityTypeArgument
import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.loottables.entries.Item
import io.github.ayfri.kore.features.predicates.PredicateAsList
import kotlinx.serialization.Serializable

@Serializable
data class SetContents(
	override var conditions: PredicateAsList? = null,
	var type: BlockEntityTypeArgument,
	var entries: List<Item>,
) : ItemFunction()

fun ItemModifier.setContents(type: BlockEntityTypeArgument, entries: MutableList<Item>.() -> Unit = {}) =
	SetContents(type = type, entries = buildList(entries)).also { modifiers += it }
