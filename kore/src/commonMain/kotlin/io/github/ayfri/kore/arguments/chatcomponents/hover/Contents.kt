package io.github.ayfri.kore.arguments.chatcomponents.hover

import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.NbtTag

/** Sealed payload for structured [HoverEvent] content used by [HoverAction.SHOW_ITEM] and [HoverAction.SHOW_ENTITY]. */
@Serializable
sealed interface Contents {
	/** Serializes this payload to an NBT compound whose keys are merged directly into the hover event tag. */
	fun toNbtTag(): NbtTag
}
