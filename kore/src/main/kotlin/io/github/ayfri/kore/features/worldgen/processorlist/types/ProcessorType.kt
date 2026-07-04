package io.github.ayfri.kore.features.worldgen.processorlist.types

import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@GeneratedSealedSerializer
@Serializable(with = ProcessorType.Companion.ProcessorTypeSerializer::class)
sealed class ProcessorType {
	companion object {
		@OptIn(InternalSerializationApi::class)
		data object ProcessorTypeSerializer : NamespacedPolymorphicSerializer<ProcessorType>(
			processorTypeSealedSerializer(),
			outputName = "processor_type"
		)
	}
}
