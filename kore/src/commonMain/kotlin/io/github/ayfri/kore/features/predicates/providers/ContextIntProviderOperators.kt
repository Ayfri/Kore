package io.github.ayfri.kore.features.predicates.providers

import io.github.ayfri.kore.features.predicates.providers.utils.addInts
import io.github.ayfri.kore.features.predicates.providers.utils.mulInts
import io.github.ayfri.kore.features.predicates.providers.utils.negateInt
import io.github.ayfri.kore.features.predicates.providers.utils.subInts

/**
 * Returns the sum of this provider and [other] as an [AddContextIntProvider].
 *
 * A sum chain is one `add`, and the constants of a chain are merged into one, put last. The game adds every input in a
 * `long` and fails once, on the final sum, so `score + 1 + 2` is `add(score, 3)` and a chain only fails when its total
 * overflows. A merged constant that wouldn't fit an int is left as written, and a `0` operand is dropped.
 *
 * ```
 * score("kills", EntityTarget.THIS) + 3 + 4   // add([<kills>, 7])
 * score("kills", EntityTarget.THIS) + 0       // <kills>
 * score + Int.MAX_VALUE + 1                   // add([<score>, 2147483647, 1])
 * ```
 */
operator fun ContextIntProvider.plus(other: ContextIntProvider) = addInts(this, other)

/** Returns the sum of this provider and the constant [other], see [plus]. */
operator fun ContextIntProvider.plus(other: Int) = addInts(this, ConstantContextIntProvider(other))

/**
 * Creates a [SubContextIntProvider] returning this provider minus [other], which fails when the result doesn't fit in 32 bits.
 *
 * Two constants are folded when the result fits an int.
 *
 * ```
 * score("kills", EntityTarget.THIS) - 2   // sub(<kills>, 2)
 * ```
 */
operator fun ContextIntProvider.minus(other: ContextIntProvider) = subInts(this, other)

/** Creates a [SubContextIntProvider] returning this provider minus the constant [other], see [minus]. */
operator fun ContextIntProvider.minus(other: Int) = subInts(this, ConstantContextIntProvider(other))

/**
 * Returns the product of this provider and [other] as a [MulContextIntProvider].
 *
 * A product chain is one `mul`, and its constants are merged into one, put last, with the same overflow rule as [plus]. A
 * `1` operand is dropped.
 *
 * ```
 * score("kills", EntityTarget.THIS) * 2 * 3   // mul([<kills>, 6])
 * ```
 */
operator fun ContextIntProvider.times(other: ContextIntProvider) = mulInts(this, other)

/** Returns the product of this provider and the constant [other], see [times]. */
operator fun ContextIntProvider.times(other: Int) = mulInts(this, ConstantContextIntProvider(other))

/**
 * Creates a [DivContextIntProvider] returning this provider divided by [other], rounded toward zero. It is never
 * folded, since a division by `0` has to fail in the game.
 *
 * ```
 * score("kills", EntityTarget.THIS) / 2   // div(<kills>, 2)
 * ```
 */
operator fun ContextIntProvider.div(other: ContextIntProvider) = DivContextIntProvider(this, other)

/** Creates a [DivContextIntProvider] returning this provider divided by the constant [other], see [div]. */
operator fun ContextIntProvider.div(other: Int) = DivContextIntProvider(this, ConstantContextIntProvider(other))

/**
 * Creates a [ModContextIntProvider] returning the remainder of this provider divided by [other], with the sign of this
 * provider like Kotlin's `%`. It is never folded.
 *
 * ```
 * score("kills", EntityTarget.THIS) % 7   // mod(<kills>, 7)
 * ```
 */
operator fun ContextIntProvider.rem(other: ContextIntProvider) = ModContextIntProvider(this, other)

/** Creates a [ModContextIntProvider] returning the remainder of this provider divided by the constant [other], see [rem]. */
operator fun ContextIntProvider.rem(other: Int) = ModContextIntProvider(this, ConstantContextIntProvider(other))

/**
 * Creates a [NegateContextIntProvider] returning the opposite of this provider, which fails for the smallest int. A
 * constant is folded, except the smallest int.
 *
 * ```
 * -score("kills", EntityTarget.THIS)   // negate(<kills>)
 * -constant(5)                         // -5
 * ```
 */
operator fun ContextIntProvider.unaryMinus() = negateInt(this)

/**
 * Creates a [FloorDivContextIntProvider] returning this provider divided by [other], rounded toward negative infinity.
 * It is never folded.
 *
 * ```
 * score("kills", EntityTarget.THIS) floorDiv 3   // floor_div(<kills>, 3)
 * ```
 */
infix fun ContextIntProvider.floorDiv(other: ContextIntProvider) = FloorDivContextIntProvider(this, other)

/** Creates a [FloorDivContextIntProvider] returning this provider divided by the constant [other], see [floorDiv]. */
infix fun ContextIntProvider.floorDiv(other: Int) = FloorDivContextIntProvider(this, ConstantContextIntProvider(other))

/**
 * Creates a [FloorModContextIntProvider] returning the remainder of this provider divided by [other], with the sign of
 * [other]. It is never folded.
 *
 * ```
 * score("kills", EntityTarget.THIS) floorMod 3   // floor_mod(<kills>, 3)
 * ```
 */
infix fun ContextIntProvider.floorMod(other: ContextIntProvider) = FloorModContextIntProvider(this, other)

/** Creates a [FloorModContextIntProvider] returning the remainder of this provider divided by the constant [other], see [floorMod]. */
infix fun ContextIntProvider.floorMod(other: Int) = FloorModContextIntProvider(this, ConstantContextIntProvider(other))

/**
 * Creates a [PowContextIntProvider] returning this provider raised to the power of [other]. It is never folded.
 *
 * ```
 * score("kills", EntityTarget.THIS) pow 2   // pow(<kills>, 2)
 * ```
 */
infix fun ContextIntProvider.pow(other: ContextIntProvider) = PowContextIntProvider(this, other)

/** Creates a [PowContextIntProvider] returning this provider raised to the constant power [other], see [pow]. */
infix fun ContextIntProvider.pow(other: Int) = PowContextIntProvider(this, ConstantContextIntProvider(other))

/** Creates a [FromIntContextFloatProvider] returning this provider as a float, to continue the expression in float maths. */
fun ContextIntProvider.toFloat() = FromIntContextFloatProvider(this)

/**
 * Creates a [MaxContextIntProvider] of a [MinContextIntProvider], clamping this provider between [min] and [max].
 *
 * ```
 * score("kills", EntityTarget.THIS).coerceIn(0, 10)   // max([min([<kills>, 10]), 0])
 * ```
 */
fun ContextIntProvider.coerceIn(min: Int, max: Int): MaxContextIntProvider {
	require(min <= max) { "Cannot coerce to an empty range: min $min is greater than max $max." }
	return MaxContextIntProvider(listOf(MinContextIntProvider(listOf(this, ConstantContextIntProvider(max))), ConstantContextIntProvider(min)))
}

/** Creates a [MaxContextIntProvider] returning at least [min], `max([<provider>, min])`. */
fun ContextIntProvider.coerceAtLeast(min: Int) = MaxContextIntProvider(listOf(this, ConstantContextIntProvider(min)))

/** Creates a [MinContextIntProvider] returning at most [max], `min([<provider>, max])`. */
fun ContextIntProvider.coerceAtMost(max: Int) = MinContextIntProvider(listOf(this, ConstantContextIntProvider(max)))

/**
 * Returns the sum of the constant `this` and [other], so a number can lead an expression, see [plus]. It only resolves
 * where [NumberProviderScope] is available, a `compute` lambda or a loot table builder.
 *
 * ```
 * compute(self()) { 1 + score("kills", EntityTarget.THIS) }   // add([<kills>, 1])
 * ```
 */
context(_: NumberProviderScope)
operator fun Int.plus(other: ContextIntProvider) = addInts(ConstantContextIntProvider(this), other)

/** Creates the constant `this` minus [other], see [minus]. */
context(_: NumberProviderScope)
operator fun Int.minus(other: ContextIntProvider) = subInts(ConstantContextIntProvider(this), other)

/** Returns the product of the constant `this` and [other], see [times]. */
context(_: NumberProviderScope)
operator fun Int.times(other: ContextIntProvider) = mulInts(ConstantContextIntProvider(this), other)

/** Creates the constant `this` divided by [other], see [div]. */
context(_: NumberProviderScope)
operator fun Int.div(other: ContextIntProvider) = DivContextIntProvider(ConstantContextIntProvider(this), other)

/** Creates the remainder of the constant `this` divided by [other], see [rem]. */
context(_: NumberProviderScope)
operator fun Int.rem(other: ContextIntProvider) = ModContextIntProvider(ConstantContextIntProvider(this), other)
