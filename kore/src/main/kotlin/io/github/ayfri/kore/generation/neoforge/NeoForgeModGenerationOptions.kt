package io.github.ayfri.kore.generation.neoforge

import com.akuleshov7.ktoml.annotations.TomlInlineTable
import io.github.ayfri.kore.generation.DataPackGenerator
import io.github.ayfri.kore.generation.DataPackJarGenerationOptions
import io.github.ayfri.kore.generation.DataPackJarGenerationProvider
import io.github.ayfri.kore.generation.tomlSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString

@Serializable
data class NeoForgeModGenerationOptions(
	var dependencies: Map<String, List<NeoForgeDependency>>? = null,
	var issueTrackerURL: String? = null,
	var license: String = "All Rights Reserved",
	var loaderVersion: String = "[0,)",
	var modLoader: String = "lowcodefml",
	var mods: List<NeoForgeModProperties>? = null,
	@TomlInlineTable
	var properties: Map<String, String>? = null,
	var showAsResourcePack: Boolean = false,
	var showAsDataPack: Boolean = false,
	var services: List<String>? = null,
) : DataPackJarGenerationProvider() {
	override fun generateAdditionalFiles(generator: DataPackGenerator, options: DataPackJarGenerationOptions) {
		val tomlSerializer = tomlSerializer(generator)
		val toml = tomlSerializer.encodeToString(this)
		generator.writeFile("META-INF/neoforge.mods.toml", toml)
	}
}

fun NeoForgeModGenerationOptions.mod(modId: String, init: NeoForgeModProperties.() -> Unit) {
	mods = (mods ?: emptyList()) + NeoForgeModProperties(modId = modId).apply(init)
}

context(options: DataPackJarGenerationOptions)
fun NeoForgeModGenerationOptions.mod(modId: String = options.datapack.name, init: NeoForgeModProperties.() -> Unit) {
	mods = (mods ?: emptyList()) + NeoForgeModProperties(modId = modId).apply(init)
}

fun NeoForgeModGenerationOptions.properties(init: MutableMap<String, String>.() -> Unit) {
	properties = buildMap(init)
}

fun DataPackJarGenerationOptions.neoForge(init: NeoForgeModGenerationOptions.() -> Unit) {
	providers += NeoForgeModGenerationOptions().apply(init)
}
