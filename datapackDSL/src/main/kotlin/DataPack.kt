
import annotations.FunctionsHolder
import arguments.TextComponents
import arguments.textComponent
import functions.Function
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import tags.Tags
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
	var description: TextComponents,
)

@Serializable
data class SerializedDataPack(
	val pack: Pack,
	val filter: Filter? = null,
)

@FunctionsHolder
class DataPack(val name: String) {
	var path = Path("out")
	var iconPath: Path? = null

	private var filter: Filter? = null
	private val pack = Pack(10, textComponent("Generated by DataPackDSL"))
	private val functions = mutableListOf<Function>()
	private val generatedFunctions = mutableListOf<Function>()
	val tags = mutableListOf<Tags>()

	fun addFunction(function: Function) {
		functions += function
	}

	fun addGeneratedFunction(function: Function): String {
		generatedFunctions.find { it.lines == function.lines }?.let {
			return@addGeneratedFunction it.name
		}

		generatedFunctions += function
		return function.name
	}

	fun pack(block: Pack.() -> Unit) = pack.run(block)
	fun filter(block: Filter.() -> Unit) = Filter().apply(block).also { filter = it }

	fun generate() {
		val start = System.currentTimeMillis()
		val root = File("$path/$name")
		root.mkdirs()

		val serialized = SerializedDataPack(pack, filter)

		File(root, "pack.mcmeta").writeText(jsonEncoder.encodeToString(serialized))
		iconPath?.let { File(root, "pack.png").writeBytes(it.toFile().readBytes()) }

		val data = File(root, "data")
		data.mkdirs()

		functions.groupBy { it.namespace }.forEach { (namespace, functions) ->
			val namespaceDir = File(data, namespace)
			namespaceDir.mkdirs()

			val functionsDir = File(namespaceDir, "functions")
			functionsDir.mkdirs()

			functions.forEach { it.generate(functionsDir) }
		}

		generatedFunctions.groupBy { it.namespace }.forEach { (namespace, functions) ->
			val namespaceDir = File(data, namespace)
			namespaceDir.mkdirs()

			val functionsDir = File(namespaceDir, "functions/$GENERATED_FUNCTIONS_FOLDER")
			functionsDir.deleteRecursively()
			functionsDir.mkdirs()

			functions.forEach { it.generate(functionsDir) }
		}

		tags.groupBy { it.namespace }.forEach { (namespace, tags) ->
			val namespaceDir = File(data, namespace)
			namespaceDir.mkdirs()

			val tagsDir = File(namespaceDir, "tags")
			tagsDir.mkdirs()

			tags.forEach { it.generate(tagsDir) }
		}

		val end = System.currentTimeMillis()
		println("Generated data pack '$name' in ${end - start}ms in: ${root.absolutePath}")
	}

	@Deprecated(
		"Generation to zip is for now not working fine with Minecraft, please use generate() instead",
		ReplaceWith("generate()"),
		DeprecationLevel.WARNING
	)
	fun generateZip() = generate()

	companion object {
		const val GENERATED_FUNCTIONS_FOLDER = "generated_scopes"

		@OptIn(ExperimentalSerializationApi::class)
		val jsonEncoder = Json {
			prettyPrint = true
			encodeDefaults = true
			ignoreUnknownKeys = true
			explicitNulls = false
		}
	}
}

fun dataPack(name: String, block: DataPack.() -> Unit) = DataPack(name).apply(block)
