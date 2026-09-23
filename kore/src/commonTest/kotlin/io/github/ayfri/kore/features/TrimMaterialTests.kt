package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.features.trimmaterial.description
import io.github.ayfri.kore.features.trimmaterial.overrideArmorAsset
import io.github.ayfri.kore.features.trimmaterial.trimMaterial
import io.github.ayfri.kore.generated.EquipmentAssets
import io.github.ayfri.kore.generated.Textures
import io.github.ayfri.kore.utils.pretty
import io.kotest.core.spec.style.FunSpec

fun DataPack.trimMaterialTests() {
	trimMaterial("test_trim_material", Textures.Trims.ColorPalettes.DIAMOND) {
		description("Test Trim Material")
		overrideArmorAsset(EquipmentAssets.DIAMOND, Textures.Trims.ColorPalettes.DIAMOND_DARKER)
	}

	trimMaterials.last() assertsIs """
		{
			"asset_name": "diamond",
			"description": "Test Trim Material",
			"override_armor_assets": {
				"minecraft:diamond": "diamond_darker"
			}
		}
	""".trimIndent()
}

class TrimMaterialTests : FunSpec({
	test("trim material") {
		dataPack("trimMaterial") {
			pretty()
			trimMaterialTests()
		}
	}
})
