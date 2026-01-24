package io.github.ayfri.kore.pack

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

/**
 * Represents a pack format value.
 *
 * Encoded as either a single integer (`major`) or a 2-int list `[major, minor]`.
 */
@Serializable(with = PackFormat.Companion.PackFormatSerializer::class)
sealed class PackFormat : Comparable<PackFormat> {
	abstract val major: Int

	companion object {
		data object PackFormatSerializer : KSerializer<PackFormat> {
			override val descriptor = buildClassSerialDescriptor("PackFormat")

			override fun deserialize(decoder: Decoder): PackFormat {
				require(decoder is JsonDecoder) { "PackFormat can only be deserialized by JSON" }
				return when (val element = decoder.decodeJsonElement()) {
					is JsonPrimitive -> {
						element.intOrNull?.let { PackFormatMajor(it) }
							?: element.doubleOrNull?.let { PackFormatDecimal(it) }
							?: throw SerializationException("Expected integer or decimal for PackFormat, found: $element")
					}

					is JsonArray -> {
						if (element.size != 2) throw SerializationException("Expected [major, minor] for PackFormat")
						PackFormatFull(element[0].jsonPrimitive.int, element[1].jsonPrimitive.int)
					}

					else -> throw SerializationException("Unsupported PackFormat value: $element")
				}
			}

			override fun serialize(encoder: Encoder, value: PackFormat) = when (value) {
				is PackFormatMajor -> encoder.encodeInt(value.major)
				is PackFormatFull -> encoder.encodeSerializableValue(
					ListSerializer(Int.serializer()),
					listOf(value.major, value.minor)
				)

				is PackFormatDecimal -> encoder.encodeDouble(value.value)
			}
		}
	}

	override fun compareTo(other: PackFormat): Int {
		val thisVal = this.toComparable(isMax = false)
		val otherVal = other.toComparable(isMax = false)
		return thisVal.compareTo(otherVal)
	}
}

/**
 * Encoded as a single integer in JSON.
 * Interpretation differs for min/max in vanilla:
 * - min_format: major == [major, 0]
 * - max_format: major == [major, 0x7fffffff]
 */
@Serializable
data class PackFormatMajor(
	override var major: Int,
) : PackFormat()

/** Encoded as a 2-int list `[major, minor]` in JSON. */
@Serializable
data class PackFormatFull(
	override var major: Int,
	var minor: Int,
) : PackFormat()

/** Encoded as a decimal in JSON. */
@Serializable
data class PackFormatDecimal(
	var value: Double,
) : PackFormat() {
	override val major get() = value.toInt()
	val minor get() = ((value - major) * 10 + 0.5).toInt()
}

fun packFormat(major: Int) = PackFormatMajor(major)
fun packFormat(major: Int, minor: Int) = PackFormatFull(major, minor)
fun packFormat(value: Double) = PackFormatDecimal(value)

fun PackFormat.asFormatString() = when (this) {
	is PackFormatMajor -> major.toString()
	is PackFormatFull -> "$major.$minor"
	is PackFormatDecimal -> value.toString()
}

fun PackSection.formatRangeString() = "${minFormat.asFormatString()}..${maxFormat.asFormatString()}"

fun PackSection.isCompatibleWith(other: PackSection): Boolean {
	val current = formatRange()
	val incoming = other.formatRange()
	return current.first <= incoming.last && incoming.first <= current.last
}

private fun PackSection.formatRange() = minFormat.toComparable(isMax = false)..maxFormat.toComparable(isMax = true)

private fun PackFormat.toComparable(isMax: Boolean) = when (this) {
	is PackFormatMajor -> packFormatValue(major, if (isMax) Int.MAX_VALUE else 0)
	is PackFormatFull -> packFormatValue(major, minor)
	is PackFormatDecimal -> packFormatValue(major, minor)
}

private fun packFormatValue(major: Int, minor: Int) = (major.toLong() shl 32) or (minor.toLong() and 0xffffffffL)
