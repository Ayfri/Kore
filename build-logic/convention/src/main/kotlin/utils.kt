import org.gradle.api.Project
import org.gradle.kotlin.dsl.extra
import java.io.File
import java.security.MessageDigest

/** A package, an annotation, the supertypes closing a constructor or a class-like declaration: the lines a class hierarchy is read from. */
private val declarationLine = Regex("""^\s*(?:package\s|@|\)\s*:)|(?:^|\s)(?:class|interface|object|typealias)\s+[\w`]""")

/**
 * Hashes the declaration lines of [sources] together with the identity of the [processors] reading them, so the result survives any
 * edit of a function or property body. It misses a supertype wrapped onto a line of its own, and a supertype whose import changes.
 */
fun declarationDigest(sources: Iterable<File>, processors: Iterable<File>): String {
	val digest = MessageDigest.getInstance("SHA-256")
	processors.sortedBy { it.path }.forEach { digest.update("${it.path}:${it.length()}:${it.lastModified()}".toByteArray()) }
	sources.sortedBy { it.path }.forEach { file ->
		digest.update(file.path.toByteArray())
		file.useLines { lines -> lines.filter(declarationLine::containsMatchIn).forEach { digest.update(it.toByteArray()) } }
	}
	return digest.digest().joinToString("") { "%02x".format(it) }
}

class ModuleMetadata {
	lateinit var name: String
	lateinit var description: String
}

fun Project.metadata(block: ModuleMetadata.() -> Unit) {
	val metadata = ModuleMetadata()
	block(metadata)

	extra.set("publicationName", metadata.name)
	extra.set("publicationDescription", metadata.description)
}
