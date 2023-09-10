package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.numbers.ranges.range
import io.github.ayfri.kore.arguments.types.resources.RandomSequenceArgument
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.functions.Function

fun Function.randomTests() {
	randomValue(1..6) assertsIs "random value 1..6"
	randomValue(1..6, "mySequence") assertsIs "random value 1..6 mySequence"
	randomValue(range(10, 20)) assertsIs "random value 10..20"
	randomValue(1..<6) assertsIs "random value 1..5"

	randomRoll(1..20) assertsIs "random roll 1..20"
	randomRoll(0..100, "sequence1") assertsIs "random roll 0..100 sequence1"

	val randomSequenceId = RandomSequenceArgument("sequence1")
	randomReset(randomSequenceId) assertsIs "random reset minecraft:sequence1"
	randomReset(randomSequenceId, 1234) assertsIs "random reset minecraft:sequence1 1234"
	randomReset(randomSequenceId, includeWorldSeed = false) assertsIs "random reset minecraft:sequence1 false"

	randomResetAll(includeSequenceId = false) assertsIs "random reset * false"
	randomResetAll(1234, includeWorldSeed = false, includeSequenceId = false) assertsIs "random reset * 1234 false false"
}
