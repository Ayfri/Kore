package io.github.ayfri.kore.bindings.generation

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.asClassName
import io.github.ayfri.kore.pack.*

private val packFormatMember = MemberName("io.github.ayfri.kore.pack", "packFormat")
private val textComponentMember = MemberName("io.github.ayfri.kore.arguments.chatcomponents", "textComponent")

/**
 * Generates the pack property from PackMCMeta.
 * Uses the Pack object directly from Kore.
 */
fun generatePackProperty(packMeta: PackMCMeta): PropertySpec {
	val packClassName = PackSection::class.asClassName()

	fun packFormatInitializer(format: PackFormat) = when (format) {
		is PackFormatMajor -> CodeBlock.of("%M(%L)", packFormatMember, format.major)
		is PackFormatFull -> CodeBlock.of("%M(%L, %L)", packFormatMember, format.major, format.minor)
		is PackFormatDecimal -> CodeBlock.of("%M(%L)", packFormatMember, format.value)
	}

	fun supportedFormatsInitializer(sf: SupportedFormats) = when {
		sf.number != null -> CodeBlock.of("%T(number = %L)", SupportedFormats::class, sf.number)
		sf.list != null -> CodeBlock.builder()
			.add("%T(list = listOf(", SupportedFormats::class)
			.apply {
				sf.list!!.forEachIndexed { index, value ->
					if (index > 0) add(", ")
					add("%L", value)
				}
			}
			.add("))")
			.build()

		else -> CodeBlock.of(
			"%T(minInclusive = %L, maxInclusive = %L)",
			SupportedFormats::class,
			sf.minInclusive,
			sf.maxInclusive
		)
	}

	val initializerCode = CodeBlock.builder()
		.add("%T(\n", packClassName)
		.indent()
		.add("description = %M(%S),\n", textComponentMember, "Imported datapack")
		.add("minFormat = %L,\n", packFormatInitializer(packMeta.pack.minFormat))
		.add("maxFormat = %L", packFormatInitializer(packMeta.pack.maxFormat))
		.apply {
			@Suppress("DEPRECATION")
			packMeta.pack.packFormat?.let { format ->
				add(",\n")
				add("packFormat = %L", packFormatInitializer(format))
			}
			@Suppress("DEPRECATION")
			packMeta.pack.supportedFormats?.let { supportedFormats ->
				add(",\n")
				add("supportedFormats = %L", supportedFormatsInitializer(supportedFormats))
			}
		}
		.add("\n")
		.unindent()
		.add(")")
		.build()

	return PropertySpec.builder("pack", packClassName)
		.initializer(initializerCode)
		.build()
}
