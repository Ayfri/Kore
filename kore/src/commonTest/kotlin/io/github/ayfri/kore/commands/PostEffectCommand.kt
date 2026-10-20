package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.types.literals.allPlayers
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.load
import io.github.ayfri.kore.generated.PostEffects
import io.github.ayfri.kore.generated.arguments.types.PostEffectArgument
import io.kotest.core.spec.style.FunSpec

fun Function.postEffectTests() {
	postEffect {
		add(allPlayers(), PostEffects.CREEPER) assertsIs "posteffect add @a minecraft:creeper"
		add(allPlayers(), PostEffectArgument("glitch", "my_pack")) assertsIs "posteffect add @a my_pack:glitch"
		clear(allPlayers()) assertsIs "posteffect clear @a"
		list(self()) assertsIs "posteffect list @s"
		remove(allPlayers(), PostEffects.INVERT) assertsIs "posteffect remove @a minecraft:invert"
	}
}

class PostEffectCommandTests : FunSpec({
	test("posteffect") {
		dataPack("unit_tests") {
			load { postEffectTests() }
		}
	}
})
