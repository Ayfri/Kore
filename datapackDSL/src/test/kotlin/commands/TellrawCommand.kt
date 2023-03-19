package commands

import arguments.allPlayers
import arguments.chatcomponents.textComponent
import functions.Function
import utils.assertsIs

fun Function.tellrawTests() {
	tellraw(allPlayers(), textComponent("test")) assertsIs "tellraw @a \"test\""
}
