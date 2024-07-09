package io.github.ayfri.kore.arguments

import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.generated.ComponentTypes
import io.github.ayfri.kore.generated.Items

fun componentsRemovablesTests() {
	Items.DIRT {
		remove("foo")
		!"bar"
		remove(ComponentTypes.FOOD, ComponentTypes.BANNER_PATTERNS)
	} assertsIs "minecraft:dirt[!foo,!bar,!food,!banner_patterns]"
}
