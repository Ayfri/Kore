package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.features.bannerpatterns.bannerPattern
import io.github.ayfri.kore.utils.pretty
import io.kotest.core.spec.style.FunSpec

fun DataPack.bannerPatternTests() {
	bannerPattern("banner_pattern_test", "banner_pattern.test", "minecraft", "test")

	bannerPatterns.last() assertsIs """
		{
			"asset_id": "minecraft:test",
			"translation_key": "banner_pattern.test"
		}
	""".trimIndent()
}

class BannerPatternTests : FunSpec({
	test("banner pattern") {
		dataPack("bannerPattern") {
			pretty()
			bannerPatternTests()
		}
	}
})
