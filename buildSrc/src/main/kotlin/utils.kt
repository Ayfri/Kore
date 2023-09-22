import gradle.kotlin.dsl.accessors._81caac640344b8dd7f3b85b1b528aa4b.ext
import org.gradle.api.Project
import org.gradle.kotlin.dsl.extra

class ModuleMetadata {
	lateinit var name: String
	lateinit var description: String
}

fun Project.metadata(block: ModuleMetadata.() -> Unit) {
	val metadata = ModuleMetadata()
	block(metadata)

	ext.set("publicationName", metadata.name)
	ext.set("publicationDescription", metadata.description)
}

fun Project.mainProjectProperty(name: String) = rootProject.extra[name] as String
