package io.github.ayfri.kore.arguments

import io.github.ayfri.kore.arguments.components.line
import io.github.ayfri.kore.arguments.components.lore
import io.github.ayfri.kore.arguments.types.resources.item
import io.github.ayfri.kore.assertions.assertsIs

fun argumentsTests() {
	val item = item("test", namespace = "nms") {
		lore {
			line("test")
		}
	}

	item.toString() assertsIs "nms:test[lore='[test]']"
}
