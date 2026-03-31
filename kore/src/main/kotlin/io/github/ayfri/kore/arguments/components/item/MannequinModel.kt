package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable(with = MannequinModel.Companion.MannequinModelSerializer::class)
enum class MannequinModel {
	/** Alex model. */
	SLIM,

	/** Steve model. */
	WIDE;

	companion object {
		data object MannequinModelSerializer : LowercaseSerializer<MannequinModel>(entries)
	}
}