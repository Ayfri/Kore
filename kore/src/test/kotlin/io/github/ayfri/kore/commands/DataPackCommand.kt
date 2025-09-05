package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.functions.Function

fun Function.dataPackTests() {
	dataPack("test") {
		create("id", textComponent("name")) assertsIs "datapack create id \"name\""
		disable() assertsIs "datapack disable test"
		enable() assertsIs "datapack enable test"
		enableFirst() assertsIs "datapack enable first test"
		enableLast() assertsIs "datapack enable last test"
		enableBefore("foo") assertsIs "datapack enable test before foo"
		enableAfter("foo") assertsIs "datapack enable test after foo"
	}

	dataPacks.available() assertsIs "datapack list available"
	dataPacks.enabled() assertsIs "datapack list enabled"
	dataPacks.list() assertsIs "datapack list"
}
