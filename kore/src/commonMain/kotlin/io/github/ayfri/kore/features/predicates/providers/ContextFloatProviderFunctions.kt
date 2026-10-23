package io.github.ayfri.kore.features.predicates.providers

import io.github.ayfri.kore.features.enchantments.values.LevelBased
import io.github.ayfri.kore.features.enchantments.values.constantLevelBased
import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.conditions.PredicateCondition
import io.github.ayfri.kore.features.predicates.conditions.PredicateReference
import io.github.ayfri.kore.features.predicates.conditions.singleCondition
import io.github.ayfri.kore.generated.arguments.tagged.ContextFloatProviderTagArgument
import io.github.ayfri.kore.generated.arguments.types.ContextFloatProviderArgument
import io.github.ayfri.kore.generated.arguments.types.EnvironmentAttributeArgument
import io.github.ayfri.kore.generated.arguments.types.PredicateArgument

/** Creates an [AbsContextFloatProvider] returning the absolute value of [input]. */
fun NumberProviderScope.abs(input: ContextFloatProvider) = AbsContextFloatProvider(input)

/**
 * Creates an [AddContextFloatProvider] returning the sum of every provider of [inputs].
 *
 * ```
 * add(constant(2f), constant(4f))  // 6.0
 * ```
 */
fun NumberProviderScope.add(vararg inputs: ContextFloatProvider) = AddContextFloatProvider(inputs.toList())

/** Creates an [AddContextFloatProvider] returning the sum of every provider of [inputs]. */
fun NumberProviderScope.add(inputs: List<ContextFloatProvider>) = AddContextFloatProvider(inputs)

/**
 * Creates an [AddContextFloatProvider] returning the sum of every float provider of the tag [tag], written `"#my_pack:my_tag"`.
 *
 * ```
 * add(ContextFloatProviderTagArgument("bonuses", "my_pack"))  // "inputs": "#my_pack:bonuses"
 * ```
 */
fun NumberProviderScope.add(tag: ContextFloatProviderTagArgument) = AddContextFloatProvider(listOf(ContextFloatProviderReference(tag)))

/**
 * Creates an [AvgContextFloatProvider] returning the mean of every provider of [inputs].
 *
 * ```
 * avg(constant(2f), constant(4f))  // 3.0
 * ```
 */
fun NumberProviderScope.avg(vararg inputs: ContextFloatProvider) = AvgContextFloatProvider(inputs.toList())

/** Creates an [AvgContextFloatProvider] returning the mean of every provider of [inputs]. */
fun NumberProviderScope.avg(inputs: List<ContextFloatProvider>) = AvgContextFloatProvider(inputs)

/**
 * Creates an [AvgContextFloatProvider] returning the mean of every float provider of the tag [tag], written `"#my_pack:my_tag"`.
 *
 * ```
 * avg(ContextFloatProviderTagArgument("bonuses", "my_pack"))  // "inputs": "#my_pack:bonuses"
 * ```
 */
fun NumberProviderScope.avg(tag: ContextFloatProviderTagArgument) = AvgContextFloatProvider(listOf(ContextFloatProviderReference(tag)))

/** Creates a [CeilContextFloatProvider] returning [input] rounded up to the next whole number. */
fun NumberProviderScope.ceil(input: ContextFloatProvider) = CeilContextFloatProvider(input)

/**
 * Creates a [ConditionalContextFloatProvider] returning [onTrue] when [condition] passes, [onFalse] otherwise.
 *
 * ```
 * conditional(SurvivesExplosion, constant(1f), constant(0f))
 * ```
 */
fun NumberProviderScope.conditional(
	condition: PredicateCondition,
	onTrue: ContextFloatProvider,
	onFalse: ContextFloatProvider? = null,
) = ConditionalContextFloatProvider(condition, onFalse, onTrue)

/** Creates a [ConditionalContextFloatProvider] returning [onTrue] when [condition] passes, [onFalse] otherwise. */
fun NumberProviderScope.conditional(condition: PredicateCondition, onTrue: Float, onFalse: Float? = null) =
	ConditionalContextFloatProvider(condition, onFalse?.let { constant(it) }, constant(onTrue))

/**
 * Creates a [ConditionalContextFloatProvider] returning [onTrue] when the predicate [condition] passes, [onFalse] otherwise.
 *
 * ```
 * conditional(PredicateArgument("fast_cooking", "my_pack"), 800f, 1600f)  // "condition": "my_pack:fast_cooking"
 * ```
 */
fun NumberProviderScope.conditional(
	condition: PredicateArgument,
	onTrue: ContextFloatProvider,
	onFalse: ContextFloatProvider? = null,
) = ConditionalContextFloatProvider(PredicateReference(condition), onFalse, onTrue)

/** Creates a [ConditionalContextFloatProvider] returning [onTrue] when the predicate [condition] passes, [onFalse] otherwise. */
fun NumberProviderScope.conditional(condition: PredicateArgument, onTrue: Float, onFalse: Float? = null) =
	conditional(condition, constant(onTrue), onFalse?.let { constant(it) })

/**
 * Creates a [ConditionalContextFloatProvider] returning [onTrue] when the single condition declared in [condition]
 * passes, [onFalse] otherwise.
 *
 * ```
 * conditional(constant(1f), constant(0f)) { killedByPlayer() }
 * ```
 */
fun NumberProviderScope.conditional(
	onTrue: ContextFloatProvider,
	onFalse: ContextFloatProvider? = null,
	condition: Predicate.() -> Unit,
) = ConditionalContextFloatProvider(singleCondition("conditional", condition), onFalse, onTrue)

/** Creates a [ConditionalContextFloatProvider] returning [onTrue] when the single condition declared in [condition] passes. */
fun NumberProviderScope.conditional(onTrue: Float, onFalse: Float? = null, condition: Predicate.() -> Unit) =
	ConditionalContextFloatProvider(singleCondition("conditional", condition), onFalse?.let { constant(it) }, constant(onTrue))

/** Creates a [ConstantContextFloatProvider] always returning [value], serialized inline as a bare number. */
fun NumberProviderScope.constant(value: Float) = ConstantContextFloatProvider(value)

/** Creates a [CosContextFloatProvider] returning the cosine of [input], in radians. */
fun NumberProviderScope.cos(input: ContextFloatProvider) = CosContextFloatProvider(input)

/** Creates a [DivContextFloatProvider] returning [left] divided by [right]. */
fun NumberProviderScope.div(left: ContextFloatProvider, right: ContextFloatProvider) = DivContextFloatProvider(left, right)

/** Creates an [EnchantmentLevelContextFloatProvider] returning the fixed [amount] whatever the enchantment level is. */
fun NumberProviderScope.enchantmentLevel(amount: Int) = EnchantmentLevelContextFloatProvider(LevelBased.constantLevelBased(amount))

/** Creates an [EnchantmentLevelContextFloatProvider] returning [amount] evaluated at the current enchantment level. */
fun NumberProviderScope.enchantmentLevel(amount: LevelBased) = EnchantmentLevelContextFloatProvider(amount)

/** Creates an [EnvironmentAttributeContextFloatProvider] returning the current value of [attribute]. */
fun NumberProviderScope.floatEnvironmentAttribute(attribute: EnvironmentAttributeArgument) =
	EnvironmentAttributeContextFloatProvider(attribute)

/**
 * Creates a [NumberDispatcherContextFloatProvider] returning the provider of the first
 * [FloatNumberDispatcherBuilder.case] whose condition passes, [default] when none does.
 *
 * ```
 * floatNumberDispatcher(default = constant(0f)) {
 *     case(1f) { killedByPlayer() }
 * }
 * ```
 */
fun NumberProviderScope.floatNumberDispatcher(
	default: ContextFloatProvider? = null,
	block: FloatNumberDispatcherBuilder.() -> Unit,
) = NumberDispatcherContextFloatProvider(FloatNumberDispatcherBuilder().apply(block).cases, default)

/** Creates a [NumberDispatcherContextFloatProvider] returning the first matching case, or [default] when none does. */
fun NumberProviderScope.floatNumberDispatcher(default: Float, block: FloatNumberDispatcherBuilder.() -> Unit) =
	floatNumberDispatcher(constant(default), block)

/** Creates a [StorageContextFloatProvider] reading the NBT [path] of the command storage [storage], [fallback] when it holds no number. */
fun NumberProviderScope.floatStorage(storage: String, path: String, fallback: ContextFloatProvider? = null) =
	StorageContextFloatProvider(fallback, path, storage)

/**
 * Creates a [WeightedListContextFloatProvider] drawing one of the [FloatWeightedListBuilder.entry] declared in [block].
 *
 * ```
 * floatWeightedList {
 *     entry(30, 1f)
 *     entry(70, 0f)
 * }
 * ```
 */
fun NumberProviderScope.floatWeightedList(block: FloatWeightedListBuilder.() -> Unit) =
	WeightedListContextFloatProvider(FloatWeightedListBuilder().apply(block).entries)

/** Creates a [FloorContextFloatProvider] returning [input] rounded down to the previous whole number. */
fun NumberProviderScope.floor(input: ContextFloatProvider) = FloorContextFloatProvider(input)

/** Creates a [FromIntContextFloatProvider] returning [input] as a float. */
fun NumberProviderScope.fromInt(input: ContextIntProvider) = FromIntContextFloatProvider(input)

/**
 * Creates a [LengthContextFloatProvider] returning the euclidean length of the vector whose components are [inputs].
 *
 * ```
 * length(constant(3f), constant(4f))  // 5.0
 * ```
 */
fun NumberProviderScope.length(vararg inputs: ContextFloatProvider) = LengthContextFloatProvider(inputs.toList())

/** Creates a [LengthContextFloatProvider] returning the euclidean length of the vector whose components are [inputs]. */
fun NumberProviderScope.length(inputs: List<ContextFloatProvider>) = LengthContextFloatProvider(inputs)

/**
 * Creates a [LengthContextFloatProvider] returning the euclidean length of the vector whose components are the float providers of the tag [tag], written `"#my_pack:my_tag"`.
 *
 * ```
 * length(ContextFloatProviderTagArgument("components", "my_pack"))  // "inputs": "#my_pack:components"
 * ```
 */
fun NumberProviderScope.length(tag: ContextFloatProviderTagArgument) = LengthContextFloatProvider(listOf(ContextFloatProviderReference(tag)))

/**
 * Creates a [MaxContextFloatProvider] returning the largest value among the providers of [inputs].
 *
 * ```
 * max(constant(2f), constant(4f))  // 4.0
 * ```
 */
fun NumberProviderScope.max(vararg inputs: ContextFloatProvider) = MaxContextFloatProvider(inputs.toList())

/** Creates a [MaxContextFloatProvider] returning the largest value among the providers of [inputs]. */
fun NumberProviderScope.max(inputs: List<ContextFloatProvider>) = MaxContextFloatProvider(inputs)

/**
 * Creates a [MaxContextFloatProvider] returning the largest value among the float providers of the tag [tag], written `"#my_pack:my_tag"`.
 *
 * ```
 * max(ContextFloatProviderTagArgument("bonuses", "my_pack"))  // "inputs": "#my_pack:bonuses"
 * ```
 */
fun NumberProviderScope.max(tag: ContextFloatProviderTagArgument) = MaxContextFloatProvider(listOf(ContextFloatProviderReference(tag)))

/** Creates a [MaxContextFloatProvider] returning the largest value between [input] and the constant [other]. */
fun NumberProviderScope.max(input: ContextFloatProvider, other: Float) = MaxContextFloatProvider(listOf(input, constant(other)))

/**
 * Creates a [MinContextFloatProvider] returning the smallest value among the providers of [inputs].
 *
 * ```
 * min(constant(2f), constant(4f))  // 2.0
 * ```
 */
fun NumberProviderScope.min(vararg inputs: ContextFloatProvider) = MinContextFloatProvider(inputs.toList())

/** Creates a [MinContextFloatProvider] returning the smallest value among the providers of [inputs]. */
fun NumberProviderScope.min(inputs: List<ContextFloatProvider>) = MinContextFloatProvider(inputs)

/**
 * Creates a [MinContextFloatProvider] returning the smallest value among the float providers of the tag [tag], written `"#my_pack:my_tag"`.
 *
 * ```
 * min(ContextFloatProviderTagArgument("bonuses", "my_pack"))  // "inputs": "#my_pack:bonuses"
 * ```
 */
fun NumberProviderScope.min(tag: ContextFloatProviderTagArgument) = MinContextFloatProvider(listOf(ContextFloatProviderReference(tag)))

/** Creates a [MinContextFloatProvider] returning the smallest value between [input] and the constant [other]. */
fun NumberProviderScope.min(input: ContextFloatProvider, other: Float) = MinContextFloatProvider(listOf(input, constant(other)))

/** Creates a [ModContextFloatProvider] returning the remainder of [left] divided by [right], with the sign of [right]. */
fun NumberProviderScope.mod(left: ContextFloatProvider, right: ContextFloatProvider) = ModContextFloatProvider(left, right)

/**
 * Creates a [MulContextFloatProvider] returning the product of every provider of [inputs].
 *
 * ```
 * mul(reference(ContextFloatProviderArgument("bonus", "my_pack")), constant(2f))
 * ```
 */
fun NumberProviderScope.mul(vararg inputs: ContextFloatProvider) = MulContextFloatProvider(inputs.toList())

/** Creates a [MulContextFloatProvider] returning the product of every provider of [inputs]. */
fun NumberProviderScope.mul(inputs: List<ContextFloatProvider>) = MulContextFloatProvider(inputs)

/**
 * Creates a [MulContextFloatProvider] returning the product of every float provider of the tag [tag], written `"#my_pack:my_tag"`.
 *
 * ```
 * mul(ContextFloatProviderTagArgument("bonuses", "my_pack"))  // "inputs": "#my_pack:bonuses"
 * ```
 */
fun NumberProviderScope.mul(tag: ContextFloatProviderTagArgument) = MulContextFloatProvider(listOf(ContextFloatProviderReference(tag)))

/** Creates a [NegateContextFloatProvider] returning the opposite of [input]. */
fun NumberProviderScope.negate(input: ContextFloatProvider) = NegateContextFloatProvider(input)

/** Creates a [PowContextFloatProvider] returning [base] raised to the power of [exponent]. */
fun NumberProviderScope.pow(base: ContextFloatProvider, exponent: ContextFloatProvider) = PowContextFloatProvider(base, exponent)

/**
 * Creates a [ContextFloatProviderReference] pointing at the float provider [id], written as a bare string.
 *
 * ```
 * reference(ContextFloatProviderArgument("compost_chance", "my_pack"))  // "my_pack:compost_chance"
 * ```
 */
fun NumberProviderScope.reference(id: ContextFloatProviderArgument) = ContextFloatProviderReference(id)

/** Creates a [RoundContextFloatProvider] returning [input] rounded to the nearest whole number. */
fun NumberProviderScope.round(input: ContextFloatProvider) = RoundContextFloatProvider(input)

/** Creates a [SinContextFloatProvider] returning the sine of [input], in radians. */
fun NumberProviderScope.sin(input: ContextFloatProvider) = SinContextFloatProvider(input)

/** Creates a [SqrtContextFloatProvider] returning the square root of [input]. */
fun NumberProviderScope.sqrt(input: ContextFloatProvider) = SqrtContextFloatProvider(input)

/** Creates a [SubContextFloatProvider] returning [left] minus [right]. */
fun NumberProviderScope.sub(left: ContextFloatProvider, right: ContextFloatProvider) = SubContextFloatProvider(left, right)

/** Creates a [TruncateContextFloatProvider] returning [input] with its decimals removed. */
fun NumberProviderScope.truncate(input: ContextFloatProvider) = TruncateContextFloatProvider(input)

/** Creates a [UniformContextFloatProvider] drawing between [min] and [max]. */
fun NumberProviderScope.uniform(min: ContextFloatProvider, max: ContextFloatProvider) = UniformContextFloatProvider(max, min)

/** Creates a [UniformContextFloatProvider] drawing between [min] and [max]. */
fun NumberProviderScope.uniform(min: Float, max: Float) = UniformContextFloatProvider(constant(max), constant(min))

/** Creates a [UniformContextFloatProvider] drawing between [min] and [max]. */
fun NumberProviderScope.uniform(min: Float, max: ContextFloatProvider) = UniformContextFloatProvider(max, constant(min))

/** Creates a [UniformContextFloatProvider] drawing between [min] and [max]. */
fun NumberProviderScope.uniform(min: ContextFloatProvider, max: Float) = UniformContextFloatProvider(constant(max), min)

/** Builder scope of [floatNumberDispatcher], collecting its cases. */
class FloatNumberDispatcherBuilder internal constructor() : NumberProviderScope {
	internal val cases = mutableListOf<NumberDispatcherContextFloatProviderCase>()

	/** Adds a case returning [value] when [condition] passes. */
	fun case(condition: PredicateCondition, value: ContextFloatProvider) {
		cases += NumberDispatcherContextFloatProviderCase(condition, value)
	}

	/** Adds a case returning [value] when [condition] passes. */
	fun case(condition: PredicateCondition, value: Float) = case(condition, constant(value))

	/** Adds a case returning [value] when the predicate [condition] passes. */
	fun case(condition: PredicateArgument, value: ContextFloatProvider) = case(PredicateReference(condition), value)

	/** Adds a case returning [value] when the predicate [condition] passes. */
	fun case(condition: PredicateArgument, value: Float) = case(condition, constant(value))

	/** Adds a case returning [value] when the single condition declared in [condition] passes. */
	fun case(value: ContextFloatProvider, condition: Predicate.() -> Unit) = case(singleCondition("case", condition), value)

	/** Adds a case returning [value] when the single condition declared in [condition] passes. */
	fun case(value: Float, condition: Predicate.() -> Unit) = case(constant(value), condition)
}

/** Builder scope of [floatWeightedList], collecting its entries. */
class FloatWeightedListBuilder internal constructor() : NumberProviderScope {
	internal val entries = mutableListOf<WeightedListContextFloatProviderEntry>()

	/** Adds an entry returning [data] with a relative chance of [weight]. */
	fun entry(weight: Int, data: ContextFloatProvider) {
		entries += WeightedListContextFloatProviderEntry(data, weight)
	}

	/** Adds an entry returning [data] with a relative chance of [weight]. */
	fun entry(weight: Int, data: Float) = entry(weight, constant(data))
}
