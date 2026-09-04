package io.github.ayfri.kore.gradle

import io.github.ayfri.kore.gradle.internal.MinecraftLocator
import io.github.ayfri.kore.gradle.internal.WorldsValueSource
import io.github.ayfri.kore.gradle.tasks.KoreBuildTask
import io.github.ayfri.kore.gradle.tasks.KoreCleanTask
import io.github.ayfri.kore.gradle.tasks.KoreLinkTask
import io.github.ayfri.kore.gradle.tasks.KoreReloadTask
import io.github.ayfri.kore.gradle.tasks.KoreWorldsTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskProvider
import java.io.File

/**
 * Registers the `kore { }` extension and the `kore` task group.
 *
 * `gradlew koreRun --continuous` is the intended development loop: regenerate, link into every configured world and
 * reload the running server on every source change.
 *
 * Written against the plain Gradle API rather than the Kotlin DSL helpers, so the plugin does not pin consumers to
 * the Kotlin version embedded in their Gradle distribution.
 */
class KorePlugin : Plugin<Project> {
	override fun apply(target: Project) {
		val kore = target.extensions.create("kore", KoreExtension::class.java)
		applyConventions(target, kore)

		val minecraftDirectory = kore.minecraftDirectory.map { it.asFile }
		val dataPackTargets = dataPackTargets(target, kore, minecraftDirectory)
		val resourcePackTargets = resourcePackTargets(kore, minecraftDirectory)
		val hasResourcePack = kore.resourcePackName.map(String::isNotEmpty).orElse(false)

		val build = registerBuildTask(target, kore)

		val linkDataPack = target.tasks.register("koreLinkDataPack", KoreLinkTask::class.java) { task ->
			task.group = GROUP
			task.description = "Copies the generated datapack into every configured world."
			task.dependsOn(build)
			task.source.set(kore.outputDirectory.dir(kore.packName))
			task.destinations.from(destinations(dataPackTargets, kore.packName))
			task.linkMode.set(kore.linkMode)
		}

		val linkResourcePack = target.tasks.register("koreLinkResourcePack", KoreLinkTask::class.java) { task ->
			task.group = GROUP
			task.description = "Copies the generated resource pack into every configured resourcepacks folder."
			task.dependsOn(build)
			task.onlyIf { hasResourcePack.get() }
			task.source.set(kore.outputDirectory.dir(kore.resourcePackName.orElse(kore.packName)))
			task.destinations.from(destinations(resourcePackTargets, kore.resourcePackName.orElse(kore.packName)))
			task.linkMode.set(kore.linkMode)
		}

		val link = target.tasks.register("koreLink") { task ->
			task.group = GROUP
			task.description = "Links every generated pack into its targets."
			task.dependsOn(linkDataPack, linkResourcePack)
		}

		val reload = target.tasks.register("koreReload", KoreReloadTask::class.java) { task ->
			task.group = GROUP
			task.description = "Sends a command to a running server over RCON, `reload` by default."
			task.mustRunAfter(linkDataPack, linkResourcePack)
			task.rconEnabled.set(kore.rcon.enabled)
			task.host.set(kore.rcon.host)
			task.port.set(kore.rcon.port)
			task.password.set(kore.rcon.password)
			task.command.set(kore.rcon.command)
			task.timeoutMillis.set(kore.rcon.timeoutMillis)
			task.failOnError.set(kore.rcon.failOnError)
		}

		target.tasks.register("koreRun") { task ->
			task.group = GROUP
			task.description = "Builds, links and reloads. Add --continuous for a watch loop."
			task.dependsOn(link, reload)
		}

		target.tasks.register("koreClean", KoreCleanTask::class.java) { task ->
			task.group = GROUP
			task.description = "Deletes the generated packs and unlinks them from every target."
			task.outputDirectory.set(kore.outputDirectory)
			task.linkedPacks.set(linkedPacks(kore, dataPackTargets, resourcePackTargets, hasResourcePack))
		}

		target.tasks.register("koreWorlds", KoreWorldsTask::class.java) { task ->
			task.group = GROUP
			task.description = "Lists the worlds found in the configured Minecraft directory."
			task.minecraftDirectory.set(kore.minecraftDirectory)
		}
	}

	private fun applyConventions(project: Project, kore: KoreExtension) {
		kore.packName.convention(project.name)
		kore.outputDirectory.convention(project.layout.buildDirectory.dir("kore"))
		kore.cleanBeforeBuild.convention(true)
		kore.linkToAllWorlds.convention(false)
		kore.linkMode.convention(LinkMode.COPY)
		kore.minecraftDirectory.convention(project.layout.dir(defaultMinecraftDirectory(project)))

		// The classpath stays overridable: Kotlin Multiplatform has no `main` source set, so those projects set it themselves.
		project.pluginManager.withPlugin("java") {
			val sourceSets = project.extensions.getByType(SourceSetContainer::class.java)
			kore.runtimeClasspath.convention(project.files(sourceSets.named("main").map { it.runtimeClasspath }))
		}

		val rcon = kore.rcon
		rcon.host.convention("localhost")
		rcon.port.convention(25575)
		rcon.password.convention(project.providers.environmentVariable(PASSWORD_ENVIRONMENT_VARIABLE))
		rcon.command.convention("reload")
		rcon.timeoutMillis.convention(5_000)
		rcon.failOnError.convention(false)
		rcon.enabled.convention(true)
	}

	private fun registerBuildTask(project: Project, kore: KoreExtension): TaskProvider<KoreBuildTask> =
		project.tasks.register("koreBuild", KoreBuildTask::class.java) { task ->
			task.group = GROUP
			task.description = "Runs the datapack entry point and generates the packs."
			task.mainClass.set(kore.mainClass)
			task.classpath = kore.runtimeClasspath
			task.generatedDirectory.set(kore.outputDirectory)
			task.cleanBeforeBuild.set(kore.cleanBeforeBuild)
			task.additionalInputs.from(kore.additionalInputs)
			task.jvmArgs = kore.jvmArguments.getOrElse(emptyList())

			// Passed both ways so an entry point can read `args[0]` or the system property, whichever fits its `main`.
			// Plain values rather than an `argumentProviders` lambda: Gradle cannot track a lambda's implementation,
			// which would leave the task permanently out of date.
			val outputPath = kore.outputDirectory.map { it.asFile.absolutePath }
			task.args = listOf(outputPath.get()) + kore.arguments.getOrElse(emptyList())
			task.systemProperties(
				kore.systemProperties.getOrElse(emptyMap()) + mapOf(
					OUTPUT_SYSTEM_PROPERTY to outputPath.get(),
					PACK_NAME_SYSTEM_PROPERTY to kore.packName.get(),
				)
			)
		}

	private fun dataPackTargets(
		project: Project,
		kore: KoreExtension,
		minecraftDirectory: Provider<File>,
	): Provider<List<String>> {
		val discoveredWorlds = minecraftDirectory.flatMap { directory ->
			project.providers.of(WorldsValueSource::class.java) {
				it.parameters.minecraftDirectory.set(directory.absolutePath)
			}
		}

		return minecraftDirectory.zip(discoveredWorlds) { directory, discovered ->
			val worlds = kore.worlds.getOrElse(emptyList()) + if (kore.linkToAllWorlds.get()) discovered else emptyList()
			worlds.distinct().map { MinecraftLocator.dataPacksDirectory(directory, it).absolutePath } +
				kore.dataPackTargets.getOrElse(emptyList())
		}
	}

	private fun resourcePackTargets(kore: KoreExtension, minecraftDirectory: Provider<File>): Provider<List<String>> =
		minecraftDirectory.map { directory ->
			kore.resourcePackTargets.getOrElse(emptyList())
				.ifEmpty { listOf(MinecraftLocator.resourcePacksDirectory(directory).absolutePath) }
		}

	/** Maps each target folder to the pack folder it receives. */
	private fun destinations(targets: Provider<List<String>>, packName: Provider<String>): Provider<List<File>> =
		targets.zip(packName) { folders, name -> folders.map { File(it, name) } }

	private fun linkedPacks(
		kore: KoreExtension,
		dataPackTargets: Provider<List<String>>,
		resourcePackTargets: Provider<List<String>>,
		hasResourcePack: Provider<Boolean>,
	): Provider<List<String>> {
		val dataPacks = destinations(dataPackTargets, kore.packName)
		val resourcePacks = destinations(resourcePackTargets, kore.resourcePackName.orElse(kore.packName))

		return dataPacks.zip(resourcePacks) { linkedDataPacks, linkedResourcePacks ->
			val paths = linkedDataPacks.map(File::getAbsolutePath)
			if (!hasResourcePack.get()) paths else paths + linkedResourcePacks.map(File::getAbsolutePath)
		}
	}

	/** Resolves the Minecraft directory through providers, so the configuration cache tracks the values it reads. */
	private fun defaultMinecraftDirectory(project: Project): Provider<File> {
		val providers = project.providers
		val environmentDirectory = providers.environmentVariable(MinecraftLocator.DIRECTORY_ENVIRONMENT_VARIABLE)
		val appData = providers.environmentVariable("APPDATA").orElse("")
		val osName = providers.systemProperty("os.name")
		val userHome = providers.systemProperty("user.home")

		val platformDirectory = osName.zip(userHome, ::Pair).zip(appData) { (os, home), appDataPath ->
			MinecraftLocator.defaultDirectory(
				environment = if (appDataPath.isEmpty()) emptyMap() else mapOf("APPDATA" to appDataPath),
				osName = os,
				userHome = home,
			)
		}

		return environmentDirectory.map(::File).orElse(platformDirectory)
	}

	companion object {
		const val GROUP = "kore"

		/** Environment variable read by default for `kore.rcon.password`. */
		const val PASSWORD_ENVIRONMENT_VARIABLE = "RCON_PASSWORD"

		/** System property carrying the output directory to the entry point. */
		const val OUTPUT_SYSTEM_PROPERTY = "kore.output"

		/** System property carrying the datapack folder name to the entry point. */
		const val PACK_NAME_SYSTEM_PROPERTY = "kore.packName"
	}
}
