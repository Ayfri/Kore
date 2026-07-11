package io.github.ayfri.kore.bindings

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.path
import java.nio.file.Path
import kotlin.io.path.createTempDirectory

data class TestContext(val name: String) {
	val tempDir = createTempDirectory("kore_import_$name")

	fun createDataPack(packName: String, block: DataPack.() -> Unit) = dataPack(packName) {
		path(tempDir.resolve(packName).toString())
		block()
	}

	fun outputDir(): Path = tempDir.resolve("output")

	fun srcDir(): Path = outputDir().resolve("src/test").also { it.toFile().mkdirs() }

	fun delete() {
		tempDir.toFile().deleteRecursively()
	}
}
