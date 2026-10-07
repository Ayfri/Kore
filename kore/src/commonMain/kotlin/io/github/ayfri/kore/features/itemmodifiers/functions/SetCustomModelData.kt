package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.colors.ColorAsDecimalSerializer
import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.itemmodifiers.types.ListOperation
import io.github.ayfri.kore.features.predicates.PredicateAsList
import io.github.ayfri.kore.features.predicates.providers.NumberProvider
import io.github.ayfri.kore.features.predicates.providers.constant
import kotlinx.serialization.Serializable

/**
 * Edits the `custom_model_data` lists on an item. Each list supports independent list operations
 * via [ListOperation] with modes like REPLACE_ALL/APPEND/INSERT.
 *
 * [floats] takes number providers, so a float can come from a score, a storage or a `sum` of several of them.
 *
 * Maps to vanilla `minecraft:set_custom_model_data`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/item-modifiers
 * See also: https://minecraft.wiki/w/Item_modifier
 */
@Serializable
data class SetCustomModelData(
	override var conditions: PredicateAsList? = null,
	var colors: ListOperation<@Serializable(ColorAsDecimalSerializer::class) Color>? = null,
	var flags: ListOperation<Boolean>? = null,
	var floats: ListOperation<NumberProvider>? = null,
	var strings: ListOperation<String>? = null,
) : ItemFunction()

/** Add a `set_custom_model_data` step to this modifier. */
fun ItemModifier.setCustomModelData(
	colors: List<Color>? = null,
	flags: List<Boolean>? = null,
	floats: List<Float>? = null,
	strings: List<String>? = null,
	block: SetCustomModelData.() -> Unit = {},
) = SetCustomModelData(
	colors = colors?.let(::ListOperation),
	flags = flags?.let(::ListOperation),
	floats = floats?.let { ListOperation(it.map(::constant)) },
	strings = strings?.let(::ListOperation),
).apply(block).also { modifiers += it }

/**
 * Sets the `floats` list from number providers, replacing the whole list.
 *
 * ```
 * setCustomModelData {
 *     floats(sum(scoreNumber("x", "#a"), scoreNumber("x", "#b", 0.001f))) // floats: {values: [{type: "minecraft:sum", ...}]}
 * }
 * ```
 */
fun SetCustomModelData.floats(vararg values: NumberProvider) = apply { floats = ListOperation(values.toList()) }
