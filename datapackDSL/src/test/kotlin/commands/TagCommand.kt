package commands

import arguments.types.literals.allPlayers
import assertions.assertsIs
import functions.Function

fun Function.tagTests() {
	tag(allPlayers()) {
		add("test") assertsIs "tag @a add test"
		list() assertsIs "tag @a list"
		remove("test") assertsIs "tag @a remove test"
	}
}
