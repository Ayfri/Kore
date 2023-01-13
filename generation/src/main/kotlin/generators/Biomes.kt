package generators

import Serializer
import generateEnum
import getFromCacheOrDownloadJson
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import minecraftVersion
import url

suspend fun downloadBiomes() {
	val netherBiomesUrl = url("generated/biome_parameters/minecraft/nether.json")
	val netherBiomes = getFromCacheOrDownloadJson("nether_biomes.json", netherBiomesUrl)

	val overworldBiomesUrl = url("generated/biome_parameters/minecraft/overworld.json")
	val overworldBiomes = getFromCacheOrDownloadJson("overworld_biomes.json", overworldBiomesUrl)

	val biomes = listOf(overworldBiomes, netherBiomes).map {
		it.jsonObject["biomes"]!!.jsonArray.map { it.jsonObject["biome"]!!.jsonPrimitive.content }
	}.flatten().distinct()

	generateBiomesEnum(biomes, netherBiomesUrl)
}

fun generateBiomesEnum(biomes: List<String>, sourceUrl: String) {
	val name = "Biomes"
	generateEnum(
		name = name,
		sourceUrl = sourceUrl,
		additionalHeaders = listOf("Minecraft version: $minecraftVersion"),
		properties = biomes.map { it.substringAfter("minecraft:").uppercase() },
		serializer = Serializer.Lowercase,
		customEncoder = """encoder.encodeString("minecraft:${"\${value.name.lowercase()}"}")""",
		additionalImports = listOf("arguments.Argument"),
		customLines = listOf(
			"override val namespace = \"minecraft\"",
			"",
			"override fun asString() = \"minecraft:\${name.lowercase()}\""
		),
		inheritances = listOf("Argument.Biome"),
	)
}
