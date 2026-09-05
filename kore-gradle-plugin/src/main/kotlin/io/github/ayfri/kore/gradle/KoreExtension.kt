package io.github.ayfri.kore.gradle

import org.gradle.api.Action
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Nested

/**
 * Configuration of the `kore { }` block.
 *
 * The entry point receives the output directory both as its first program argument and as the `kore.output` system
 * property, so a datapack can pick it up with `Path(System.getProperty("kore.output"))`.
 */
abstract class KoreExtension {
	/** Fully-qualified name of the class whose `main` builds and generates the datapack, e.g. `com.example.MainKt`. */
	abstract val mainClass: Property<String>

	/** Folder name of the generated datapack inside [outputDirectory]. Defaults to the project name. */
	abstract val packName: Property<String>

	/** Directory the entry point generates into. Defaults to `build/kore`. */
	abstract val outputDirectory: DirectoryProperty

	/** Whether [outputDirectory] is wiped before each generation, so removed resources do not linger. Defaults to `true`. */
	abstract val cleanBeforeBuild: Property<Boolean>

	/** Classpath the entry point runs on. Defaults to the `main` source set runtime classpath when the `java` plugin is applied. */
	abstract val runtimeClasspath: ConfigurableFileCollection

	/** Extra program arguments appended after the output directory. */
	abstract val arguments: ListProperty<String>

	/** Extra JVM arguments for the generation process. */
	abstract val jvmArguments: ListProperty<String>

	/** Extra system properties for the generation process, merged with `kore.output` and `kore.packName`. */
	abstract val systemProperties: MapProperty<String, String>

	/**
	 * Files the entry point reads that are not on the classpath, such as textures or external JSON. Adding them keeps
	 * up-to-date checks and `--continuous` correct, since the classpath alone would not notice the change.
	 */
	abstract val additionalInputs: ConfigurableFileCollection

	/**
	 * Minecraft installation directory. Defaults to the `MINECRAFT_DIR` environment variable, then to the standard
	 * location for the current OS.
	 */
	abstract val minecraftDirectory: DirectoryProperty

	/** World names under `saves/` the datapack is linked into. */
	abstract val worlds: ListProperty<String>

	/** Links into every world found under `saves/`, on top of [worlds]. Useful on a test instance. */
	abstract val linkToAllWorlds: Property<Boolean>

	/** Extra `datapacks` directories, for dedicated servers or launchers with a custom root. */
	abstract val dataPackTargets: ListProperty<String>

	/** Folder name of a generated resource pack inside [outputDirectory]. Leave unset to skip resource pack linking. */
	abstract val resourcePackName: Property<String>

	/**
	 * Directories the resource pack is linked into. Defaults to `<minecraftDirectory>/resourcepacks` once
	 * [resourcePackName] is set.
	 */
	abstract val resourcePackTargets: ListProperty<String>

	/** Whether a pack is copied or symlinked into its targets. Defaults to [LinkMode.COPY]. */
	abstract val linkMode: Property<LinkMode>

	@get:Nested
	abstract val rcon: RconOptions

	fun rcon(action: Action<RconOptions>) = action.execute(rcon)
}
