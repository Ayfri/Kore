package io.github.ayfri.kore.pack

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.utils.warn
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure

/**
 * Represents the `pack` section of a `pack.mcmeta` file.
 *
 * [minFormat] and [maxFormat] are the primary fields since Minecraft 1.21.9 (25w31a).
 * [packFormat] and [supportedFormats] are legacy fields only emitted when [supportsOldVersions] is `true`.
 *
 * @see [Pack.mcmeta – Minecraft Wiki](https://minecraft.wiki/w/Pack.mcmeta)
 */
@Serializable(with = PackSection.Companion.PackSectionSerializer::class)
data class PackSection(
	var description: ChatComponents,
	var minFormat: PackFormat,
	var maxFormat: PackFormat,
	@Deprecated(
		message = "pack_format is a legacy field kept for backwards compatibility with old game versions " +
			"(data pack < 82, resource pack < 65). It is automatically included during serialization " +
			"when supportsOldVersions() is true. Set it only if you need to override the default value.",
		level = DeprecationLevel.WARNING,
	)
	var packFormat: PackFormat? = null,
	@Deprecated(
		message = "supported_formats is a legacy field kept for backwards compatibility with old game versions " +
			"(data pack < 82, resource pack < 65). It is automatically included during serialization " +
			"when supportsOldVersions() is true. Set it only if you need to override the default range.",
		level = DeprecationLevel.WARNING,
	)
	var supportedFormats: SupportedFormats? = null,
) {
	/** Whether this is a resource pack (`true`) or a data pack (`false`). Affects [supportsOldVersions] threshold. Not serialized. */
	@Transient
	var isResourcePack: Boolean = false

	/** Legacy threshold: `65` for resource packs, `82` for data packs. */
	private val threshold get() = if (isResourcePack) 65 else 82

	/** Returns `true` if [minFormat] is below the legacy threshold (data pack < 82, resource pack < 65). */
	fun supportsOldVersions() = minFormat < packFormat(threshold)

	init {
		if (minFormat > maxFormat) {
			warn("minFormat (${minFormat.asFormatString()}) is greater than maxFormat (${maxFormat.asFormatString()})")
		}
		if (minFormat is PackFormatDecimal) {
			warn("minFormat (${minFormat.asFormatString()}) cannot be a decimal")
		}
		if (maxFormat is PackFormatDecimal) {
			warn("maxFormat (${maxFormat.asFormatString()}) cannot be a decimal")
		}
		@Suppress("DEPRECATION")
		packFormat?.let {
			if (it !in minFormat..maxFormat) {
				warn("packFormat (${it.asFormatString()}) is outside the range [${minFormat.asFormatString()}, ${maxFormat.asFormatString()}]")
			}
		}
	}

	/** Sets the legacy [supportedFormats] from an [IntRange]. Only required when [supportsOldVersions] is `true`. */
	@Suppress("DEPRECATION")
	fun supportedFormats(range: IntRange) {
		if (range.isEmpty()) {
			warn("supportedFormats range is empty")
		}
		supportedFormats = SupportedFormats(
			minInclusive = range.first,
			maxInclusive = range.last
		)
	}

	/** Sets the legacy [supportedFormats] from [min] and optional [max]. Only required when [supportsOldVersions] is `true`. */
	@Suppress("DEPRECATION")
	fun supportedFormats(min: Int, max: Int? = null) {
		if (max != null && min > max) {
			warn("supportedFormats min ($min) is greater than max ($max)")
		}
		supportedFormats = if (max == null || min == max) {
			SupportedFormats(number = min)
		} else {
			SupportedFormats(minInclusive = min, maxInclusive = max)
		}
	}

	companion object {
		data object PackSectionSerializer : KSerializer<PackSection> {
			override val descriptor = buildClassSerialDescriptor("PackSection") {
				element<ChatComponents>("description")
				element<PackFormat>("min_format")
				element<PackFormat>("max_format")
				element<PackFormat>("pack_format", isOptional = true)
				element<SupportedFormats>("supported_formats", isOptional = true)
			}

			override fun deserialize(decoder: Decoder): PackSection = decoder.decodeStructure(descriptor) {
				var description: ChatComponents? = null
				var minFormat: PackFormat? = null
				var maxFormat: PackFormat? = null
				var packFormat: PackFormat? = null
				var supportedFormats: SupportedFormats? = null

				while (true) {
					when (val index = decodeElementIndex(descriptor)) {
						0 -> description = decodeSerializableElement(descriptor, 0, ChatComponents.serializer())
						1 -> minFormat = decodeSerializableElement(descriptor, 1, PackFormat.serializer())
						2 -> maxFormat = decodeSerializableElement(descriptor, 2, PackFormat.serializer())
						3 -> packFormat = decodeSerializableElement(descriptor, 3, PackFormat.serializer())
						4 -> supportedFormats = decodeSerializableElement(descriptor, 4, SupportedFormats.serializer())
						-1 -> break
						else -> throw kotlinx.serialization.SerializationException("Unknown index $index")
					}
				}

				@Suppress("DEPRECATION")
				PackSection(
					description = description ?: throw kotlinx.serialization.SerializationException("description is missing"),
					minFormat = minFormat ?: throw kotlinx.serialization.SerializationException("min_format is missing"),
					maxFormat = maxFormat ?: throw kotlinx.serialization.SerializationException("max_format is missing"),
					packFormat = packFormat,
					supportedFormats = supportedFormats
				)
			}

			override fun serialize(encoder: Encoder, value: PackSection) = encoder.encodeStructure(descriptor) {
				encodeSerializableElement(descriptor, 0, ChatComponents.serializer(), value.description)
				encodeSerializableElement(descriptor, 1, PackFormat.serializer(), value.minFormat)
				encodeSerializableElement(descriptor, 2, PackFormat.serializer(), value.maxFormat)

				val supportsOld = value.supportsOldVersions()

				@Suppress("DEPRECATION")
				if (supportsOld || value.packFormat != null) {
					encodeSerializableElement(descriptor, 3, PackFormat.serializer(), value.packFormat ?: value.minFormat)
				}

				@Suppress("DEPRECATION")
				if (supportsOld) {
					val sf = value.supportedFormats ?: SupportedFormats(
						minInclusive = value.minFormat.major,
						maxInclusive = value.maxFormat.major
					)
					encodeSerializableElement(descriptor, 4, SupportedFormats.serializer(), sf)
				}
			}
		}
	}
}

/** Represents the `overlays` section of a `pack.mcmeta` file. Order of [entries] matters; first is applied first. */
@Serializable
data class PackOverlays(
	var entries: List<PackOverlayEntry>,
)

/**
 * Represents a single entry in the `overlays.entries` list of a `pack.mcmeta` file.
 *
 * [minFormat] and [maxFormat] are the primary fields since Minecraft 1.21.9 (25w31a).
 * The legacy [formats] field is only emitted when [supportsOldVersions] is `true` (data pack < 82, resource pack < 65).
 */
@Serializable(with = PackOverlayEntry.Companion.PackOverlayEntrySerializer::class)
data class PackOverlayEntry(
	var directory: String,
	var minFormat: PackFormat,
	var maxFormat: PackFormat,
	@Deprecated(
		message = "formats is a legacy field kept for backwards compatibility with old game versions " +
			"(data pack < 82, resource pack < 65). It is automatically included during serialization " +
			"when supportsOldVersions() is true. Set it only if you need to override the default range.",
		level = DeprecationLevel.WARNING,
	)
	var formats: SupportedFormats? = null,
) {
	/** Whether this is a resource pack (`true`) or a data pack (`false`). Affects [supportsOldVersions] threshold. Not serialized. */
	@Transient
	var isResourcePack: Boolean = false

	/** Legacy threshold: `65` for resource packs, `82` for data packs. */
	private val threshold get() = if (isResourcePack) 65 else 82

	/** Returns `true` if [minFormat] is below the legacy threshold (data pack < 82, resource pack < 65). */
	fun supportsOldVersions() = minFormat < packFormat(threshold)

	init {
		if (minFormat is PackFormatDecimal) {
			warn("minFormat (${minFormat.asFormatString()}) in overlay '$directory' cannot be a decimal")
		}
		if (maxFormat is PackFormatDecimal) {
			warn("maxFormat (${maxFormat.asFormatString()}) in overlay '$directory' cannot be a decimal")
		}
	}

	/** Warns if this overlay's format range is outside [pack]'s declared range. */
	fun validateAgainst(pack: PackSection) {
		if (minFormat < pack.minFormat || maxFormat > pack.maxFormat) {
			warn("Overlay entry '$directory' range [${minFormat.asFormatString()}, ${maxFormat.asFormatString()}] is outside pack range [${pack.minFormat.asFormatString()}, ${pack.maxFormat.asFormatString()}]")
		}
	}

	companion object {
		data object PackOverlayEntrySerializer : KSerializer<PackOverlayEntry> {
			override val descriptor = buildClassSerialDescriptor("PackOverlayEntry") {
				element<String>("directory")
				element<PackFormat>("min_format")
				element<PackFormat>("max_format")
				element<SupportedFormats>("formats", isOptional = true)
			}

			override fun deserialize(decoder: Decoder): PackOverlayEntry = decoder.decodeStructure(descriptor) {
				var directory: String? = null
				var minFormat: PackFormat? = null
				var maxFormat: PackFormat? = null
				var formats: SupportedFormats? = null

				while (true) {
					when (val index = decodeElementIndex(descriptor)) {
						0 -> directory = decodeStringElement(descriptor, 0)
						1 -> minFormat = decodeSerializableElement(descriptor, 1, PackFormat.serializer())
						2 -> maxFormat = decodeSerializableElement(descriptor, 2, PackFormat.serializer())
						3 -> formats = decodeSerializableElement(descriptor, 3, SupportedFormats.serializer())
						-1 -> break
						else -> throw kotlinx.serialization.SerializationException("Unknown index $index")
					}
				}

				@Suppress("DEPRECATION")
				PackOverlayEntry(
					directory = directory ?: throw kotlinx.serialization.SerializationException("directory is missing"),
					minFormat = minFormat ?: throw kotlinx.serialization.SerializationException("min_format is missing"),
					maxFormat = maxFormat ?: throw kotlinx.serialization.SerializationException("max_format is missing"),
					formats = formats
				)
			}

			override fun serialize(encoder: Encoder, value: PackOverlayEntry) = encoder.encodeStructure(descriptor) {
				encodeStringElement(descriptor, 0, value.directory)
				encodeSerializableElement(descriptor, 1, PackFormat.serializer(), value.minFormat)
				encodeSerializableElement(descriptor, 2, PackFormat.serializer(), value.maxFormat)

				@Suppress("DEPRECATION")
				if (value.supportsOldVersions()) {
					val f = value.formats ?: SupportedFormats(
						minInclusive = value.minFormat.major,
						maxInclusive = value.maxFormat.major
					)
					encodeSerializableElement(descriptor, 3, SupportedFormats.serializer(), f)
				}
			}
		}
	}
}

/** Sets the pack of the datapack. */
fun DataPack.pack(block: PackSection.() -> Unit) = pack.run(block)

fun PackSection.minFormat(major: Int) {
	minFormat = packFormat(major)
}

fun PackSection.minFormat(major: Int, minor: Int) {
	minFormat = packFormat(major, minor)
}

fun PackSection.minFormat(value: Double) {
	minFormat = packFormat(value)
}

fun PackSection.minFormat(format: PackFormat) {
	minFormat = format
}

fun PackSection.maxFormat(major: Int) {
	maxFormat = packFormat(major)
}

fun PackSection.maxFormat(major: Int, minor: Int) {
	maxFormat = packFormat(major, minor)
}

fun PackSection.maxFormat(value: Double) {
	maxFormat = packFormat(value)
}

fun PackSection.maxFormat(format: PackFormat) {
	maxFormat = format
}

fun PackOverlayEntry.minFormat(major: Int) {
	minFormat = packFormat(major)
}

fun PackOverlayEntry.minFormat(major: Int, minor: Int) {
	minFormat = packFormat(major, minor)
}

fun PackOverlayEntry.minFormat(value: Double) {
	minFormat = packFormat(value)
}

fun PackOverlayEntry.minFormat(format: PackFormat) {
	minFormat = format
}

fun PackOverlayEntry.maxFormat(major: Int) {
	maxFormat = packFormat(major)
}

fun PackOverlayEntry.maxFormat(major: Int, minor: Int) {
	maxFormat = packFormat(major, minor)
}

fun PackOverlayEntry.maxFormat(value: Double) {
	maxFormat = packFormat(value)
}

fun PackOverlayEntry.maxFormat(format: PackFormat) {
	maxFormat = format
}

/** DSL builder for [PackOverlays]. Use [entry] to add overlay entries. */
class PackOverlaysBuilder {
	val entries = mutableListOf<PackOverlayEntry>()

	/** Adds an overlay entry for the given [directory], configured via [block]. */
	fun entry(directory: String, block: PackOverlayEntry.() -> Unit) {
		val entry = PackOverlayEntry(directory, packFormat(0), packFormat(0))
		entry.block()
		entries += entry
	}

	/** Builds and returns the [PackOverlays] instance. */
	fun build() = PackOverlays(entries)
}

/** Sets the overlays of the datapack using a [PackOverlaysBuilder] DSL. */
fun DataPack.overlays(block: PackOverlaysBuilder.() -> Unit) {
	overlays = PackOverlaysBuilder().apply(block).build()
}
