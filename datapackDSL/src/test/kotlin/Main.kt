import commands.runUnitTests
import features.featuresTests
import helpers.helpersTests
import io.github.cdimascio.dotenv.dotenv
import kotlin.io.path.Path
import serialization.datapackTests
import serialization.selectorTests

val configuration = dotenv()
val minecraftSaveTestPath = Path(configuration["TEST_FOLDER"])

fun DataPack.setTestPath() {
	path = minecraftSaveTestPath
}

fun main() {
	colorTests()

	datapackTests()
	featuresTests()
	helpersTests()
	selectorTests()
	runUnitTests()
}
