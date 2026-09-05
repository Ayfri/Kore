package io.github.ayfri.kore.serializers

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.json.JsonNamingStrategy

private fun List<Annotation>.hasJsonSerialName() = any { it is JsonSerialName }
private fun List<Annotation>.getJsonSerialName() = (first { it is JsonSerialName } as JsonSerialName).name

/** Custom Json Naming Strategy with support for [JsonSerialName] annotation. */
@OptIn(ExperimentalSerializationApi::class)
data object JsonNamingSnakeCaseStrategy : JsonNamingStrategy {
	override fun serialNameForJson(descriptor: SerialDescriptor, elementIndex: Int, serialName: String) =
		when {
			descriptor.annotations.hasJsonSerialName() -> descriptor.annotations.getJsonSerialName()
			descriptor.getElementAnnotations(elementIndex).hasJsonSerialName() -> descriptor
				.getElementAnnotations(elementIndex)
				.getJsonSerialName()

			else -> JsonNamingStrategy.SnakeCase.serialNameForJson(descriptor, elementIndex, serialName)
		}
}
