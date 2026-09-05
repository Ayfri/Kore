package io.github.ayfri.kore.serializers

import io.github.ayfri.kore.utils.EnumStringSerializer
import kotlin.enums.EnumEntries

open class UppercaseSerializer<T : Enum<T>>(values: EnumEntries<T>) : EnumStringSerializer<T>(
	values,
	encode = { name.uppercase() },
	decode = { str -> values.first { it.name.uppercase() == str } },
)
