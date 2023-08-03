package commands

import arguments.numbers.ranges.IntRange
import arguments.numbers.ranges.asRange
import arguments.types.literals.bool
import arguments.types.literals.int
import arguments.types.literals.literal
import arguments.types.resources.RandomSequenceArgument
import functions.Function
import kotlin.ranges.IntRange as KotlinIntRange

fun Function.randomValue(range: KotlinIntRange, sequenceId: String? = null) =
	addLine(command("random", literal("value"), range.asRange(), literal(sequenceId)))

fun Function.randomValue(range: IntRange, sequenceId: String? = null) =
	addLine(command("random", literal("value"), range, literal(sequenceId)))

fun Function.randomRoll(range: KotlinIntRange, sequenceId: String? = null) =
	addLine(command("random", literal("roll"), range.asRange(), literal(sequenceId)))

fun Function.randomRoll(range: IntRange, sequenceId: String? = null) =
	addLine(command("random", literal("roll"), range, literal(sequenceId)))

fun Function.randomReset(
	sequenceId: RandomSequenceArgument,
	seed: Long? = null,
	includeWorldSeed: Boolean? = null,
	includeSequenceId: Boolean? = null,
) =
	addLine(command("random", literal("reset"), sequenceId, int(seed), bool(includeWorldSeed), bool(includeSequenceId)))

fun Function.randomResetAll(seed: Long? = null, includeWorldSeed: Boolean? = null, includeSequenceId: Boolean? = null) =
	addLine(command("random", literal("reset"), literal("*"), int(seed), bool(includeWorldSeed), bool(includeSequenceId)))
