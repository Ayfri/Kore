package io.github.ayfri.kore.features.slotsources

import io.github.ayfri.kore.arguments.SlotsArgument
import io.github.ayfri.kore.serializers.InlinableList
import io.github.ayfri.kore.serializers.InlinableListSerializer
import io.github.ayfri.kore.utils.encodeToSnbt

/** Slot sources written inline in a command, as SNBT. */
data class InlineSlotSource(var sources: InlinableList<SlotSource>) : SlotsArgument {
	override fun asString() = encodeToSnbt(InlinableListSerializer(SlotSource.serializer()), sources)
}

/**
 * Creates slot sources written inline in a command, several sources being concatenated.
 *
 * ```kotlin
 * items.fill(self(), inlineSlotSource { slotRange(HOTBAR) }, Items.STONE)
 * ```
 * writes `item fill entity @s {type:"minecraft:slot_range",slots:"hotbar.*"} with minecraft:stone`.
 */
fun inlineSlotSource(block: SlotSourcesBuilder.() -> Unit) = InlineSlotSource(buildSlotSources(block))
