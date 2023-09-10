package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.numbers.days
import io.github.ayfri.kore.arguments.numbers.seconds
import io.github.ayfri.kore.arguments.numbers.ticks
import io.github.ayfri.kore.arguments.types.literals.randomPlayer
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.functions.Function

fun Function.titleTests() {
	title(randomPlayer(), TitleAction.CLEAR) assertsIs "title @r clear"
	title(self(), TitleAction.RESET) assertsIs "title @s reset"
	title(self(), TitleLocation.TITLE, textComponent("test")) assertsIs "title @s title \"test\""
	title(self(), TitleLocation.SUBTITLE, textComponent("test")) assertsIs "title @s subtitle \"test\""
	title(self(), TitleLocation.ACTIONBAR, textComponent("test") {
		color = Color.AQUA
	}) assertsIs "title @s actionbar {\"text\":\"test\",\"color\":\"aqua\"}"
	title(self(), 1.0, 2.0, 3.0) assertsIs "title @s times 1 2 3"
	title(self(), 0.2.days, 1.seconds, 1.ticks) assertsIs "title @s times 0.2d 1s 1"
}
