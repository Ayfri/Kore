package io.github.ayfri.kore.features.predicates.providers

import io.github.ayfri.kore.features.predicates.conditions.PredicateCondition
import io.github.ayfri.kore.features.predicates.providers.utils.requireTagOnlyAlone
import io.github.ayfri.kore.generated.arguments.ContextIntProviderOrTagArgument
import io.github.ayfri.kore.generated.arguments.tagged.ContextIntProviderTagArgument
import io.github.ayfri.kore.generated.arguments.types.ContextIntProviderArgument
import io.github.ayfri.kore.generated.arguments.types.EnvironmentAttributeArgument
import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.IdOrTagReferenceSerializer
import io.github.ayfri.kore.serializers.InlinableList
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

/**
 * Picks an integer, either a fixed one or one derived from the loot context, used by the loot tables, the item
 * modifiers, the predicates, the trades and the `compute` command.
 *
 * Wherever the game takes a context int provider it also takes a bare number, the bare ID of a
 * [context int provider file][io.github.ayfri.kore.features.contextproviders.ContextIntProviderFile], or a typed
 * object. A [ConstantContextIntProvider] is inlined to its value, so `constant(5)` serializes as `5` instead of an object
 * with a `type` field, and a [ContextIntProviderReference] is the bare string of its ID. Every other provider is an
 * object that always carries its `type` field: the game rejects one without it.
 *
 * Outside the `/compute` command, a provider that hits an error (a division by zero, an overflow) returns `0`.
 *
 * Every builder is an extension on [NumberProviderScope] (e.g. [constant], [uniform]), so they only resolve inside a
 * block that actually accepts a number provider.
 *
 * Minecraft Wiki: [Number provider](https://minecraft.wiki/w/Number_provider)
 */
@GeneratedSealedSerializer
@Serializable(with = ContextIntProvider.Companion.ContextIntProviderSerializer::class)
sealed class ContextIntProvider {
	companion object {
		data object ContextIntProviderSerializer : NamespacedPolymorphicSerializer<ContextIntProvider>(
			contextIntProviderSealedSerializer(),
			inlinedSubtype = "constant",
			suffix = "ContextIntProvider",
		)
	}
}

/**
 * Returns the absolute value of [input], which fails for the smallest int.
 *
 * Minecraft Wiki: [Number provider - abs](https://minecraft.wiki/w/Number_provider#abs)
 *
 * @property input The provider whose absolute value is returned.
 */
@Serializable
data class AbsContextIntProvider(var input: ContextIntProvider) : ContextIntProvider()

/**
 * Returns the sum of every provider of [inputs], which fails when the result doesn't fit in 32 bits.
 *
 * Minecraft Wiki: [Number provider - add](https://minecraft.wiki/w/Number_provider#add)
 *
 * @property inputs The providers whose values are added together: one provider or ID, a list mixing IDs and inline providers, or a `#` tag of int providers, only as the whole list. Never empty.
 */
@Serializable
data class AddContextIntProvider(var inputs: InlinableList<ContextIntProvider>) : ContextIntProvider() {
	init {
		require(inputs.isNotEmpty()) { "An 'add' provider needs at least one input." }
		inputs.requireTagOnlyAlone()
	}
}

/**
 * Returns the mean of every provider of [inputs], which can't overflow.
 *
 * Minecraft Wiki: [Number provider - avg](https://minecraft.wiki/w/Number_provider#avg)
 *
 * @property inputs The providers whose values are averaged, rounded toward zero: one provider or ID, a list mixing IDs and inline providers, or a `#` tag of int providers, only as the whole list. Never empty.
 */
@Serializable
data class AvgContextIntProvider(var inputs: InlinableList<ContextIntProvider>) : ContextIntProvider() {
	init {
		require(inputs.isNotEmpty()) { "An 'avg' provider needs at least one input." }
		inputs.requireTagOnlyAlone()
	}
}

/**
 * Returns the number of successes of [n] trials, each succeeding with a probability of [p].
 *
 * Minecraft Wiki: [Number provider - binomial](https://minecraft.wiki/w/Number_provider#binomial)
 *
 * @property n The number of trials.
 * @property p The probability of success of each individual trial, between `0` and `1`.
 */
@Serializable
data class BinomialContextIntProvider(var n: ContextIntProvider, var p: ContextFloatProvider) : ContextIntProvider()

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
data class ConditionalContextIntProvider(
	var condition: PredicateCondition,
	var onFalse: ContextIntProvider? = null,
	var onTrue: ContextIntProvider,
) : ContextIntProvider()

/**
 * Always returns the same integer.
 *
 * It is inlined when serialized, so it produces `5` rather than an object with a `type` field.
 *
 * Minecraft Wiki: [Number provider - constant](https://minecraft.wiki/w/Number_provider#constant)
 *
 * @property value The value returned on every call.
 */
@Serializable(with = ConstantContextIntProvider.Companion.ConstantContextIntProviderSerializer::class)
data class ConstantContextIntProvider(val value: Int) : ContextIntProvider() {
	companion object {
		data object ConstantContextIntProviderSerializer :
			InlineAutoSerializer<ConstantContextIntProvider, Int>(
				serializer<Int>(),
				ConstantContextIntProvider::value,
				::ConstantContextIntProvider,
				"constant",
			)
	}
}

/**
 * Returns [left] divided by [right], rounded toward zero.
 *
 * Minecraft Wiki: [Number provider - div](https://minecraft.wiki/w/Number_provider#div)
 *
 * @property left The dividend.
 * @property right The divisor, which fails the division when it returns `0`.
 */
@Serializable
data class DivContextIntProvider(var left: ContextIntProvider, var right: ContextIntProvider) : ContextIntProvider()

/**
 * Returns the current value of an environment attribute.
 *
 * No vanilla attribute is representable as an integer, so wrap a float one with [fromFloat] to read it as an int:
 * `fromFloat(floatEnvironmentAttribute(attribute))`.
 *
 * Minecraft Wiki: [Number provider - environment_attribute](https://minecraft.wiki/w/Number_provider#environment_attribute)
 *
 * @property attribute The environment attribute to read, which must hold an integer.
 */
@Serializable
data class EnvironmentAttributeContextIntProvider(var attribute: EnvironmentAttributeArgument) : ContextIntProvider()

/**
 * Returns [left] divided by [right], rounded toward negative infinity, which fails when the result doesn't fit in 32 bits.
 *
 * Minecraft Wiki: [Number provider - floor_div](https://minecraft.wiki/w/Number_provider#floor_div)
 *
 * @property left The dividend.
 * @property right The divisor, which fails the division when it returns `0`.
 */
@Serializable
data class FloorDivContextIntProvider(var left: ContextIntProvider, var right: ContextIntProvider) : ContextIntProvider()

/**
 * Returns the remainder of [left] divided by [right], with the sign of [right].
 *
 * Minecraft Wiki: [Number provider - floor_mod](https://minecraft.wiki/w/Number_provider#floor_mod)
 *
 * @property left The dividend.
 * @property right The divisor, which fails the division when it returns `0`.
 */
@Serializable
data class FloorModContextIntProvider(var left: ContextIntProvider, var right: ContextIntProvider) : ContextIntProvider()

/**
 * Returns [input] truncated toward zero, which fails when it isn't finite or doesn't fit in an integer.
 *
 * Minecraft Wiki: [Number provider - from_float](https://minecraft.wiki/w/Number_provider#from_float)
 *
 * @property input The float provider to convert.
 */
@Serializable
data class FromFloatContextIntProvider(var input: ContextFloatProvider) : ContextIntProvider()

/**
 * Returns the largest value among the providers of [inputs].
 *
 * Minecraft Wiki: [Number provider - max](https://minecraft.wiki/w/Number_provider#max)
 *
 * @property inputs The providers to compare: one provider or ID, a list mixing IDs and inline providers, or a `#` tag of int providers, only as the whole list. Never empty.
 */
@Serializable
data class MaxContextIntProvider(var inputs: InlinableList<ContextIntProvider>) : ContextIntProvider() {
	init {
		require(inputs.isNotEmpty()) { "A 'max' provider needs at least one input." }
		inputs.requireTagOnlyAlone()
	}
}

/**
 * Returns the smallest value among the providers of [inputs].
 *
 * Minecraft Wiki: [Number provider - min](https://minecraft.wiki/w/Number_provider#min)
 *
 * @property inputs The providers to compare: one provider or ID, a list mixing IDs and inline providers, or a `#` tag of int providers, only as the whole list. Never empty.
 */
@Serializable
data class MinContextIntProvider(var inputs: InlinableList<ContextIntProvider>) : ContextIntProvider() {
	init {
		require(inputs.isNotEmpty()) { "A 'min' provider needs at least one input." }
		inputs.requireTagOnlyAlone()
	}
}

/**
 * Returns the remainder of [left] divided by [right], with the sign of [left].
 *
 * Minecraft Wiki: [Number provider - mod](https://minecraft.wiki/w/Number_provider#mod)
 *
 * @property left The dividend.
 * @property right The divisor, which fails the division when it returns `0`.
 */
@Serializable
data class ModContextIntProvider(var left: ContextIntProvider, var right: ContextIntProvider) : ContextIntProvider()

/**
 * Returns the product of every provider of [inputs], which fails when the result doesn't fit in 32 bits.
 *
 * Minecraft Wiki: [Number provider - mul](https://minecraft.wiki/w/Number_provider#mul)
 *
 * @property inputs The providers whose values are multiplied together: one provider or ID, a list mixing IDs and inline providers, or a `#` tag of int providers, only as the whole list. Never empty.
 */
@Serializable
data class MulContextIntProvider(var inputs: InlinableList<ContextIntProvider>) : ContextIntProvider() {
	init {
		require(inputs.isNotEmpty()) { "A 'mul' provider needs at least one input." }
		inputs.requireTagOnlyAlone()
	}
}

/**
 * Returns the opposite of [input], which fails for the smallest int.
 *
 * Minecraft Wiki: [Number provider - negate](https://minecraft.wiki/w/Number_provider#negate)
 *
 * @property input The provider whose opposite is returned.
 */
@Serializable
data class NegateContextIntProvider(var input: ContextIntProvider) : ContextIntProvider()

/**
 * Returns the provider of the first case of [cases] whose condition passes, [default] when none does.
 *
 * Minecraft Wiki: [Number provider - number_dispatcher](https://minecraft.wiki/w/Number_provider#number_dispatcher)
 *
 * @property cases The cases, checked in order.
 * @property default The provider used when no case matches, `0` when `null`.
 */
@Serializable
data class NumberDispatcherContextIntProvider(
	var cases: List<NumberDispatcherContextIntProviderCase>,
	var default: ContextIntProvider? = null,
) : ContextIntProvider()

/**
 * One case of a [NumberDispatcherContextIntProvider].
 *
 * @property condition The predicate condition that selects this case.
 * @property value The provider used when [condition] passes.
 */
@Serializable
data class NumberDispatcherContextIntProviderCase(
	var condition: PredicateCondition,
	var value: ContextIntProvider,
)

/**
 * Returns [base] raised to the power of [exponent], which fails when the result doesn't fit in 32 bits or for `0` to the power of `0`.
 *
 * Minecraft Wiki: [Number provider - pow](https://minecraft.wiki/w/Number_provider#pow)
 *
 * @property base The provider raised to the power.
 * @property exponent The power [base] is raised to.
 */
@Serializable
data class PowContextIntProvider(var base: ContextIntProvider, var exponent: ContextIntProvider) : ContextIntProvider()

/**
 * Returns the value a score holder has on a scoreboard objective.
 *
 * Minecraft Wiki: [Number provider - score](https://minecraft.wiki/w/Number_provider#score)
 *
 * @property fallback The provider used when the holder has no score, `0` when `null`.
 * @property score The scoreboard objective to read.
 * @property target The score holder to read, either a loot context entity or a fixed name.
 */
@Serializable
data class ScoreContextIntProvider(
	var fallback: ContextIntProvider? = null,
	var score: String,
	var target: ScoreProvider,
) : ContextIntProvider()

/**
 * Returns an integer read from command storage.
 *
 * Minecraft Wiki: [Number provider - storage](https://minecraft.wiki/w/Number_provider#storage)
 *
 * @property fallback The provider used when the path holds no number, `0` when `null`.
 * @property path NBT path of the value inside the storage.
 * @property storage Resource location of the storage, e.g. `my_pack:data`.
 */
@Serializable
data class StorageContextIntProvider(
	var fallback: ContextIntProvider? = null,
	var path: String,
	var storage: String,
) : ContextIntProvider()

/**
 * Returns [left] minus [right].
 *
 * Minecraft Wiki: [Number provider - sub](https://minecraft.wiki/w/Number_provider#sub)
 *
 * @property left The provider subtracted from.
 * @property right The provider subtracted.
 */
@Serializable
data class SubContextIntProvider(var left: ContextIntProvider, var right: ContextIntProvider) : ContextIntProvider()

/**
 * Returns an integer drawn uniformly between [min] and [max], both included, or [min] when [min] is at or above [max].
 *
 * Minecraft Wiki: [Number provider - uniform](https://minecraft.wiki/w/Number_provider#uniform)
 *
 * @property max Highest value that can be drawn.
 * @property min Lowest value that can be drawn.
 */
@Serializable
data class UniformContextIntProvider(var max: ContextIntProvider, var min: ContextIntProvider) : ContextIntProvider()

/**
 * Returns the value of one provider of [distribution], picked with a probability of its weight over the total weight.
 *
 * Minecraft Wiki: [Number provider - weighted_list](https://minecraft.wiki/w/Number_provider#weighted_list)
 *
 * @property distribution The entries to draw from, which must not be empty.
 */
@Serializable
data class WeightedListContextIntProvider(var distribution: List<WeightedListContextIntProviderEntry>) : ContextIntProvider()

/**
 * One entry of a [WeightedListContextIntProvider].
 *
 * @property data The provider used when this entry is drawn.
 * @property weight Relative chance of drawing this entry, compared to the other entries of the list.
 */
@Serializable
data class WeightedListContextIntProviderEntry(var data: ContextIntProvider, var weight: Int)

/**
 * Returns the int provider [id] points to, written as the bare string `"my_pack:my_provider"` wherever an int provider
 * is expected. A tag (`"#my_pack:my_tag"`) is only valid as the whole `inputs` of an `add`, `avg`, `max`, `min` or `mul`.
 * A cyclic reference makes the pack fail to load. A list can mix IDs and inline values.
 *
 * Minecraft Wiki: [Number provider](https://minecraft.wiki/w/Number_provider)
 *
 * @property id The ID of the context int provider file, or of the context int provider tag as the `inputs` of a provider that takes several.
 */
@Serializable(with = ContextIntProviderReference.Companion.ContextIntProviderReferenceSerializer::class)
data class ContextIntProviderReference(var id: ContextIntProviderOrTagArgument) : ContextIntProvider() {
	companion object {
		data object ContextIntProviderReferenceSerializer :
			IdOrTagReferenceSerializer<ContextIntProviderReference, ContextIntProviderOrTagArgument>(
				ContextIntProviderReference::id,
				::ContextIntProviderReference,
				{ name, namespace -> ContextIntProviderArgument(name, namespace) },
				{ name, namespace -> ContextIntProviderTagArgument(name, namespace) },
				"context_int_provider_reference",
			)
	}
}
