package io.github.ayfri.kore.generation.quilt

import io.github.ayfri.kore.generation.DataPackGenerator
import io.github.ayfri.kore.generation.DataPackJarGenerationOptions
import io.github.ayfri.kore.generation.DataPackJarGenerationProvider
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString

@Serializable
data class QuiltModGenerationOptions(
	var schemaVersion: Int = 1,
	var loader: QuiltLoader,
) : DataPackJarGenerationProvider() {
	override fun generateAdditionalFiles(generator: DataPackGenerator, options: DataPackJarGenerationOptions) {
		val quiltOptions = options.providers.filterIsInstance<QuiltModGenerationOptions>()
		if (quiltOptions.isEmpty()) return

		quiltOptions.forEach { quiltFileOptions ->
			val json = generator.datapack.jsonEncoder.encodeToString(quiltFileOptions)
			generator.writeFile("quilt.mod.json", json)
		}
	}
}

fun DataPackJarGenerationOptions.quilt(group: String, init: QuiltLoader.() -> Unit) {
	providers += QuiltModGenerationOptions(loader = QuiltLoader(group = group, id = datapack.name, version = "0.0.1").apply(init))
}
