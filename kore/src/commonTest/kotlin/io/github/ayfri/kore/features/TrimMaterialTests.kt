package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.features.trimmaterial.description
import io.github.ayfri.kore.features.trimmaterial.trimMaterial
import io.github.ayfri.kore.generated.Textures
import io.github.ayfri.kore.generated.arguments.types.TrimColorPaletteArgument
import io.github.ayfri.kore.utils.pretty
import io.kotest.core.spec.style.FunSpec

fun DataPack.trimMaterialTests() {
	trimMaterial("test_trim_material", Textures.Palettes.Trim.DIAMOND) {
		description("Test Trim Material")
	}

	trimMaterials.last() assertsIs """
		{
			"description": "Test Trim Material",
			"palette_id": "minecraft:trim/diamond"
		}
	""".trimIndent()

	trimMaterial("custom_palette", TrimColorPaletteArgument("trim/ruby", "my_pack")) {
		description("Ruby")
	}

	trimMaterials.last() assertsIs """
		{
			"description": "Ruby",
			"palette_id": "my_pack:trim/ruby"
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
