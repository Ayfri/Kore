package io.github.ayfri.kore.bindings.generation

import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.asClassName
import io.github.ayfri.kore.pack.*

/**
 * Generates the pack property from PackMCMeta.
 * Uses the Pack object directly from Kore.
 */
fun generatePackProperty(packMeta: PackMCMeta): PropertySpec {
	val packClassName = PackSection::class.asClassName()

	fun packFormatInitializer(format: PackFormat) = when (format) {
		is PackFormatMajor -> "packFormat(${format.major})"
		is PackFormatFull -> "packFormat(${format.major}, ${format.minor})"
		is PackFormatDecimal -> "packFormat(${format.value})"
	}

	fun supportedFormatsInitializer(sf: SupportedFormats) = when {
		sf.number != null -> "SupportedFormats(number = ${sf.number})"
		sf.list != null -> "SupportedFormats(list = listOf(${sf.list!!.joinToString(", ")}))"
		else -> "SupportedFormats(minInclusive = ${sf.minInclusive}, maxInclusive = ${sf.maxInclusive})"
	}

	// Build the initializer code
	val initializerCode = buildString {
		append("PackSection(\n")

		// For description, we'll generate a simple textComponent call
		// The actual description will be preserved through the Pack object
		append("    description = textComponent(\"Imported datapack\"),\n")
		append("    minFormat = ${packFormatInitializer(packMeta.pack.minFormat)},\n")
		append("    maxFormat = ${packFormatInitializer(packMeta.pack.maxFormat)}")
		packMeta.pack.packFormat?.let { format ->
			append(",\n")
			append("    packFormat = ${packFormatInitializer(format)}")
		}
		packMeta.pack.supportedFormats?.let { sf ->
			append(",\n")
			append("    supportedFormats = ${supportedFormatsInitializer(sf)}")
		}

		append("\n)")
	}

	return PropertySpec.builder("pack", packClassName)
		.initializer(initializerCode)
		.build()
}
