package io.github.ayfri.kore

import io.github.ayfri.kore.arguments.argumentsTests
import io.github.ayfri.kore.commands.runUnitTests
import io.github.ayfri.kore.features.featuresTests
import io.github.ayfri.kore.helpers.helpersTests
import io.github.ayfri.kore.pack.packFormatTests
import io.github.ayfri.kore.pack.packMCMetaTests
import io.github.ayfri.kore.serialization.*
import io.github.cdimascio.dotenv.dotenv
import kotlinx.io.files.Path

val configuration = dotenv()
val minecraftSaveTestPath = Path(configuration["TEST_FOLDER", "out"])

fun DataPack.setTestPath() {
	path = minecraftSaveTestPath
}

fun main() {
	argumentsTests()
	colorTests()
	vec2Tests()
	vec3Tests()

	chatComponentsTests()
	datapackTests()
	featuresTests()
	functionsTests()
	helpersTests()
	packFormatTests()
	packMCMetaTests()
	selectorTests()
	serializersTests()
	runUnitTests()

	morpion()
}
