import generators.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import java.io.File
import java.nio.file.Paths
import kotlin.KotlinVersion as Version

const val header = """// Automatically generated - do not modify!"""
const val mainGitHubUrl = "https://raw.githubusercontent.com/PixiGeko/Minecraft-generated-data/master"

val rootDir: File = Paths.get(".").toFile()
val libDir = File(rootDir, "datapackDSL")
val cacheDir = File(rootDir, "generation/build/cache")
val minecraftVersion = Version(1, 19, 2)

val client = HttpClient(CIO) {
	install(ContentNegotiation) {
		json(Json {
			ignoreUnknownKeys = true
			coerceInputValues = true
		}, ContentType.Any)
	}
}

fun url(path: String) = "$mainGitHubUrl/${minecraftVersion.major}.${minecraftVersion.minor}/releases/$minecraftVersion/$path"

fun clearGeneratedPackage() {
	println("Clearing generated packages")
	val generatedDir = File(libDir, "src/main/kotlin/generated")
	if (generatedDir.exists()) generatedDir.deleteRecursively()
	generatedDir.mkdirs()
}

suspend fun main(args: Array<String>) {
	if ("--reload-cache" in args) clearCache()
	clearGeneratedPackage()

	downloadAdvancements()
	downloadAttributes()
	downloadBiomes()
	downloadBlocks()
	downloadEffects()
	downloadEnchantments()
	downloadEntities()
	downloadItems()
	downloadParticules()
	downloadRecipes()
}
