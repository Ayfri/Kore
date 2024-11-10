package io.github.ayfri.kore.utils

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.configuration
import io.github.ayfri.kore.generation.DataPackGenerationOptions
import io.github.ayfri.kore.generation.DataPackJarGenerationOptions
import io.github.ayfri.kore.minecraftSaveTestPath

data class TestDataPack(internal val dp: DataPack) {
	private val calledAfterGeneration = mutableListOf<DataPack.() -> Unit>()

	init {
		dp.path = minecraftSaveTestPath
	}

	fun callAfterGeneration(block: DataPack.() -> Unit) {
		calledAfterGeneration += block
	}

	fun generate(init: DataPackGenerationOptions.() -> Unit = {}) {
		dp.generate(init)
		calledAfterGeneration.forEach { it(dp) }
	}

	fun generateJar(init: DataPackJarGenerationOptions.() -> Unit = {}) {
		dp.generateJar(init)
		calledAfterGeneration.forEach { it(dp) }
	}

	fun generateZip(init: DataPackGenerationOptions.() -> Unit = {}) {
		dp.generateZip(init)
		calledAfterGeneration.forEach { it(dp) }
	}
}

internal fun testDataPack(name: String, block: DataPack.() -> Unit): TestDataPack {
	val testDataPack = TestDataPack(DataPack(name))
	testDataPack.dp.apply(block)
	return testDataPack
}

fun DataPack.pretty() = configuration {
	prettyPrint = true
	prettyPrintIndent = "\t"
}
