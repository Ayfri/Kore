package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.colors.ColorAsDecimalSerializer
import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.itemmodifiers.types.ListOperation
import io.github.ayfri.kore.features.predicates.conditions.PredicateCondition
import io.github.ayfri.kore.features.predicates.providers.ConstantContextFloatProvider
import io.github.ayfri.kore.features.predicates.providers.ConstantContextIntProvider
import io.github.ayfri.kore.features.predicates.providers.ContextFloatProvider
import io.github.ayfri.kore.features.predicates.providers.ContextIntProvider
import io.github.ayfri.kore.serializers.EitherInlineSerializer
import kotlinx.serialization.KeepGeneratedSerializer
import kotlinx.serialization.Serializable

/**
 * A `custom_model_data` color, written either as a decimal RGB number ([color]) or as a context int provider ([provider]).
 * Exactly one of the two is set.
 *
 * @property color The fixed color, written as its decimal RGB value.
 * @property provider The context int provider computing the decimal RGB value.
 */
@KeepGeneratedSerializer
@Serializable(with = CustomModelDataColor.Companion.CustomModelDataColorSerializer::class)
data class CustomModelDataColor(
	@Serializable(ColorAsDecimalSerializer::class) var color: Color? = null,
	var provider: ContextIntProvider? = null,
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
 * [floats] takes context float providers, so a float can come from a storage or an `add` of several of them.
 * [colors] takes fixed colors or context int providers.
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
	var floats: ListOperation<ContextFloatProvider>? = null,
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
	floats = floats?.let { ListOperation(it.map(::ConstantContextFloatProvider)) },
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
 * Sets the `colors` list from decimal RGB numbers, replacing the whole list.
 *
 * ```
 * setCustomModelData {
 *     colors(0xFF5555) // colors: {values: [16733525]}
 * }
 * ```
 */
fun SetCustomModelData.colors(vararg values: Int) =
	apply { colors = ListOperation(values.map { CustomModelDataColor(provider = ConstantContextIntProvider(it)) }) }

/**
 * Sets the `colors` list from context int providers, replacing the whole list. Each provider computes a decimal RGB value.
 *
 * ```
 * setCustomModelData {
 *     colors(score("color", "#tint")) // colors: {values: [{type: "minecraft:score", ...}]}
 * }
 * ```
 */
fun SetCustomModelData.colors(vararg values: ContextIntProvider) =
	apply { colors = ListOperation(values.map { CustomModelDataColor(provider = it) }) }

/**
 * Sets the `floats` list from context float providers, replacing the whole list.
 *
 * ```
 * setCustomModelData {
 *     floats(add(floatStorage("my_pack:data", "a"), floatStorage("my_pack:data", "b"))) // floats: {values: [{type: "minecraft:add", ...}]}
 * }
 * ```
 */
fun SetCustomModelData.floats(vararg values: ContextFloatProvider) = apply { floats = ListOperation(values.toList()) }

/**
 * Sets the `floats` list from fixed numbers, replacing the whole list. Needs at least one value, a bare `floats()` is ambiguous with the provider overload.
 *
 * ```
 * setCustomModelData {
 *     floats(0.5f, 1f) // floats: {values: [0.5, 1.0]}
 * }
 * ```
 */
fun SetCustomModelData.floats(vararg values: Float) = apply { floats = ListOperation(values.map(::ConstantContextFloatProvider)) }
