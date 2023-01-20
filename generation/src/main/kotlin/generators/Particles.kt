package generators

import Serializer
import generateEnum
import getFromCacheOrDownloadTxt
import minecraftVersion
import url

suspend fun downloadParticles() {
	val url = url("data/registries/particle_type.txt")
	val particles = getFromCacheOrDownloadTxt("particles.txt", url).lines()

	generateParticlesEnum(particles, url)
}

fun generateParticlesEnum(particles: List<String>, sourceUrl: String) {
	val name = "Particles"
	generateEnum(
		name = name,
		sourceUrl = sourceUrl,
		additionalHeaders = listOf("Minecraft version: $minecraftVersion"),
		properties = particles.map { it.substringAfter("minecraft:").uppercase() },
		serializer = Serializer.Lowercase,
		customEncoder = """encoder.encodeString("minecraft:${"\${value.name.lowercase()}"}")""",
		additionalImports = listOf("arguments.Argument"),
		customLines = listOf(
			"override val namespace = \"minecraft\"",
			"",
			"override fun asString() = \"minecraft:\${name.lowercase()}\""
		),
		inheritances = listOf("Argument.Particle")
	)
}
