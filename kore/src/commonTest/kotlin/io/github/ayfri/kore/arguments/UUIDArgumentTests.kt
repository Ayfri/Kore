package io.github.ayfri.kore.arguments

import io.github.ayfri.kore.arguments.types.literals.entityUUID
import io.github.ayfri.kore.arguments.types.literals.hashedUUID
import io.github.ayfri.kore.dataPack
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class UUIDArgumentTests : FunSpec({
	test("hashedUUID only depends on its key") {
		hashedUUID("a") shouldBe hashedUUID("a")
		hashedUUID("a") shouldNotBe hashedUUID("b")
	}

	test("entityUUID is unique per call and identical across builds") {
		fun uuids(packName: String) = with(dataPack(packName) {}) { List(3) { entityUUID("marker") } + entityUUID("other") }

		uuids("pack").distinct().size shouldBe 4
		uuids("pack") shouldBe uuids("pack")
		uuids("pack") shouldNotBe uuids("other_pack")
	}
})
