import com.google.devtools.ksp.gradle.KspAATask
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.targets.js.ir.KotlinJsIrTarget
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

// SERIALIZER_TYPE_INCOMPATIBLE fires on the intentional cross-type KSerializer shapes used throughout the codebase
// (e.g. NamespacedPolymorphicSerializer, generated Gamerules), so it's disabled everywhere.
pluginManager.withPlugin("org.jetbrains.kotlin.plugin.serialization") {
	tasks.withType<KotlinCompilationTask<*>>().configureEach {
		compilerOptions.freeCompilerArgs.add("-Xwarning-level=SERIALIZER_TYPE_INCOMPATIBLE:disabled")
	}
}

// KSP builds a full analysis session (~3s on `kore`) even for a compilation without processors, like most `jvmTest` ones.
pluginManager.withPlugin("com.google.devtools.ksp") {
	tasks.withType<KspAATask>().configureEach {
		val processors = kspConfig.processorClasspath
		onlyIf("it has symbol processors") { !processors.isEmpty }
	}
}

pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") {
	extensions.configure<KotlinMultiplatformExtension> {
		// The Kotlin plugin also lists the module's own jar as a friend of its JVM tests, so every test compile rebuilds that jar.
		// The tests only see their module as class folders. A dependency that puts a module's own jar back on its test classpath
		// needs the jar listed here again, else the tests lose `internal` access (INVISIBLE_REFERENCE).
		targets.withType<KotlinJvmTarget>().configureEach {
			val mainClasses = compilations.named("main").map { it.output.classesDirs }
			compilations.named("test") {
				compileTaskProvider.configure {
					(this as KotlinJvmCompile).friendPaths.setFrom(mainClasses)
				}
			}
		}

		targets.withType<KotlinJsIrTarget>().configureEach {
			// npm workspace versions land in kotlin-js-store/package-lock.json, so pinning them keeps Kore/Minecraft version bumps out of the lock file.
			compilations.configureEach {
				packageJson {
					version = "0.0.0"
				}
			}
		}
	}
}
