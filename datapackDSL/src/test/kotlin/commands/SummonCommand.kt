package commands

import arguments.maths.vec3
import assertions.assertsIs
import functions.Function
import generated.EntityTypes
import utils.nbt
import utils.set

fun Function.summonTests() {
	summon(EntityTypes.BAT) assertsIs "summon minecraft:bat ~ ~ ~"
	summon(EntityTypes.BAT, vec3(1, 2, 3)) assertsIs "summon minecraft:bat 1 2 3"

	summon(EntityTypes.BAT, vec3(1, 2, 3), nbt {
		this["test"] = 1
	}) assertsIs "summon minecraft:bat 1 2 3 {test:1}"

	summon(EntityTypes.BAT, vec3(1, 2, 3)) {
		this["test"] = 1
	} assertsIs "summon minecraft:bat 1 2 3 {test:1}"
}
