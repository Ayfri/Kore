package features.worldgen.configuredcarver

import serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

@Serializable(with = Config.Companion.ConfigSerializer::class)
sealed class Config {
	companion object {
		data object ConfigSerializer : NamespacedPolymorphicSerializer<Config>(Config::class, moveIntoProperty = "config")
	}
}
