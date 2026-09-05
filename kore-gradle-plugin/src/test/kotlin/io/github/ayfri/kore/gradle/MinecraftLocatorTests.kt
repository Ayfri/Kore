package io.github.ayfri.kore.gradle

import io.github.ayfri.kore.gradle.internal.MinecraftLocator
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import java.io.File
import kotlin.io.path.createTempDirectory

class MinecraftLocatorTests : FunSpec({
	val temporaryDirectory = createTempDirectory("kore-locator").toFile()
	afterSpec { temporaryDirectory.deleteRecursively() }

	test("the MINECRAFT_DIR environment variable wins over the platform default") {
		val directory = MinecraftLocator.defaultDirectory(
			environment = mapOf("MINECRAFT_DIR" to "D:/games/mc", "APPDATA" to "C:/Users/Test/AppData/Roaming"),
			osName = "Windows 11",
			userHome = "C:/Users/Test",
		)

		directory shouldBe File("D:/games/mc")
	}

	test("a blank MINECRAFT_DIR falls back to the platform default") {
		val directory = MinecraftLocator.defaultDirectory(
			environment = mapOf("MINECRAFT_DIR" to "   ", "APPDATA" to "C:/Users/Test/AppData/Roaming"),
			osName = "Windows 11",
			userHome = "C:/Users/Test",
		)

		directory shouldBe File("C:/Users/Test/AppData/Roaming", ".minecraft")
	}

	test("Windows uses APPDATA and falls back to the user home when it is missing") {
		MinecraftLocator.defaultDirectory(emptyMap(), "Windows 11", "C:/Users/Test") shouldBe
			File("C:/Users/Test", ".minecraft")
	}

	test("macOS uses the Application Support directory") {
		MinecraftLocator.defaultDirectory(emptyMap(), "Mac OS X", "/Users/test") shouldBe
			File("/Users/test").resolve("Library/Application Support/minecraft")
	}

	test("Linux uses the dot directory in the user home") {
		MinecraftLocator.defaultDirectory(emptyMap(), "Linux", "/home/test") shouldBe
			File("/home/test").resolve(".minecraft")
	}

	test("worlds are the saves subdirectories holding a level.dat, sorted") {
		val saves = temporaryDirectory.resolve("saves")
		listOf("Zeta", "Alpha").forEach { saves.resolve(it).apply { mkdirs() }.resolve("level.dat").writeText("") }
		saves.resolve("NotAWorld").mkdirs()
		saves.resolve("loose-file.txt").writeText("")

		MinecraftLocator.worlds(temporaryDirectory) shouldContainExactly listOf("Alpha", "Zeta")
	}

	test("a missing saves directory yields no world instead of failing") {
		MinecraftLocator.worlds(temporaryDirectory.resolve("absent")) shouldContainExactly emptyList()
	}

	test("datapack and resource pack directories follow the vanilla layout") {
		MinecraftLocator.dataPacksDirectory(File("/mc"), "World") shouldBe File("/mc/saves/World/datapacks")
		MinecraftLocator.resourcePacksDirectory(File("/mc")) shouldBe File("/mc/resourcepacks")
	}
})
