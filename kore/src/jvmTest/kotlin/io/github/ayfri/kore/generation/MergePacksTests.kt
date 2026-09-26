package io.github.ayfri.kore.generation

import io.github.ayfri.kore.commands.say
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.functions.tick
import io.github.ayfri.kore.generation.platform.commonUnzipToTempDir
import io.github.ayfri.kore.path
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import kotlinx.io.files.Path
import java.io.File
import java.nio.file.Files
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class MergePacksTests : FunSpec({
	val root = Files.createTempDirectory("kore_merge_tests").toFile()
	afterSpec { root.deleteRecursively() }

	test("merging a zip pack copies its files and removes the extraction folder") {
		dataPack("zip_other") {
			path(File(root, "zip_other").absolutePath)
			function("x") { say("x") }
		}.generateZip()

		val tempDirectory = File(System.getProperty("java.io.tmpdir"))
		val extractedBefore = tempDirectory.list { _, name -> name.startsWith("kore_unzipped_datapack_zip_other") }.orEmpty().toSet()

		dataPack("zip_main") { path(File(root, "zip_main").absolutePath) }.generate {
			mergeWithPacks(Path(File(root, "zip_other/zip_other.zip").absolutePath))
		}

		File(root, "zip_main/zip_main/data/zip_other/function/x.mcfunction").readText() shouldBe "say x"
		tempDirectory.list { _, name -> name.startsWith("kore_unzipped_datapack_zip_other") }.orEmpty().toSet() shouldBe extractedBefore
	}

	test("merging keeps the tick functions of both packs") {
		val other = dataPack("tick_other") {
			path(File(root, "tick_other").absolutePath)
			tick("t_other") { say("o") }
		}

		dataPack("tick_main") {
			path(File(root, "tick_main").absolutePath)
			tick("t_main") { say("m") }
		}.generate { mergeWithPacks(other) }

		val tickTag = File(root, "tick_main/tick_main/data/minecraft/tags/function/tick.json").readText()
		tickTag shouldContain "tick_main:generated_scopes/t_main"
		tickTag shouldContain "tick_other:generated_scopes/t_other"
	}

	test("zip entries escaping the extraction folder are rejected") {
		val zip = File(root, "evil.zip")
		ZipOutputStream(zip.outputStream()).use {
			it.putNextEntry(ZipEntry("../evil.txt"))
			it.write("evil".encodeToByteArray())
			it.closeEntry()
		}

		shouldThrow<IllegalArgumentException> { commonUnzipToTempDir(Path(zip.absolutePath)) }
		File(System.getProperty("java.io.tmpdir"), "evil.txt").exists() shouldBe false
	}
})
