package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.colors.ColorAsDecimalSerializer
import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.itemmodifiers.types.ListOperation
import io.github.ayfri.kore.features.predicates.conditions.PredicateCondition
import io.github.ayfri.kore.features.predicates.providers.ConstantNumberProvider
import io.github.ayfri.kore.features.predicates.providers.NumberProvider
import io.github.ayfri.kore.serializers.EitherInlineSerializer
import kotlinx.serialization.KeepGeneratedSerializer
import kotlinx.serialization.Serializable

/**
 * A `custom_model_data` color, written either as a decimal RGB number ([color]) or as a number provider ([provider]).
 * Exactly one of the two is set.
 *
 * @property color The fixed color, written as its decimal RGB value.
 * @property provider The number provider computing the decimal RGB value.
 */
@KeepGeneratedSerializer
@Serializable(with = CustomModelDataColor.Companion.CustomModelDataColorSerializer::class)
data class CustomModelDataColor(
	@Serializable(ColorAsDecimalSerializer::class) var color: Color? = null,
	var provider: NumberProvider? = null,
) {
	companion object {
		data object CustomModelDataColorSerializer :
			EitherInlineSerializer<CustomModelDataColor>(generatedSerializer(), "color", "provider")
	}
}

/**
 * Edits the `custom_model_data` lists on an item. Each list supports independent list operations
 * via [ListOperation] with modes like REPLACE_ALL/APPEND/INSERT.
 *
 * [floats] takes number providers, so a float can come from a score, a storage or a `sum` of several of them.
 * [colors] takes fixed colors or number providers.
 *
 * Maps to vanilla `minecraft:set_custom_model_data`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/item-modifiers
 * See also: https://minecraft.wiki/w/Item_modifier
 */
@Serializable
data class SetCustomModelData(
	var colors: ListOperation<CustomModelDataColor>? = null,
	override var condition: PredicateCondition? = null,
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
	colors = colors?.let { ListOperation(it.map(::CustomModelDataColor)) },
	flags = flags?.let(::ListOperation),
	floats = floats?.let { ListOperation(it.map(::ConstantNumberProvider)) },
	strings = strings?.let(::ListOperation),
).apply(block).also { modifiers += it }

/**
 * Sets the `colors` list from fixed colors, replacing the whole list.
 *
 * ```
 * setCustomModelData {
 *     colors(Color.RED, Color.BLUE) // colors: {values: [16733525, 5592575]}
 * }
 * ```
 */
fun SetCustomModelData.colors(vararg values: Color) = apply { colors = ListOperation(values.map(::CustomModelDataColor)) }

/**
 * Sets the `colors` list from number providers, replacing the whole list. Each provider computes a decimal RGB value.
 *
 * ```
 * setCustomModelData {
 *     colors(scoreNumber("color", "#tint")) // colors: {values: [{type: "minecraft:score", ...}]}
 * }
 * ```
 */
fun SetCustomModelData.colors(vararg values: NumberProvider) =
	apply { colors = ListOperation(values.map { CustomModelDataColor(provider = it) }) }

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
