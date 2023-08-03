package serialization

import arguments.chatcomponents.textComponent
import arguments.colors.Color
import configuration
import dataPack
import generated.DataPacks
import kotlin.io.path.Path
import pack.features
import pack.filter
import pack.pack
import pack.supportedFormat
import utils.assertsIsJson

fun datapackTests() = dataPack("test") {
	configuration {
		prettyPrint = true
		prettyPrintIndent = "\t"
	}

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
}.generatePackMCMetaFile() assertsIsJson """
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
