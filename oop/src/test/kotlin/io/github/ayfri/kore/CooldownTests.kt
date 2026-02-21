package io.github.ayfri.kore

import io.github.ayfri.kore.arguments.numbers.seconds
import io.github.ayfri.kore.assertions.assertFileGenerated
import io.github.ayfri.kore.assertions.assertGeneratorsGenerated
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.commands.say
import io.github.ayfri.kore.cooldown.registerCooldown
import io.github.ayfri.kore.entities.player
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.utils.testDataPack

fun cooldownTests() = testDataPack("cooldown_tests") {
	val player = player("TestPlayer")
	val cd = registerCooldown("attack_cd", 2.seconds)

	function("test_cooldown") {
		cd.apply {
			start(player)
			ifReady(player) {
				say("Cooldown ready!")
			}
			reset(player)
		}
	}

	cd.cooldown.name assertsIs "attack_cd"
	cd.cooldown.duration.value.toInt() assertsIs 2

	generatedFunctions.any { it.name.contains("cooldown_attack_cd_init") } assertsIs true
	generatedFunctions.any { it.name.contains("cooldown_attack_cd_tick") } assertsIs true
	generatedFunctions.any { it.name.contains("cooldown_attack_cd_ready") } assertsIs true
}.apply {
	val n = "cooldown_tests"
	val d = "$n/data/$n"
	val g = DataPack.DEFAULT_GENERATED_FUNCTIONS_FOLDER

	assertFileGenerated("$d/function/test_cooldown.mcfunction")
	assertFileGenerated("$d/function/$g/cooldown_attack_cd_init.mcfunction")
	assertFileGenerated("$d/function/$g/cooldown_attack_cd_tick.mcfunction")
	assertGeneratorsGenerated()
	generate()
}
