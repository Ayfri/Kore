import kotlinx.serialization.Transient
import java.io.File

interface Generator {
	@Transient
	var fileName: String
	fun generateJson(dataPack: DataPack): String
	fun generateFile(dataPack: DataPack, directory: File) = File(directory, "$fileName.json").writeText(generateJson(dataPack))
}
