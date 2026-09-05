package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.numbers.ranges.IntRange
import io.github.ayfri.kore.arguments.numbers.ranges.asRange
import io.github.ayfri.kore.arguments.types.literals.all
import io.github.ayfri.kore.arguments.types.literals.bool
import io.github.ayfri.kore.arguments.types.literals.int
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.arguments.types.resources.RandomSequenceArgument
import io.github.ayfri.kore.functions.Function
import kotlin.ranges.IntRange as KotlinIntRange

/**
 * Draws a random integer in [range], optionally using the named random [sequence].
 *
 * Identical to [randomRoll] but broadcasts the result to everyone.
 */
fun Function.randomValue(range: KotlinIntRange, sequence: RandomSequenceArgument? = null) =
	addLine(command("random", literal("value"), range.asRange(), sequence))

/** Convenience overload accepting a Kore [IntRange]. */
fun Function.randomValue(range: IntRange, sequence: RandomSequenceArgument? = null) =
	addLine(command("random", literal("value"), range, sequence))

/**
 * Rolls a random integer in [range] and whispers the result privately to the executor.
 */
fun Function.randomRoll(range: KotlinIntRange, sequence: RandomSequenceArgument? = null) =
	addLine(command("random", literal("roll"), range.asRange(), sequence))

/** Convenience overload accepting a Kore [IntRange]. */
fun Function.randomRoll(range: IntRange, sequence: RandomSequenceArgument? = null) =
	addLine(command("random", literal("roll"), range, sequence))

/**
 * Resets the random [sequence] using an optional [seed].
 *
 * @param includeWorldSeed When true, the world seed is mixed into the sequence.
 * @param includeSequenceId When true, the sequence id is mixed into the sequence.
 */
fun Function.randomReset(
	sequence: RandomSequenceArgument,
	seed: Long? = null,
	includeWorldSeed: Boolean? = null,
	includeSequenceId: Boolean? = null,
) = addLine(command("random", literal("reset"), sequence, int(seed), bool(includeWorldSeed), bool(includeSequenceId)))

/** Resets every random sequence at once (`random reset *`). */
fun Function.randomResetAll(seed: Long? = null, includeWorldSeed: Boolean? = null, includeSequenceId: Boolean? = null) =
	addLine(command("random", literal("reset"), all(), int(seed), bool(includeWorldSeed), bool(includeSequenceId)))
