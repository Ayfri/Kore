package io.github.ayfri.kore.arguments

import io.github.ayfri.kore.arguments.chatcomponents.text
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.components.lore
import io.github.ayfri.kore.arguments.types.resources.item
import io.github.ayfri.kore.assertions.assertsIs

fun argumentsTests() {
	itemsTests()
}

fun itemsTests() {
	val item = item("test", namespace = "nms") {
		lore("test")
	}

	item.asString() assertsIs """nms:test[lore=['"test"']]"""

	item.components!!.lore(
		textComponent("test", Color.AQUA) + text("test2", Color.BLACK) + text("a")
	)

	item.asString() assertsIs """nms:test[lore=['[{"text":"test","color":"aqua"},{"text":"test2","color":"black"},"a"]']]"""
}
