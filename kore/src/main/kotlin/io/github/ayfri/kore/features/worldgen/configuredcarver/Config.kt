package io.github.ayfri.kore.features.worldgen.configuredcarver

import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@GeneratedSealedSerializer
@Serializable(with = Config.Companion.ConfigSerializer::class)
sealed class Config {
	companion object {
		@OptIn(InternalSerializationApi::class)
		data object ConfigSerializer :
			NamespacedPolymorphicSerializer<Config>(configSealedSerializer(), moveIntoProperty = "config")
	}
}
