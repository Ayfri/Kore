package io.github.ayfri.kore.website.playground.snippets.cooldowns

import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.numbers.seconds
import io.github.ayfri.kore.commands.say
import io.github.ayfri.kore.cooldown.registerCooldown
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.entities.player
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.pack.pack

fun playground() = dataPack("dash") {
	pack { description = textComponent("A dash usable every 3 seconds") }

	val player = player("Steve")
	val dash = registerCooldown("dash_cd", 3.seconds)

	function("use_dash") {
		dash.ifReady(player) { say("Dash!") }
	}
}
