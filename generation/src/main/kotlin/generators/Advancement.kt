package generators

import Serializer
import generateEnumText
import generateFile
import getFromCacheOrDownloadTxt
import minecraftVersion
import pascalCase
import url

suspend fun downloadAdvancements() {
	val url = url("data/misc/advancements.txt")
	val attributes = getFromCacheOrDownloadTxt("advancements.txt", url).lines()
	val objectName = "Advancements"

	val content = buildString {
		append("sealed interface $objectName {")

		val attributesTree = attributes.groupBy {
			it.substringBeforeLast("/")
		}

		val alreadyTakenPaths = mutableListOf<String>()
		attributesTree.forEach { (parent, values) ->
			fun generateEnum(name: String, properties: List<String>, parent: String, indentsSize: Int = 1) {
				val finalName = name.substringAfterLast("/")
				val path = if (parent == "") finalName else "$parent/$finalName"

				val generateEnumText = generateEnumText(
					enumName = finalName.pascalCase(),
					properties = properties.map { it.substringAfterLast("/").pascalCase() },
					serializer = Serializer.Lowercase,
					parent = "$objectName.${parent.pascalCase()}",
					customEncoder = """encoder.encodeString("$path/${"\${value.name.lowercase()}"}")""",
					enumInheritances = listOf(objectName),
				)

				appendLine()
				generateEnumText.lines().forEach { appendLine("${"\t".repeat(indentsSize)}$it") }
			}

			if (alreadyTakenPaths.any { parent.startsWith(it) }) return@forEach

			if ("/" in parent) {
				val parentName = parent.substringBeforeLast("/")
				alreadyTakenPaths.add(parentName)

				appendLine()
				append("\tobject ${parentName.pascalCase()} {")

				attributesTree.filter { it.key.startsWith(parentName) }.forEach { (subParent, values) ->
					generateEnum(subParent, values, parentName, 2)
				}

				appendLine("\t}")
			} else {
				generateEnum(parent, values, "")
			}
		}

		append("}")
	}

	generateFile(
		name = objectName,
		content = content,
		sourceUrl = url,
		additionalHeaders = listOf("Minecraft version: $minecraftVersion"),
		additionalImports = buildList {
			add("serializers.${Serializer.Lowercase.name}Serializer")
			add("kotlinx.serialization.Serializable")
			add("kotlinx.serialization.encoding.Encoder")
		}
	)
}
