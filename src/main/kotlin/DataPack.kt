import annotations.FunctionsHolder
import functions.Function
import java.io.File

data class Pack(
	var packFormat: Int,
	var description: String
)

@FunctionsHolder
class DataPack(val name: String) {
	val pack = Pack(10, "Kordex Plugin")
	val path = "out"
	val functions = mutableListOf<Function>()
	
	fun generate() {
		val root = File("$path/$name")
		root.mkdirs()
		
		File(root, "pack.mcmeta").writeText(
			"""
			{
				"pack": {
					"pack_format": ${pack.packFormat},
					"description": "${pack.description}"
				}
			}
			""".trimIndent()
		)
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
}

fun dataPack(name: String, block: DataPack.() -> Unit): DataPack {
	val dataPack = DataPack(name)
	dataPack.block()
	return dataPack
}

fun DataPack.pack(block: Pack.() -> Unit) {
	pack.block()
}
