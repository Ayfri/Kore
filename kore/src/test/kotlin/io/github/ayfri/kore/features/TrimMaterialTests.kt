package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.assertions.assertGeneratorsGenerated
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.features.trimmaterial.ArmorMaterial
import io.github.ayfri.kore.features.trimmaterial.description
import io.github.ayfri.kore.features.trimmaterial.overrideArmorMaterials
import io.github.ayfri.kore.features.trimmaterial.trimMaterial
import io.github.ayfri.kore.generated.Textures
import io.github.ayfri.kore.utils.pretty
import io.github.ayfri.kore.utils.testDataPack
import io.kotest.core.spec.style.FunSpec

fun DataPack.trimMaterialTests() {
	trimMaterial("test_trim_material", Textures.Trims.ColorPalettes.DIAMOND) {
		description("Test Trim Material")

		overrideArmorMaterials(
			ArmorMaterial.DIAMOND to Color.AQUA
		)
	}

	trimMaterials.last() assertsIs """
		{
			"asset_name": "minecraft:diamond",
			"description": "Test Trim Material",
			"override_armor_materials": {
				"minecraft:diamond": "aqua"
			}
		}
	""".trimIndent()
}

class TrimMaterialTests : FunSpec({
	test("trim material") {
		testDataPack("trimMaterial") {
			pretty()
			trimMaterialTests()
		}.apply {
			assertGeneratorsGenerated()
			generate()
		}
	}
})
