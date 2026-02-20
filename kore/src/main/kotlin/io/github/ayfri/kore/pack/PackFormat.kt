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
 * Represents a pack format version used in `pack.mcmeta`.
 *
 * Three forms: a plain integer ([PackFormatMajor]), a `[major, minor]` array ([PackFormatFull]),
 * or a legacy decimal ([PackFormatDecimal]).
 * Since Minecraft 1.21.9 (25w31a), `min_format`/`max_format` are the primary fields.
 */
@Serializable(with = PackFormat.Companion.PackFormatSerializer::class)
sealed class PackFormat : Comparable<PackFormat> {
	/** The major (pack) version number. */
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
 * A pack format as a plain integer. Serialized as `82`.
 * As `min_format` means `[N, 0]`; as `max_format` means `[N, 0x7fffffff]`.
 */
@Serializable
data class PackFormatMajor(
	override var major: Int,
) : PackFormat()

/** A pack format as a `[major, minor]` pair. Serialized as `[82, 1]`. */
@Serializable
data class PackFormatFull(
	override var major: Int,
	var minor: Int,
) : PackFormat()

/** A pack format as a legacy decimal. Not valid for `min_format`/`max_format` since Minecraft 1.21.9. */
@Serializable
data class PackFormatDecimal(
	var value: Double,
) : PackFormat() {
	override val major get() = value.toInt()
	val minor get() = ((value - major) * 10 + 0.5).toInt()
}

/** Creates a [PackFormatMajor]. */
fun packFormat(major: Int) = PackFormatMajor(major)

/** Creates a [PackFormatFull]. */
fun packFormat(major: Int, minor: Int) = PackFormatFull(major, minor)

/** Creates a [PackFormatDecimal] (legacy). */
fun packFormat(value: Double) = PackFormatDecimal(value)

/** Returns a human-readable string: `"82"`, `"82.1"`, or the decimal value. */
fun PackFormat.asFormatString() = when (this) {
	is PackFormatMajor -> major.toString()
	is PackFormatFull -> "$major.$minor"
	is PackFormatDecimal -> value.toString()
}

/** Returns `true` if this is a [PackFormatMajor]. */
fun PackFormat.isMajorOnly() = this is PackFormatMajor

/** Returns `true` if this is a [PackFormatFull]. */
fun PackFormat.hasMajorMinor() = this is PackFormatFull

/** Returns the minor version, or `null` for [PackFormatMajor]. */
fun PackFormat.minorOrNull(): Int? = when (this) {
	is PackFormatMajor -> null
	is PackFormatFull -> minor
	is PackFormatDecimal -> minor
}

/** Returns a [PackFormatFull] with the given [minor], replacing any existing minor. */
fun PackFormat.withMinor(minor: Int) = PackFormatFull(major, minor)

/** Returns a [PackFormatMajor] dropping any minor version. */
fun PackFormat.toMajorOnly() = PackFormatMajor(major)

/** Returns a string describing the format range, e.g. `"80..84"`. */
fun PackSection.formatRangeString() = "${minFormat.asFormatString()}..${maxFormat.asFormatString()}"

/** Returns `true` if this pack's format range overlaps with [other]'s. */
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
