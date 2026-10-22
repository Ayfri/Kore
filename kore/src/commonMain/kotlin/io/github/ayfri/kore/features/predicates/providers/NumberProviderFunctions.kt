package io.github.ayfri.kore.features.predicates.providers

import io.github.ayfri.kore.features.enchantments.values.LevelBased
import io.github.ayfri.kore.features.enchantments.values.constantLevelBased
import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.conditions.PredicateCondition
import io.github.ayfri.kore.features.predicates.conditions.PredicateReference
import io.github.ayfri.kore.features.predicates.conditions.singleCondition
import io.github.ayfri.kore.features.predicates.types.EntityTarget
import io.github.ayfri.kore.generated.arguments.tagged.NumberProviderTagArgument
import io.github.ayfri.kore.generated.arguments.types.EnvironmentAttributeArgument
import io.github.ayfri.kore.generated.arguments.types.NumberProviderArgument
import io.github.ayfri.kore.generated.arguments.types.PredicateArgument

/**
 * Creates an [AverageNumberProvider] returning the mean of every provider of [operands].
 *
 * ```
 * average(constant(2f), constant(4f))  // 3.0
 * ```
 */
fun NumberProviderScope.average(vararg operands: NumberProvider) = AverageNumberProvider(operands.toList())

/** Creates an [AverageNumberProvider] returning the mean of every provider of [operands]. */
fun NumberProviderScope.average(operands: List<NumberProvider>) = AverageNumberProvider(operands)

/**
 * Creates an [AverageNumberProvider] returning the mean of every number provider of the tag [tag], written `"#my_pack:my_tag"`.
 *
 * ```
 * average(NumberProviderTagArgument("bonuses", "my_pack"))  // "operands": "#my_pack:bonuses"
 * ```
 */
fun NumberProviderScope.average(tag: NumberProviderTagArgument) = AverageNumberProvider(listOf(NumberProviderReference(tag)))

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
 * Creates a [ConditionalNumberProvider] returning [onTrue] when the predicate [condition] passes, [onFalse] otherwise.
 *
 * ```
 * conditionalNumber(PredicateArgument("fast_cooking", "my_pack"), 800f, 1600f)  // "condition": "my_pack:fast_cooking"
 * ```
 */
fun NumberProviderScope.conditionalNumber(
	condition: PredicateArgument,
	onTrue: NumberProvider,
	onFalse: NumberProvider? = null,
) = ConditionalNumberProvider(PredicateReference(condition), onFalse, onTrue)

/** Creates a [ConditionalNumberProvider] returning [onTrue] when the predicate [condition] passes, [onFalse] otherwise. */
fun NumberProviderScope.conditionalNumber(condition: PredicateArgument, onTrue: Float, onFalse: Float? = null) =
	conditionalNumber(condition, constant(onTrue), onFalse?.let(::constant))

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
 * Creates a [MaximumNumberProvider] returning the largest value among the providers of [operands].
 *
 * ```
 * maximum(constant(2f), constant(4f))  // 4.0
 * ```
 */
fun NumberProviderScope.maximum(vararg operands: NumberProvider) = MaximumNumberProvider(operands.toList())

/** Creates a [MaximumNumberProvider] returning the largest value among the providers of [operands]. */
fun NumberProviderScope.maximum(operands: List<NumberProvider>) = MaximumNumberProvider(operands)

/**
 * Creates a [MaximumNumberProvider] returning the largest value among the number providers of the tag [tag], written `"#my_pack:my_tag"`.
 *
 * ```
 * maximum(NumberProviderTagArgument("bonuses", "my_pack"))  // "operands": "#my_pack:bonuses"
 * ```
 */
fun NumberProviderScope.maximum(tag: NumberProviderTagArgument) = MaximumNumberProvider(listOf(NumberProviderReference(tag)))

/**
 * Creates a [MinimumNumberProvider] returning the smallest value among the providers of [operands].
 *
 * ```
 * minimum(scoreNumber("kills", EntityTarget.THIS), constant(5f))  // caps the score at 5
 * ```
 */
fun NumberProviderScope.minimum(vararg operands: NumberProvider) = MinimumNumberProvider(operands.toList())

/** Creates a [MinimumNumberProvider] returning the smallest value among the providers of [operands]. */
fun NumberProviderScope.minimum(operands: List<NumberProvider>) = MinimumNumberProvider(operands)

/**
 * Creates a [MinimumNumberProvider] returning the smallest value among the number providers of the tag [tag], written `"#my_pack:my_tag"`.
 *
 * ```
 * minimum(NumberProviderTagArgument("bonuses", "my_pack"))  // "operands": "#my_pack:bonuses"
 * ```
 */
fun NumberProviderScope.minimum(tag: NumberProviderTagArgument) = MinimumNumberProvider(listOf(NumberProviderReference(tag)))

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

/**
 * Creates a [ProductNumberProvider] returning the product of every provider of [operands].
 *
 * ```
 * product(reference(NumberProviderArgument("bonus", "my_pack")), constant(2f))
 * ```
 */
fun NumberProviderScope.product(vararg operands: NumberProvider) = ProductNumberProvider(operands.toList())

/** Creates a [ProductNumberProvider] returning the product of every provider of [operands]. */
fun NumberProviderScope.product(operands: List<NumberProvider>) = ProductNumberProvider(operands)

/**
 * Creates a [ProductNumberProvider] returning the product of every number provider of the tag [tag], written `"#my_pack:my_tag"`.
 *
 * ```
 * product(NumberProviderTagArgument("bonuses", "my_pack"))  // "operands": "#my_pack:bonuses"
 * ```
 */
fun NumberProviderScope.product(tag: NumberProviderTagArgument) = ProductNumberProvider(listOf(NumberProviderReference(tag)))

/**
 * Creates a [NumberProviderReference] pointing at the number provider [id], written as a bare string.
 *
 * ```
 * reference(NumberProviderArgument("compost_chance", "my_pack"))  // "my_pack:compost_chance"
 * ```
 */
fun NumberProviderScope.reference(id: NumberProviderArgument) = NumberProviderReference(id)

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

/** Creates a [SumNumberProvider] adding every provider of [operands]. */
fun NumberProviderScope.sum(vararg operands: NumberProvider) = SumNumberProvider(operands.toList())

/** Creates a [SumNumberProvider] adding every provider of [operands]. */
fun NumberProviderScope.sum(operands: List<NumberProvider>) = SumNumberProvider(operands)

/**
 * Creates a [SumNumberProvider] adding every number provider of the tag [tag], written `"#my_pack:my_tag"`.
 *
 * ```
 * sum(NumberProviderTagArgument("bonuses", "my_pack"))  // "operands": "#my_pack:bonuses"
 * ```
 */
fun NumberProviderScope.sum(tag: NumberProviderTagArgument) = SumNumberProvider(listOf(NumberProviderReference(tag)))

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

	/** Adds a case returning [numberProvider] when the predicate [condition] passes. */
	fun case(condition: PredicateArgument, numberProvider: NumberProvider) = case(PredicateReference(condition), numberProvider)

	/** Adds a case returning [numberProvider] when the predicate [condition] passes. */
	fun case(condition: PredicateArgument, numberProvider: Float) = case(condition, constant(numberProvider))

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
