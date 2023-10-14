package io.github.ayfri.kore.serialization

import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.assertions.assertFileGenerated
import io.github.ayfri.kore.assertions.assertsIsJson
import io.github.ayfri.kore.generated.DataPacks
import io.github.ayfri.kore.pack.features
import io.github.ayfri.kore.pack.filter
import io.github.ayfri.kore.pack.pack
import io.github.ayfri.kore.pack.supportedFormat
import io.github.ayfri.kore.utils.pretty
import io.github.ayfri.kore.utils.testDataPack
import kotlin.io.path.Path

fun datapackTests() = testDataPack("test") {
	pretty()

	pack {
		format = 16
		description = textComponent("Test pack", color = Color.AQUA)
		supportedFormat(16..18)
	}

	filter {
		block(path = "stone*")
	}

	features(DataPacks.BUNDLE)

	iconPath = Path("kore", "src", "test", "resources", "kore_icon.png")
}.apply {
	assertFileGenerated("pack.mcmeta")
	assertFileGenerated("pack.png")
}.dp.generatePackMCMetaFile() assertsIsJson """
	{
		"pack": {
			"pack_format": 16,
			"description": {
				"text": "Test pack",
				"color": "aqua",
				"type": "text"
			},
			"supported_formats": {
				"min_inclusive": 16,
				"max_inclusive": 18
			}
		},
		"features": {
			"enabled": [
				"bundle"
			]
		},
		"filter": {
			"block": [
				{
					"path": "stone*"
				}
			]
		}
	}
""".trimIndent()
