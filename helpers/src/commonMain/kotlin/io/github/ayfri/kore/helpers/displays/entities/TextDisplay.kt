package io.github.ayfri.kore.helpers.displays.entities

import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.colors.ARGB
import io.github.ayfri.kore.arguments.colors.ColorAsARGBDecimalSerializer
import io.github.ayfri.kore.generated.EntityTypes
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class TextDisplay(
	var alignment: TextAlignment? = null,
	@Serializable(with = ColorAsARGBDecimalSerializer::class)
	var background: ARGB? = null,
	var defaultBackground: Boolean? = null,
	var lineWidth: Int? = null,
	var seeThrough: Boolean? = null,
	var shadow: Boolean? = null,
	var text: ChatComponents? = null,
	var textOpacity: Byte? = null,
) : DisplayEntity() {
	@Transient
	override val entityType = EntityTypes.TEXT_DISPLAY
}
