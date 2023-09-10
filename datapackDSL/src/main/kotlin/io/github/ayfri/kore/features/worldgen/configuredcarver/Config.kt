package io.github.ayfri.kore.features.worldgen.configuredcarver

import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

@Serializable(with = Config.Companion.ConfigSerializer::class)
sealed class Config {
	companion object {
		data object ConfigSerializer : NamespacedPolymorphicSerializer<Config>(Config::class, moveIntoProperty = "config")
	}
}
