import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
	kotlin("jvm") apply false
	kotlin("multiplatform") apply false
	kotlin("plugin.serialization") apply false
	kotlin("plugin.compose") apply false
	alias(libs.plugins.vanniktech.publish) apply false
}

// Repo-wide: SERIALIZER_TYPE_INCOMPATIBLE fires on the intentional cross-type KSerializer shapes used
// throughout the codebase (e.g. NamespacedPolymorphicSerializer, generated Gamerules), so it's disabled globally.
subprojects {
	pluginManager.withPlugin("org.jetbrains.kotlin.plugin.serialization") {
		tasks.withType<KotlinCompilationTask<*>>().configureEach {
			compilerOptions.freeCompilerArgs.add("-Xwarning-level=SERIALIZER_TYPE_INCOMPATIBLE:disabled")
		}
	}
}
