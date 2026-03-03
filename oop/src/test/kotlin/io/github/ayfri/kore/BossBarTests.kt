package io.github.ayfri.kore

import io.github.ayfri.kore.arguments.colors.BossBarColor
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
		bar.setValue(100) assertsIs "bossbar set bossbar_tests:my_bar value 100"
		bar.setColor(BossBarColor.BLUE) assertsIs "bossbar set bossbar_tests:my_bar color blue"
		bar.setPlayers(player) assertsIs "bossbar set bossbar_tests:my_bar players @e[limit=1,name=TestPlayer,type=minecraft:player]"
		bar.show() assertsIs "bossbar set bossbar_tests:my_bar visible true"
		bar.hide() assertsIs "bossbar set bossbar_tests:my_bar visible false"
		bar.remove() assertsIs "bossbar remove bossbar_tests:my_bar"
		lines.size assertsIs 6
	}

	generatedFunctions.any { it.name == "bossbar_my_bar_init" } assertsIs true
}.apply {
	generate()
}
