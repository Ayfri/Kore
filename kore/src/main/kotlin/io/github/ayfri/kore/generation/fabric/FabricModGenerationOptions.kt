package io.github.ayfri.kore.generation.fabric

import io.github.ayfri.kore.generation.DataPackGenerator
import io.github.ayfri.kore.generation.DataPackJarGenerationOptions
import io.github.ayfri.kore.generation.DataPackJarGenerationProvider
import io.github.ayfri.kore.serializers.InlinableList
import io.github.ayfri.kore.utils.warn
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import java.nio.file.Path

@Serializable
data class FabricModGenerationOptions(
	var authors: List<Author>? = null,
	var contact: Contact? = null,
	var contributors: List<Author>? = null,
	var depends: Map<String, String> = mapOf("fabric-resource-loader-v0" to "*"),
	var description: String? = null,
	var environment: String = "*",
	var icon: Path? = null,
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

fun FabricModGenerationOptions.authors(vararg authors: Author) {
	this.authors = authors.toList()
}

fun FabricModGenerationOptions.author(name: String, init: (Contact.() -> Unit)? = null) {
	authors = listOf(Author(name, init?.let(Contact()::apply)))
}

fun FabricModGenerationOptions.contact(init: Contact.() -> Unit) {
	contact = Contact().apply(init)
}

fun FabricModGenerationOptions.contributors(vararg contributors: Author) {
	this.contributors = contributors.toList()
}

fun FabricModGenerationOptions.contributor(name: String, init: Contact.() -> Unit = {}) {
	contributors = listOf(Author(name, Contact().apply(init)))
}

fun FabricModGenerationOptions.depends(vararg dependencies: Pair<String, String>) {
	depends = dependencies.toMap()
}

fun FabricModGenerationOptions.icon(path: Path) {
	if (!path.toFile().exists()) warn("Icon file '${path.toFile().absolutePath}' not found.")

	icon = path
}

fun FabricModGenerationOptions.license(vararg licences: String) {
	license = licences.toList()
}

fun FabricModGenerationOptions.license(path: Path) {
	if (!path.toFile().exists()) warn("License file '${path.toFile().absolutePath}' not found.")

	license = listOf(path.toFile().readText())
}

fun DataPackJarGenerationOptions.fabric(init: FabricModGenerationOptions.() -> Unit = {}) {
	providers += FabricModGenerationOptions(
		id = datapack.name,
		version = "0.0.1",
		name = datapack.name,
		icon = datapack.iconPath,
	).apply(init)
}
