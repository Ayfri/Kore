package io.github.ayfri.kore.generation.quilt

import kotlinx.serialization.Serializable

@Serializable
data class QuiltModProvider(
	var id: String,
	var version: String,
)

@Serializable
enum class QuiltLoadType {
	ALWAYS,
	IF_POSSIBLE,
	IF_REQUIRED,
}

@Serializable
data class QuiltLoader(
	var breaks: List<QuiltDependency>? = null,
	var depends: List<QuiltDependency>? = listOf(
		QuiltDependency(
			"quilt_resource_loader",
			versions = "*",
			unless = QuiltDependency("fabric-resource-loader-v0")
		)
	),
	var entrypoints: Map<String, String>? = null,
	var group: String,
	var id: String,
	var intermediateMappings: String? = "net.fabricmc:intermediary",
	var jars: List<String>? = null,
	var languageAdapters: Map<String, String>? = null,
	var loadType: QuiltLoadType? = null,
	var metadata: QuiltMetadata? = null,
	var plugins: List<String>? = null,
	var provides: List<QuiltModProvider>? = null,
	var repositories: List<String>? = null,
	var version: String,
)

fun QuiltLoader.breaks(vararg dependencies: QuiltDependency) {
	breaks = dependencies.toList()
}

fun QuiltLoader.breaks(id: String, init: QuiltDependency.() -> Unit = {}) {
	breaks = listOf(QuiltDependency(id).apply(init))
}

fun QuiltLoader.depends(vararg dependencies: QuiltDependency) {
	depends = dependencies.toList()
}

fun QuiltLoader.depends(id: String, init: QuiltDependency.() -> Unit = {}) {
	depends = listOf(QuiltDependency(id).apply(init))
}

fun QuiltLoader.entrypoints(init: MutableMap<String, String>.() -> Unit) {
	entrypoints = buildMap(init)
}

fun QuiltLoader.languageAdapters(init: MutableMap<String, String>.() -> Unit) {
	languageAdapters = buildMap(init)
}

fun QuiltLoader.metadata(init: QuiltMetadata.() -> Unit) {
	metadata = QuiltMetadata().apply(init)
}

fun QuiltLoader.plugins(vararg plugins: String) {
	this.plugins = plugins.toList()
}

fun QuiltLoader.provides(vararg providers: QuiltModProvider) {
	provides = providers.toList()
}

fun QuiltLoader.provides(id: String, version: String) {
	provides = listOf(QuiltModProvider(id, version))
}

fun QuiltLoader.repositories(vararg repositories: String) {
	this.repositories = repositories.toList()
}
