package io.github.ayfri.kore.arguments

import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.generated.Items
import io.kotest.core.spec.style.FunSpec

fun componentsPatchsTests() {
	Items.DIRT {
		remove("foo")
		!"bar"
		remove(ItemComponentTypes.FOOD, ItemComponentTypes.BANNER_PATTERNS)
	} assertsIs "minecraft:dirt[!foo,!bar,!food,!banner_patterns]"
}

class ComponentsPatchsTests : FunSpec({
	test("components patchs") {
		componentsPatchsTests()
	}
})
