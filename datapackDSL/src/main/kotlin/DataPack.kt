import annotations.FunctionsHolder
import arguments.Argument
import arguments.chatcomponents.textComponent
import features.advancements.Advancement
import features.chattype.ChatType
import features.damagetypes.DamageType
import features.itemmodifiers.ItemModifier
import features.loottables.LootTable
import features.predicates.Predicate
import features.recipes.RecipeFile
import features.tags.Tags
import features.worldgen.biome.Biome
import features.worldgen.dimensiontype.DimensionType
import functions.Function
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import java.io.File
import java.nio.file.Path
import kotlin.io.path.Path

@FunctionsHolder
class DataPack(val name: String) {
	private var filter: Filter? = null
	private val pack = Pack(15, textComponent("Generated by DataPackDSL"))
	private val functions = mutableListOf<Function>()
	private val generatedFunctions = mutableListOf<Function>()
	val advancements = mutableListOf<Advancement>()
	val biomes = mutableListOf<Biome>()
	val chatTypes = mutableListOf<ChatType>()
	val damageTypes = mutableListOf<DamageType>()
	val dimensionTypes = mutableListOf<DimensionType>()
	val itemModifiers = mutableListOf<ItemModifier>()
	val lootTables = mutableListOf<LootTable>()
	val predicates = mutableListOf<Predicate>()
	val recipes = mutableListOf<RecipeFile>()
	val tags = mutableListOf<Tags>()

	var configuration: Configuration = Configuration.Default
	var iconPath: Path? = null
	var path = Path("out")

	fun addFunction(function: Function): Argument.Function {
		functions += function
		return function
	}

	fun addGeneratedFunction(function: Function): Argument.Function {
		generatedFunctions.find { it.lines == function.lines }?.let {
			return@addGeneratedFunction it
		}

		generatedFunctions += function
		return function
	}

	fun pack(block: Pack.() -> Unit) = pack.run(block)
	fun filter(block: Filter.() -> Unit) = Filter().apply(block).let { filter = it }

	fun generate() {
		val start = System.currentTimeMillis()
		val root = File("$path/$name")
		root.mkdirs()

		val packMCMeta = PackMCMeta(pack, filter)
		File(root, "pack.mcmeta").writeText(jsonEncoder.encodeToString(packMCMeta))
		iconPath?.let { File(root, "pack.png").writeBytes(it.toFile().readBytes()) }

		val data = File(root, "data")
		data.mkdirs()

		data.generateResources("advancements", advancements)
		data.generateResources("chat_type", chatTypes)
		data.generateResources("damage_type", damageTypes)
		data.generateResources("dimension_type", dimensionTypes)
		data.generateResources("item_modifiers", itemModifiers)
		data.generateResources("loot_tables", lootTables)
		data.generateResources("predicates", predicates)
		data.generateResources("recipes", recipes)
		data.generateResources("tags", tags.groupBy(Tags::namespace))
		data.generateResources("worldgen/biome", biomes)

		data.generateFunctions("functions", functions.groupBy(Function::namespace))
		data.generateFunctions(
			dirName = "functions/$GENERATED_FUNCTIONS_FOLDER",
			functionsMap = generatedFunctions.map {
				it.directory = it.directory.removePrefix(GENERATED_FUNCTIONS_FOLDER)
				it
			}.groupBy(Function::namespace),
			deleteOldFiles = true
		)
		val end = System.currentTimeMillis()
		println("Generated data pack '$name' in ${end - start}ms in: ${root.absolutePath}")
	}

	private fun File.generateFunctions(
		dirName: String,
		functionsMap: Map<String, List<Function>>,
		deleteOldFiles: Boolean = false
	) =
		functionsMap.forEach { (namespace, functions) ->
			val namespaceDir = File(this, namespace)
			namespaceDir.mkdirs()

			if (functions.isEmpty()) return
			val dir = File(let { if (it.name == "data") File(it, this@DataPack.name) else it }, dirName)
			if (deleteOldFiles) dir.deleteRecursively()
			dir.mkdirs()

			functions.forEach { it.generate(dir) }
		}


	private fun <T : Generator> File.generateResources(
		dirName: String,
		resources: Map<String, List<T>>,
		deleteOldFiles: Boolean = false
	) =
		resources.forEach { (namespace, resource) ->
			val namespaceDir = File(this, namespace)
			namespaceDir.mkdirs()

			namespaceDir.generateResources(dirName, resource, deleteOldFiles)
		}

	private fun <T : Generator> File.generateResources(
		dirName: String,
		resources: List<T>,
		deleteOldFiles: Boolean = false
	) {
		if (resources.isEmpty()) return
		val dir = File(let { if (it.name == "data") File(it, this@DataPack.name) else it }, dirName)
		if (deleteOldFiles) dir.deleteRecursively()
		dir.mkdirs()

		resources.forEach { it.generateFile(this@DataPack, dir) }
	}

	@Deprecated(
		"Generation to zip is for now not working fine with Minecraft, please use generate() instead",
		ReplaceWith("generate()"),
		DeprecationLevel.WARNING
	)
	fun generateZip() = generate()

	@OptIn(ExperimentalSerializationApi::class)
	val jsonEncoder
		get() = Json {
			prettyPrint = configuration.prettyPrint
			if (prettyPrint) prettyPrintIndent = configuration.prettyPrintIndent
			encodeDefaults = true
			explicitNulls = false
			ignoreUnknownKeys = true
			namingStrategy = JsonNamingStrategy.SnakeCase
			useAlternativeNames = false
		}

	companion object {
		const val GENERATED_FUNCTIONS_FOLDER = "generated_scopes"
	}
}

fun dataPack(name: String, block: DataPack.() -> Unit) = DataPack(name).apply(block)

fun DataPack.configuration(block: ConfigurationBuilder.() -> Unit) {
	configuration = Configuration(block)
}
