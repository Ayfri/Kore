package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.features.bannerpatterns.bannerPattern

fun DataPack.bannerPatternTests() {
	bannerPattern("banner_pattern_test", "banner_pattern.test", "minecraft", "test")

	bannerPatterns.last() assertsIs """
		{
			"asset_id": "minecraft:test",
			"translation_key": "banner_pattern.test"
		}
	""".trimIndent()
}
