package serialization

import arguments.chatcomponents.textComponent
import arguments.colors.Color
import assertions.assertFileGenerated
import assertions.assertsIsJson
import generated.DataPacks
import kotlin.io.path.Path
import pack.features
import pack.filter
import pack.pack
import pack.supportedFormat
import utils.pretty
import utils.testDataPack

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

	iconPath = Path("datapackDSL", "src", "test", "resources", "Kotlin Full Color Logo Mark RGB.png")
}.apply {
	assertFileGenerated("pack.mcmeta")
	assertFileGenerated("pack.png")
}.dp.generatePackMCMetaFile() assertsIsJson """
	{
		"pack": {
			"pack_format": 16,
			"description": {
				"text": "Test pack",
				"color": "aqua"
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
