package io.github.ayfri.kore.helpers

import io.github.ayfri.kore.DataPack
import kotlinx.io.files.Path

val minecraftSaveTestPath = Path("out")

fun DataPack.setTestPath() {
	path = minecraftSaveTestPath
}

fun main() {
	asciiRendererTests()
	helpersTests()
	markdownRendererTests()
	miniMessageRendererTests()
	mathTests()
	raycastTests()
	vfxTests()
}