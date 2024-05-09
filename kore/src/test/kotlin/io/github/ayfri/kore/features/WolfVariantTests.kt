package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.features.wolfvariant.wolfVariant
import io.github.ayfri.kore.generated.Biomes

fun DataPack.wolfVariantTests() {
	wolfVariant("test_wolf_variant") {
		wildTexture = "my_datapack:entity/cute_wolf/wolf"
		angryTexture = "my_datapack:entity/cute_wolf/wolf_angry"
		tameTexture = "my_datapack:entity/cute_wolf/wolf_tame"
		biomes = listOf(Biomes.OCEAN, Biomes.SNOWY_SLOPES)
	}

	wolfVariants.last() assertsIs """
		{
			"wild_texture": "my_datapack:entity/cute_wolf/wolf",
			"angry_texture": "my_datapack:entity/cute_wolf/wolf_angry",
			"tame_texture": "my_datapack:entity/cute_wolf/wolf_tame",
			"biomes": [
				"minecraft:ocean",
				"minecraft:snowy_slopes"
			]
		}
	""".trimIndent()
}
