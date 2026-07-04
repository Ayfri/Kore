package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.load
import io.github.ayfri.kore.generated.EntityTypes
import io.github.ayfri.kore.utils.nbt
import io.github.ayfri.kore.utils.set
import io.kotest.core.spec.style.FunSpec

fun Function.summonTests() {
	summon(EntityTypes.BAT) assertsIs "summon minecraft:bat ~ ~ ~"
	summon(EntityTypes.BAT, vec3(1, 2, 3)) assertsIs "summon minecraft:bat 1.0 2.0 3.0"

	summon(EntityTypes.BAT, vec3(1, 2, 3), nbt {
		this["test"] = 1
	}) assertsIs "summon minecraft:bat 1.0 2.0 3.0 {test:1}"

	summon(EntityTypes.BAT, vec3(1, 2, 3)) {
		this["test"] = 1
	} assertsIs "summon minecraft:bat 1.0 2.0 3.0 {test:1}"
}

class SummonCommandTests : FunSpec({
	test("summon") {
		dataPack("unit_tests") {
			load { summonTests() }
		}.generate()
	}
})
