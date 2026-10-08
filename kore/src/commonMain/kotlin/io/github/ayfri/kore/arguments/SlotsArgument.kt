package io.github.ayfri.kore.arguments

/**
 * The `<slot source>` argument of `/item` and `/execute if items|slots`, selecting one or several slots: an [ItemSlot]
 * (`armor.chest`, `hotbar.*`), a slot source file ID or an inline slot source built with `inlineSlotSource { }`.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Slot_source
 */
interface SlotsArgument : Argument
