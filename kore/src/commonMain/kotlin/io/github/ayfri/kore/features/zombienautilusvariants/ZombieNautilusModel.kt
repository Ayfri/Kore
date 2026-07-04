package io.github.ayfri.kore.features.zombienautilusvariants

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable(with = ZombieNautilusModel.Companion.ZombieNautilusModelSerializer::class)
enum class ZombieNautilusModel {
	NORMAL,
	WARM;

	companion object {
		data object ZombieNautilusModelSerializer : LowercaseSerializer<ZombieNautilusModel>(entries)
	}
}
