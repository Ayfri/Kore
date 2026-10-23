package io.github.ayfri.kore.features.predicates.providers

import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.conditions.PredicateCondition
import io.github.ayfri.kore.features.predicates.conditions.PredicateReference
import io.github.ayfri.kore.features.predicates.conditions.singleCondition
import io.github.ayfri.kore.features.predicates.types.EntityTarget
import io.github.ayfri.kore.generated.arguments.tagged.ContextIntProviderTagArgument
import io.github.ayfri.kore.generated.arguments.types.ContextIntProviderArgument
import io.github.ayfri.kore.generated.arguments.types.EnvironmentAttributeArgument
import io.github.ayfri.kore.generated.arguments.types.PredicateArgument

/** Creates an [AbsContextIntProvider] returning the absolute value of [input]. */
fun NumberProviderScope.abs(input: ContextIntProvider) = AbsContextIntProvider(input)

/**
 * Creates an [AddContextIntProvider] returning the sum of every provider of [inputs].
 *
 * ```
 * add(constant(2), constant(4))  // 6
 * ```
 */
fun NumberProviderScope.add(vararg inputs: ContextIntProvider) = AddContextIntProvider(inputs.toList())

/** Creates an [AddContextIntProvider] returning the sum of every provider of [inputs]. */
fun NumberProviderScope.add(inputs: List<ContextIntProvider>) = AddContextIntProvider(inputs)

/**
 * Creates an [AddContextIntProvider] returning the sum of every int provider of the tag [tag], written `"#my_pack:my_tag"`.
 *
 * ```
 * add(ContextIntProviderTagArgument("bonuses", "my_pack"))  // "inputs": "#my_pack:bonuses"
 * ```
 */
fun NumberProviderScope.add(tag: ContextIntProviderTagArgument) = AddContextIntProvider(listOf(ContextIntProviderReference(tag)))

/**
 * Creates an [AvgContextIntProvider] returning the mean of every provider of [inputs].
 *
 * ```
 * avg(constant(2), constant(4))  // 3
 * ```
 */
fun NumberProviderScope.avg(vararg inputs: ContextIntProvider) = AvgContextIntProvider(inputs.toList())

/** Creates an [AvgContextIntProvider] returning the mean of every provider of [inputs]. */
fun NumberProviderScope.avg(inputs: List<ContextIntProvider>) = AvgContextIntProvider(inputs)

/**
 * Creates an [AvgContextIntProvider] returning the mean of every int provider of the tag [tag], written `"#my_pack:my_tag"`.
 *
 * ```
 * avg(ContextIntProviderTagArgument("bonuses", "my_pack"))  // "inputs": "#my_pack:bonuses"
 * ```
 */
fun NumberProviderScope.avg(tag: ContextIntProviderTagArgument) = AvgContextIntProvider(listOf(ContextIntProviderReference(tag)))

/** Creates a [BinomialContextIntProvider] returning the number of successes of [n] trials of probability [p]. */
fun NumberProviderScope.binomial(n: ContextIntProvider, p: ContextFloatProvider) = BinomialContextIntProvider(n, p)

/**
 * Creates a [BinomialContextIntProvider] returning the number of successes of [n] trials of probability [p].
 *
 * ```
 * binomial(10, 0.5f)
 * ```
 */
fun NumberProviderScope.binomial(n: Int, p: Float) = BinomialContextIntProvider(constant(n), constant(p))

/** Creates a [BinomialContextIntProvider] returning the number of successes of [n] trials of probability [p]. */
fun NumberProviderScope.binomial(n: Int, p: ContextFloatProvider) = BinomialContextIntProvider(constant(n), p)

/** Creates a [BinomialContextIntProvider] returning the number of successes of [n] trials of probability [p]. */
fun NumberProviderScope.binomial(n: ContextIntProvider, p: Float) = BinomialContextIntProvider(n, constant(p))

/**
 * Creates a [ConditionalContextIntProvider] returning [onTrue] when [condition] passes, [onFalse] otherwise.
 *
 * ```
 * conditional(SurvivesExplosion, constant(1), constant(0))
 * ```
 */
fun NumberProviderScope.conditional(
	condition: PredicateCondition,
	onTrue: ContextIntProvider,
	onFalse: ContextIntProvider? = null,
) = ConditionalContextIntProvider(condition, onFalse, onTrue)

/** Creates a [ConditionalContextIntProvider] returning [onTrue] when [condition] passes, [onFalse] otherwise. */
fun NumberProviderScope.conditional(condition: PredicateCondition, onTrue: Int, onFalse: Int? = null) =
	ConditionalContextIntProvider(condition, onFalse?.let { constant(it) }, constant(onTrue))

/**
 * Creates a [ConditionalContextIntProvider] returning [onTrue] when the predicate [condition] passes, [onFalse] otherwise.
 *
 * ```
 * conditional(PredicateArgument("fast_cooking", "my_pack"), 800, 1600)  // "condition": "my_pack:fast_cooking"
 * ```
 */
fun NumberProviderScope.conditional(
	condition: PredicateArgument,
	onTrue: ContextIntProvider,
	onFalse: ContextIntProvider? = null,
) = ConditionalContextIntProvider(PredicateReference(condition), onFalse, onTrue)

/** Creates a [ConditionalContextIntProvider] returning [onTrue] when the predicate [condition] passes, [onFalse] otherwise. */
fun NumberProviderScope.conditional(condition: PredicateArgument, onTrue: Int, onFalse: Int? = null) =
	conditional(condition, constant(onTrue), onFalse?.let { constant(it) })

/**
 * Creates a [ConditionalContextIntProvider] returning [onTrue] when the single condition declared in [condition] passes,
 * [onFalse] otherwise.
 *
 * ```
 * conditional(constant(1), constant(0)) { killedByPlayer() }
 * ```
 */
fun NumberProviderScope.conditional(
	onTrue: ContextIntProvider,
	onFalse: ContextIntProvider? = null,
	condition: Predicate.() -> Unit,
) = ConditionalContextIntProvider(singleCondition("conditional", condition), onFalse, onTrue)

/** Creates a [ConditionalContextIntProvider] returning [onTrue] when the single condition declared in [condition] passes. */
fun NumberProviderScope.conditional(onTrue: Int, onFalse: Int? = null, condition: Predicate.() -> Unit) =
	ConditionalContextIntProvider(singleCondition("conditional", condition), onFalse?.let { constant(it) }, constant(onTrue))

/** Creates a [ConstantContextIntProvider] always returning [value], serialized inline as a bare number. */
fun NumberProviderScope.constant(value: Int) = ConstantContextIntProvider(value)

/** Creates a [DivContextIntProvider] returning [left] divided by [right], rounded toward zero. */
fun NumberProviderScope.div(left: ContextIntProvider, right: ContextIntProvider) = DivContextIntProvider(left, right)

/** Creates a [FloorDivContextIntProvider] returning [left] divided by [right], rounded toward negative infinity. */
fun NumberProviderScope.floorDiv(left: ContextIntProvider, right: ContextIntProvider) = FloorDivContextIntProvider(left, right)

/** Creates a [FloorModContextIntProvider] returning the remainder of [left] divided by [right], with the sign of [right]. */
fun NumberProviderScope.floorMod(left: ContextIntProvider, right: ContextIntProvider) = FloorModContextIntProvider(left, right)

/** Creates a [FromFloatContextIntProvider] returning [input] truncated toward zero. */
fun NumberProviderScope.fromFloat(input: ContextFloatProvider) = FromFloatContextIntProvider(input)

/**
 * Creates an [EnvironmentAttributeContextIntProvider] returning the current value of [attribute].
 *
 * No vanilla attribute is representable as an integer, so read a float one with [fromFloat] and [floatEnvironmentAttribute]:
 *
 * ```
 * fromFloat(floatEnvironmentAttribute(EnvironmentAttributes.Gameplay.SKY_LIGHT_LEVEL))
 * ```
 */
fun NumberProviderScope.intEnvironmentAttribute(attribute: EnvironmentAttributeArgument) =
	EnvironmentAttributeContextIntProvider(attribute)

/**
 * Creates a [NumberDispatcherContextIntProvider] returning the provider of the first [IntNumberDispatcherBuilder.case]
 * whose condition passes, [default] when none does.
 *
 * ```
 * intNumberDispatcher(default = constant(0)) {
 *     case(1) { killedByPlayer() }
 * }
 * ```
 */
fun NumberProviderScope.intNumberDispatcher(default: ContextIntProvider? = null, block: IntNumberDispatcherBuilder.() -> Unit) =
	NumberDispatcherContextIntProvider(IntNumberDispatcherBuilder().apply(block).cases, default)

/** Creates a [NumberDispatcherContextIntProvider] returning the first matching case, or [default] when none does. */
fun NumberProviderScope.intNumberDispatcher(default: Int, block: IntNumberDispatcherBuilder.() -> Unit) =
	intNumberDispatcher(constant(default), block)

/** Creates a [StorageContextIntProvider] reading the NBT [path] of the command storage [storage], [fallback] when it holds no number. */
fun NumberProviderScope.intStorage(storage: String, path: String, fallback: ContextIntProvider? = null) =
	StorageContextIntProvider(fallback, path, storage)

/**
 * Creates a [WeightedListContextIntProvider] drawing one of the [IntWeightedListBuilder.entry] declared in [block].
 *
 * ```
 * intWeightedList {
 *     entry(30, 1)
 *     entry(70, 0)
 * }
 * ```
 */
fun NumberProviderScope.intWeightedList(block: IntWeightedListBuilder.() -> Unit) =
	WeightedListContextIntProvider(IntWeightedListBuilder().apply(block).entries)

/**
 * Creates a [MaxContextIntProvider] returning the largest value among the providers of [inputs].
 *
 * ```
 * max(constant(2), constant(4))  // 4
 * ```
 */
fun NumberProviderScope.max(vararg inputs: ContextIntProvider) = MaxContextIntProvider(inputs.toList())

/** Creates a [MaxContextIntProvider] returning the largest value among the providers of [inputs]. */
fun NumberProviderScope.max(inputs: List<ContextIntProvider>) = MaxContextIntProvider(inputs)

/**
 * Creates a [MaxContextIntProvider] returning the largest value among the int providers of the tag [tag], written `"#my_pack:my_tag"`.
 *
 * ```
 * max(ContextIntProviderTagArgument("bonuses", "my_pack"))  // "inputs": "#my_pack:bonuses"
 * ```
 */
fun NumberProviderScope.max(tag: ContextIntProviderTagArgument) = MaxContextIntProvider(listOf(ContextIntProviderReference(tag)))

/**
 * Creates a [MinContextIntProvider] returning the smallest value among the providers of [inputs].
 *
 * ```
 * min(score("kills", EntityTarget.THIS), constant(5))  // caps the score at 5
 * ```
 */
fun NumberProviderScope.min(vararg inputs: ContextIntProvider) = MinContextIntProvider(inputs.toList())

/** Creates a [MinContextIntProvider] returning the smallest value among the providers of [inputs]. */
fun NumberProviderScope.min(inputs: List<ContextIntProvider>) = MinContextIntProvider(inputs)

/**
 * Creates a [MinContextIntProvider] returning the smallest value among the int providers of the tag [tag], written `"#my_pack:my_tag"`.
 *
 * ```
 * min(ContextIntProviderTagArgument("bonuses", "my_pack"))  // "inputs": "#my_pack:bonuses"
 * ```
 */
fun NumberProviderScope.min(tag: ContextIntProviderTagArgument) = MinContextIntProvider(listOf(ContextIntProviderReference(tag)))

/** Creates a [ModContextIntProvider] returning the remainder of [left] divided by [right], with the sign of [left]. */
fun NumberProviderScope.mod(left: ContextIntProvider, right: ContextIntProvider) = ModContextIntProvider(left, right)

/**
 * Creates a [MulContextIntProvider] returning the product of every provider of [inputs].
 *
 * ```
 * mul(reference(ContextIntProviderArgument("bonus", "my_pack")), constant(2))
 * ```
 */
fun NumberProviderScope.mul(vararg inputs: ContextIntProvider) = MulContextIntProvider(inputs.toList())

/** Creates a [MulContextIntProvider] returning the product of every provider of [inputs]. */
fun NumberProviderScope.mul(inputs: List<ContextIntProvider>) = MulContextIntProvider(inputs)

/**
 * Creates a [MulContextIntProvider] returning the product of every int provider of the tag [tag], written `"#my_pack:my_tag"`.
 *
 * ```
 * mul(ContextIntProviderTagArgument("bonuses", "my_pack"))  // "inputs": "#my_pack:bonuses"
 * ```
 */
fun NumberProviderScope.mul(tag: ContextIntProviderTagArgument) = MulContextIntProvider(listOf(ContextIntProviderReference(tag)))

/** Creates a [NegateContextIntProvider] returning the opposite of [input]. */
fun NumberProviderScope.negate(input: ContextIntProvider) = NegateContextIntProvider(input)

/** Creates a [PowContextIntProvider] returning [base] raised to the power of [exponent]. */
fun NumberProviderScope.pow(base: ContextIntProvider, exponent: ContextIntProvider) = PowContextIntProvider(base, exponent)

/**
 * Creates a [ContextIntProviderReference] pointing at the int provider [id], written as a bare string.
 *
 * ```
 * reference(ContextIntProviderArgument("compost_layers", "my_pack"))  // "my_pack:compost_layers"
 * ```
 */
fun NumberProviderScope.reference(id: ContextIntProviderArgument) = ContextIntProviderReference(id)

/** Creates a [ScoreContextIntProvider] reading [score] from the loot context entity [target], [fallback] when it has none. */
fun NumberProviderScope.score(score: String, target: EntityTarget, fallback: ContextIntProvider? = null) =
	ScoreContextIntProvider(fallback, score, contextScore(target))

/** Creates a [ScoreContextIntProvider] reading [score] from the score holder [name], [fallback] when it has none. */
fun NumberProviderScope.score(score: String, name: String, fallback: ContextIntProvider? = null) =
	ScoreContextIntProvider(fallback, score, fixedScore(name))

/** Creates a [ScoreContextIntProvider] reading [score] from [target], [fallback] when it has none. */
fun NumberProviderScope.score(score: String, target: ScoreProvider, fallback: ContextIntProvider? = null) =
	ScoreContextIntProvider(fallback, score, target)

/** Creates a [SubContextIntProvider] returning [left] minus [right]. */
fun NumberProviderScope.sub(left: ContextIntProvider, right: ContextIntProvider) = SubContextIntProvider(left, right)

/** Creates a [UniformContextIntProvider] drawing between [min] and [max], both included. */
fun NumberProviderScope.uniform(min: ContextIntProvider, max: ContextIntProvider) = UniformContextIntProvider(max, min)

/** Creates a [UniformContextIntProvider] drawing between [min] and [max], both included. */
fun NumberProviderScope.uniform(min: Int, max: Int) = UniformContextIntProvider(constant(max), constant(min))

/** Creates a [UniformContextIntProvider] drawing between [min] and [max], both included. */
fun NumberProviderScope.uniform(min: Int, max: ContextIntProvider) = UniformContextIntProvider(max, constant(min))

/** Creates a [UniformContextIntProvider] drawing between [min] and [max], both included. */
fun NumberProviderScope.uniform(min: ContextIntProvider, max: Int) = UniformContextIntProvider(constant(max), min)

/** Builder scope of [intNumberDispatcher], collecting its cases. */
class IntNumberDispatcherBuilder internal constructor() : NumberProviderScope {
	internal val cases = mutableListOf<NumberDispatcherContextIntProviderCase>()

	/** Adds a case returning [value] when [condition] passes. */
	fun case(condition: PredicateCondition, value: ContextIntProvider) {
		cases += NumberDispatcherContextIntProviderCase(condition, value)
	}

	/** Adds a case returning [value] when [condition] passes. */
	fun case(condition: PredicateCondition, value: Int) = case(condition, constant(value))

	/** Adds a case returning [value] when the predicate [condition] passes. */
	fun case(condition: PredicateArgument, value: ContextIntProvider) = case(PredicateReference(condition), value)

	/** Adds a case returning [value] when the predicate [condition] passes. */
	fun case(condition: PredicateArgument, value: Int) = case(condition, constant(value))

	/** Adds a case returning [value] when the single condition declared in [condition] passes. */
	fun case(value: ContextIntProvider, condition: Predicate.() -> Unit) = case(singleCondition("case", condition), value)

	/** Adds a case returning [value] when the single condition declared in [condition] passes. */
	fun case(value: Int, condition: Predicate.() -> Unit) = case(constant(value), condition)
}

/** Builder scope of [intWeightedList], collecting its entries. */
class IntWeightedListBuilder internal constructor() : NumberProviderScope {
	internal val entries = mutableListOf<WeightedListContextIntProviderEntry>()

	/** Adds an entry returning [data] with a relative chance of [weight]. */
	fun entry(weight: Int, data: ContextIntProvider) {
		entries += WeightedListContextIntProviderEntry(data, weight)
	}

	/** Adds an entry returning [data] with a relative chance of [weight]. */
	fun entry(weight: Int, data: Int) = entry(weight, constant(data))
}
