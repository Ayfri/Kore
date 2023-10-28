package io.github.ayfri.kore.serialization

import io.github.ayfri.kore.arguments.chatcomponents.nbtComponent
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.assertions.assertsIsJson

fun chatComponentsTests() {
	nbtComponent("test", self()) {
		nbt = "test"
	} assertsIsJson """
		{
			"type": "nbt",
			"nbt": "test",
			"entity": "@s",
			"source": "entity"
		}
	""".trimIndent()
}
