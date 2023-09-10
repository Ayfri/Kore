package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.types.literals.allPlayers
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.functions.Function

fun Function.tagTests() {
	tag(allPlayers()) {
		add("test") assertsIs "tag @a add test"
		list() assertsIs "tag @a list"
		remove("test") assertsIs "tag @a remove test"
	}
}
