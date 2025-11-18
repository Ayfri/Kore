package io.github.ayfri.kore.bindings.generation

import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.asClassName
import io.github.ayfri.kore.pack.Pack
import io.github.ayfri.kore.pack.PackMCMeta

/**
 * Generates the pack property from PackMCMeta.
 * Uses the Pack object directly from Kore.
 */
fun generatePackProperty(packMeta: PackMCMeta): PropertySpec {
	val packClassName = Pack::class.asClassName()

	// Build the initializer code
	val initializerCode = buildString {
		append("Pack(\n")
		append("    format = FORMAT,\n")

		// For description, we'll generate a simple textComponent call
		// The actual description will be preserved through the Pack object
		append("    description = textComponent(\"Imported datapack\")")

		// Handle supportedFormats if present
		packMeta.pack.supportedFormats?.let { sf ->
			append(",\n")
			append("    supportedFormats = ")

			when {
				sf.number != null -> {
					append("SupportedFormats(number = ${sf.number})")
				}
				sf.list != null && sf.list!!.isNotEmpty() -> {
					append("SupportedFormats(list = listOf(${sf.list!!.joinToString(", ")}))")
				}
				sf.minInclusive != null || sf.maxInclusive != null -> {
					append("SupportedFormats(")
					if (sf.minInclusive != null) {
						append("minInclusive = ${sf.minInclusive}")
					}
					if (sf.maxInclusive != null) {
						if (sf.minInclusive != null) append(", ")
						append("maxInclusive = ${sf.maxInclusive}")
					}
					append(")")
				}
			}
		}

		append("\n)")
	}

	return PropertySpec.builder("pack", packClassName)
		.initializer(initializerCode)
		.build()
}
