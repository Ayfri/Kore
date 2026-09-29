package io.github.ayfri.kore.arguments.enums

import io.github.ayfri.kore.arguments.EnumArgument
import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable(DataType.Companion.DataTypeSerializer::class)
enum class DataType : EnumArgument {
	BYTE,
	SHORT,
	INT,
	LONG,
	FLOAT,
	DOUBLE;

	companion object {
		data object DataTypeSerializer : LowercaseSerializer<DataType>(entries)
	}
}
