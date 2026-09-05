package io.github.ayfri.kore.gradle

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.gradle.testkit.runner.TaskOutcome

class KorePluginFunctionalTests : FunSpec({
	test("koreRun generates the pack and links it into the configured world") {
		val project = TestProject(koreConfiguration = "\tworlds = listOf(\"MyWorld\")")

		try {
			val result = project.run("koreRun")

			result.task(":koreBuild")?.outcome shouldBe TaskOutcome.SUCCESS
			result.task(":koreLinkDataPack")?.outcome shouldBe TaskOutcome.SUCCESS
			project.linkedPack("MyWorld").resolve("data/test/function/load.mcfunction").readText() shouldBe "say hello"
			result.output shouldContain "No RCON password set, skipping reload."
		} finally {
			project.delete()
		}
	}

	test("a second run with no change is up to date and reuses the configuration cache") {
		val project = TestProject(koreConfiguration = "\tworlds = listOf(\"MyWorld\")")

		try {
			project.run("koreRun")
			val result = project.run("koreRun")

			result.task(":koreBuild")?.outcome shouldBe TaskOutcome.UP_TO_DATE
			result.task(":koreLinkDataPack")?.outcome shouldBe TaskOutcome.UP_TO_DATE
			result.output shouldContain "Configuration cache entry reused."
		} finally {
			project.delete()
		}
	}

	test("editing the generator relinks the new content") {
		val project = TestProject(koreConfiguration = "\tworlds = listOf(\"MyWorld\")")

		try {
			project.run("koreRun")
			project.writeGenerator(functionBody = "say goodbye")
			val result = project.run("koreRun")

			result.task(":koreBuild")?.outcome shouldBe TaskOutcome.SUCCESS
			project.linkedPack("MyWorld").resolve("data/test/function/load.mcfunction").readText() shouldBe "say goodbye"
		} finally {
			project.delete()
		}
	}

	test("cleanBeforeBuild drops resources that the generator no longer produces") {
		val project = TestProject(koreConfiguration = "\tworlds = listOf(\"MyWorld\")")

		try {
			project.writeGenerator(functionBody = "say hello", extraFile = "data/test/function/stale.mcfunction")
			project.run("koreRun")
			project.linkedPack("MyWorld").resolve("data/test/function/stale.mcfunction").exists() shouldBe true

			project.writeGenerator(functionBody = "say hello")
			project.run("koreRun")

			project.outputDirectory.resolve("my_pack/data/test/function/stale.mcfunction").exists() shouldBe false
			project.linkedPack("MyWorld").resolve("data/test/function/stale.mcfunction").exists() shouldBe false
		} finally {
			project.delete()
		}
	}

	test("linkToAllWorlds discovers every world holding a level.dat") {
		val project = TestProject(koreConfiguration = "\tlinkToAllWorlds = true")

		try {
			project.world("SecondWorld")
			project.minecraftDirectory.resolve("saves/NotAWorld").mkdirs()

			project.run("koreLink")

			project.linkedPack("MyWorld").resolve("pack.mcmeta").exists() shouldBe true
			project.linkedPack("SecondWorld").resolve("pack.mcmeta").exists() shouldBe true
			project.minecraftDirectory.resolve("saves/NotAWorld/datapacks").exists() shouldBe false
		} finally {
			project.delete()
		}
	}

	test("dataPackTargets links into an arbitrary datapacks folder, such as a server") {
		val project = TestProject()
		val serverDataPacks = project.root.resolve("server/world/datapacks")

		try {
			project.root.resolve("build.gradle.kts").appendText(
				"\nkore { dataPackTargets = listOf(\"${serverDataPacks.invariantSeparatorsPath}\") }\n"
			)

			project.run("koreLink")

			serverDataPacks.resolve("my_pack/pack.mcmeta").exists() shouldBe true
		} finally {
			project.delete()
		}
	}

	test("the resource pack link task is skipped until resourcePackName is set") {
		val project = TestProject(koreConfiguration = "\tworlds = listOf(\"MyWorld\")")

		try {
			val result = project.run("koreLink")
			result.task(":koreLinkResourcePack")?.outcome shouldBe TaskOutcome.SKIPPED
		} finally {
			project.delete()
		}
	}

	test("a resource pack is linked into the resourcepacks folder when named") {
		val project = TestProject(
			koreConfiguration = "\tworlds = listOf(\"MyWorld\")\n\tresourcePackName = \"my_assets\""
		)

		try {
			project.writeResourcePackGenerator()
			val result = project.run("koreLink")

			result.task(":koreLinkResourcePack")?.outcome shouldBe TaskOutcome.SUCCESS
			project.minecraftDirectory.resolve("resourcepacks/my_assets/pack.mcmeta").exists() shouldBe true
		} finally {
			project.delete()
		}
	}

	test("koreClean unlinks every pack and deletes the output") {
		val project = TestProject(koreConfiguration = "\tworlds = listOf(\"MyWorld\")")

		try {
			project.run("koreLink")
			project.run("koreClean")

			project.linkedPack("MyWorld").exists() shouldBe false
			project.outputDirectory.exists() shouldBe false
		} finally {
			project.delete()
		}
	}

	test("koreWorlds lists the worlds and their datapacks folder") {
		val project = TestProject()

		try {
			project.world("SecondWorld")
			val result = project.run("koreWorlds")

			result.output shouldContain "Worlds (2):"
			result.output shouldContain "MyWorld"
			result.output shouldContain "SecondWorld"
		} finally {
			project.delete()
		}
	}

	test("an empty classpath fails with a message naming the fix") {
		val project = TestProject()

		try {
			project.root.resolve("build.gradle.kts").appendText("\nkore { runtimeClasspath.setFrom() }\n")
			val result = project.runAndFail("koreBuild")

			result.output shouldContain "runtime classpath is empty"
		} finally {
			project.delete()
		}
	}

	test("additionalInputs makes an external file part of up-to-date checking") {
		val project = TestProject(
			koreConfiguration = "\tworlds = listOf(\"MyWorld\")\n\tadditionalInputs.from(file(\"assets\"))"
		)

		try {
			val asset = project.root.resolve("assets/texture.txt")
			asset.parentFile.mkdirs()
			asset.writeText("first")

			project.run("koreBuild")
			project.run("koreBuild").task(":koreBuild")?.outcome shouldBe TaskOutcome.UP_TO_DATE

			asset.writeText("second")
			project.run("koreBuild").task(":koreBuild")?.outcome shouldBe TaskOutcome.SUCCESS
		} finally {
			project.delete()
		}
	}

	test("a failing reload does not fail the build unless failOnError is set") {
		val project = TestProject(
			koreConfiguration = "\trcon {\n\t\tpassword = \"secret\"\n\t\tport = 1\n\t\ttimeoutMillis = 500\n\t}"
		)

		try {
			val result = project.run("koreReload")
			result.output shouldContain "Could not send `reload`"

			project.root.resolve("build.gradle.kts").appendText("\nkore { rcon { failOnError = true } }\n")
			project.runAndFail("koreReload").output shouldContain "Could not send `reload`"
		} finally {
			project.delete()
		}
	}
})
