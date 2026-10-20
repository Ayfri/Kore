package io.github.ayfri.kore.features.predicates.providers

import io.github.ayfri.kore.features.enchantments.values.LevelBased
import io.github.ayfri.kore.features.predicates.conditions.PredicateCondition
import io.github.ayfri.kore.generated.arguments.types.EnvironmentAttributeArgument
import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

/**
 * Picks a number, either a fixed one or one derived from the loot context, used by the loot tables, the item
 * modifiers, the predicates and the trades.
 *
 * A [ConstantNumberProvider] is inlined to its value, so `constant(5f)` serializes as `5.0` instead of an object
 * with a `type` field. Every other type keeps its `type` field.
 *
 * Every builder is an extension on [NumberProviderScope] (e.g. [constant], [uniform]), so they only resolve inside a
 * block that actually accepts a number provider.
 *
 * Minecraft Wiki: [Number provider](https://minecraft.wiki/w/Number_provider)
 */
@GeneratedSealedSerializer
@Serializable(with = NumberProvider.Companion.NumberProviderSerializer::class)
sealed class NumberProvider {
	companion object {
		data object NumberProviderSerializer :
			NamespacedPolymorphicSerializer<NumberProvider>(numberProviderSealedSerializer())
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
@SerialName("binomial")
@Serializable
data class BinomialNumberProvider(var n: NumberProvider, var p: NumberProvider) : NumberProvider()

/**
 * Returns [onTrue] when [condition] passes, [onFalse] otherwise.
 *
 * Minecraft Wiki: [Number provider - conditional](https://minecraft.wiki/w/Number_provider#conditional)
 *
 * @property condition The predicate condition evaluated against the loot context.
 * @property onFalse The provider used when [condition] fails, `0` when `null`.
 * @property onTrue The provider used when [condition] passes.
 */
@SerialName("conditional")
@Serializable
data class ConditionalNumberProvider(
	var condition: PredicateCondition,
	var onFalse: NumberProvider? = null,
	var onTrue: NumberProvider,
) : NumberProvider()

/**
 * Always returns the same number.
 *
 * It is inlined when serialized, so it produces `5.0` rather than an object with a `type` field.
 *
 * Minecraft Wiki: [Number provider - constant](https://minecraft.wiki/w/Number_provider#constant)
 *
 * @property value The value returned on every call.
 */
@Serializable(with = ConstantNumberProvider.Companion.ConstantNumberProviderSerializer::class)
data class ConstantNumberProvider(val value: Float) : NumberProvider() {
	companion object {
		data object ConstantNumberProviderSerializer : InlineAutoSerializer<ConstantNumberProvider, Float>(
			serializer<Float>(),
			ConstantNumberProvider::value,
			::ConstantNumberProvider,
			"constant",
		)
	}
}

/**
 * Returns [amount] evaluated against the level of the enchantment the loot context runs for.
 *
 * Minecraft Wiki: [Number provider - enchantment_level](https://minecraft.wiki/w/Number_provider#enchantment_level)
 *
 * @property amount The level-based value evaluated at the current enchantment level.
 */
@SerialName("enchantment_level")
@Serializable
data class EnchantmentLevelNumberProvider(var amount: LevelBased) : NumberProvider()

/**
 * Returns the current value of a numeric environment attribute.
 *
 * Requires a loot context with an origin position as long as the attribute can vary positionally.
 *
 * Minecraft Wiki: [Number provider - environment_attribute](https://minecraft.wiki/w/Number_provider#environment_attribute)
 *
 * @property attribute The environment attribute to read, which must hold a number.
 */
@SerialName("environment_attribute")
@Serializable
data class EnvironmentAttributeNumberProvider(
	var attribute: EnvironmentAttributeArgument,
) : NumberProvider()

/**
 * Returns the provider of the first case of [cases] whose condition passes, [default] when none does.
 *
 * Minecraft Wiki: [Number provider - number_dispatcher](https://minecraft.wiki/w/Number_provider#number_dispatcher)
 *
 * @property cases The cases, checked in order.
 * @property default The provider used when no case matches, `0` when `null`.
 */
@SerialName("number_dispatcher")
@Serializable
data class NumberDispatcherNumberProvider(
	var cases: List<NumberDispatcherCase>,
	var default: NumberProvider? = null,
) : NumberProvider()

/**
 * One case of a [NumberDispatcherNumberProvider].
 *
 * @property condition The predicate condition that selects this case.
 * @property numberProvider The provider used when [condition] passes.
 */
@Serializable
data class NumberDispatcherCase(
	var condition: PredicateCondition,
	var numberProvider: NumberProvider,
)

/**
 * Returns the value a score holder has on a scoreboard objective, optionally scaled.
 *
 * Minecraft Wiki: [Number provider - score](https://minecraft.wiki/w/Number_provider#score)
 *
 * @property target The score holder to read, either a loot context entity or a fixed name.
 * @property score The scoreboard objective to read.
 * @property scale The multiplier applied to the score before returning it, `1` when `null`.
 */
@SerialName("score")
@Serializable
data class ScoreNumberProvider(
	var target: ScoreProvider,
	var score: String,
	var scale: Float? = null,
) : NumberProvider()

/**
 * Returns a number read from command storage.
 *
 * Minecraft Wiki: [Number provider - storage](https://minecraft.wiki/w/Number_provider#storage)
 *
 * @property storage Resource location of the storage, e.g. `my_pack:data`.
 * @property path NBT path of the value inside the storage.
 */
@SerialName("storage")
@Serializable
data class StorageNumberProvider(var storage: String, var path: String) : NumberProvider()

/**
 * Returns the sum of every provider of [summands].
 *
 * Minecraft Wiki: [Number provider - sum](https://minecraft.wiki/w/Number_provider#sum)
 *
 * @property summands The providers whose values are added together.
 */
@SerialName("sum")
@Serializable
data class SumNumberProvider(var summands: List<NumberProvider>) : NumberProvider()

/**
 * Returns a number drawn uniformly between [min] and [max], both included.
 *
 * Minecraft Wiki: [Number provider - uniform](https://minecraft.wiki/w/Number_provider#uniform)
 *
 * @property min Lowest value that can be drawn.
 * @property max Highest value that can be drawn, which must be at or above [min].
 */
@SerialName("uniform")
@Serializable
data class UniformNumberProvider(var min: NumberProvider, var max: NumberProvider) : NumberProvider()

/**
 * Returns the value of one provider of [distribution], picked with a probability of its weight over the total weight.
 *
 * Minecraft Wiki: [Number provider - weighted_list](https://minecraft.wiki/w/Number_provider#weighted_list)
 *
 * @property distribution The entries to draw from, which must not be empty.
 */
@SerialName("weighted_list")
@Serializable
data class WeightedListNumberProvider(var distribution: List<WeightedListNumberProviderEntry>) : NumberProvider()

/**
 * One entry of a [WeightedListNumberProvider].
 *
 * @property data The provider used when this entry is drawn.
 * @property weight Relative chance of drawing this entry, compared to the other entries of the list.
 */
@Serializable
data class WeightedListNumberProviderEntry(var data: NumberProvider, var weight: Int)
