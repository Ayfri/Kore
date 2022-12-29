package generators

import Serializer
import generateEnum
import getFromCacheOrDownloadTxt
import minecraftVersion
import url

suspend fun downloadParticules() {
	val url = url("data/registries/particle_type.txt")
	val particules = getFromCacheOrDownloadTxt("particules.txt", url).lines()

	generateParticulesEnum(particules, url)
}

fun generateParticulesEnum(particules: List<String>, sourceUrl: String) {
	val name = "Particules"
	generateEnum(
		name = name,
		sourceUrl = sourceUrl,
		additionalHeaders = listOf("Minecraft version: $minecraftVersion"),
		properties = particules.map { it.substringAfter("minecraft:").uppercase() },
		serializer = Serializer.Lowercase,
		customEncoder = """encoder.encodeString("minecraft:${"\${value.name.lowercase()}"}")""",
		additionalImports = listOf("arguments.Argument")
	)
}
