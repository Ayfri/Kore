package io.github.ayfri.kore.features.predicates.providers

import io.github.ayfri.kore.features.enchantments.values.LevelBased
import io.github.ayfri.kore.features.enchantments.values.constantLevelBased
import io.github.ayfri.kore.features.predicates.types.EntityTarget
import io.github.ayfri.kore.generated.arguments.types.EnvironmentAttributeArgument

/** Creates a [BinomialNumberProvider] returning the number of successes of [n] trials of probability [p]. */
fun NumberProviderScope.binomial(n: NumberProvider, p: NumberProvider) = BinomialNumberProvider(n, p)

/** Creates a [BinomialNumberProvider] returning the number of successes of [n] trials of probability [p]. */
fun NumberProviderScope.binomial(n: Float, p: Float) = BinomialNumberProvider(constant(n), constant(p))

/** Creates a [BinomialNumberProvider] returning the number of successes of [n] trials of probability [p]. */
fun NumberProviderScope.binomial(n: Float, p: NumberProvider) = BinomialNumberProvider(constant(n), p)

/** Creates a [BinomialNumberProvider] returning the number of successes of [n] trials of probability [p]. */
fun NumberProviderScope.binomial(n: NumberProvider, p: Float) = BinomialNumberProvider(n, constant(p))

/** Creates a [ConstantNumberProvider] always returning [value], serialized inline as a bare number. */
fun NumberProviderScope.constant(value: Float) = ConstantNumberProvider(value)

/** Creates an [EnchantmentLevelNumberProvider] returning the fixed [amount] whatever the enchantment level is. */
fun NumberProviderScope.enchantmentLevel(amount: Int) = EnchantmentLevelNumberProvider(LevelBased.constantLevelBased(amount))

/** Creates an [EnchantmentLevelNumberProvider] returning [amount] evaluated at the current enchantment level. */
fun NumberProviderScope.enchantmentLevel(amount: LevelBased) = EnchantmentLevelNumberProvider(amount)

/** Creates an [EnvironmentAttributeNumberProvider] returning the current value of [attribute]. */
fun NumberProviderScope.environmentAttribute(attribute: EnvironmentAttributeArgument) = EnvironmentAttributeNumberProvider(attribute)

/** Creates a [ScoreNumberProvider] reading [score] from the loot context entity [target], optionally scaled by [scale]. */
fun NumberProviderScope.scoreNumber(score: String, target: EntityTarget, scale: Float? = null) =
	ScoreNumberProvider(contextScore(target), score, scale)

/** Creates a [ScoreNumberProvider] reading [score] from the score holder [name], optionally scaled by [scale]. */
fun NumberProviderScope.scoreNumber(score: String, name: String, scale: Float? = null) =
	ScoreNumberProvider(fixedScore(name), score, scale)

/** Creates a [ScoreNumberProvider] reading [score] from [target], optionally scaled by [scale]. */
fun NumberProviderScope.scoreNumber(score: String, target: ScoreProvider, scale: Float? = null) = ScoreNumberProvider(target, score, scale)

/** Creates a [StorageNumberProvider] reading the NBT [path] of the command storage [storage]. */
fun NumberProviderScope.storageNumber(storage: String, path: String) = StorageNumberProvider(storage, path)

/** Creates a [SumNumberProvider] adding every provider of [summands]. */
fun NumberProviderScope.sum(vararg summands: NumberProvider) = SumNumberProvider(summands.toList())

/** Creates a [SumNumberProvider] adding every provider of [summands]. */
fun NumberProviderScope.sum(summands: List<NumberProvider>) = SumNumberProvider(summands)

/** Creates a [UniformNumberProvider] drawing between [min] and [max], both included. */
fun NumberProviderScope.uniform(min: NumberProvider, max: NumberProvider) = UniformNumberProvider(min, max)

/** Creates a [UniformNumberProvider] drawing between [min] and [max], both included. */
fun NumberProviderScope.uniform(min: Float, max: Float) = UniformNumberProvider(constant(min), constant(max))

/** Creates a [UniformNumberProvider] drawing between [min] and [max], both included. */
fun NumberProviderScope.uniform(min: Float, max: NumberProvider) = UniformNumberProvider(constant(min), max)

/** Creates a [UniformNumberProvider] drawing between [min] and [max], both included. */
fun NumberProviderScope.uniform(min: NumberProvider, max: Float) = UniformNumberProvider(min, constant(max))
