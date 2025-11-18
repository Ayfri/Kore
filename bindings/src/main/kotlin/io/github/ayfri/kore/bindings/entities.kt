package io.github.ayfri.kore.bindings

import io.github.ayfri.kore.pack.PackMCMeta
import java.nio.file.Path

data class Function(
	val id: String,
	val macroArguments: List<String>,
)

data class Resource(
	val id: String,
	val type: String,
)

data class Datapack(
	val name: String,
	val path: Path,
	val functions: List<Function> = emptyList(),
	val resources: Map<String, List<Resource>> = emptyMap(),
	val features: Map<String, List<String>> = emptyMap(),
	val pack: PackMCMeta? = null,
)
