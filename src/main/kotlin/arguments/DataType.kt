package arguments

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
		val values = values()
		
		object DataTypeSerializer : LowercaseSerializer<DataType>(values)
	}
}
