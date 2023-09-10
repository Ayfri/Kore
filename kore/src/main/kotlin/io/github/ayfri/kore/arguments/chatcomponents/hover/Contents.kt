package io.github.ayfri.kore.arguments.chatcomponents.hover

import net.benwoodworth.knbt.NbtTag
import kotlinx.serialization.Serializable

@Serializable
sealed interface Contents {
	fun toNbtTag(): NbtTag
}
