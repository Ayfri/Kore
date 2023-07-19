package helpers.displays.entities

import arguments.colors.RGB
import arguments.types.resources.EntityTypeArgument
import helpers.displays.maths.Transformation
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.descriptors.*
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
		data object BillboardModeSerializer : LowercaseSerializer<BillboardMode>(entries)
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
	abstract val entityType: EntityTypeArgument

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
		protected fun <T : DisplayEntity> CompositeEncoder.encodeDisplayEntity(value: T, descriptor: SerialDescriptor, offset: Int) {
			value.billboardMode?.let { encodeSerializableElement(descriptor, offset, BillboardMode.serializer(), it) }
			value.brightness?.let { encodeSerializableElement(descriptor, offset + 1, Brightness.serializer(), it) }
			value.glowColorOverride?.let { encodeSerializableElement(descriptor, offset + 2, RGB.serializer(), it) }
			value.height?.let { encodeIntElement(descriptor, offset + 3, it) }
			value.interpolationDuration?.let { encodeIntElement(descriptor, offset + 4, it) }
			value.startInterpolation?.let { encodeIntElement(descriptor, offset + 5, it) }
			value.shadowRadius?.let { encodeFloatElement(descriptor, offset + 6, it) }
			value.shadowStrength?.let { encodeFloatElement(descriptor, offset + 7, it) }
			value.transformation?.let { encodeSerializableElement(descriptor, offset + 8, Transformation.serializer(), it) }
			value.viewRange?.let { encodeIntElement(descriptor, offset + 9, it) }
			value.width?.let { encodeIntElement(descriptor, offset + 10, it) }
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
