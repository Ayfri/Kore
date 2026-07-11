package io.github.ayfri.kore.utils

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.configuration
import io.github.ayfri.kore.generation.DataPackGenerationOptions
import kotlinx.io.files.Path

val oopTestPath = Path("out")

data class TestDataPack(internal val dp: DataPack) {
	private val calledAfterGeneration = mutableListOf<DataPack.() -> Unit>()

	init {
		dp.path = oopTestPath
	}

	fun callAfterGeneration(block: DataPack.() -> Unit) {
		calledAfterGeneration += block
	}

	fun generate(init: DataPackGenerationOptions.() -> Unit = {}) {
		dp.generate(init)
		calledAfterGeneration.forEach { it(dp) }
	}
}

fun testDataPack(name: String, block: DataPack.() -> Unit): TestDataPack {
	val testDataPack = TestDataPack(DataPack(name))
	testDataPack.dp.apply(block)
	return testDataPack
}

fun DataPack.pretty() = configuration {
	prettyPrint = true
	prettyPrintIndent = "	"
}
