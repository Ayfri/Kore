package commands

import arguments.types.literals.allPlayers
import functions.Function
import utils.assertsIs

fun Function.tagTests() {
	tag(allPlayers()) {
		add("test") assertsIs "tag @a add test"
		list() assertsIs "tag @a list"
		remove("test") assertsIs "tag @a remove test"
	}
}
