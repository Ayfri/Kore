package io.github.ayfri.kore.serializers

import io.github.ayfri.kore.utils.EnumStringSerializer
import kotlinx.serialization.encoding.Encoder
import kotlin.enums.EnumEntries

open class LowercaseSerializer<T : Enum<T>>(
	values: EnumEntries<T>,
	val transform: (T.(Encoder) -> String)? = null,
) : EnumStringSerializer<T>(
	values,
	encode = { name.lowercase() },
	decode = { str -> values.first { it.name.lowercase() == str } },
) {
	override fun serialize(encoder: Encoder, value: T) =
		encoder.encodeString(transform?.invoke(value, encoder) ?: value.name.lowercase())
}
