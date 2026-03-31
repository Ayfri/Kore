package io.github.ayfri.kore.serializers

import io.github.ayfri.kore.utils.EnumStringSerializer
import kotlin.enums.EnumEntries

private fun String.camelcase(): String {
	val words = lowercase().split("_")
	return words[0] + words.drop(1).joinToString("") { word ->
		word.replaceFirstChar { it.titlecase() }
	}
}

open class CamelcaseSerializer<T : Enum<T>>(values: EnumEntries<T>) : EnumStringSerializer<T>(
	values,
	encode = { name.camelcase() },
	decode = { str -> values.first { it.name.camelcase() == str } },
)
