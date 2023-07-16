import generators.*
import generators.worldgen.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import java.io.File
import java.nio.file.Paths
import java.util.*
import kotlin.io.path.absolute

const val header = """// Automatically generated - do not modify!"""
const val mainGitHubUrl = "https://raw.githubusercontent.com/PixiGeko/Minecraft-generated-data"

val rootDir: File = Paths.get(".").absolute().normalize().let {
	if (it.endsWith("generation")) it.parent
	else it
}.toFile()

val libDir = File(rootDir, "datapackDSL")
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

fun url(path: String) = "$mainGitHubUrl/$minecraftVersion/$path"

fun clearGeneratedPackage() {
	println("Clearing generated packages")
	val generatedDir = File(libDir, "src/main/kotlin/generated")
	if (generatedDir.exists()) generatedDir.deleteRecursively()
	generatedDir.mkdirs()
}

suspend fun main(args: Array<String>) {
	if ("--reload-cache" in args) clearCache()
	clearGeneratedPackage()

	println("Generating assets from minecraft version: $minecraftVersion")

	downloadAdvancements()
	downloadAttributes()
	downloadBannerPatterns()
	downloadBiomes()
	downloadBiomePresets()
	downloadBlocks()
	downloadCarvers()
	downloadCatVariants()
	downloadDamageTypes()
	downloadDensityFunctions()
	downloadEffects()
	downloadEnchantments()
	downloadEntities()
	downloadFeatures()
	downloadFluids()
	downloadFrogVariants()
	downloadGamerules()
	downloadInstruments()
	downloadItems()
	downloadLootTables()
	downloadNoises()
	downloadNoiseSettings()
	downloadParticles()
	downloadPotions()
	downloadRecipes()
	downloadSounds()
	downloadStatisticTypes()
	downloadStructures()
	downloadStructureSets()
	downloadTags()
}
