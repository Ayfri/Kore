package helpers.displays.entities

import arguments.chatcomponents.ChatComponents
import arguments.chatcomponents.PlainTextComponent
import arguments.colors.ARGB
import generated.Entities
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeStructure
import utils.stringifiedNbt

@Serializable(with = TextDisplay.Companion.DisplayEntitySerializer::class)
data class TextDisplay(
	var alignment: TextAlignment? = null,
	var background: ARGB? = null,
	var defaultBackground: Boolean? = null,
	var lineWidth: Int? = null,
	var seeThrough: Boolean? = null,
	var shadow: Boolean? = null,
	var text: PlainTextComponent? = null,
	var textOpacity: Byte? = null,
) : DisplayEntity() {
	override val entityType = Entities.TEXT_DISPLAY

	override fun toString() =
		"TextDisplay(alignment=$alignment, background=$background, defaultBackground=$defaultBackground, lineWidth=$lineWidth, seeThrough=$seeThrough, shadow=$shadow, text=$text, textOpacity=$textOpacity, billboardMode=$billboardMode, brightness=$brightness, glowColorOverride=$glowColorOverride, height=$height, interpolationDuration=$interpolationDuration, startInterpolation=$startInterpolation, shadowRadius=$shadowRadius, shadowStrength=$shadowStrength, transformation=$transformation, viewRange=$viewRange, width=$width)"

	companion object {
		object DisplayEntitySerializer : KSerializer<TextDisplay> {
			override val descriptor = buildClassSerialDescriptor("TextDisplay") {
				element("alignment", TextAlignment.serializer().descriptor)
				element("background", ARGB.serializer().descriptor)
				element("default_background", Boolean.serializer().descriptor)
				element("line_width", Int.serializer().descriptor)
				element("see_through", Boolean.serializer().descriptor)
				element("shadow", Boolean.serializer().descriptor)
				element("text", String.serializer().descriptor)
				element("text_opacity", Byte.serializer().descriptor)
				addDisplayEntity()
			}

			override fun deserialize(decoder: Decoder) = error("Not implemented")

			override fun serialize(encoder: Encoder, value: TextDisplay) {
				encoder.encodeStructure(descriptor) {
					value.alignment?.let { encodeSerializableElement(descriptor, 0, TextAlignment.serializer(), it) }
					value.background?.let { encodeSerializableElement(descriptor, 1, ARGB.serializer(), it) }
					value.defaultBackground?.let { encodeSerializableElement(descriptor, 2, Boolean.serializer(), it) }
					value.lineWidth?.let { encodeSerializableElement(descriptor, 3, Int.serializer(), it) }
					value.seeThrough?.let { encodeSerializableElement(descriptor, 4, Boolean.serializer(), it) }
					value.shadow?.let { encodeSerializableElement(descriptor, 5, Boolean.serializer(), it) }
					value.text?.let {
						encodeSerializableElement(
							descriptor,
							6,
							String.serializer(),
							stringifiedNbt(ChatComponents(it).toNbtTag())
						)
					}
					value.textOpacity?.let { encodeSerializableElement(descriptor, 7, Byte.serializer(), it) }
					encodeDisplayEntity(value, descriptor, 8)
				}
			}
		}
	}
}
