package io.github.ayfri.kore.generation.forge

import com.akuleshov7.ktoml.annotations.TomlInlineTable
import io.github.ayfri.kore.generation.DataPackGenerator
import io.github.ayfri.kore.generation.DataPackJarGenerationOptions
import io.github.ayfri.kore.generation.DataPackJarGenerationProvider
import io.github.ayfri.kore.generation.tomlSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString

@Serializable
data class ForgeModGenerationOptions(
	var dependencies: Map<String, List<ForgeDependency>>? = null,
	var issueTrackerURL: String? = null,
	var license: String = "All Rights Reserved",
	var loaderVersion: String = "[0,)",
	var modLoader: String = "lowcodefml",
	var mods: List<ForgeModProperties>? = null,
	@TomlInlineTable
	var properties: Map<String, String>? = null,
	var showAsResourcePack: Boolean = false,
) : DataPackJarGenerationProvider() {
	override fun generateAdditionalFiles(generator: DataPackGenerator, options: DataPackJarGenerationOptions) {
		val tomlSerializer = tomlSerializer(generator)
		val toml = tomlSerializer.encodeToString(this)
		generator.writeFile("META-INF/mods.toml", toml)
	}
}

fun ForgeModGenerationOptions.mod(modId: String, init: ForgeModProperties.() -> Unit) {
	mods = (mods ?: emptyList()) + ForgeModProperties(modId = modId).apply(init)
}

context(options: DataPackJarGenerationOptions)
fun ForgeModGenerationOptions.mod(modId: String = options.datapack.name, init: ForgeModProperties.() -> Unit) {
	mods = (mods ?: emptyList()) + ForgeModProperties(modId = modId).apply(init)
}

fun ForgeModGenerationOptions.properties(init: MutableMap<String, String>.() -> Unit) {
	properties = buildMap(init)
}

fun DataPackJarGenerationOptions.forge(init: ForgeModGenerationOptions.() -> Unit) {
	providers += ForgeModGenerationOptions().apply(init)
}
