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
	addLine(command("random", literal("reset"), all(), int(seed), bool(includeWorldSeed), bool(includeSequenceId)))
