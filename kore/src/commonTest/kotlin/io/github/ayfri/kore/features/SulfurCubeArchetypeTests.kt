package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.commands.AttributeModifierOperation
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.features.sulfurcubearchetype.modifier
import io.github.ayfri.kore.features.sulfurcubearchetype.sulfurCubeArchetype
import io.github.ayfri.kore.generated.Attributes
import io.github.ayfri.kore.generated.Tags
import io.github.ayfri.kore.utils.pretty
import io.kotest.core.spec.style.FunSpec

fun DataPack.sulfurCubeArchetypeTests() {
	sulfurCubeArchetype("regular", items = Tags.Item.SWORDS) {
		buoyant = true
		explosionFuse = 80

		modifier(
			amount = 4.0,
			attribute = Attributes.MAX_HEALTH,
			id = "sulfur_cube_archetype:regular",
			operation = AttributeModifierOperation.ADD_VALUE,
		)
	}

	sulfurCubeArchetypes.last() assertsIs """
		{
			"attribute_modifiers": [
				{
					"amount": 4.0,
					"attribute": "minecraft:max_health",
					"id": "sulfur_cube_archetype:regular",
					"operation": "add_value"
				}
			],
			"buoyant": true,
			"explosion_fuse": 80,
			"items": "#minecraft:swords"
		}
	""".trimIndent()
}

class SulfurCubeArchetypeTests : FunSpec({
	test("sulfur cube archetype") {
		dataPack("sulfurCubeArchetype") {
			pretty()
			sulfurCubeArchetypeTests()
		}
	}
})
