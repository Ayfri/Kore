package io.github.ayfri.kore

import io.github.ayfri.kore.arguments.numbers.seconds
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.commands.say
import io.github.ayfri.kore.cooldown.registerCooldown
import io.github.ayfri.kore.entities.player
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.utils.testDataPack
import io.kotest.core.spec.style.FunSpec

fun cooldownTests() = testDataPack("cooldown_tests") {
	val player = player("TestPlayer")
	val cd = registerCooldown("attack_cd", 2.seconds)

	function("test_cooldown") {
		cd.start(player)
		lines[0] assertsIs "scoreboard players set @e[limit=1,name=TestPlayer,type=minecraft:player] attack_cd 2"

		cd.ifReady(player) {
			say("Cooldown ready!")
		}
		lines[1].startsWith("execute if score @e[limit=1,name=TestPlayer,type=minecraft:player] attack_cd matches 0 run function cooldown_tests:generated_scopes/cooldown_attack_cd_ready_") assertsIs true

		cd.reset(player)
		lines[2] assertsIs "scoreboard players set @e[limit=1,name=TestPlayer,type=minecraft:player] attack_cd 0"

		lines.size assertsIs 3
	}

	generatedFunctions.any { it.name == OopConstants.cooldownInitFunctionName("attack_cd") } assertsIs true
	generatedFunctions.any { it.name == OopConstants.cooldownTickFunctionName("attack_cd") } assertsIs true
}.apply {
	generate()
}

class CooldownTests : FunSpec({
	test("cooldown") {
		cooldownTests()
	}
})
