package io.github.ayfri.kore.features

import io.github.ayfri.kore.assertions.assertFileJsonContent
import io.github.ayfri.kore.features.contextproviders.contextFloatProvider
import io.github.ayfri.kore.features.contextproviders.contextIntProvider
import io.github.ayfri.kore.utils.testDataPack
import io.kotest.core.spec.style.FunSpec

class ContextProviderFilePathTests : FunSpec({
	test("context provider files are written under their own folders") {
		testDataPack("context_provider_paths") {
			contextIntProvider("always_one", 1)
			contextFloatProvider("always_one_and_a_half", 1.5f)
		}.apply {
			assertFileJsonContent("context_provider_paths/data/context_provider_paths/context_int_provider/always_one.json", "1")
			assertFileJsonContent("context_provider_paths/data/context_provider_paths/context_float_provider/always_one_and_a_half.json", "1.5")
			generate()
		}
	}
})
