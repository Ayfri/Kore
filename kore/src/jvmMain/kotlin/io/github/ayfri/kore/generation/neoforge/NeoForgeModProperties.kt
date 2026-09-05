package io.github.ayfri.kore.generation.neoforge

import com.akuleshov7.ktoml.annotations.TomlInlineTable
import kotlinx.serialization.Serializable
import java.nio.file.Path

@Serializable
data class NeoForgeModFeatures(
	var javaVersion: String? = null,
	var openGLVersion: String? = null,
)

@Serializable
data class NeoForgeModProperties(
	var authors: String? = null,
	var credits: String? = null,
	var description: String? = null,
	var displayName: String? = null,
	var displayURL: String? = null,
	var enumExtensions: String? = null,
	@TomlInlineTable
	var features: NeoForgeModFeatures? = null,
	var logoBlur: Boolean? = null,
	var logoFile: Path? = null,
	var modId: String,
	@TomlInlineTable
	var modProperties: Map<String, String>? = null,
	var modUrl: String? = null,
	var namespace: String? = null,
	var updateJSONURL: String? = null,
	var version: String? = null,
)

fun NeoForgeModProperties.features(init: NeoForgeModFeatures.() -> Unit) {
	features = NeoForgeModFeatures().apply(init)
}

fun NeoForgeModProperties.modProperties(init: MutableMap<String, String>.() -> Unit) {
	modProperties = buildMap(init)
}
