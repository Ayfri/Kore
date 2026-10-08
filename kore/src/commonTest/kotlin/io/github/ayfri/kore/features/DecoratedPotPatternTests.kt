package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.features.decoratedpotpatterns.decoratedPotPattern
import io.github.ayfri.kore.generated.Textures
import io.github.ayfri.kore.generated.arguments.types.DecoratedPotPatternAssetArgument
import io.github.ayfri.kore.utils.pretty
import io.kotest.core.spec.style.FunSpec

fun DataPack.decoratedPotPatternTests() {
	decoratedPotPattern("angler", Textures.Entity.DecoratedPot.ANGLER_POTTERY_PATTERN)

	decoratedPotPatterns.last() assertsIs """
		{
			"asset_id": "minecraft:angler_pottery_pattern"
		}
	""".trimIndent()

	val star = decoratedPotPattern("star", DecoratedPotPatternAssetArgument("star_pottery_pattern", "my_pack"))

	star.asId() assertsIs "decorated_pot_pattern:star"
	decoratedPotPatterns.last() assertsIs """
		{
			"asset_id": "my_pack:star_pottery_pattern"
		}
	""".trimIndent()
}

class DecoratedPotPatternTests : FunSpec({
	test("decorated pot pattern") {
		dataPack("decorated_pot_pattern") {
			pretty()
			decoratedPotPatternTests()
		}
	}
})
