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

	// The `kore-ksp` processors only read class declarations, while KSP rebuilds its analysis session of the whole module for any
	// edited file. The declaration scan is textual: when a generated dispatcher stops compiling after a class changed parents,
	// it missed that change, and `-Pkore.ksp` forces the run.
	tasks.withType<KspAATask>().matching { it.name == "kspCommonMainKotlinMetadata" }.configureEach {
		val digestFile = layout.buildDirectory.file("kspDeclarations/$name.sha256")
		val forced = providers.gradleProperty("kore.ksp").isPresent
		val generated = kspConfig.kotlinOutputDir.asFileTree
		val processors = kspConfig.processorClasspath
		val sources = (kspConfig.sourceRoots + kspConfig.commonSourceRoots).asFileTree.matching { include("**/*.kt") }

		outputs.file(digestFile).withPropertyName("declarationDigest")
		onlyIf("a class declaration changed since its last run") {
			val previous = digestFile.get().asFile
			forced || generated.isEmpty || !previous.exists() || previous.readText() != declarationDigest(sources, processors)
		}
		doLast {
			digestFile.get().asFile.writeText(declarationDigest(sources, processors))
		}
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
			// Kotlin 2.4 inlines same-module calls while writing the klib, but an incremental compile doesn't recompile the callers
			// of an inline function whose body changed, so they keep the old one. Left to the link step, like cross-module calls,
			// inlining always sees the current body: this is what keeps JS incremental compilation correct.
			compilerOptions {
				freeCompilerArgs.add("-Xklib-ir-inliner=disabled")
			}

			// npm workspace versions land in kotlin-js-store/package-lock.json, so pinning them keeps Kore/Minecraft version bumps out of the lock file.
			compilations.configureEach {
				packageJson {
					version = "0.0.0"
				}
			}
		}
	}
}
