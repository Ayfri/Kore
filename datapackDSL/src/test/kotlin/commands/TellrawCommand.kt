package commands

import arguments.allPlayers
import arguments.chatcomponents.textComponent
import functions.Function

fun Function.tellrawTests() {
	tellraw(allPlayers(), textComponent("test"))
}
