import annotations.FunctionsHolder
import arguments.Argument
import arguments.ChatComponents
import arguments.chatcomponents.textComponent
import features.advancements.Advancement
import features.predicates.Predicate
import features.recipes.RecipeFile
import features.tags.Tags
import features.worldgen.DimensionType
import functions.Function
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.nio.file.Path
import kotlin.io.path.Path

@Serializable
data class FilteredBlock(
	var namespace: String? = null,
	var path: String? = null,
)

@Serializable
class Filter {
	@SerialName("block")
	internal val blocks = mutableListOf<FilteredBlock>()

	fun block(namespace: String? = null, path: String? = null) {
		blocks += FilteredBlock(namespace, path)
	}

	fun block(block: FilteredBlock) {
		blocks += block
	}

	fun block(block: FilteredBlock.() -> Unit) {
		blocks += FilteredBlock().apply(block)
	}

	fun blocks(vararg blocks: FilteredBlock) {
		this.blocks += blocks
	}

	fun blocks(blocks: Collection<FilteredBlock>) {
		this.blocks += blocks
	}
}

@Serializable
data class Pack(
	@SerialName("pack_format")
	var format: Int,
	@Serializable
	var description: ChatComponents,
)

@Serializable
data class SerializedDataPack(
	val pack: Pack,
	val filter: Filter? = null,
)

@FunctionsHolder
class DataPack(val name: String) {
	private var filter: Filter? = null
	private val pack = Pack(12, textComponent("Generated by DataPackDSL"))
	private val functions = mutableListOf<Function>()
	private val generatedFunctions = mutableListOf<Function>()
	val advancements = mutableListOf<Advancement>()
	val dimensionTypes = mutableListOf<DimensionType>()
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

		val serialized = SerializedDataPack(pack, filter)
		File(root, "pack.mcmeta").writeText(jsonEncoder.encodeToString(serialized))
		iconPath?.let { File(root, "pack.png").writeBytes(it.toFile().readBytes()) }

		val data = File(root, "data")
		data.mkdirs()

		data.generateResources("advancements", advancements)
		data.generateResources("dimension_type", dimensionTypes)
		data.generateResources("functions", functions.groupBy(Function::namespace))
		data.generateResources(
			dirName = "functions/$GENERATED_FUNCTIONS_FOLDER",
			resources = generatedFunctions.map {
				it.directory = it.directory.removePrefix(GENERATED_FUNCTIONS_FOLDER)
				it
			}.groupBy(Function::namespace),
			deleteOldFiles = true
		)
		data.generateResources("predicates", predicates)
		data.generateResources("recipes", recipes)
		data.generateResources("tags", tags.groupBy(Tags::namespace))

		val end = System.currentTimeMillis()
		println("Generated data pack '$name' in ${end - start}ms in: ${root.absolutePath}")
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

		resources.forEach { it.generate(this@DataPack, dir) }
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
			useAlternativeNames = false
		}

	companion object {
		const val GENERATED_FUNCTIONS_FOLDER = "generated_scopes"
		const val defaultIndent = "   "
	}
}

fun dataPack(name: String, block: DataPack.() -> Unit) = DataPack(name).apply(block)

fun DataPack.configuration(block: ConfigurationBuilder.() -> Unit) {
	configuration = Configuration(block)
}
