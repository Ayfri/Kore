package io.github.ayfri.kore.features.predicates.providers

import io.github.ayfri.kore.features.enchantments.values.LevelBased
import io.github.ayfri.kore.features.enchantments.values.constantLevelBased
import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.conditions.PredicateCondition
import io.github.ayfri.kore.features.predicates.conditions.singleCondition
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

/**
 * Creates a [ConditionalNumberProvider] returning [onTrue] when [condition] passes, [onFalse] otherwise.
 *
 * ```
 * conditionalNumber(SurvivesExplosion, constant(1f), constant(0f))
 * ```
 */
fun NumberProviderScope.conditionalNumber(
	condition: PredicateCondition,
	onTrue: NumberProvider,
	onFalse: NumberProvider? = null,
) = ConditionalNumberProvider(condition, onFalse, onTrue)

/** Creates a [ConditionalNumberProvider] returning [onTrue] when [condition] passes, [onFalse] otherwise. */
fun NumberProviderScope.conditionalNumber(condition: PredicateCondition, onTrue: Float, onFalse: Float? = null) =
	ConditionalNumberProvider(condition, onFalse?.let(::constant), constant(onTrue))

/**
 * Creates a [ConditionalNumberProvider] returning [onTrue] when the single condition declared in [condition] passes,
 * [onFalse] otherwise.
 *
 * ```
 * conditionalNumber(constant(1f), constant(0f)) { killedByPlayer() }
 * ```
 */
fun NumberProviderScope.conditionalNumber(
	onTrue: NumberProvider,
	onFalse: NumberProvider? = null,
	condition: Predicate.() -> Unit,
) = ConditionalNumberProvider(singleCondition("conditionalNumber", condition), onFalse, onTrue)

/** Creates a [ConditionalNumberProvider] returning [onTrue] when the single condition declared in [condition] passes. */
fun NumberProviderScope.conditionalNumber(onTrue: Float, onFalse: Float? = null, condition: Predicate.() -> Unit) =
	ConditionalNumberProvider(
		singleCondition("conditionalNumber", condition),
		onFalse?.let(::constant),
		constant(onTrue),
	)

/** Creates a [ConstantNumberProvider] always returning [value], serialized inline as a bare number. */
fun NumberProviderScope.constant(value: Float) = ConstantNumberProvider(value)

/** Creates an [EnchantmentLevelNumberProvider] returning the fixed [amount] whatever the enchantment level is. */
fun NumberProviderScope.enchantmentLevel(amount: Int) = EnchantmentLevelNumberProvider(LevelBased.constantLevelBased(amount))

/** Creates an [EnchantmentLevelNumberProvider] returning [amount] evaluated at the current enchantment level. */
fun NumberProviderScope.enchantmentLevel(amount: LevelBased) = EnchantmentLevelNumberProvider(amount)

/** Creates an [EnvironmentAttributeNumberProvider] returning the current value of [attribute]. */
fun NumberProviderScope.environmentAttribute(attribute: EnvironmentAttributeArgument) = EnvironmentAttributeNumberProvider(attribute)

/**
 * Creates a [NumberDispatcherNumberProvider] returning the provider of the first [NumberDispatcherBuilder.case] whose
 * condition passes, [default] when none does.
 *
 * ```
 * numberDispatcher(default = constant(0f)) {
 *     case(1f) { killedByPlayer() }
 * }
 * ```
 */
fun NumberProviderScope.numberDispatcher(default: NumberProvider? = null, block: NumberDispatcherBuilder.() -> Unit) =
	NumberDispatcherNumberProvider(NumberDispatcherBuilder().apply(block).cases, default)

/** Creates a [NumberDispatcherNumberProvider] returning the first matching case, or [default] when none does. */
fun NumberProviderScope.numberDispatcher(default: Float, block: NumberDispatcherBuilder.() -> Unit) =
	numberDispatcher(constant(default), block)

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

/**
 * Creates a [WeightedListNumberProvider] drawing one of the [WeightedListNumberProviderBuilder.entry] declared in
 * [block].
 *
 * ```
 * weightedListNumber {
 *     entry(30, 1f)
 *     entry(70, 0f)
 * }
 * ```
 */
fun NumberProviderScope.weightedListNumber(block: WeightedListNumberProviderBuilder.() -> Unit) =
	WeightedListNumberProvider(WeightedListNumberProviderBuilder().apply(block).entries)

/** Builder scope of [numberDispatcher], collecting its cases. */
class NumberDispatcherBuilder internal constructor() : NumberProviderScope {
	internal val cases = mutableListOf<NumberDispatcherCase>()

	/** Adds a case returning [numberProvider] when [condition] passes. */
	fun case(condition: PredicateCondition, numberProvider: NumberProvider) {
		cases += NumberDispatcherCase(condition, numberProvider)
	}

	/** Adds a case returning [numberProvider] when [condition] passes. */
	fun case(condition: PredicateCondition, numberProvider: Float) = case(condition, constant(numberProvider))

	/** Adds a case returning [numberProvider] when the single condition declared in [condition] passes. */
	fun case(numberProvider: NumberProvider, condition: Predicate.() -> Unit) =
		case(singleCondition("case", condition), numberProvider)

	/** Adds a case returning [numberProvider] when the single condition declared in [condition] passes. */
	fun case(numberProvider: Float, condition: Predicate.() -> Unit) = case(constant(numberProvider), condition)
}

/** Builder scope of [weightedListNumber], collecting its entries. */
class WeightedListNumberProviderBuilder internal constructor() : NumberProviderScope {
	internal val entries = mutableListOf<WeightedListNumberProviderEntry>()

	/** Adds an entry returning [data] with a relative chance of [weight]. */
	fun entry(weight: Int, data: NumberProvider) {
		entries += WeightedListNumberProviderEntry(data, weight)
	}

	/** Adds an entry returning [data] with a relative chance of [weight]. */
	fun entry(weight: Int, data: Float) = entry(weight, constant(data))
}
