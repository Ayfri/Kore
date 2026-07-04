package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.types.literals.allPlayers
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.load
import io.kotest.core.spec.style.FunSpec

fun Function.tellrawTests() {
	tellraw(allPlayers(), textComponent("test")) assertsIs "tellraw @a \"test\""
	tellraw(allPlayers(), "test", color = Color.RED) assertsIs "tellraw @a {type:\"text\",color:\"red\",text:\"test\"}"
}

class TellrawCommandTests : FunSpec({
	test("tellraw") {
		dataPack("unit_tests") {
			load { tellrawTests() }
		}.generate()
	}
})
