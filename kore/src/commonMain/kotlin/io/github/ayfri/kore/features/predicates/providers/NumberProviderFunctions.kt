@file:Suppress("NOTHING_TO_INLINE")

package io.github.ayfri.kore.features.predicates.providers

import io.github.ayfri.kore.features.enchantments.values.LevelBased
import io.github.ayfri.kore.features.enchantments.values.constantLevelBased
import io.github.ayfri.kore.features.predicates.types.EntityTarget
import io.github.ayfri.kore.generated.LootScoreProviderTypes
import io.github.ayfri.kore.generated.arguments.types.EnvironmentAttributeArgument

/** Creates a [BinomialNumberProvider] number provider with [n] trials and success probability [p]. */
fun binomial(n: NumberProvider, p: NumberProvider) = BinomialNumberProvider(n, p)

/** Creates a [BinomialNumberProvider] number provider with [n] trials and success probability [p]. */
fun binomial(n: Float, p: Float) = BinomialNumberProvider(constant(n), constant(p))

/** Creates a [BinomialNumberProvider] number provider with [n] trials and success probability [p]. */
fun binomial(n: Float, p: NumberProvider) = BinomialNumberProvider(constant(n), p)

/** Creates a [BinomialNumberProvider] number provider with [n] trials and success probability [p]. */
fun binomial(n: NumberProvider, p: Float) = BinomialNumberProvider(n, constant(p))


/** Creates a [ConstantNumberProvider] number provider with the fixed [value]. Serializes inline (no wrapper object). */
fun constant(value: Float) = ConstantNumberProvider(value)


/** Creates an [EnchantmentLevelNumberProvider] number provider from a fixed integer [amount]. */
fun enchantmentLevel(amount: Int) = EnchantmentLevelNumberProvider(LevelBased.constantLevelBased(amount))

/** Creates an [EnchantmentLevelNumberProvider] number provider from a [LevelBased] expression. */
fun enchantmentLevel(amount: LevelBased) = EnchantmentLevelNumberProvider(amount)


/** Returns an [EnvironmentAttributeNumberProvider] that reads the current value of [attribute]. */
fun environmentAttribute(attribute: EnvironmentAttributeArgument) = EnvironmentAttributeNumberProvider(attribute)


/** Creates a [ScoreNumberProvider] number provider that reads [score] from a loot context [target] entity, optionally multiplied by [scale]. */
fun scoreNumber(score: String, target: EntityTarget? = null, scale: Float? = null) =
	ScoreNumberProvider(ScoreTargetNumberProvider(LootScoreProviderTypes.CONTEXT, target = target), score, scale)

/** Creates a [ScoreNumberProvider] number provider that reads [score] from a fixed player [name], optionally multiplied by [scale]. */
fun scoreNumber(score: String, name: String? = null, scale: Float? = null) =
	ScoreNumberProvider(ScoreTargetNumberProvider(LootScoreProviderTypes.FIXED, name), score, scale)


/** Creates a [StorageNumberProvider] number provider that reads from command storage at [storage] using NBT [path]. */
fun storageNumber(storage: String, path: String) = StorageNumberProvider(storage, path)


/** Creates a [SumNumberProvider] number provider that adds all given [summands]. */
fun sum(vararg summands: NumberProvider) = SumNumberProvider(summands.toList())

/** Creates a [SumNumberProvider] number provider that adds all providers in [summands]. */
fun sum(summands: List<NumberProvider>) = SumNumberProvider(summands)


/** Creates a [UniformNumberProvider] number provider returning a random value between [min] and [max] (inclusive). */
fun uniform(min: NumberProvider, max: NumberProvider) = UniformNumberProvider(min, max)

/** Creates a [UniformNumberProvider] number provider returning a random value between [min] and [max] (inclusive). */
fun uniform(min: Float, max: Float) = UniformNumberProvider(constant(min), constant(max))

/** Creates a [UniformNumberProvider] number provider returning a random value between [min] and [max] (inclusive). */
fun uniform(min: Float, max: NumberProvider) = UniformNumberProvider(constant(min), max)

/** Creates a [UniformNumberProvider] number provider returning a random value between [min] and [max] (inclusive). */
fun uniform(min: NumberProvider, max: Float) = UniformNumberProvider(min, constant(max))
