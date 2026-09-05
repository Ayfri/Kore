package io.github.ayfri.kore.arguments.enums

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable(with = SalmonSizes.Companion.SalmonSizeSerializer::class)
enum class SalmonSizes {
	LARGE,
	MEDIUM,
	SMALL;

	companion object {
		data object SalmonSizeSerializer : LowercaseSerializer<SalmonSizes>(entries)
	}
}
