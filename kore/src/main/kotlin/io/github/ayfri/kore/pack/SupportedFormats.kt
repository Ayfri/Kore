package io.github.ayfri.kore.pack

import io.github.ayfri.kore.serializers.EitherInlineSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents the supported pack formats for a Minecraft data pack or resource pack.
 *
 * Serialization rules:
 * - If `number` is not null, serializes as a single integer (the pack format).
 * - If `list` is not empty, serializes as a list of integers (multiple supported formats).
 * - Otherwise, serializes as an object with `"min_inclusive"` and `"max_inclusive"` properties, representing a supported range.
 *
 * JSON format reference: https://minecraft.wiki/w/Pack_format
 */
@Serializable(with = SupportedFormats.Companion.SupportedFormatsSerializer::class)
data class SupportedFormats(
	var number: Int? = null,
	var list: List<Int>? = null,
	@SerialName("min_inclusive")
	var minInclusive: Int? = null,
	@SerialName("max_inclusive")
	var maxInclusive: Int? = null,
) {
	/** Checks if the given value is in the supported formats. */
	fun isInRange(value: Int) = when {
		number != null -> value == number
		list != null -> value in list!!
		minInclusive != null && maxInclusive != null -> value in minInclusive!!..maxInclusive!!
		else -> false
	}

	/** Checks if the given range is in the supported formats. */
	fun isInRange(value: IntRange) = when {
		number != null -> value.first == number && value.last == number
		list != null -> value.all { it in list!! }
		minInclusive != null && maxInclusive != null -> value.first in minInclusive!!..maxInclusive!! && value.last in minInclusive!!..maxInclusive!!
		else -> false
	}

	/** Checks if the given supported formats are compatible. */
	fun isCompatibleWith(other: SupportedFormats) = when {
		number != null && other.number != null -> number == other.number
		list != null && other.list != null -> list!!.intersect(other.list!!.toSet()).isNotEmpty()
		minInclusive != null && maxInclusive != null && other.minInclusive != null && other.maxInclusive != null -> minInclusive!! in other.minInclusive!!..other.maxInclusive!! || maxInclusive!! in other.minInclusive!!..other.maxInclusive!!
		else -> false
	}

	override fun toString() = when {
		number != null -> number.toString()
		list != null -> list.toString()
		minInclusive != null && maxInclusive != null -> "$minInclusive..$maxInclusive"
		else -> "Unsupported format"
	}

	companion object {
		data object SupportedFormatsSerializer : EitherInlineSerializer<SupportedFormats>(
			SupportedFormats::class,
			SupportedFormats::number,
			SupportedFormats::list
		)
	}
}
