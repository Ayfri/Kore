package io.github.ayfri.kore.features.worldgen.processorlist.types.rule.blockentitymodifier

import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

/**
 * What happens to the block entity data of a block placed by a
 * [io.github.ayfri.kore.features.worldgen.processorlist.types.rule.ProcessorRule].
 *
 * Every builder is an extension on [io.github.ayfri.kore.features.worldgen.processorlist.types.rule.ProcessorRule],
 * so they only resolve inside a `rule { }` block.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Processor_list
 */
@GeneratedSealedSerializer
@Serializable(with = BlockEntityModifier.Companion.BlockEntityModifierSerializer::class)
sealed class BlockEntityModifier {
	companion object {
		@OptIn(InternalSerializationApi::class)
		data object BlockEntityModifierSerializer :
			NamespacedPolymorphicSerializer<BlockEntityModifier>(blockEntityModifierSealedSerializer())
	}
}
