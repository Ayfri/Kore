package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.types.literals.allPlayers
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.functions.Function

fun Function.tellrawTests() {
	tellraw(allPlayers(), textComponent("test")) assertsIs "tellraw @a \"test\""
	tellraw(allPlayers(), "test", color = Color.RED) assertsIs "tellraw @a {type:\"text\",color:\"red\",text:\"test\"}"
}
