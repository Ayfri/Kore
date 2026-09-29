package io.github.ayfri.kore.serializers

import io.github.ayfri.kore.utils.EnumStringSerializer
import kotlin.enums.EnumEntries

/** Serializes an enum entry as [transform], its lowercase name by default; decoding also accepts that name, with or without `minecraft:`. */
open class LowercaseSerializer<T : Enum<T>>(
	values: EnumEntries<T>,
	transform: T.() -> String = { name.lowercase() },
) : EnumStringSerializer<T>(values, transform) {
	private val byName = lazy { values.associateBy { it.name.lowercase() } }

	override fun decode(string: String) = super.decode(string) ?: byName.value[string.removePrefix("minecraft:")]
}
