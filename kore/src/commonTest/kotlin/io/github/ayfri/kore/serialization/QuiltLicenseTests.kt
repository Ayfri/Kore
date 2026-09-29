package io.github.ayfri.kore.serialization

import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.assertions.assertsIsJson
import io.github.ayfri.kore.generation.quilt.QuiltLicense
import io.github.ayfri.kore.generation.quilt.QuiltLicenseObject
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec

class QuiltLicenseTests : FunSpec({
	test("an identifier serializes as a string") {
		json.encodeToString(QuiltLicense.serializer(), QuiltLicense("MIT")) assertsIs "\"MIT\""
	}

	test("a single license object is inlined") {
		val license = QuiltLicense(licenses = listOf(QuiltLicenseObject(id = "MIT", name = "MIT License", url = "https://mit.edu")))

		json.encodeToString(QuiltLicense.serializer(), license) assertsIsJson """
			{
				"id": "MIT",
				"name": "MIT License",
				"url": "https://mit.edu"
			}
		""".trimIndent()
	}

	test("a license needs exactly one of identifier and licenses") {
		shouldThrow<IllegalArgumentException> { json.encodeToString(QuiltLicense.serializer(), QuiltLicense()) }
	}
})
