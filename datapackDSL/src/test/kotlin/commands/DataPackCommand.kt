package commands

import functions.Function
import utils.assertsIs

fun Function.dataPackTests() {
	dataPack("test") {
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
