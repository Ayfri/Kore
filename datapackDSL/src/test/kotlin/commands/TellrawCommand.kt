package commands

import arguments.chatcomponents.textComponent
import arguments.types.literals.allPlayers
import functions.Function
import utils.assertsIs

fun Function.tellrawTests() {
	tellraw(allPlayers(), textComponent("test")) assertsIs "tellraw @a \"test\""
}
