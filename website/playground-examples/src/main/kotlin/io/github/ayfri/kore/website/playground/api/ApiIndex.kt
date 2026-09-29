package io.github.ayfri.kore.website.playground.api

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File

/**
 * One parameter as a signature shows it, types rendered with simple names.
 *
 * [default] is the default value's source text, `…` when too long to read in a popup.
 */
@Serializable
data class ApiParameter(
	val name: String,
	val type: String,
	val default: String? = null,
	val doc: String? = null,
	val vararg: Boolean = false,
)

/**
 * A public declaration of kore, oop or helpers, as the playground completes, documents and imports it.
 *
 * [owner] is the package of a top-level declaration and the class of a [member]. Fully qualified names ([receiver],
 * [context], [returns], [lambda], [supertypes]) drive the editor's receiver matching, display strings ([receiverType],
 * [type], [typeParameters], [params]) its signatures. [lambda] is the receiver of a trailing `T.() -> R` parameter,
 * what `this` becomes inside the block.
 */
@Serializable
data class ApiDeclaration(
	val name: String,
	val kind: String,
	val owner: String,
	val member: Boolean = false,
	val receiver: String? = null,
	val receiverType: String? = null,
	val context: List<String> = emptyList(),
	val returns: String? = null,
	val type: String? = null,
	val lambda: String? = null,
	val typeParameters: String? = null,
	val modifiers: List<String> = emptyList(),
	val params: List<ApiParameter>? = null,
	val constructors: List<List<ApiParameter>> = emptyList(),
	val supertypes: List<String> = emptyList(),
	val entries: List<String> = emptyList(),
	val companion: String? = null,
	val doc: String? = null,
	val deprecated: Boolean = false,
	val source: String? = null,
)

@Serializable
data class ApiIndex(val declarations: List<ApiDeclaration>)

/**
 * Writes the playground's API index, given the output file, the repository root, the common source roots relative to
 * it (comma-separated) and the compiled libraries (path-separated).
 */
fun main(args: Array<String>) {
	val (output, repository, roots, libraries) = args
	val sources = SourceIndex(File(repository), roots.split(','))
	val declarations = MetadataIndex(libraries.split(File.pathSeparator).map(::File), sources).declarations
		.sortedWith(compareBy({ it.owner }, { it.name }, { it.receiver }))

	val file = File(output).apply { parentFile.mkdirs() }
	file.writeText(Json.encodeToString(ApiIndex(declarations)))
	println("Indexed ${declarations.size} declarations (${declarations.sumOf { it.entries.size }} enum entries) into $file, ${file.length() / 1024} kB")
}
