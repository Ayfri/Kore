package io.github.ayfri.kore.arguments.enums

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable(DataType.Companion.DataTypeSerializer::class)
enum class DataType {
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
