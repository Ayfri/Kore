import org.gradle.api.Project
import org.gradle.kotlin.dsl.extra

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

fun Project.mainProjectProperty(name: String) = rootProject.extra[name] as String
