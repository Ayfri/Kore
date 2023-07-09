package arguments.enums

import kotlinx.serialization.Serializable
import serializers.LowercaseSerializer

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
