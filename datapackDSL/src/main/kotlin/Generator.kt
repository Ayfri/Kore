import java.io.File

interface Generator {
	fun generate(dataPack: DataPack, directory: File)
}
