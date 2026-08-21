package io.github.ayfri.kore.features.predicates.providers

import io.github.ayfri.kore.features.enchantments.values.LevelBased
import io.github.ayfri.kore.features.predicates.types.EntityTarget
import io.github.ayfri.kore.generated.arguments.types.EnvironmentAttributeArgument
import io.github.ayfri.kore.generated.arguments.types.LootScoreProviderTypeArgument
import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

@GeneratedSealedSerializer
@Serializable(with = NumberProvider.Companion.NumberProviderSerializer::class)
sealed class NumberProvider {
	companion object {
		@OptIn(InternalSerializationApi::class)
		data object NumberProviderSerializer :
			NamespacedPolymorphicSerializer<NumberProvider>(numberProviderSealedSerializer())
	}
}

/**
 * A random number following a binomial distribution.
 *
 * @param n Number provider for the number of trials.
 * @param p Number provider for the probability of success on each individual trial.
 *
 * Minecraft Wiki: [Number provider - binomial](https://minecraft.wiki/w/Number_provider#binomial)
 */
@SerialName("binomial")
@Serializable
data class BinomialNumberProvider(var n: NumberProvider, var p: NumberProvider) : NumberProvider()

/**
 * A constant fixed numeric value. Serializes inline (no wrapper object).
 *
 * @param value The exact value.
 *
 * Minecraft Wiki: [Number provider - constant](https://minecraft.wiki/w/Number_provider#constant)
 */
@Serializable(with = ConstantNumberProvider.Companion.ConstantNumberProviderSerializer::class)
data class ConstantNumberProvider(val value: Float) : NumberProvider() {
	companion object {
		data object ConstantNumberProviderSerializer :
			InlineAutoSerializer<ConstantNumberProvider, Float>(serializer<Float>(), ConstantNumberProvider::value, ::ConstantNumberProvider, "constant")
	}
}

/**
 * A value derived from the level of the loot context enchantment.
 *
 * @param amount A [LevelBased] expression evaluated against the current enchantment level.
 *
 * Minecraft Wiki: [Number provider - enchantment_level](https://minecraft.wiki/w/Number_provider#enchantment_level)
 */
@SerialName("enchantment_level")
@Serializable
data class EnchantmentLevelNumberProvider(var amount: LevelBased) : NumberProvider()

/**
 * Reads the current value of an environment attribute (must be representable as a number).
 *
 * Requires a loot context with an origin position when the attribute varies positionally.
 *
 * @param attribute The environment attribute to read.
 *
 * Minecraft Wiki: [Number provider - environment_attribute](https://minecraft.wiki/w/Number_provider#environment_attribute)
 */
@SerialName("environment_attribute")
@Serializable
data class EnvironmentAttributeNumberProvider(
	var attribute: EnvironmentAttributeArgument,
) : NumberProvider()

/**
 * Selects the player name or context entity for a [ScoreNumberProvider] query.
 *
 * @param type [io.github.ayfri.kore.generated.LootScoreProviderTypes.FIXED] to use a literal player name/UUID, [io.github.ayfri.kore.generated.LootScoreProviderTypes.CONTEXT] to use a loot context entity.
 * @param name Player name or UUID string; required when [type] is [io.github.ayfri.kore.generated.LootScoreProviderTypes.FIXED].
 * @param target Loot context entity selector; required when [type] is [io.github.ayfri.kore.generated.LootScoreProviderTypes.CONTEXT].
 */
@Serializable
data class ScoreTargetNumberProvider(
	var type: LootScoreProviderTypeArgument,
	var name: String? = null,
	var target: EntityTarget? = null,
)

/**
 * Reads and optionally scales a scoreboard value.
 *
 * @param target The player name or context entity to query, via [ScoreTargetNumberProvider].
 * @param score The scoreboard objective name to query.
 * @param scale Optional multiplier applied to the score before returning it.
 *
 * Minecraft Wiki: [Number provider - score](https://minecraft.wiki/w/Number_provider#score)
 */
@SerialName("score")
@Serializable
data class ScoreNumberProvider(
	var target: ScoreTargetNumberProvider,
	var score: String,
	var scale: Float? = null,
) : NumberProvider()

/**
 * Reads a numeric value from command storage.
 *
 * @param storage Resource location of the storage (e.g. `my_pack:data`).
 * @param path NBT path to the value within the storage.
 *
 * Minecraft Wiki: [Number provider - storage](https://minecraft.wiki/w/Number_provider#storage)
 */
@SerialName("storage")
@Serializable
data class StorageNumberProvider(var storage: String, var path: String) : NumberProvider()

/**
 * The sum of multiple number providers.
 *
 * @param summands List of providers whose values are added together.
 *
 * Minecraft Wiki: [Number provider - sum](https://minecraft.wiki/w/Number_provider#sum)
 */
@SerialName("sum")
@Serializable
data class SumNumberProvider(var summands: List<NumberProvider>) : NumberProvider()

/**
 * A uniformly distributed random value between [min] and [max] (inclusive).
 *
 * @param min Number provider for the minimum value.
 * @param max Number provider for the maximum value.
 *
 * Minecraft Wiki: [Number provider - uniform](https://minecraft.wiki/w/Number_provider#uniform)
 */
@SerialName("uniform")
@Serializable
data class UniformNumberProvider(var min: NumberProvider, var max: NumberProvider) : NumberProvider()
