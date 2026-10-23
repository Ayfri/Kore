package io.github.ayfri.kore.features

import io.github.ayfri.kore.assertions.assertFileJsonContent
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.blockStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.simpleStateProvider
import io.github.ayfri.kore.generated.Blocks
import io.github.ayfri.kore.utils.testDataPack
import io.kotest.core.spec.style.FunSpec

class BlockStateProviderFilePathTests : FunSpec({
	test("block state provider files are written under worldgen/block_state_provider") {
		testDataPack("block_state_provider_paths") {
			blockStateProvider("always_dirt") { simpleStateProvider(Blocks.DIRT) }
		}.apply {
			assertFileJsonContent(
				"block_state_provider_paths/data/block_state_provider_paths/worldgen/block_state_provider/always_dirt.json",
				"""{"type":"minecraft:simple","state":"minecraft:dirt"}""",
			)
			generate()
		}
	}
})
