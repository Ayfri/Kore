package io.github.ayfri.kore.bindings.generation

import io.github.ayfri.kore.bindings.generation.codegen.KtPropertySpec
import io.github.ayfri.kore.bindings.generation.codegen.KtRef
import io.github.ayfri.kore.bindings.generation.codegen.kotlinStringLiteral
import io.github.ayfri.kore.pack.PackFormat
import io.github.ayfri.kore.pack.PackFormatDecimal
import io.github.ayfri.kore.pack.PackFormatFull
import io.github.ayfri.kore.pack.PackFormatMajor
import io.github.ayfri.kore.pack.PackMCMeta
import io.github.ayfri.kore.pack.SupportedFormats

private val packSectionRef = KtRef("io.github.ayfri.kore.pack", "PackSection")
private val supportedFormatsRef = KtRef("io.github.ayfri.kore.pack", "SupportedFormats")
private val packFormatRef = KtRef("io.github.ayfri.kore.pack", "packFormat")
private val textComponentRef = KtRef("io.github.ayfri.kore.arguments.chatcomponents", "textComponent")

private fun packFormatInitializer(format: PackFormat) = when (format) {
	is PackFormatMajor -> "${packFormatRef.simpleName}(${format.major})"
	is PackFormatFull -> "${packFormatRef.simpleName}(${format.major}, ${format.minor})"
	is PackFormatDecimal -> "${packFormatRef.simpleName}(${format.value})"
}

private fun supportedFormatsInitializer(sf: SupportedFormats) = when {
	sf.number != null -> "${supportedFormatsRef.simpleName}(number = ${sf.number})"
	sf.list != null -> "${supportedFormatsRef.simpleName}(list = listOf(${sf.list!!.joinToString(", ")}))"
	else -> "${supportedFormatsRef.simpleName}(minInclusive = ${sf.minInclusive}, maxInclusive = ${sf.maxInclusive})"
}

/**
 * Generates the pack property from PackMCMeta.
 * Uses the Pack object directly from Kore.
 */
fun generatePackProperty(packMeta: PackMCMeta): KtPropertySpec {
	val initializer = buildString {
		append("${packSectionRef.simpleName}(\n")
		append("\tdescription = ${textComponentRef.simpleName}(${kotlinStringLiteral("Imported datapack")}),\n")
		append("\tminFormat = ${packFormatInitializer(packMeta.pack.minFormat)},\n")
		append("\tmaxFormat = ${packFormatInitializer(packMeta.pack.maxFormat)}")

		@Suppress("DEPRECATION")
		packMeta.pack.packFormat?.let { append(",\n\tpackFormat = ${packFormatInitializer(it)}") }
		@Suppress("DEPRECATION")
		packMeta.pack.supportedFormats?.let { append(",\n\tsupportedFormats = ${supportedFormatsInitializer(it)}") }

		append("\n)")
	}

	return KtPropertySpec(
		name = "pack",
		type = packSectionRef,
		initializer = initializer,
		referencedTypes = listOf(packFormatRef, textComponentRef, supportedFormatsRef),
	)
}
