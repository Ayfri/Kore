package io.github.ayfri.kore.gradle.tasks

import io.github.ayfri.kore.gradle.internal.AotLauncher
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.jvm.toolchain.JavaLauncher
import org.gradle.process.ExecOperations
import org.gradle.work.DisableCachingByDefault
import java.io.File
import java.util.jar.JarEntry
import java.util.jar.JarOutputStream
import java.util.zip.CRC32
import javax.inject.Inject

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

	@get:Input
	abstract val aotCache: Property<Boolean>

	@get:Internal
	abstract val aotCacheDirectory: DirectoryProperty

	/** Used to refuse wiping a [generatedDirectory] that contains the project itself. */
	@get:Internal
	abstract val projectDirectory: DirectoryProperty

	@get:Inject
	protected abstract val execOperations: ExecOperations

	override fun exec() {
		check(!classpath.isEmpty) {
			"The `kore` runtime classpath is empty. Apply the `java` or `kotlin(\"jvm\")` plugin, or set " +
				"`kore.runtimeClasspath` explicitly (Kotlin Multiplatform projects need the latter)."
		}

		if (cleanBeforeBuild.get()) {
			val output = generatedDirectory.get().asFile.toPath().toAbsolutePath().normalize()
			check(!projectDirectory.get().asFile.toPath().toAbsolutePath().normalize().startsWith(output)) {
				"Refusing to wipe $output before the build since it contains the project, point `kore.outputDirectory` at a dedicated folder."
			}
			output.toFile().deleteRecursively()
		}

		val launcher = javaLauncher.orNull
		if (aotCache.get() && launcher != null && launcher.metadata.languageVersion.canCompileOrRun(25)) execWithAotCache(launcher)
		else super.exec()
	}

	/**
	 * Runs on a JDK AOT cache trained by the first run, so Kore and its dependencies start already loaded and linked,
	 * ~3x faster for a small pack. The cache only covers jars: the compiled sources go through [AotLauncher], and the
	 * cache file is keyed on the jars and the JDK so a dependency bump trains a new one.
	 *
	 * Forks through [execOperations] because Gradle ignores changes to the task's own classpath or main class once it runs.
	 */
	private fun execWithAotCache(launcher: JavaLauncher) {
		val directories = classpath.filter(File::isDirectory).files
		val cacheDirectory = aotCacheDirectory.get().asFile.apply { mkdirs() }
		val launcherJar = launcherJar(cacheDirectory)
		val jars = classpath.filter(File::isFile).files + launcherJar

		val key = (jars.map { "${it.absolutePath}|${it.length()}|${it.lastModified()}" } + launcher.metadata.javaRuntimeVersion).hashCode()
		val cache = cacheDirectory.resolve("kore-%08x.aot".format(key))
		cacheDirectory.listFiles { file -> file != cache && file != launcherJar }?.forEach(File::delete)

		execOperations.javaexec { spec ->
			copyTo(spec)
			spec.executable = launcher.executablePath.asFile.absolutePath
			spec.classpath(jars)
			spec.mainClass.set(AotLauncher::class.java.name)
			spec.args(listOf(directories.joinToString(File.pathSeparator), mainClass.get()) + args.orEmpty())
			spec.argumentProviders.addAll(argumentProviders)
			spec.jvmArgs(if (cache.exists()) "-XX:AOTCache=$cache" else "-XX:AOTCacheOutput=$cache", "-Xlog:aot=warning")
			spec.isIgnoreExitValue = isIgnoreExitValue
		}
	}

	/** Packs [AotLauncher] into its own jar, named after its bytecode so a plugin update does not reuse a stale one. */
	private fun launcherJar(directory: File): File {
		val path = AotLauncher::class.java.name.replace('.', '/') + ".class"
		val bytes = AotLauncher::class.java.classLoader.getResourceAsStream(path)!!.use { it.readBytes() }
		val jar = directory.resolve("launcher-%08x.jar".format(CRC32().apply { update(bytes) }.value))
		if (jar.exists()) return jar

		JarOutputStream(jar.outputStream()).use {
			it.putNextEntry(JarEntry(path))
			it.write(bytes)
		}
		return jar
	}
}
