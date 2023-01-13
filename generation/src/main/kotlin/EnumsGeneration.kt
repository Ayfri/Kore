import java.io.File


enum class Serializer {
	Lowercase, Camelcase;
}

fun generateEnum(
	name: String,
	path: String? = null,
	sourceUrl: String? = null,
	additionalHeaders: List<String> = emptyList(),
	properties: Collection<String>,
	serializer: Serializer,
	customEncoder: String? = null,
	additionalImports: List<String> = emptyList(),
	customLines: List<String> = emptyList(),
	inheritances: List<String> = emptyList(),
	vararg additionalLines: String,
) {
	val enumName = name.pascalCase()
	val imports = additionalImports.toMutableList().apply {
		add("serializers.${serializer.name}Serializer")
		add("kotlinx.serialization.Serializable")
		if (customEncoder != null) add("kotlinx.serialization.encoding.Encoder")
	}

	generateFile(
		name,
		path,
		sourceUrl,
		additionalHeaders,
		generateEnumText(
			enumName,
			customEncoder,
			serializer,
			properties,
			enumInheritances = inheritances,
			customLines = customLines.toTypedArray()
		),
		imports,
		*additionalLines
	)
}

fun generateEnumText(
	enumName: String,
	customEncoder: String?,
	serializer: Serializer,
	properties: Collection<String>,
	parent: String? = null,
	enumInheritances: List<String> = emptyList(),
	vararg customLines: String
): String {
	val serializerName = "${enumName}Serializer"
	val serializerGenericType = if (enumName in properties) "$parent.$enumName" else enumName

	val encoder = when (customEncoder) {
		null -> ""
		else -> """
			|{
			|			override fun serialize(encoder: Encoder, value: $serializerGenericType) {
			|				$customEncoder
			|			}
			|		}
		""".trimMargin()
	}

	val serializerLines = """
		|object $serializerName : ${serializer.name}Serializer<$serializerGenericType>(values) $encoder
	""".trimMargin()

	val inheritances = enumInheritances.joinToString(prefix = if (enumInheritances.isNotEmpty()) " : " else "", separator = ", ")
	return """
		|@Serializable(with = $enumName.Companion.$serializerName::class)
		|enum class $enumName$inheritances {
		|	${properties.sorted().joinToString(separator = ",\n\t", postfix = ";")}
		|	
		|	${customLines.joinToString(separator = "\n\t")}
		|
		|	companion object {
		|		val values = values()
		|
		|		$serializerLines
		|	}
		|}
	""".trimMargin().replace(Regex("(\n\t+){2,}"), "$1$1")
}

fun generateFile(
	name: String,
	path: String? = null,
	sourceUrl: String? = null,
	additionalHeaders: Collection<String> = emptyList(),
	content: String,
	additionalImports: Collection<String> = emptyList(),
	vararg additionalLines: String,
) {
	val file = File(libDir, "src/main/kotlin/generated${path?.let { "/${it.removeSurrounding("/")}" } ?: ""}/$name.kt")
	file.parentFile.mkdirs()
	println("Generating ${file.canonicalPath}")
	val headers = additionalHeaders.toMutableList().apply {
		if (sourceUrl != null) add("Source: $sourceUrl")
		add(0, header.removePrefix("// "))
	}

	val imports = additionalImports.toMutableList().sorted().map { "import $it" }

	val text = """
		|${headers.joinToString(separator = "\n// ", prefix = "// ")}
		|
		|package generated${path?.let { ".${it.removeSurrounding("/").replace("/", ".")}" } ?: ""}
		|
		|${imports.joinToString(separator = "\n")}
		|
		|$content
		|
		|${additionalLines.joinToString(separator = "\n")}
		|
	""".trimMargin().trim().replace(Regex("(\n\t*){2,}"), "$1$1") + "\n"

	file.writeText(text)

	println("Generated ${file.canonicalPath}")
}
