package utils

import DataPack
import Generator
import java.nio.file.Path

fun Generator.getFinalPath(dataPack: DataPack): Path {
	val dataFolder = dataPack.path.resolve(dataPack.name).resolve("data")
	val namespace = namespace ?: dataPack.name
	return dataFolder.resolve(namespace).resolve(resourceFolder).resolve("$fileName.json")
}
