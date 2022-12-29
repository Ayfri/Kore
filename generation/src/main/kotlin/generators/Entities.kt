package generators

import Serializer
import generateEnum
import getFromCacheOrDownloadTxt
import minecraftVersion
import url

suspend fun downloadEntities() {
	val url = url("data/registries/entity_type.txt")
	val entities = getFromCacheOrDownloadTxt("entities.txt", url).lines()

	generateEntitiesEnum(entities, url)
}

fun generateEntitiesEnum(entities: List<String>, sourceUrl: String) {
	val name = "Entities"
	generateEnum(
		name = name,
		sourceUrl = sourceUrl,
		additionalHeaders = listOf("Minecraft version: $minecraftVersion"),
		properties = entities.map { it.substringAfter("minecraft:").uppercase() },
		serializer = Serializer.Lowercase,
		customEncoder = """encoder.encodeString("minecraft:${"\${value.name.lowercase()}"}")""",
		additionalImports = listOf("arguments.Argument", "arguments.selector.SelectorNbtData", "commands.asArg"),
		additionalLines = arrayOf("fun SelectorNbtData.type(entity: $name) {\n\ttype = entity.asArg()\n}")
	)
}
