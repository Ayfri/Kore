package io.github.ayfri.kore.generation.forge

import com.akuleshov7.ktoml.annotations.TomlInlineTable
import kotlinx.serialization.Serializable
import java.nio.file.Path

@Serializable
enum class DisplayType {
	MATCH_VERSION,
	IGNORE_SERVER_VERSION,
	IGNORE_ALL_VERSION,
	NONE,
}

@Serializable
data class ForgeModProperties(
	var authors: String? = null,
	var credits: String? = null,
	var description: String? = null,
	var displayName: String? = null,
	var displayTest: DisplayType? = null,
	var displayURL: String? = null,
	var logoBlur: Boolean? = null,
	var logoFile: Path? = null,
	var modId: String,
	@TomlInlineTable
	var modProperties: Map<String, String>? = null,
	var namespace: String? = null,
	var updateJSONURL: String? = null,
	var version: String? = null,
)

fun ForgeModProperties.modProperties(init: MutableMap<String, String>.() -> Unit) {
	modProperties = buildMap(init)
}
