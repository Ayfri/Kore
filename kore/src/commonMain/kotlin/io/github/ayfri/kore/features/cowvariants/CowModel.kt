package io.github.ayfri.kore.features.cowvariants

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable(with = CowModel.Companion.CowModelSerializer::class)
enum class CowModel {
	COLD,
	NORMAL,
	WARM;

	companion object {
		data object CowModelSerializer : LowercaseSerializer<CowModel>(entries)
	}
}
