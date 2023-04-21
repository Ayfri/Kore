package helpers.displays.entities

import arguments.Argument
import arguments.colors.RGB
import helpers.displays.maths.Transformation
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.descriptors.ClassSerialDescriptorBuilder
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.benwoodworth.knbt.StringifiedNbt
import net.benwoodworth.knbt.encodeToNbtTag
import net.benwoodworth.knbt.nbtCompound
import serializers.LowercaseSerializer

@Serializable(with = BillboardMode.Companion.BillboardModeSerializer::class)
enum class BillboardMode {
	FIXED,
	VERTICAL,
	HORIZONTAL,
	CENTER;

	companion object {
		val values = values()

		object BillboardModeSerializer : LowercaseSerializer<BillboardMode>(values)
	}
}

@Serializable
data class Brightness(
	var block: Int? = null,
	var light: Int? = null,
)

@Serializable(DisplayEntity.Companion.DisplayEntitySerializer::class)
sealed class DisplayEntity(
	var billboardMode: BillboardMode? = null,
	var brightness: Brightness? = null,
	var glowColorOverride: RGB? = null,
	var height: Int? = null,
	var interpolationDuration: Int? = null,
	var startInterpolation: Int? = null,
	var shadowRadius: Float? = null,
	var shadowStrength: Float? = null,
	var transformation: Transformation? = null,
	var viewRange: Int? = null,
	var width: Int? = null,
) {
	@Transient
	abstract val entityType: Argument.EntitySummon

	open fun toNbt() = StringifiedNbt.encodeToNbtTag(this).nbtCompound

	companion object {
		class DisplayEntitySerializer<T : DisplayEntity> : KSerializer<T> {
			override val descriptor = PrimitiveSerialDescriptor("DisplayEntity", PrimitiveKind.STRING)

			override fun deserialize(decoder: Decoder) = error("Not implemented")

			override fun serialize(encoder: Encoder, value: T) = when (value) {
				is BlockDisplay -> encoder.encodeSerializableValue(BlockDisplay.serializer(), value)
				is TextDisplay -> encoder.encodeSerializableValue(TextDisplay.serializer(), value)
				is ItemDisplay -> encoder.encodeSerializableValue(ItemDisplay.serializer(), value)
				else -> error("Unknown DisplayEntity type: $value")
			}
		}

		@JvmStatic
		protected fun <T : DisplayEntity> CompositeEncoder.encodeDisplayEntity(value: T) {
			value.billboardMode?.let {
				encodeSerializableElement(
					ItemDisplay.Companion.DisplayEntitySerializer.descriptor,
					2,
					BillboardMode.serializer(),
					it
				)
			}
			value.brightness?.let {
				encodeSerializableElement(
					ItemDisplay.Companion.DisplayEntitySerializer.descriptor,
					3,
					Brightness.serializer(),
					it
				)
			}
			value.glowColorOverride?.let {
				encodeSerializableElement(
					ItemDisplay.Companion.DisplayEntitySerializer.descriptor,
					4,
					RGB.serializer(),
					it
				)
			}
			value.height?.let { encodeIntElement(ItemDisplay.Companion.DisplayEntitySerializer.descriptor, 5, it) }
			value.interpolationDuration?.let { encodeIntElement(ItemDisplay.Companion.DisplayEntitySerializer.descriptor, 6, it) }
			value.startInterpolation?.let { encodeIntElement(ItemDisplay.Companion.DisplayEntitySerializer.descriptor, 7, it) }
			value.shadowRadius?.let { encodeFloatElement(ItemDisplay.Companion.DisplayEntitySerializer.descriptor, 8, it) }
			value.shadowStrength?.let { encodeFloatElement(ItemDisplay.Companion.DisplayEntitySerializer.descriptor, 9, it) }
			value.transformation?.let {
				encodeSerializableElement(
					ItemDisplay.Companion.DisplayEntitySerializer.descriptor,
					10,
					Transformation.serializer(),
					it
				)
			}
			value.viewRange?.let { encodeIntElement(ItemDisplay.Companion.DisplayEntitySerializer.descriptor, 11, it) }
			value.width?.let { encodeIntElement(ItemDisplay.Companion.DisplayEntitySerializer.descriptor, 12, it) }
		}

		@JvmStatic
		protected fun ClassSerialDescriptorBuilder.addDisplayEntity() {
			element("billboard_mode", BillboardMode.serializer().descriptor)
			element("brightness", Brightness.serializer().descriptor)
			element("glow_color_override", RGB.serializer().descriptor)
			element<Int>("height")
			element<Int>("interpolation_duration")
			element<Int>("start_interpolation")
			element<Float>("shadow_radius")
			element<Float>("shadow_strength")
			element("transformation", Transformation.serializer().descriptor)
			element<Int>("view_range")
			element<Int>("width")
		}
	}
}
