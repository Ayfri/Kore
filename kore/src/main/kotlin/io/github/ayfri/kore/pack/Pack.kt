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
 * Represents the `pack` section of a Minecraft pack.mcmeta file.
 *
 * @property description The description of the pack.
 * @property minFormat The minimum supported pack format.
 * @property maxFormat The maximum supported pack format.
 * @property packFormat The explicit pack format (optional in newer formats).
 */
@Serializable(with = PackSection.Companion.PackSectionSerializer::class)
data class PackSection(
	var description: ChatComponents,
	var minFormat: PackFormat,
	var maxFormat: PackFormat,
	var packFormat: PackFormat? = null,
	var supportedFormats: SupportedFormats? = null,
) {
	@Transient
	var isResourcePack: Boolean = false

	private val threshold get() = if (isResourcePack) 65 else 82

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
		packFormat?.let {
			if (it < minFormat || it > maxFormat) {
				warn("packFormat (${it.asFormatString()}) is outside the range [${minFormat.asFormatString()}, ${maxFormat.asFormatString()}]")
			}
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

				if (supportsOld || value.packFormat != null) {
					encodeSerializableElement(descriptor, 3, PackFormat.serializer(), value.packFormat ?: value.minFormat)
				}

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

/** Represents optional overlay entries in pack.mcmeta. */
@Serializable
data class PackOverlays(
	var entries: List<PackOverlayEntry>,
)

/** An overlay entry with its own format range. */
@Serializable(with = PackOverlayEntry.Companion.PackOverlayEntrySerializer::class)
data class PackOverlayEntry(
	var directory: String,
	var minFormat: PackFormat,
	var maxFormat: PackFormat,
	var formats: SupportedFormats? = null,
) {
	@Transient
	var isResourcePack: Boolean = false

	private val threshold get() = if (isResourcePack) 65 else 82

	fun supportsOldVersions() = minFormat < packFormat(threshold)

	init {
		if (minFormat is PackFormatDecimal) {
			warn("minFormat (${minFormat.asFormatString()}) in overlay '$directory' cannot be a decimal")
		}
		if (maxFormat is PackFormatDecimal) {
			warn("maxFormat (${maxFormat.asFormatString()}) in overlay '$directory' cannot be a decimal")
		}
	}

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
