package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.arguments.types.literals.allEntities
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.arguments.types.resources.storage
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.features.predicates.providers.add
import io.github.ayfri.kore.features.predicates.providers.constant
import io.github.ayfri.kore.features.predicates.providers.reference
import io.github.ayfri.kore.features.predicates.providers.score
import io.github.ayfri.kore.features.predicates.providers.sqrt
import io.github.ayfri.kore.features.predicates.types.EntityTarget
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.load
import io.github.ayfri.kore.generated.ContextFloatProviders
import io.github.ayfri.kore.generated.ContextIntProviders
import io.github.ayfri.kore.generated.arguments.types.ContextFloatProviderArgument
import io.github.ayfri.kore.generated.arguments.types.ContextIntProviderArgument
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

fun Function.computeTests() {
	val bonus = ContextIntProviderArgument("bonus", "my_pack")
	val speed = ContextFloatProviderArgument("speed", "my_pack")
	val kills = "{type:\"minecraft:score\",score:kills,target:{type:\"minecraft:context\",target:target_entity}}"
	val five = "{type:\"minecraft:constant\",value:5}"
	val fiveFloat = "{type:\"minecraft:constant\",value:5.0f}"

	compute(bonus) assertsIs "compute default integer my_pack:bonus"
	compute(vec3(0, 64, 0), bonus) assertsIs "compute block 0 64 0 integer my_pack:bonus"
	compute(vec3(0, 64, 0), ContextIntProviders.Cooking.TIME_COAL) assertsIs "compute block 0 64 0 integer minecraft:cooking/time_coal"
	compute(self(), bonus) assertsIs "compute entity @s integer my_pack:bonus"

	compute(speed) assertsIs "compute default float my_pack:speed"
	compute(speed, 2f) assertsIs "compute default float my_pack:speed 2"
	compute(vec3(0, 64, 0), speed, 2f) assertsIs "compute block 0 64 0 float my_pack:speed 2"
	compute(self(), ContextFloatProviders.Cooking.SPEED_DEFAULT, 10f) assertsIs "compute entity @s float minecraft:cooking/speed_default 10"
	compute(self(), speed, 0.5f) assertsIs "compute entity @s float my_pack:speed 0.5"

	compute(5) assertsIs "compute default integer $five"
	compute(vec3(0, 64, 0), 5) assertsIs "compute block 0 64 0 integer $five"
	compute(self(), 5) assertsIs "compute entity @s integer $five"
	compute(5f) assertsIs "compute default float $fiveFloat"
	compute(5f, 2f) assertsIs "compute default float $fiveFloat 2"
	compute(vec3(0, 64, 0), 5f) assertsIs "compute block 0 64 0 float $fiveFloat"
	compute(self(), 5f, 3f) assertsIs "compute entity @s float $fiveFloat 3"

	compute { constant(5) } assertsIs "compute default integer $five"
	compute(vec3(0, 64, 0)) { constant(5) } assertsIs "compute block 0 64 0 integer $five"
	compute { add(constant(1), constant(2)) } assertsIs "compute default integer {type:\"minecraft:add\",inputs:[1,2]}"
	compute { reference(bonus) } assertsIs "compute default integer my_pack:bonus"
	compute { reference(speed) } assertsIs "compute default float my_pack:speed"
	compute(self()) { score("kills", EntityTarget.TARGET_ENTITY) } assertsIs "compute entity @s integer $kills"
	compute(self()) { add(reference(bonus), reference(bonus)) } assertsIs "compute entity @s integer {type:\"minecraft:add\",inputs:[\"my_pack:bonus\",\"my_pack:bonus\"]}"

	compute { constant(5f) } assertsIs "compute default float $fiveFloat"
	compute(3f) { constant(5f) } assertsIs "compute default float $fiveFloat 3"
	compute(vec3(0, 64, 0)) { constant(5f) } assertsIs "compute block 0 64 0 float $fiveFloat"
	compute(vec3(0, 64, 0), 3f) { constant(5f) } assertsIs "compute block 0 64 0 float $fiveFloat 3"
	compute(allEntities(), 2f) { sqrt(constant(4f)) } assertsIs "compute entity @e float {type:\"minecraft:sqrt\",input:4} 2"
	compute { add(constant(1f), constant(2.5f)) } assertsIs "compute default float {type:\"minecraft:add\",inputs:[1.0d,2.5d]}"

	data(storage("foo")) {
		modify("bar", ComputeSource(bonus)) assertsIs "data modify storage minecraft:foo bar set compute default integer my_pack:bonus"
		modify("bar") {
			set(ComputeSource(vec3(1, 2, 3), bonus))
		} assertsIs "data modify storage minecraft:foo bar set compute block 1 2 3 integer my_pack:bonus"
		modify("bar") { append(ComputeSource(self(), bonus)) } assertsIs "data modify storage minecraft:foo bar append compute entity @s integer my_pack:bonus"
		modify("bar") { insert(2, ComputeSource(speed)) } assertsIs "data modify storage minecraft:foo bar insert 2 compute default float my_pack:speed"
		modify("bar") { merge(ComputeSource { constant(5) }) } assertsIs "data modify storage minecraft:foo bar merge compute default integer $five"
		modify("bar") { prepend(ComputeSource(vec3(1, 2, 3)) { constant(5) }) } assertsIs "data modify storage minecraft:foo bar prepend compute block 1 2 3 integer $five"
		set("bar", ComputeSource(self()) { constant(5f) }) assertsIs "data modify storage minecraft:foo bar set compute entity @s float $fiveFloat"
		set("bar", ComputeSource(5)) assertsIs "data modify storage minecraft:foo bar set compute default integer $five"
		set("bar", ComputeSource(vec3(1, 2, 3), 5f)) assertsIs "data modify storage minecraft:foo bar set compute block 1 2 3 float $fiveFloat"
		set("bar", ComputeSource(self(), 2.5f)) assertsIs "data modify storage minecraft:foo bar set compute entity @s float {type:\"minecraft:constant\",value:2.5f}"
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
				compute(5).toString() shouldBe "compute default integer {type:\"minecraft:constant\",value:5}"
				compute(5f).toString() shouldBe "compute default float {type:\"minecraft:constant\",value:5.0f}"
				compute { constant(5) }.toString() shouldBe "compute default integer {type:\"minecraft:constant\",value:5}"
				compute { constant(0.5f) }.toString() shouldBe "compute default float {type:\"minecraft:constant\",value:0.5f}"
				compute { constant(0.1f) }.toString() shouldBe "compute default float {type:\"minecraft:constant\",value:0.1f}"
				compute(5f, 2f).toString() shouldBe "compute default float {type:\"minecraft:constant\",value:5.0f} 2"
				shouldThrow<IllegalArgumentException> { compute(Float.NaN) }
			}
		}
	}
})
