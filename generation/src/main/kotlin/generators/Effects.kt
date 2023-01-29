package generators

import generateEnum
import getFromCacheOrDownloadTxt
import url

suspend fun downloadEffects() {
	val url = url("data/registries/mob_effect.txt")
	val effects = getFromCacheOrDownloadTxt("effects.txt", url).lines()

	generateEffectsEnum(effects, url)
}

fun generateEffectsEnum(effects: List<String>, sourceUrl: String) {
	generateEnum(effects, "Effects", sourceUrl, "MobEffect")
	/*
		val name = "Effects"
		generateEnum(
			name = name,
			sourceUrl = sourceUrl,
			additionalHeaders = listOf("Minecraft version: $minecraftVersion"),
			properties = effects.map { it.substringAfter("minecraft:").uppercase() },
			serializer = Serializer.Lowercase,
			customEncoder = """encoder.encodeString("minecraft:${"\${value.name.lowercase()}"}")""",
			additionalImports = listOf("arguments.Argument"),
			customLines = listOf(
				"override val namespace = \"minecraft\"",
				"",
				"override fun asString() = \"minecraft:\${name.lowercase()}\""
			),
			inheritances = listOf("Argument.MobEffect"),
		) */
}
