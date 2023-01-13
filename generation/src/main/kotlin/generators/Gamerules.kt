package generators

import Serializer
import generateEnum
import generateFile
import getFromCacheOrDownloadTxt
import minecraftVersion
import snakeCase
import url

suspend fun downloadGamerules() {
	val url = url("data/commands/syntaxes/gamerule.txt")
	val gamerules = getFromCacheOrDownloadTxt("gamerules.txt", url).lines()

	generateGamerulesEnums(gamerules, url)
}

private fun String.toGameruleName() = substringAfter("gamerule").substringBefore("<").snakeCase().trim().uppercase()

fun generateGamerulesEnums(gamerules: List<String>, sourceUrl: String) {
	val name = "Gamerules"
	val booleanGamerules = gamerules.filter { it.endsWith("<value: bool>") }.map(String::toGameruleName)
	val integerGamerules = gamerules.filter { it.endsWith("<value: integer>") }.map(String::toGameruleName)

	generateEnum(
		name = name,
		sourceUrl = sourceUrl,
		additionalHeaders = listOf("Minecraft version: $minecraftVersion"),
		properties = booleanGamerules + integerGamerules.map { "$it { override val isInt = true }" },
		serializer = Serializer.Lowercase,
		customEncoder = """encoder.encodeString(value.name.lowercase())""",
		customLines = listOf("open val isInt = false")
	)

	generateFile(
		name = name,
		sourceUrl = sourceUrl,
		additionalHeaders = listOf(),
		content = """
			|@Serializable(with = $name.Companion.Serializer::class)
			|sealed interface $name {
			|	val name get() = this::class.simpleName!!
			|
			|	@Serializable(with = Serializer::class)
			|	sealed interface Boolean : $name
			|	@Serializable(with = Serializer::class)
			|	sealed interface Int : $name
			|
			|	${booleanGamerules.joinToString(separator = "\n\t") { "object $it : Boolean" }}
			|	${integerGamerules.joinToString(separator = "\n\t") { "object $it : Int" }}
			|
			|	companion object {
			|		val values = $name::class.sealedSubclasses.map {
			|			it.sealedSubclasses.map { it.objectInstance!! }
			|		}.flatten()
			|
			|		fun fromString(name: String) = values.firstOrNull { it.name.equals(name, ignoreCase = true) }
			|
			|		object Serializer : KSerializer<$name> {
			|			override val descriptor = PrimitiveSerialDescriptor("$name", PrimitiveKind.STRING)
			|
			|			override fun deserialize(decoder: Decoder): $name {
			|				val name = decoder.decodeString()
			|
			|				return fromString(name) ?: throw IllegalArgumentException("Unknown $name: $${'$'}name")
			|			}
			|
			|			override fun serialize(encoder: Encoder, value: $name) {
			|				fun String.camelCase(separator: String = "_"): String {
			|					val words = lowercase().split(separator)
			|					return words[0] + words.drop(1).joinToString("") { word ->
			|						word.replaceFirstChar { it.titlecase(Locale.ENGLISH) }
			|					}
			|				}
			|
			|				encoder.encodeString(value.name.camelCase())
			|			}
			|		}
			|	}
			|}
		""".trimMargin(),
		additionalImports = listOf(
			"kotlinx.serialization.KSerializer",
			"kotlinx.serialization.descriptors.PrimitiveKind",
			"kotlinx.serialization.descriptors.PrimitiveSerialDescriptor",
			"kotlinx.serialization.encoding.Decoder",
			"kotlinx.serialization.encoding.Encoder",
			"kotlinx.serialization.Serializable",
			"java.util.*"
		),
		suppresses = listOf("ClassName", "SERIALIZER_TYPE_INCOMPATIBLE"),
		additionalLines = arrayOf("")
	)
}
