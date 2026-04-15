package io.github.ayfri.kore.utils

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.io.files.Path

class TestDataPackTests : FunSpec({
	test("test datapack uses out as the default save path when TEST_FOLDER is missing") {
		minecraftSaveTestPath shouldBe Path("out")
	}
})