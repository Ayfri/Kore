package io.github.ayfri.kore.features.pigvariants

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable(with = PigModel.Companion.PigModelSerializer::class)
enum class PigModel {
	NORMAL,
	COLD;

	companion object {
		data object PigModelSerializer : LowercaseSerializer<PigModel>(entries)
	}
}
