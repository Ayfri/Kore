package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.features.paintingvariant.paintingVariant
import io.github.ayfri.kore.generated.Textures

fun DataPack.paintingVariantTests() {
	paintingVariant(
		assetId = Textures.Painting.KEBAB,
		height = 16,
		width = 16
	)

	paintingVariants.last() assertsIs """
		{
			"asset_id": "minecraft:kebab",
			"height": 16,
			"width": 16
		}
	""".trimIndent()

	paintingVariant(
		assetId = Textures.Painting.AZTEC
	)

	paintingVariants.last() assertsIs """
		{
			"asset_id": "minecraft:aztec",
			"height": 1,
			"width": 1
		}
	""".trimIndent()

	paintingVariant(
		assetId = Textures.Painting.AZTEC,
	) {
		author = textComponent("Ayfri")
		title = textComponent("Aztec")
	}

	paintingVariants.last() assertsIs """
		{
			"asset_id": "minecraft:aztec",
			"height": 1,
			"width": 1,
			"author": "Ayfri",
			"title": "Aztec"
		}
	""".trimIndent()
}
