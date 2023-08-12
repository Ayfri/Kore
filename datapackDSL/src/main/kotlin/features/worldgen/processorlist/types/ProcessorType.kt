package features.worldgen.processorlist.types

import serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

@Serializable(with = ProcessorType.Companion.ProcessorTypeSerializer::class)
sealed class ProcessorType {
	companion object {
		data object ProcessorTypeSerializer : NamespacedPolymorphicSerializer<ProcessorType>(
			ProcessorType::class,
			outputName = "processor_type"
		)
	}
}
