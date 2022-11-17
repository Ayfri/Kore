
import annotations.FunctionsHolder
import functions.Function
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

@Serializable
data class Pack(
	var packFormat: Int,
	var description: String,
)

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
data class SerializedDataPack(
	val pack: Pack,
	val filter: Filter,
)

@FunctionsHolder
class DataPack(val name: String) {
	val filter = Filter()
	val pack = Pack(10, "Kordex Plugin")
	val path = "out"
	val functions = mutableListOf<Function>()
	
	fun pack(block: Pack.() -> Unit) = pack.run(block)
	fun filter(block: Filter.() -> Unit) = filter.run(block)
	
	fun generate() {
		val root = File("$path/$name")
		root.mkdirs()
		
		val serialized = SerializedDataPack(pack, filter)
		
		File(root, "pack.mcmeta").writeText(json.encodeToString(serialized))
		val data = File(root, "data")
		data.mkdirs()
		
		functions.groupBy { it.namespace }.forEach { (namespace, functions) ->
			val namespaceDir = File(data, namespace)
			namespaceDir.mkdirs()
			
			val functionsDir = File(namespaceDir, "functions")
			functionsDir.mkdirs()
			
			functions.forEach { it.generate(functionsDir) }
		}
	}
	
	companion object {
		@OptIn(ExperimentalSerializationApi::class)
		private val json = Json {
			prettyPrint = true
			ignoreUnknownKeys = true
			explicitNulls = false
		}
	}
}

fun dataPack(name: String, block: DataPack.() -> Unit) = DataPack(name).apply(block)
