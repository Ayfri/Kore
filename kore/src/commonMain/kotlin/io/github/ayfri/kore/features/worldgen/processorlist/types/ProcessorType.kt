package io.github.ayfri.kore.features.worldgen.processorlist.types

import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

/**
 * A structure processor, one step of a [io.github.ayfri.kore.features.worldgen.processorlist.ProcessorList].
 *
 * Every builder is an extension on [ProcessorsScope], so they only resolve inside a `processorList { }` block or a
 * nested block such as [capped]'s delegate.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Processor_list
 */
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
