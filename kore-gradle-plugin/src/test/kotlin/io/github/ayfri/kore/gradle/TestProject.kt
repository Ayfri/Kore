package io.github.ayfri.kore.gradle

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import java.io.File
import kotlin.io.path.createTempDirectory

/**
 * A throwaway Gradle build applying the plugin under test, driven through TestKit.
 *
 * The generator is plain Java so the nested build needs no network access, and the Minecraft directory points inside
 * the temporary project so linking never touches a real installation.
 */
class TestProject(private val koreConfiguration: String = "") {
	val root: File = createTempDirectory("kore-plugin").toFile()
	val minecraftDirectory: File = root.resolve("minecraft")
	val outputDirectory: File = root.resolve("build/kore")

	init {
		world("MyWorld")
		writeSettings()
		writeBuildScript()
		writeGenerator(functionBody = "say hello")
	}

	/** Creates a world in the fake Minecraft directory, recognised by its `level.dat`. */
	fun world(name: String): File = minecraftDirectory.resolve("saves/$name")
		.apply { mkdirs() }
		.also { it.resolve("level.dat").writeText("") }

	fun dataPacksDirectory(world: String): File = minecraftDirectory.resolve("saves/$world/datapacks")

	fun linkedPack(world: String, pack: String = "my_pack"): File = dataPacksDirectory(world).resolve(pack)

	/** Rewrites the generator so a rebuild produces different content, and optionally an extra file. */
	fun writeGenerator(functionBody: String, extraFile: String? = null) {
		val extra = extraFile?.let {
			"""
			|		Files.writeString(pack.resolve("$it"), "extra");
			""".trimMargin()
		} ?: ""

		root.resolve("src/main/java").mkdirs()
		root.resolve("src/main/java/Generator.java").writeText(
			"""
			|import java.nio.file.Files;
			|import java.nio.file.Path;
			|
			|public class Generator {
			|	public static void main(String[] args) throws Exception {
			|		Path output = Path.of(System.getProperty("kore.output", args[0]));
			|		Path pack = output.resolve(System.getProperty("kore.packName", "my_pack"));
			|		Files.createDirectories(pack.resolve("data/test/function"));
			|		Files.writeString(pack.resolve("pack.mcmeta"), "{\"pack\":{\"pack_format\":90}}");
			|		Files.writeString(pack.resolve("data/test/function/load.mcfunction"), "$functionBody");
			|$extra
			|		System.out.println("generated " + pack);
			|	}
			|}
			""".trimMargin()
		)
	}

	/** Adds a second pack folder to the output, standing in for a resource pack until Kore generates one. */
	fun writeResourcePackGenerator() {
		root.resolve("src/main/java/Generator.java").writeText(
			"""
			|import java.nio.file.Files;
			|import java.nio.file.Path;
			|
			|public class Generator {
			|	public static void main(String[] args) throws Exception {
			|		Path output = Path.of(args[0]);
			|		Path pack = output.resolve("my_pack");
			|		Files.createDirectories(pack.resolve("data"));
			|		Files.writeString(pack.resolve("pack.mcmeta"), "{}");
			|		Path assets = output.resolve("my_assets");
			|		Files.createDirectories(assets.resolve("assets"));
			|		Files.writeString(assets.resolve("pack.mcmeta"), "{}");
			|	}
			|}
			""".trimMargin()
		)
	}

	fun run(vararg arguments: String): BuildResult = runner(*arguments).build()

	fun runAndFail(vararg arguments: String): BuildResult = runner(*arguments).buildAndFail()

	private fun runner(vararg arguments: String): GradleRunner = GradleRunner.create()
		.withProjectDir(root)
		.withPluginClasspath()
		.withArguments(*arguments, "--stacktrace")
		.forwardOutput()

	fun delete() = root.deleteRecursively()

	private fun writeSettings() {
		root.mkdirs()
		root.resolve("settings.gradle.kts").writeText("""rootProject.name = "test-pack"""")
		// TestKit does not inherit the outer build's properties, so the configuration cache is opted into explicitly.
		root.resolve("gradle.properties").writeText("org.gradle.configuration-cache=true\n")
	}

	private fun writeBuildScript() = root.resolve("build.gradle.kts").writeText(
		"""
		|plugins {
		|	java
		|	id("io.github.ayfri.kore")
		|}
		|
		|kore {
		|	mainClass = "Generator"
		|	packName = "my_pack"
		|	minecraftDirectory = file("${minecraftDirectory.invariantSeparatorsPath}")
		|$koreConfiguration
		|}
		""".trimMargin()
	)
}
