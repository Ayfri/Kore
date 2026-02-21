package io.github.ayfri.kore

import io.github.ayfri.kore.arguments.colors.BossBarColor
import io.github.ayfri.kore.assertions.assertFileGenerated
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.bossbar.registerBossBar
import io.github.ayfri.kore.commands.BossBarStyle
import io.github.ayfri.kore.entities.player
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.utils.testDataPack

fun bossBarTests() = testDataPack("bossbar_tests") {
	val player = player("TestPlayer")

	val bar = registerBossBar("my_bar", name) {
		color = BossBarColor.RED
		max = 200
		style = BossBarStyle.NOTCHED_10
		value = 50
	}

	function("test_bossbar") {
		bar.apply {
			setValue(100)
			setColor(BossBarColor.BLUE)
			setPlayers(player)
			show()
			hide()
			remove()
		}

		lines.any { it.contains("bossbar set") && it.contains("value") && it.contains("100") } assertsIs true
		lines.any { it.contains("bossbar set") && it.contains("color") && it.contains("blue") } assertsIs true
		lines.any { it.contains("bossbar set") && it.contains("players") } assertsIs true
		lines.any { it.contains("bossbar set") && it.contains("visible") && it.contains("true") } assertsIs true
		lines.any { it.contains("bossbar set") && it.contains("visible") && it.contains("false") } assertsIs true
		lines.any { it.contains("bossbar remove") } assertsIs true
		lines.size assertsIs 6
	}

	bar.config.id assertsIs "my_bar"
	bar.config.color assertsIs BossBarColor.RED
	bar.config.max assertsIs 200
	bar.config.style assertsIs BossBarStyle.NOTCHED_10
	bar.config.value assertsIs 50

	generatedFunctions.any { it.name.contains("bossbar_my_bar_init") } assertsIs true
}.apply {
	val n = "bossbar_tests"
	val d = "$n/data/$n"
	val g = DataPack.DEFAULT_GENERATED_FUNCTIONS_FOLDER

	assertFileGenerated("$d/function/test_bossbar.mcfunction")
	assertFileGenerated("$d/function/$g/bossbar_my_bar_init.mcfunction")
	generate()
}
