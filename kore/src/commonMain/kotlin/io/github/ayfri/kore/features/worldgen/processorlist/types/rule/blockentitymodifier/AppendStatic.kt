package io.github.ayfri.kore.features.worldgen.processorlist.types.rule.blockentitymodifier

import io.github.ayfri.kore.features.worldgen.processorlist.types.rule.ProcessorRule
import io.github.ayfri.kore.serializers.NbtAsJsonSerializer
import io.github.ayfri.kore.utils.nbt
import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.NbtCompound
import net.benwoodworth.knbt.NbtCompoundBuilder

/**
 * Keeps the block entity data of the template and merges [data] into it.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Processor_list
 *
 * @property data The block entity data merged into the placed block.
 */
@Serializable
data class AppendStatic(
	@Serializable(with = NbtAsJsonSerializer::class)
	var data: NbtCompound = nbt {},
) : BlockEntityModifier()

/**
 * Creates an `append_static` block entity modifier merging [data] into the block entity of the placed block.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Processor_list
 */
fun ProcessorRule.appendStatic(data: NbtCompound = nbt {}) = AppendStatic(data)

/**
 * Creates an `append_static` block entity modifier merging the data built in [block] into the block entity of the
 * placed block.
 *
 * ```kotlin
 * rule {
 *     blockEntityModifier = appendStatic {
 *         this["CustomName"] = "Loot"
 *     }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Processor_list
 */
fun ProcessorRule.appendStatic(block: NbtCompoundBuilder.() -> Unit) = AppendStatic(nbt(block))
