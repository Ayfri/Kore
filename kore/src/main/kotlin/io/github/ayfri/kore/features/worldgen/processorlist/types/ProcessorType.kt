package io.github.ayfri.kore.features.worldgen.processorlist.types

import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
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
