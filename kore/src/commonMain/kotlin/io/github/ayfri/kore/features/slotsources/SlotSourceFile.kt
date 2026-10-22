package io.github.ayfri.kore.features.slotsources

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.generated.arguments.types.SlotSourceArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Transient

/**
 * Data-driven slot source, usable by ID in `/item`, `/execute if items|slots` and in other slot sources through
 * [SlotSourceReference]. A file holds exactly one source object: a single source that isn't a reference is written as is,
 * while several sources or a lone reference are wrapped in a `minecraft:group`. A file without any source isn't
 * possible, use `empty()` to select no slot.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/slot-sources
 * Minecraft Wiki: https://minecraft.wiki/w/Slot_source
 */
data class SlotSourceFile(
	@Transient
	override var fileName: String = "slot_source",
	var sources: InlinableList<SlotSource>,
) : Generator("slot_source") {
	override fun generateJson(dataPack: DataPack): String {
		require(sources.isNotEmpty()) { "Slot source file '$fileName' needs at least one source, use `empty()` to select no slot." }
		val source = sources.singleOrNull()?.takeUnless { it is SlotSourceReference } ?: GroupSlotSource(sources)
		return dataPack.jsonEncoder.encodeToString(SlotSource.serializer(), source)
	}
}

/**
 * Creates a slot source file selecting the slots of the sources appended in [block], several sources being concatenated.
 *
 * ```kotlin
 * val hands = slotSource("hands") {
 *     slotRange(WEAPON.MAINHAND)
 *     slotRange(WEAPON.OFFHAND)
 * }
 *
 * execute {
 *     ifCondition { slots(self(), hands) }
 *     run { say("Something in hand") }
 * }
 * ```
 *
 * Produces `data/<namespace>/slot_source/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/slot-sources
 * Minecraft Wiki: https://minecraft.wiki/w/Slot_source
 */
fun DataPack.slotSource(fileName: String = "slot_source", block: SlotSourcesBuilder.() -> Unit): SlotSourceArgument {
	val file = SlotSourceFile(fileName, buildSlotSources(block))
	slotSources += file
	return SlotSourceArgument(fileName, file.namespace ?: name)
}
