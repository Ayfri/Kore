package io.github.ayfri.kore.website.playground.snippets.timerbossbar

import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.BossBarColor
import io.github.ayfri.kore.arguments.numbers.seconds
import io.github.ayfri.kore.commands.BossBarStyle
import io.github.ayfri.kore.commands.say
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.entities.player
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.pack.pack
import io.github.ayfri.kore.timer.onComplete
import io.github.ayfri.kore.timer.registerTimerWithBossBar
import io.github.ayfri.kore.timer.start

fun playground() = dataPack("boss_round") {
	pack { description = textComponent("A 10 second round shown in a boss bar") }

	val player = player("Steve")
	val round = registerTimerWithBossBar("round", 10.seconds) {
		color = BossBarColor.GREEN
		style = BossBarStyle.NOTCHED_20
	}

	function("start_round") {
		round.start(player)
		round.onComplete(player) { say("Round over!") }
	}
}
