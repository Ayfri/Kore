import kotlin.io.path.Path
import kotlin.io.path.createParentDirectories
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
abstract class Generator(@Transient val resourceFolder: String = error("Generator must have a resource folder")) {
	@Transient
	var namespace: String? = null

	abstract var fileName: String

	abstract fun generateJson(dataPack: DataPack): String

	fun generateFile(dataPack: DataPack) {
		val dataFolder = Path(dataPack.path.toString(), dataPack.name, "data")
		val file = Path(dataFolder.toString(), namespace ?: dataPack.name, resourceFolder, "$fileName.json")
		file.createParentDirectories()
		file.toFile().writeText(generateJson(dataPack))
	}
}
