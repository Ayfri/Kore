package io.github.ayfri.kore.gradle.internal

import org.gradle.api.provider.Property
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import java.io.File

/**
 * Lists the worlds of a Minecraft directory.
 *
 * A value source rather than a plain provider, so the configuration cache re-checks the `saves` folder on every build
 * instead of freezing the world list at the first configuration.
 */
abstract class WorldsValueSource : ValueSource<List<String>, WorldsValueSource.Parameters> {
	interface Parameters : ValueSourceParameters {
		val minecraftDirectory: Property<String>
	}

	override fun obtain(): List<String> = MinecraftLocator.worlds(File(parameters.minecraftDirectory.get()))
}
