package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.features.paintingvariant.paintingVariant

fun DataPack.paintingVariantTests() {
	paintingVariant(
		assetId = "minecraft:textures/painting/kebab.png",
		height = 16,
		width = 16
	)

	paintingVariants.last() assertsIs """
		{
			"asset_id": "minecraft:textures/painting/kebab.png",
			"height": 16,
			"width": 16
		}
	""".trimIndent()

	paintingVariant(
		assetId = "minecraft:textures/painting/aztec.png",
	)

	paintingVariants.last() assertsIs """
		{
			"asset_id": "minecraft:textures/painting/aztec.png",
			"height": 1,
			"width": 1
		}
	""".trimIndent()
}
