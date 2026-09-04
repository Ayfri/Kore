package io.github.ayfri.kore.gradle.internal

import java.io.File

/** Resolves the Minecraft installation directory and the worlds inside it. */
object MinecraftLocator {
	/** Environment variable checked first, so CI and non-standard installs do not need per-project configuration. */
	const val DIRECTORY_ENVIRONMENT_VARIABLE = "MINECRAFT_DIR"

	fun defaultDirectory(
		environment: Map<String, String> = System.getenv(),
		osName: String = System.getProperty("os.name"),
		userHome: String = System.getProperty("user.home"),
	): File {
		environment[DIRECTORY_ENVIRONMENT_VARIABLE]?.takeIf(String::isNotBlank)?.let { return File(it) }

		val home = File(userHome)
		val os = osName.lowercase()
		return when {
			"win" in os -> File(environment["APPDATA"]?.takeIf(String::isNotBlank) ?: home.path, ".minecraft")
			"mac" in os || "darwin" in os -> home.resolve("Library/Application Support/minecraft")
			else -> home.resolve(".minecraft")
		}
	}

	/** Names of the worlds under `<minecraftDirectory>/saves`, identified by their `level.dat`, sorted alphabetically. */
	fun worlds(minecraftDirectory: File): List<String> {
		val saves = minecraftDirectory.resolve("saves")
		val worlds = saves.listFiles() ?: return emptyList()
		return worlds.filter { it.isDirectory && it.resolve("level.dat").isFile }.map(File::getName).sorted()
	}

	fun dataPacksDirectory(minecraftDirectory: File, world: String): File =
		minecraftDirectory.resolve("saves").resolve(world).resolve("datapacks")

	fun resourcePacksDirectory(minecraftDirectory: File): File = minecraftDirectory.resolve("resourcepacks")
}
