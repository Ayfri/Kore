package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.load
import io.kotest.core.spec.style.FunSpec

fun Function.dataPackTests() {
	dataPack("test") {
		create("id", textComponent("name")) assertsIs "datapack create id \"name\""
		disable() assertsIs "datapack disable test"
		enable() assertsIs "datapack enable test"
		enable(DatapackPriority.FIRST) assertsIs "datapack enable test first"
		enableLast() assertsIs "datapack enable test last"
		enableBefore("foo") assertsIs "datapack enable test before foo"
		enableAfter("foo") assertsIs "datapack enable test after foo"
	}

	dataPacks.available() assertsIs "datapack list available"
	dataPacks.enabled() assertsIs "datapack list enabled"
	dataPacks.list() assertsIs "datapack list"
}

class DataPackCommandTests : FunSpec({
	test("data pack") {
		dataPack("unit_tests") {
			load { dataPackTests() }
		}.generate()
	}
})
