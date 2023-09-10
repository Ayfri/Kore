import gradle.kotlin.dsl.accessors._b6bea14fb88fd11e46d6fb1ebe601eab.ext
import org.gradle.api.Project

class ModuleMetadata {
	lateinit var name: String
	lateinit var description: String
}

fun Project.metadata(block: ModuleMetadata.() -> Unit) {
	val metadata = ModuleMetadata().apply(block)
	ext["publication-name"] = metadata.name
	ext["publication-description"] = metadata.description
}
