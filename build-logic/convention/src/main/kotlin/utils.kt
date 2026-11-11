import com.google.devtools.ksp.gradle.KspAATask
import org.gradle.api.Project
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.withType
import java.io.File
import java.security.MessageDigest

/** A package, an annotation, the supertypes closing a constructor or a class-like declaration: the lines a class hierarchy is read from. */
private val declarationLine = Regex("""^\s*(?:package\s|@|\)\s*:)|(?:^|\s)(?:class|interface|object|typealias)\s+[\w`]""")

/**
 * Hashes the declaration lines of [sources] together with the identity of the [processors] reading them, so the result survives any
 * edit of a function or property body. A top-level annotation brings the line it marks along, the signature of an annotated function.
 * It misses a supertype or a parameter wrapped onto a line of its own, and a supertype whose import changes.
 */
fun declarationDigest(sources: Iterable<File>, processors: Iterable<File>): String {
	val digest = MessageDigest.getInstance("SHA-256")
	processors.sortedBy { it.path }.forEach { digest.update("${it.path}:${it.length()}:${it.lastModified()}".toByteArray()) }
	sources.sortedBy { it.path }.parallelStream().map { file ->
		buildString {
			append(file.path)
			var annotated = false
			file.forEachLine { line ->
				if (annotated || declarationLine.containsMatchIn(line)) append(line)
				annotated = line.startsWith("@")
			}
		}
	}.forEachOrdered { digest.update(it.toByteArray()) }
	return digest.digest().joinToString("") { "%02x".format(it) }
}

/**
 * Skips the KSP task [taskName] while [declarationDigest] is unchanged, for processors that read declarations and never a body: KSP
 * rebuilds its analysis session of the whole module for any edited file. When the textual scan misses a change, the code generated
 * from the stale output stops compiling, and `-Pkore.ksp` forces the run.
 */
fun Project.skipKspUntilDeclarationsChange(taskName: String) {
	tasks.withType<KspAATask>().matching { it.name == taskName }.configureEach {
		val digestFile = layout.buildDirectory.file("kspDeclarations/$name.sha256")
		val forced = providers.gradleProperty("kore.ksp").isPresent
		val generated = kspConfig.outputBaseDir.asFileTree
		val processors = kspConfig.processorClasspath
		val sources = (kspConfig.sourceRoots + kspConfig.commonSourceRoots).asFileTree.matching { include("**/*.kt") }

		outputs.file(digestFile).withPropertyName("declarationDigest")
		onlyIf("a declaration changed since its last run") {
			val previous = digestFile.get().asFile
			forced || generated.isEmpty || !previous.exists() || previous.readText() != declarationDigest(sources, processors)
		}
		doLast {
			digestFile.get().asFile.writeText(declarationDigest(sources, processors))
		}
	}
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
