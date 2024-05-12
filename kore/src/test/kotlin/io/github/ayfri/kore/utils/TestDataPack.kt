package io.github.ayfri.kore.utils

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.configuration
import io.github.ayfri.kore.minecraftSaveTestPath

data class TestDataPack(internal val dp: DataPack) {
	private val calledAfterGeneration = mutableListOf<DataPack.() -> Unit>()

	init {
		dp.path = minecraftSaveTestPath
	}

	fun callAfterGeneration(block: DataPack.() -> Unit) {
		calledAfterGeneration += block
	}

	fun generate() {
		dp.generate()
		calledAfterGeneration.forEach { it(dp) }
	}

	fun generateZip() {
		dp.generateZip()
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
