package io.github.ayfri.kore.features.predicates.providers

import io.github.ayfri.kore.arguments.numbers.toStringWithDecimal
import io.github.ayfri.kore.features.enchantments.values.LevelBased
import io.github.ayfri.kore.features.predicates.conditions.PredicateCondition
import io.github.ayfri.kore.generated.arguments.ContextFloatProviderOrTagArgument
import io.github.ayfri.kore.generated.arguments.tagged.ContextFloatProviderTagArgument
import io.github.ayfri.kore.generated.arguments.types.ContextFloatProviderArgument
import io.github.ayfri.kore.generated.arguments.types.EnvironmentAttributeArgument
import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.IdOrTagReferenceSerializer
import io.github.ayfri.kore.serializers.InlinableList
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonUnquotedLiteral
import kotlinx.serialization.serializer

/**
 * Picks a float, either a fixed one or one derived from the loot context, used by the loot tables, the item modifiers,
 * the predicates, the trades and the `compute` command.
 *
 * Wherever the game takes a context float provider it also takes a bare number, the bare ID of a
 * [context float provider file][io.github.ayfri.kore.features.contextproviders.ContextFloatProviderFile], or a typed
 * object. A [ConstantContextFloatProvider] is inlined to its value, so `constant(5f)` serializes as `5.0` instead of an
 * object with a `type` field, and a [ContextFloatProviderReference] is the bare string of its ID. Every other provider is
 * an object that always carries its `type` field: the game rejects one without it.
 *
 * Floats are 32-bit, so integers are only exact up to 2^24 (16 777 216). Outside the `/compute` command, a provider that
 * returns `Infinity` or `NaN`, or hits an error, returns `0`.
 *
 * Every builder is an extension on [NumberProviderScope] (e.g. [constant], [uniform]), so they only resolve inside a
 * block that actually accepts a number provider.
 *
 * Minecraft Wiki: [Number provider](https://minecraft.wiki/w/Number_provider)
 */
@GeneratedSealedSerializer
@Serializable(with = ContextFloatProvider.Companion.ContextFloatProviderSerializer::class)
sealed class ContextFloatProvider {
	companion object {
		data object ContextFloatProviderSerializer : NamespacedPolymorphicSerializer<ContextFloatProvider>(
			contextFloatProviderSealedSerializer(),
			inlinedSubtype = "constant",
			suffix = "ContextFloatProvider",
		)
	}
}

/**
 * Returns the absolute value of [input].
 *
 * Minecraft Wiki: [Number provider - abs](https://minecraft.wiki/w/Number_provider#abs)
 *
 * @property input The provider whose absolute value is returned.
 */
@Serializable
data class AbsContextFloatProvider(var input: ContextFloatProvider) : ContextFloatProvider()

/**
 * Returns the sum of every provider of [inputs].
 *
 * Minecraft Wiki: [Number provider - add](https://minecraft.wiki/w/Number_provider#add)
 *
 * @property inputs The providers whose values are added together: one provider or ID, a list mixing IDs and inline providers, or a `#` tag of float providers. Never empty.
 */
@Serializable
data class AddContextFloatProvider(var inputs: InlinableList<ContextFloatProvider>) : ContextFloatProvider() {
	init {
		require(inputs.isNotEmpty()) { "An 'add' provider needs at least one input." }
	}
}

/**
 * Returns the mean of every provider of [inputs].
 *
 * Minecraft Wiki: [Number provider - avg](https://minecraft.wiki/w/Number_provider#avg)
 *
 * @property inputs The providers whose values are averaged: one provider or ID, a list mixing IDs and inline providers, or a `#` tag of float providers. Never empty.
 */
@Serializable
data class AvgContextFloatProvider(var inputs: InlinableList<ContextFloatProvider>) : ContextFloatProvider() {
	init {
		require(inputs.isNotEmpty()) { "An 'avg' provider needs at least one input." }
	}
}

/**
 * Returns [input] rounded up to the next whole number.
 *
 * Minecraft Wiki: [Number provider - ceil](https://minecraft.wiki/w/Number_provider#ceil)
 *
 * @property input The provider to round.
 */
@Serializable
data class CeilContextFloatProvider(var input: ContextFloatProvider) : ContextFloatProvider()

/**
 * Returns [onTrue] when [condition] passes, [onFalse] otherwise.
 *
 * Minecraft Wiki: [Number provider - conditional](https://minecraft.wiki/w/Number_provider#conditional)
 *
 * @property condition The predicate condition evaluated against the loot context.
 * @property onFalse The provider used when [condition] fails, `0` when `null`.
 * @property onTrue The provider used when [condition] passes.
 */
@Serializable
data class ConditionalContextFloatProvider(
	var condition: PredicateCondition,
	var onFalse: ContextFloatProvider? = null,
	var onTrue: ContextFloatProvider,
) : ContextFloatProvider()

/**
 * Always returns the same float.
 *
 * It is inlined when serialized, so it produces `5.0` rather than an object with a `type` field. The decimal point is
 * kept on every platform.
 *
 * Minecraft Wiki: [Number provider - constant](https://minecraft.wiki/w/Number_provider#constant)
 *
 * @property value The value returned on every call.
 */
@Serializable(with = ConstantContextFloatProvider.Companion.ConstantContextFloatProviderSerializer::class)
data class ConstantContextFloatProvider(val value: Float) : ContextFloatProvider() {
	companion object {
		@OptIn(ExperimentalSerializationApi::class)
		data object ConstantContextFloatProviderSerializer :
			InlineAutoSerializer<ConstantContextFloatProvider, Float>(
				serializer<Float>(),
				ConstantContextFloatProvider::value,
				::ConstantContextFloatProvider,
				"constant",
			) {
			override fun serialize(encoder: Encoder, value: ConstantContextFloatProvider) =
				if (encoder is JsonEncoder && value.value.isFinite()) {
					encoder.encodeJsonElement(JsonUnquotedLiteral(value.value.toStringWithDecimal))
				} else super.serialize(encoder, value)
		}
	}
}

/**
 * Returns the cosine of [input], in radians.
 *
 * Minecraft Wiki: [Number provider - cos](https://minecraft.wiki/w/Number_provider#cos)
 *
 * @property input The angle in radians.
 */
@Serializable
data class CosContextFloatProvider(var input: ContextFloatProvider) : ContextFloatProvider()

/**
 * Returns [left] divided by [right].
 *
 * Minecraft Wiki: [Number provider - div](https://minecraft.wiki/w/Number_provider#div)
 *
 * @property left The dividend.
 * @property right The divisor.
 */
@Serializable
data class DivContextFloatProvider(var left: ContextFloatProvider, var right: ContextFloatProvider) : ContextFloatProvider()

/**
 * Returns [amount] evaluated against the level of the enchantment the loot context runs for.
 *
 * Minecraft Wiki: [Number provider - enchantment_level](https://minecraft.wiki/w/Number_provider#enchantment_level)
 *
 * @property amount The level-based value evaluated at the current enchantment level.
 */
@Serializable
data class EnchantmentLevelContextFloatProvider(var amount: LevelBased) : ContextFloatProvider()

/**
 * Returns the current value of a numeric environment attribute.
 *
 * Requires a loot context with an origin position as long as the attribute can vary positionally.
 *
 * Minecraft Wiki: [Number provider - environment_attribute](https://minecraft.wiki/w/Number_provider#environment_attribute)
 *
 * @property attribute The environment attribute to read, which must be convertible to a float.
 */
@Serializable
data class EnvironmentAttributeContextFloatProvider(var attribute: EnvironmentAttributeArgument) : ContextFloatProvider()

/**
 * Returns [input] rounded down to the previous whole number.
 *
 * Minecraft Wiki: [Number provider - floor](https://minecraft.wiki/w/Number_provider#floor)
 *
 * @property input The provider to round.
 */
@Serializable
data class FloorContextFloatProvider(var input: ContextFloatProvider) : ContextFloatProvider()

/**
 * Returns [input] as a float.
 *
 * Minecraft Wiki: [Number provider - from_int](https://minecraft.wiki/w/Number_provider#from_int)
 *
 * @property input The int provider to convert.
 */
@Serializable
data class FromIntContextFloatProvider(var input: ContextIntProvider) : ContextFloatProvider()

/**
 * Returns the euclidean length of the vector whose components are the providers of [inputs]: the square root of the sum of their squares.
 *
 * Minecraft Wiki: [Number provider - length](https://minecraft.wiki/w/Number_provider#length)
 *
 * @property inputs The providers holding the components: one provider or ID, a list mixing IDs and inline providers, or a `#` tag of float providers. Never empty.
 */
@Serializable
data class LengthContextFloatProvider(var inputs: InlinableList<ContextFloatProvider>) : ContextFloatProvider() {
	init {
		require(inputs.isNotEmpty()) { "A 'length' provider needs at least one input." }
	}
}

/**
 * Returns the largest value among the providers of [inputs].
 *
 * Minecraft Wiki: [Number provider - max](https://minecraft.wiki/w/Number_provider#max)
 *
 * @property inputs The providers to compare: one provider or ID, a list mixing IDs and inline providers, or a `#` tag of float providers. Never empty.
 */
@Serializable
data class MaxContextFloatProvider(var inputs: InlinableList<ContextFloatProvider>) : ContextFloatProvider() {
	init {
		require(inputs.isNotEmpty()) { "A 'max' provider needs at least one input." }
	}
}

/**
 * Returns the smallest value among the providers of [inputs].
 *
 * Minecraft Wiki: [Number provider - min](https://minecraft.wiki/w/Number_provider#min)
 *
 * @property inputs The providers to compare: one provider or ID, a list mixing IDs and inline providers, or a `#` tag of float providers. Never empty.
 */
@Serializable
data class MinContextFloatProvider(var inputs: InlinableList<ContextFloatProvider>) : ContextFloatProvider() {
	init {
		require(inputs.isNotEmpty()) { "A 'min' provider needs at least one input." }
	}
}

/**
 * Returns the remainder of [left] divided by [right], with the sign of [right], `NaN` when [right] is `0`.
 *
 * Minecraft Wiki: [Number provider - mod](https://minecraft.wiki/w/Number_provider#mod)
 *
 * @property left The dividend.
 * @property right The divisor.
 */
@Serializable
data class ModContextFloatProvider(var left: ContextFloatProvider, var right: ContextFloatProvider) : ContextFloatProvider()

/**
 * Returns the product of every provider of [inputs].
 *
 * Minecraft Wiki: [Number provider - mul](https://minecraft.wiki/w/Number_provider#mul)
 *
 * @property inputs The providers whose values are multiplied together: one provider or ID, a list mixing IDs and inline providers, or a `#` tag of float providers. Never empty.
 */
@Serializable
data class MulContextFloatProvider(var inputs: InlinableList<ContextFloatProvider>) : ContextFloatProvider() {
	init {
		require(inputs.isNotEmpty()) { "A 'mul' provider needs at least one input." }
	}
}

/**
 * Returns the opposite of [input].
 *
 * Minecraft Wiki: [Number provider - negate](https://minecraft.wiki/w/Number_provider#negate)
 *
 * @property input The provider whose opposite is returned.
 */
@Serializable
data class NegateContextFloatProvider(var input: ContextFloatProvider) : ContextFloatProvider()

/**
 * Returns the provider of the first case of [cases] whose condition passes, [default] when none does.
 *
 * Minecraft Wiki: [Number provider - number_dispatcher](https://minecraft.wiki/w/Number_provider#number_dispatcher)
 *
 * @property cases The cases, checked in order.
 * @property default The provider used when no case matches, `0` when `null`.
 */
@Serializable
data class NumberDispatcherContextFloatProvider(
	var cases: List<NumberDispatcherContextFloatProviderCase>,
	var default: ContextFloatProvider? = null,
) : ContextFloatProvider()

/**
 * One case of a [NumberDispatcherContextFloatProvider].
 *
 * @property condition The predicate condition that selects this case.
 * @property value The provider used when [condition] passes.
 */
@Serializable
data class NumberDispatcherContextFloatProviderCase(
	var condition: PredicateCondition,
	var value: ContextFloatProvider,
)

/**
 * Returns [base] raised to the power of [exponent].
 *
 * Minecraft Wiki: [Number provider - pow](https://minecraft.wiki/w/Number_provider#pow)
 *
 * @property base The provider raised to the power.
 * @property exponent The power [base] is raised to.
 */
@Serializable
data class PowContextFloatProvider(var base: ContextFloatProvider, var exponent: ContextFloatProvider) : ContextFloatProvider()

/**
 * Returns [input] rounded to the nearest whole number, halves going up.
 *
 * Minecraft Wiki: [Number provider - round](https://minecraft.wiki/w/Number_provider#round)
 *
 * @property input The provider to round.
 */
@Serializable
data class RoundContextFloatProvider(var input: ContextFloatProvider) : ContextFloatProvider()

/**
 * Returns the sine of [input], in radians.
 *
 * Minecraft Wiki: [Number provider - sin](https://minecraft.wiki/w/Number_provider#sin)
 *
 * @property input The angle in radians.
 */
@Serializable
data class SinContextFloatProvider(var input: ContextFloatProvider) : ContextFloatProvider()

/**
 * Returns the square root of [input], `NaN` when it is negative.
 *
 * Minecraft Wiki: [Number provider - sqrt](https://minecraft.wiki/w/Number_provider#sqrt)
 *
 * @property input The provider whose square root is returned.
 */
@Serializable
data class SqrtContextFloatProvider(var input: ContextFloatProvider) : ContextFloatProvider()

/**
 * Returns a float read from command storage.
 *
 * Minecraft Wiki: [Number provider - storage](https://minecraft.wiki/w/Number_provider#storage)
 *
 * @property fallback The provider used when the path holds no number, `0` when `null`.
 * @property path NBT path of the value inside the storage.
 * @property storage Resource location of the storage, e.g. `my_pack:data`.
 */
@Serializable
data class StorageContextFloatProvider(
	var fallback: ContextFloatProvider? = null,
	var path: String,
	var storage: String,
) : ContextFloatProvider()

/**
 * Returns [left] minus [right].
 *
 * Minecraft Wiki: [Number provider - sub](https://minecraft.wiki/w/Number_provider#sub)
 *
 * @property left The provider subtracted from.
 * @property right The provider subtracted.
 */
@Serializable
data class SubContextFloatProvider(var left: ContextFloatProvider, var right: ContextFloatProvider) : ContextFloatProvider()

/**
 * Returns [input] with its decimals removed, rounding toward zero.
 *
 * Minecraft Wiki: [Number provider - truncate](https://minecraft.wiki/w/Number_provider#truncate)
 *
 * @property input The provider to truncate.
 */
@Serializable
data class TruncateContextFloatProvider(var input: ContextFloatProvider) : ContextFloatProvider()

/**
 * Returns a float drawn uniformly between [min] and [max].
 *
 * Minecraft Wiki: [Number provider - uniform](https://minecraft.wiki/w/Number_provider#uniform)
 *
 * @property max Highest value that can be drawn.
 * @property min Lowest value that can be drawn, returned when it is at or above [max].
 */
@Serializable
data class UniformContextFloatProvider(var max: ContextFloatProvider, var min: ContextFloatProvider) : ContextFloatProvider()

/**
 * Returns the value of one provider of [distribution], picked with a probability of its weight over the total weight.
 *
 * Minecraft Wiki: [Number provider - weighted_list](https://minecraft.wiki/w/Number_provider#weighted_list)
 *
 * @property distribution The entries to draw from, which must not be empty.
 */
@Serializable
data class WeightedListContextFloatProvider(var distribution: List<WeightedListContextFloatProviderEntry>) : ContextFloatProvider()

/**
 * One entry of a [WeightedListContextFloatProvider].
 *
 * @property data The provider used when this entry is drawn.
 * @property weight Relative chance of drawing this entry, compared to the other entries of the list.
 */
@Serializable
data class WeightedListContextFloatProviderEntry(var data: ContextFloatProvider, var weight: Int)

/**
 * Returns the float provider [id] points to, written as the bare string `"my_pack:my_provider"` wherever a float
 * provider is expected. A tag (`"#my_pack:my_tag"`) is only valid as the whole `inputs` of an `add`, `avg`, `length`, `max`, `min` or `mul`.
 * A cyclic reference makes the pack fail to load. A list can mix IDs and inline values.
 *
 * Minecraft Wiki: [Number provider](https://minecraft.wiki/w/Number_provider)
 *
 * @property id The ID of the context float provider file, or of the context float provider tag as the `inputs` of a provider that takes several.
 */
@Serializable(with = ContextFloatProviderReference.Companion.ContextFloatProviderReferenceSerializer::class)
data class ContextFloatProviderReference(var id: ContextFloatProviderOrTagArgument) : ContextFloatProvider() {
	companion object {
		data object ContextFloatProviderReferenceSerializer :
			IdOrTagReferenceSerializer<ContextFloatProviderReference, ContextFloatProviderOrTagArgument>(
				ContextFloatProviderReference::id,
				::ContextFloatProviderReference,
				{ name, namespace -> ContextFloatProviderArgument(name, namespace) },
				{ name, namespace -> ContextFloatProviderTagArgument(name, namespace) },
				"context_float_provider_reference",
			)
	}
}
