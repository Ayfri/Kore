package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.features.trimpattern.description
import io.github.ayfri.kore.features.trimpattern.trimPattern
import io.github.ayfri.kore.generated.Textures
import io.github.ayfri.kore.utils.pretty
import io.kotest.core.spec.style.FunSpec

fun DataPack.trimPatternTests() {
	trimPattern("test_trim_pattern", Textures.Trims.Entity.Humanoid.SILENCE) {
		description("Test Trim Pattern")
		decal = true
	}

	trimPatterns.last() assertsIs """
		{
			"asset_id": "minecraft:trims/entity/humanoid/silence",
			"description": "Test Trim Pattern",
			"decal": true
		}
	""".trimIndent()
}

class TrimPatternTests : FunSpec({
	test("trim pattern") {
		dataPack("trimPattern") {
			pretty()
			trimPatternTests()
		}
	}
})
