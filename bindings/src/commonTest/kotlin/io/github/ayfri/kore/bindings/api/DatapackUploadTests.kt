package io.github.ayfri.kore.bindings.api

import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.generation.zip.ZipWriter
import io.kotest.core.spec.style.FunSpec

fun commonDatapackUploadTests() {
	testExploreDatapackZip()
	testImportDatapackZip()
}

private fun sampleDatapackZipBytes(): ByteArray = ZipWriter().apply {
	addEntry("pack.mcmeta", """{"pack":{"description":"Uploaded pack","min_format":95,"max_format":95}}""")
	addEntry("data/uploaded/function/greet.mcfunction", "say hello")
	addEntry("data/uploaded/enchantment/custom_sharp.json", "{}")
}.toByteArray()

fun testExploreDatapackZip() {
	val datapack = exploreDatapackZip(sampleDatapackZipBytes(), "uploaded_pack.zip")

	datapack.functions.size assertsIs 1
	datapack.functions.first().id assertsIs "uploaded:greet"
	datapack.resources["enchantment"]?.first()?.id assertsIs "uploaded:custom_sharp"
}

fun testImportDatapackZip() {
	val generated = importDatapackZip(sampleDatapackZipBytes(), "uploaded_pack.zip")

	generated.objectName assertsIs "UploadedPack"
	generated.source.contains("data object UploadedPack") assertsIs true
	generated.source.contains("const val NAMESPACE: String = \"uploaded\"") assertsIs true
}

class DatapackUploadTests : FunSpec({
	test("explore and import datapack from zip bytes") {
		commonDatapackUploadTests()
	}
})
