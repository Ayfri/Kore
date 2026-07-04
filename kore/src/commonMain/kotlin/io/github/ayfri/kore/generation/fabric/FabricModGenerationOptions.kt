package io.github.ayfri.kore.generation.fabric

import io.github.ayfri.kore.generation.DataPackGenerator
import io.github.ayfri.kore.generation.DataPackJarGenerationOptions
import io.github.ayfri.kore.generation.DataPackJarGenerationProvider
import io.github.ayfri.kore.serializers.InlinableList
import io.github.ayfri.kore.serializers.PathSerializer
import io.github.ayfri.kore.utils.absolute
import io.github.ayfri.kore.utils.exists
import io.github.ayfri.kore.utils.readText
import io.github.ayfri.kore.utils.warn
import kotlinx.io.files.Path
import kotlinx.serialization.Serializable

@Serializable
data class FabricModGenerationOptions(
	var authors: List<FabricAuthor>? = null,
	var contact: FabricContact? = null,
	var contributors: List<FabricAuthor>? = null,
	var depends: Map<String, String>? = mapOf("fabric-resource-loader-v0" to "*"),
	var description: String? = null,
	var environment: String = "*",
	var icon: @Serializable(PathSerializer::class) Path? = null,
	var id: String,
	var license: InlinableList<String>? = null,
	var name: String? = null,
	var schemaVersion: Int = 1,
	var version: String,
) : DataPackJarGenerationProvider() {
	override fun generateAdditionalFiles(generator: DataPackGenerator, options: DataPackJarGenerationOptions) {
		val fabricOptions = options.providers.filterIsInstance<FabricModGenerationOptions>()
		if (fabricOptions.isEmpty()) return

		fabricOptions.forEach { fabricFileOptions ->
			val json = generator.datapack.jsonEncoder.encodeToString(fabricFileOptions)
			generator.writeFile("fabric.mod.json", json)
		}
	}
}

fun FabricModGenerationOptions.authors(vararg authors: FabricAuthor) {
	this.authors = authors.toList()
}

fun FabricModGenerationOptions.author(name: String, init: (FabricContact.() -> Unit)? = null) {
	authors = listOf(FabricAuthor(name, init?.let(FabricContact()::apply)))
}

fun FabricModGenerationOptions.contact(init: FabricContact.() -> Unit) {
	contact = FabricContact().apply(init)
}

fun FabricModGenerationOptions.contributors(vararg contributors: FabricAuthor) {
	this.contributors = contributors.toList()
}

fun FabricModGenerationOptions.contributor(name: String, init: FabricContact.() -> Unit = {}) {
	contributors = listOf(FabricAuthor(name, FabricContact().apply(init)))
}

fun FabricModGenerationOptions.depends(vararg dependencies: Pair<String, String>) {
	depends = dependencies.toMap()
}

fun FabricModGenerationOptions.icon(path: Path) {
	if (!path.exists()) warn("Icon file '${path.absolute()}' not found.")

	icon = path
}

fun FabricModGenerationOptions.license(vararg licences: String) {
	license = licences.toList()
}

fun FabricModGenerationOptions.license(path: Path) {
	if (!path.exists()) warn("License file '${path.absolute()}' not found.")

	license = listOf(path.readText())
}

fun DataPackJarGenerationOptions.fabric(init: FabricModGenerationOptions.() -> Unit = {}) {
	providers += FabricModGenerationOptions(
		id = datapack.name,
		version = "0.0.1",
		name = datapack.name,
		icon = datapack.iconPath,
	).apply(init)
}
