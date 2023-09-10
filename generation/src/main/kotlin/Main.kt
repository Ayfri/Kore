import generators.downloadDataPacks
import generators.downloadGamerules
import generators.launchAllSimpleGenerators
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlin.io.path.absolute
import java.io.File
import java.nio.file.Paths
import java.util.*
import kotlinx.serialization.json.Json

const val HEADER = """// Automatically generated - do not modify!"""
const val MAIN_GITHUB_URL = "https://raw.githubusercontent.com/PixiGeko/Minecraft-generated-data"
const val CODE_FOLDER = "src/main/kotlin"
const val CODE_PACKAGE = "io.github.ayfri.kore"
const val GENERATED_FOLDER = "$CODE_FOLDER/io/github/ayfri/kore/generated"
const val GENERATED_PACKAGE = "$CODE_PACKAGE.generated"

val rootDir: File = Paths.get(".").absolute().normalize().let {
	if (it.endsWith("generation")) it.parent
	else it
}.toFile()

val libDir = File(rootDir, "kore")
val cacheDir = File(rootDir, "generation/build/cache")

val properties = Properties().apply { load(File(rootDir, "gradle.properties").inputStream()) }
val minecraftVersion: String = properties.getProperty("minecraft.version")

val client = HttpClient(CIO) {
	install(ContentNegotiation) {
		json(Json {
			ignoreUnknownKeys = true
			coerceInputValues = true
		}, ContentType.Any)
	}
}

fun url(path: String) = "$MAIN_GITHUB_URL/$minecraftVersion/$path"

fun clearGeneratedPackage() {
	println("Clearing generated packages")
	val generatedDir = File(libDir, GENERATED_FOLDER)
	if (generatedDir.exists()) generatedDir.deleteRecursively()
	generatedDir.mkdirs()
}

suspend fun main(args: Array<String>) {
	if ("--reload-cache" in args) clearCache()
	clearGeneratedPackage()

	println("Generating assets from minecraft version: $minecraftVersion")

	downloadDataPacks()
	downloadGamerules()
	launchAllSimpleGenerators()
}
