package io.github.ayfri.kore.gradle.tasks

import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.work.DisableCachingByDefault

/**
 * Runs the datapack entry point. The output directory is wiped first when [cleanBeforeBuild] is set, so resources
 * removed from the Kotlin sources disappear from the generated pack instead of lingering.
 */
@DisableCachingByDefault(because = "The entry point is arbitrary user code, so its inputs cannot be fully declared.")
abstract class KoreBuildTask : JavaExec() {
	@get:Input
	abstract val cleanBeforeBuild: Property<Boolean>

	@get:OutputDirectory
	abstract val generatedDirectory: DirectoryProperty

	@get:InputFiles
	@get:PathSensitive(PathSensitivity.RELATIVE)
	abstract val additionalInputs: ConfigurableFileCollection

	override fun exec() {
		check(!classpath.isEmpty) {
			"The `kore` runtime classpath is empty. Apply the `java` or `kotlin(\"jvm\")` plugin, or set " +
				"`kore.runtimeClasspath` explicitly (Kotlin Multiplatform projects need the latter)."
		}

		if (cleanBeforeBuild.get()) generatedDirectory.get().asFile.deleteRecursively()

		super.exec()
	}
}
