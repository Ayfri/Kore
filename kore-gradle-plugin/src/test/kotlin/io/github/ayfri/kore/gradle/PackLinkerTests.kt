package io.github.ayfri.kore.gradle

import io.github.ayfri.kore.gradle.internal.PackLinker
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.io.File
import java.nio.file.Files
import kotlin.io.path.createTempDirectory

class PackLinkerTests : FunSpec({
	lateinit var root: File
	lateinit var source: File

	beforeTest {
		root = createTempDirectory("kore-linker").toFile()
		source = root.resolve("my_pack").apply { resolve("data/test").mkdirs() }
		source.resolve("pack.mcmeta").writeText("{}")
		source.resolve("data/test/load.mcfunction").writeText("say hello")
	}

	afterTest { root.deleteRecursively() }

	test("copying places the whole pack in the target") {
		val result = PackLinker.link(source, root.resolve("target/my_pack"), LinkMode.COPY)

		result.mode shouldBe LinkMode.COPY
		result.fallbackReason shouldBe null
		root.resolve("target/my_pack/data/test/load.mcfunction").readText() shouldBe "say hello"
	}

	test("copying replaces a stale pack instead of merging into it") {
		val destination = root.resolve("target/my_pack").apply { mkdirs() }
		destination.resolve("stale.mcfunction").writeText("say stale")

		PackLinker.link(source, destination, LinkMode.COPY)

		destination.resolve("stale.mcfunction").exists() shouldBe false
		destination.resolve("pack.mcmeta").exists() shouldBe true
	}

	// Symlink creation needs Developer Mode or administrator rights on Windows, so both outcomes are valid here.
	test("symlinking either links or falls back to a copy, and the pack is readable either way") {
		val destination = root.resolve("target/my_pack")
		val result = PackLinker.link(source, destination, LinkMode.SYMLINK)

		destination.resolve("pack.mcmeta").readText() shouldBe "{}"
		when (result.mode) {
			LinkMode.SYMLINK -> Files.isSymbolicLink(destination.toPath()) shouldBe true
			LinkMode.COPY -> result.fallbackReason.isNullOrEmpty() shouldBe false
		}
	}

	test("deleting a symlinked pack unlinks it without touching the source") {
		val destination = root.resolve("target/my_pack")
		val linked = PackLinker.link(source, destination, LinkMode.SYMLINK)

		PackLinker.delete(destination) shouldBe true
		destination.exists() shouldBe false
		source.resolve("pack.mcmeta").exists() shouldBe true
		source.resolve("data/test/load.mcfunction").exists() shouldBe true
		linked.destination shouldBe destination
	}

	test("deleting an absent destination reports that nothing was removed") {
		PackLinker.delete(root.resolve("target/absent")) shouldBe false
	}

	test("linking without a generated pack fails with an actionable message") {
		val exception = runCatching { PackLinker.link(root.resolve("absent"), root.resolve("target"), LinkMode.COPY) }
		exception.isFailure shouldBe true
		(exception.exceptionOrNull()?.message?.contains("run the build task first") ?: false) shouldBe true
	}
})
