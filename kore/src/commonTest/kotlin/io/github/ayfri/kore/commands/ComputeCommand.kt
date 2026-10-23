package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.arguments.types.literals.allEntities
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.arguments.types.resources.storage
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.features.predicates.providers.constant
import io.github.ayfri.kore.features.predicates.providers.reference
import io.github.ayfri.kore.features.predicates.providers.scoreNumber
import io.github.ayfri.kore.features.predicates.providers.sum
import io.github.ayfri.kore.features.predicates.types.EntityTarget
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.load
import io.github.ayfri.kore.generated.arguments.types.NumberProviderArgument
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

fun Function.computeTests() {
	val bonus = NumberProviderArgument("bonus", "my_pack")
	val kills = "{type:\"minecraft:score\",target:{type:\"minecraft:context\",target:\"target_entity\"},score:\"kills\"}"
	val five = "{type:\"minecraft:constant\",value:5}"

	compute(bonus) assertsIs "compute default my_pack:bonus"
	compute(bonus, 2f) assertsIs "compute default my_pack:bonus 2"
	compute(vec3(0, 64, 0), bonus) assertsIs "compute block 0 64 0 my_pack:bonus"
	compute(vec3(0, 64, 0), bonus, 2f) assertsIs "compute block 0 64 0 my_pack:bonus 2"
	compute(self(), bonus) assertsIs "compute entity @s my_pack:bonus"
	compute(self(), bonus, 0.5f) assertsIs "compute entity @s my_pack:bonus 0.5"

	compute { constant(5f) } assertsIs "compute default $five"
	compute(3f) { constant(5f) } assertsIs "compute default $five 3"
	compute(vec3(0, 64, 0)) { constant(5f) } assertsIs "compute block 0 64 0 $five"
	compute(vec3(0, 64, 0), 3f) { constant(5f) } assertsIs "compute block 0 64 0 $five 3"
	compute(self()) { scoreNumber("kills", EntityTarget.TARGET_ENTITY) } assertsIs "compute entity @s $kills"
	compute(allEntities(), 2f) { scoreNumber("kills", EntityTarget.TARGET_ENTITY) } assertsIs "compute entity @e $kills 2"
	compute(5f) assertsIs "compute default $five"
	compute(5f, 3f) assertsIs "compute default $five 3"
	compute(vec3(0, 64, 0), 5f) assertsIs "compute block 0 64 0 $five"
	compute(self(), 5f, 3f) assertsIs "compute entity @s $five 3"
	computeInteger(5f) assertsIs "compute default $five integer"
	computeInteger(vec3(0, 64, 0), 5f) assertsIs "compute block 0 64 0 $five integer"
	computeInteger(self(), 2.5f) assertsIs "compute entity @s {type:\"minecraft:constant\",value:2.5d} integer"
	compute { reference(bonus) } assertsIs "compute default my_pack:bonus"
	compute(self()) { sum(reference(bonus), reference(bonus)) } assertsIs "compute entity @s {type:\"minecraft:sum\",operands:[\"my_pack:bonus\",\"my_pack:bonus\"]}"

	computeInteger(bonus) assertsIs "compute default my_pack:bonus integer"
	computeInteger(vec3(0, 64, 0), bonus) assertsIs "compute block 0 64 0 my_pack:bonus integer"
	computeInteger(self(), bonus) assertsIs "compute entity @s my_pack:bonus integer"
	computeInteger { constant(5f) } assertsIs "compute default $five integer"
	computeInteger(vec3(0, 64, 0)) { constant(5f) } assertsIs "compute block 0 64 0 $five integer"
	computeInteger(self()) { scoreNumber("kills", EntityTarget.TARGET_ENTITY) } assertsIs "compute entity @s $kills integer"

	data(storage("foo")) {
		modify("bar", ComputeSource(bonus)) assertsIs "data modify storage minecraft:foo bar set compute default my_pack:bonus"
		modify("bar") {
			set(ComputeSource(vec3(1, 2, 3), bonus, integer = true))
		} assertsIs "data modify storage minecraft:foo bar set compute block 1 2 3 my_pack:bonus integer"
		modify("bar") { append(ComputeSource(self(), bonus)) } assertsIs "data modify storage minecraft:foo bar append compute entity @s my_pack:bonus"
		modify("bar") { insert(2, ComputeSource(bonus, integer = true)) } assertsIs "data modify storage minecraft:foo bar insert 2 compute default my_pack:bonus integer"
		modify("bar") { merge(ComputeSource(integer = true) { constant(5f) }) } assertsIs "data modify storage minecraft:foo bar merge compute default $five integer"
		modify("bar") { prepend(ComputeSource(vec3(1, 2, 3)) { constant(5f) }) } assertsIs "data modify storage minecraft:foo bar prepend compute block 1 2 3 $five"
		set("bar", ComputeSource(self(), integer = true) { constant(5f) }) assertsIs "data modify storage minecraft:foo bar set compute entity @s $five integer"
		set("bar", ComputeSource(5f)) assertsIs "data modify storage minecraft:foo bar set compute default $five"
		set("bar", ComputeSource(vec3(1, 2, 3), 5f)) assertsIs "data modify storage minecraft:foo bar set compute block 1 2 3 $five"
		set("bar", ComputeSource(self(), 5f, integer = true)) assertsIs "data modify storage minecraft:foo bar set compute entity @s $five integer"
		set("bar", ComputeSource(self(), bonus, integer = true)) assertsIs "data modify storage minecraft:foo bar set compute entity @s my_pack:bonus integer"
	}
}

class ComputeCommandTests : FunSpec({
	test("compute") {
		dataPack("unit_tests") {
			load { computeTests() }
		}
	}

	test("constant provider is written identically on every platform") {
		dataPack("unit_tests") {
			load {
				compute { constant(5f) }.toString() shouldBe "compute default {type:\"minecraft:constant\",value:5}"
				compute { constant(0.5f) }.toString() shouldBe "compute default {type:\"minecraft:constant\",value:0.5d}"
				compute { constant(0.1f) }.toString() shouldBe "compute default {type:\"minecraft:constant\",value:0.1d}"
			}
		}
	}
})
