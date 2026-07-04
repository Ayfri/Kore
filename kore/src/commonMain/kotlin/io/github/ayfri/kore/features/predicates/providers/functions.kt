@file:Suppress("NOTHING_TO_INLINE")

package io.github.ayfri.kore.features.predicates.providers

import io.github.ayfri.kore.features.enchantments.values.LevelBased
import io.github.ayfri.kore.features.enchantments.values.constantLevelBased
import io.github.ayfri.kore.features.predicates.types.EntityType
import io.github.ayfri.kore.generated.LootScoreProviderTypes
import io.github.ayfri.kore.generated.arguments.types.EnvironmentAttributeArgument

/** Creates a [Binomial] number provider with [n] trials and success probability [p]. */
fun binomial(n: NumberProvider, p: NumberProvider) = Binomial(n, p)

/** Creates a [Binomial] number provider with [n] trials and success probability [p]. */
fun binomial(n: Float, p: Float) = Binomial(constant(n), constant(p))

/** Creates a [Binomial] number provider with [n] trials and success probability [p]. */
fun binomial(n: Float, p: NumberProvider) = Binomial(constant(n), p)

/** Creates a [Binomial] number provider with [n] trials and success probability [p]. */
fun binomial(n: NumberProvider, p: Float) = Binomial(n, constant(p))


/** Creates a [Constant] number provider with the fixed [value]. Serializes inline (no wrapper object). */
fun constant(value: Float) = Constant(value)


/** Creates an [EnchantmentLevel] number provider from a fixed integer [amount]. */
fun enchantmentLevel(amount: Int) = EnchantmentLevel(constantLevelBased(amount))

/** Creates an [EnchantmentLevel] number provider from a [LevelBased] expression. */
fun enchantmentLevel(amount: LevelBased) = EnchantmentLevel(amount)


/** Returns an [EnvironmentAttributeNumberProvider] that reads the current value of [attribute]. */
fun environmentAttribute(attribute: EnvironmentAttributeArgument) = EnvironmentAttributeNumberProvider(attribute)


/** Creates a [Score] number provider that reads [score] from a loot context [target] entity, optionally multiplied by [scale]. */
fun scoreNumber(score: String, target: EntityType? = null, scale: Float? = null) =
	Score(ScoreTargetNumberProvider(LootScoreProviderTypes.CONTEXT, target = target), score, scale)

/** Creates a [Score] number provider that reads [score] from a fixed player [name], optionally multiplied by [scale]. */
fun scoreNumber(score: String, name: String? = null, scale: Float? = null) =
	Score(ScoreTargetNumberProvider(LootScoreProviderTypes.FIXED, name), score, scale)


/** Creates a [Storage] number provider that reads from command storage at [storage] using NBT [path]. */
fun storageNumber(storage: String, path: String) = Storage(storage, path)


/** Creates a [Sum] number provider that adds all given [summands]. */
fun sum(vararg summands: NumberProvider) = Sum(summands.toList())

/** Creates a [Sum] number provider that adds all providers in [summands]. */
fun sum(summands: List<NumberProvider>) = Sum(summands)


/** Creates a [Uniform] number provider returning a random value between [min] and [max] (inclusive). */
fun uniform(min: NumberProvider, max: NumberProvider) = Uniform(min, max)

/** Creates a [Uniform] number provider returning a random value between [min] and [max] (inclusive). */
fun uniform(min: Float, max: Float) = Uniform(constant(min), constant(max))

/** Creates a [Uniform] number provider returning a random value between [min] and [max] (inclusive). */
fun uniform(min: Float, max: NumberProvider) = Uniform(constant(min), max)

/** Creates a [Uniform] number provider returning a random value between [min] and [max] (inclusive). */
fun uniform(min: NumberProvider, max: Float) = Uniform(min, constant(max))
