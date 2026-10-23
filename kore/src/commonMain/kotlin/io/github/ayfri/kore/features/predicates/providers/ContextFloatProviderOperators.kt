package io.github.ayfri.kore.features.predicates.providers

import io.github.ayfri.kore.features.predicates.providers.utils.addFloats
import io.github.ayfri.kore.features.predicates.providers.utils.mulFloats

/**
 * Returns the sum of this provider and [other] as an [AddContextFloatProvider].
 *
 * Only the left operand is flattened, so `(a + b) + c` is one `add` and `a + (b + c)` keeps its nesting: float sums
 * aren't associative, and the game adds the inputs of an `add` in order. Constants are never folded, since that could
 * change the rounding. An operand of `0f` is dropped, which can keep a `-0.0` that the game, whose float `add` starts
 * from `0.0f`, would turn into `0.0`.
 *
 * ```
 * score.toFloat() + 1.5f + 2f   // add([from_int(<score>), 1.5, 2.0])
 * score.toFloat() + 0f          // from_int(<score>)
 * ```
 */
operator fun ContextFloatProvider.plus(other: ContextFloatProvider) = addFloats(this, other)

/** Returns the sum of this provider and the constant [other], see [plus]. */
operator fun ContextFloatProvider.plus(other: Float) = addFloats(this, ConstantContextFloatProvider(other))

/**
 * Creates a [SubContextFloatProvider] returning this provider minus [other]. It is never folded.
 *
 * ```
 * speed - 0.5f   // sub(<speed>, 0.5)
 * ```
 */
operator fun ContextFloatProvider.minus(other: ContextFloatProvider) = SubContextFloatProvider(this, other)

/** Creates a [SubContextFloatProvider] returning this provider minus the constant [other], see [minus]. */
operator fun ContextFloatProvider.minus(other: Float) = SubContextFloatProvider(this, ConstantContextFloatProvider(other))

/**
 * Returns the product of this provider and [other] as a [MulContextFloatProvider], flattened and shaped like [plus]. An
 * operand of `1f` is dropped.
 *
 * ```
 * score.toFloat() * 1.5f * 2f   // mul([from_int(<score>), 1.5, 2.0])
 * ```
 */
operator fun ContextFloatProvider.times(other: ContextFloatProvider) = mulFloats(this, other)

/** Returns the product of this provider and the constant [other], see [times]. */
operator fun ContextFloatProvider.times(other: Float) = mulFloats(this, ConstantContextFloatProvider(other))

/**
 * Creates a [DivContextFloatProvider] returning this provider divided by [other]. It is never folded.
 *
 * ```
 * speed / 2f   // div(<speed>, 2.0)
 * ```
 */
operator fun ContextFloatProvider.div(other: ContextFloatProvider) = DivContextFloatProvider(this, other)

/** Creates a [DivContextFloatProvider] returning this provider divided by the constant [other], see [div]. */
operator fun ContextFloatProvider.div(other: Float) = DivContextFloatProvider(this, ConstantContextFloatProvider(other))

/**
 * Creates a [ModContextFloatProvider] returning the remainder of this provider divided by [other]. The game gives it the
 * sign of [other], unlike Kotlin's `%` on floats. It is never folded.
 *
 * ```
 * angle % 360f   // mod(<angle>, 360.0)
 * ```
 */
operator fun ContextFloatProvider.rem(other: ContextFloatProvider) = ModContextFloatProvider(this, other)

/** Creates a [ModContextFloatProvider] returning the remainder of this provider divided by the constant [other], see [rem]. */
operator fun ContextFloatProvider.rem(other: Float) = ModContextFloatProvider(this, ConstantContextFloatProvider(other))

/**
 * Creates a [NegateContextFloatProvider] returning the opposite of this provider. It is never folded.
 *
 * ```
 * -speed   // negate(<speed>)
 * ```
 */
operator fun ContextFloatProvider.unaryMinus() = NegateContextFloatProvider(this)

/**
 * Creates a [PowContextFloatProvider] returning this provider raised to the power of [other]. It is never folded.
 *
 * ```
 * speed pow 2f   // pow(<speed>, 2.0)
 * ```
 */
infix fun ContextFloatProvider.pow(other: ContextFloatProvider) = PowContextFloatProvider(this, other)

/** Creates a [PowContextFloatProvider] returning this provider raised to the constant power [other], see [pow]. */
infix fun ContextFloatProvider.pow(other: Float) = PowContextFloatProvider(this, ConstantContextFloatProvider(other))

/** Creates a [FromFloatContextIntProvider] returning this provider truncated toward zero, to continue the expression in int maths. */
fun ContextFloatProvider.toInt() = FromFloatContextIntProvider(this)

/**
 * Creates a [MaxContextFloatProvider] of a [MinContextFloatProvider], clamping this provider between [min] and [max].
 *
 * ```
 * speed.coerceIn(0f, 2f)   // max([min([<speed>, 2.0]), 0.0])
 * ```
 */
fun ContextFloatProvider.coerceIn(min: Float, max: Float): MaxContextFloatProvider {
	require(min <= max) { "Cannot coerce to an empty range: min $min is greater than max $max." }
	return MaxContextFloatProvider(listOf(MinContextFloatProvider(listOf(this, ConstantContextFloatProvider(max))), ConstantContextFloatProvider(min)))
}

/** Creates a [MaxContextFloatProvider] returning at least [min], `max([<provider>, min])`. */
fun ContextFloatProvider.coerceAtLeast(min: Float) = MaxContextFloatProvider(listOf(this, ConstantContextFloatProvider(min)))

/** Creates a [MinContextFloatProvider] returning at most [max], `min([<provider>, max])`. */
fun ContextFloatProvider.coerceAtMost(max: Float) = MinContextFloatProvider(listOf(this, ConstantContextFloatProvider(max)))

/**
 * Returns the sum of the constant `this` and [other], so a number can lead an expression, see [plus]. It only resolves
 * where [NumberProviderScope] is available, a `compute` lambda or a loot table builder.
 *
 * ```
 * compute(self()) { 1.5f + speed }   // add([1.5, <speed>])
 * ```
 */
context(_: NumberProviderScope)
operator fun Float.plus(other: ContextFloatProvider) = addFloats(ConstantContextFloatProvider(this), other)

/** Creates the constant `this` minus [other], see [minus]. */
context(_: NumberProviderScope)
operator fun Float.minus(other: ContextFloatProvider) = SubContextFloatProvider(ConstantContextFloatProvider(this), other)

/** Returns the product of the constant `this` and [other], see [times]. */
context(_: NumberProviderScope)
operator fun Float.times(other: ContextFloatProvider) = mulFloats(ConstantContextFloatProvider(this), other)

/** Creates the constant `this` divided by [other], see [div]. */
context(_: NumberProviderScope)
operator fun Float.div(other: ContextFloatProvider) = DivContextFloatProvider(ConstantContextFloatProvider(this), other)

/** Creates the remainder of the constant `this` divided by [other], see [rem]. */
context(_: NumberProviderScope)
operator fun Float.rem(other: ContextFloatProvider) = ModContextFloatProvider(ConstantContextFloatProvider(this), other)
