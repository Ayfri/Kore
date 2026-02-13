package io.github.ayfri.kore.pack

import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.assertions.assertsIsJson
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

@OptIn(ExperimentalSerializationApi::class)
private val json = Json {
	prettyPrint = true
	encodeDefaults = false
	explicitNulls = false
	prettyPrintIndent = "\t"
}

fun packFormatTests() {
	testPackFormatMajorCreation()
	testPackFormatFullCreation()
	testPackFormatDecimalCreation()
	testPackFormatSerializationMajor()
	testPackFormatSerializationFull()
	testPackFormatSerializationDecimal()
	testPackFormatDeserializationInteger()
	testPackFormatDeserializationArray()
	testPackFormatDeserializationDecimal()
	testPackFormatAsFormatString()
	testPackFormatComparison()
	testPackSectionCompatibility()
	testPackSectionValidation()
	testSupportedFormatsSerialization()
	testSupportedFormatsDsl()
	testPackSectionConditionalFields()
}

private fun testPackSectionValidation() {
	// min > max
	PackSection(
		description = textComponent("Test"),
		minFormat = packFormat(15),
		maxFormat = packFormat(10)
	)

	// packFormat outside range
	PackSection(
		description = textComponent("Test"),
		minFormat = packFormat(10),
		maxFormat = packFormat(15),
		packFormat = packFormat(20)
	)

	// Decimal min/max
	PackSection(
		description = textComponent("Test"),
		minFormat = packFormat(94.1),
		maxFormat = packFormat(95)
	)

	// Overlay outside range
	val section = PackSection(
		description = textComponent("Test"),
		minFormat = packFormat(10),
		maxFormat = packFormat(15)
	)
	val overlay = PackOverlayEntry("dir", packFormat(5), packFormat(20))
	overlay.validateAgainst(section)

	// Overlay decimal
	PackOverlayEntry("dir", packFormat(94.1), packFormat(95))
}

private fun testSupportedFormatsSerialization() {
	val sfInt = SupportedFormats(number = 15)
	json.encodeToString(SupportedFormats.serializer(), sfInt) assertsIsJson "15"

	val sfList = SupportedFormats(list = listOf(15, 16, 17))
	json.encodeToString(SupportedFormats.serializer(), sfList) assertsIsJson """
		[
			15,
			16,
			17
		]
	""".trimIndent()

	val sfRange = SupportedFormats(minInclusive = 15, maxInclusive = 20)
	json.encodeToString(SupportedFormats.serializer(), sfRange) assertsIsJson """
		{
			"min_inclusive": 15,
			"max_inclusive": 20
		}
	""".trimIndent()
}

private fun testSupportedFormatsDsl() {
	val sectionRange = PackSection(
		description = textComponent("Test"),
		minFormat = packFormat(91),
		maxFormat = packFormat(99)
	).apply {
		supportedFormats(91..99)
	}
	requireNotNull(sectionRange.supportedFormats) assertsIs SupportedFormats(minInclusive = 91, maxInclusive = 99)

	val sectionMinOnly = PackSection(
		description = textComponent("Test"),
		minFormat = packFormat(91),
		maxFormat = packFormat(99)
	).apply {
		supportedFormats(min = 91)
	}
	requireNotNull(sectionMinOnly.supportedFormats) assertsIs SupportedFormats(number = 91)

	val sectionMinMax = PackSection(
		description = textComponent("Test"),
		minFormat = packFormat(91),
		maxFormat = packFormat(99)
	).apply {
		supportedFormats(min = 91, max = 99)
	}
	requireNotNull(sectionMinMax.supportedFormats) assertsIs SupportedFormats(minInclusive = 91, maxInclusive = 99)
}

private fun testPackFormatMajorCreation() {
	val format = packFormat(10)
	format.major assertsIs 10
	format assertsIs PackFormatMajor(10)
}

private fun testPackFormatFullCreation() {
	val format = packFormat(94, 1)
	format assertsIs PackFormatFull(94, 1)
}

private fun testPackFormatSerializationMajor() {
	val format = packFormat(16)
	json.encodeToString(PackFormat.serializer(), format) assertsIsJson "16"
}

private fun testPackFormatSerializationFull() {
	val format = packFormat(94, 1)
	json.encodeToString(PackFormat.serializer(), format) assertsIsJson """
		[
			94,
			1
		]
	""".trimIndent()
}

private fun testPackFormatDeserializationInteger() {
	val jsonStr = "16"
	val format = json.decodeFromString<PackFormat>(jsonStr)
	format assertsIs PackFormatMajor(16)
}

private fun testPackFormatDeserializationArray() {
	val jsonStr = "[94, 1]"
	val format = json.decodeFromString<PackFormat>(jsonStr)
	format assertsIs PackFormatFull(94, 1)
}

private fun testPackFormatDeserializationDecimal() {
	val jsonStr = "94.1"
	val format = json.decodeFromString<PackFormat>(jsonStr)
	format assertsIs PackFormatDecimal(94.1)
}

private fun testPackFormatAsFormatString() {
	val majorFormat = packFormat(16)
	majorFormat.asFormatString() assertsIs "16"

	val fullFormat = packFormat(94, 1)
	fullFormat.asFormatString() assertsIs "94.1"

	val fullFormatZeroMinor = packFormat(16, 0)
	fullFormatZeroMinor.asFormatString() assertsIs "16.0"
}

private fun testPackFormatComparison() {
	val pack1 = PackSection(
		description = textComponent("Test"),
		minFormat = packFormat(10),
		maxFormat = packFormat(12)
	)

	val pack2 = PackSection(
		description = textComponent("Test"),
		minFormat = packFormat(11),
		maxFormat = packFormat(13)
	)

	// Overlapping ranges should be compatible
	pack1.isCompatibleWith(pack2) assertsIs true
	pack2.isCompatibleWith(pack1) assertsIs true

	val pack3 = PackSection(
		description = textComponent("Test"),
		minFormat = packFormat(14),
		maxFormat = packFormat(15)
	)

	// Non-overlapping ranges should not be compatible
	pack1.isCompatibleWith(pack3) assertsIs false
	pack3.isCompatibleWith(pack1) assertsIs false
}

private fun testPackSectionCompatibility() {
	// Test with full version numbers
	val pack1 = PackSection(
		description = textComponent("Test"),
		minFormat = packFormat(94, 0),
		maxFormat = packFormat(94, 5)
	)

	val pack2 = PackSection(
		description = textComponent("Test"),
		minFormat = packFormat(94, 3),
		maxFormat = packFormat(94, 8)
	)

	pack1.isCompatibleWith(pack2) assertsIs true

	val pack3 = PackSection(
		description = textComponent("Test"),
		minFormat = packFormat(95, 0),
		maxFormat = packFormat(95, 5)
	)

	// Different major versions should not be compatible if ranges don't overlap
	pack1.isCompatibleWith(pack3) assertsIs false

	// Test edge cases with major only versions
	val pack4 = PackSection(
		description = textComponent("Test"),
		minFormat = packFormat(10),
		maxFormat = packFormat(12)
	)

	val pack5 = PackSection(
		description = textComponent("Test"),
		minFormat = packFormat(12, 0),
		maxFormat = packFormat(13, 0)
	)

	// Should be compatible at the boundary
	pack4.isCompatibleWith(pack5) assertsIs true
}

private fun testPackSectionConditionalFields() {
	// DP Supporting old versions (threshold 82)
	val sectionOld = PackSection(
		description = textComponent("Old"),
		minFormat = packFormat(40),
		maxFormat = packFormat(60)
	)
	val jsonOld = json.encodeToString(PackSection.serializer(), sectionOld)
	// Should contain pack_format and supported_formats
	jsonOld.contains("\"pack_format\"") assertsIs true
	jsonOld.contains("\"supported_formats\"") assertsIs true

	// DP NOT supporting old versions
	val sectionNew = PackSection(
		description = textComponent("New"),
		minFormat = packFormat(82),
		maxFormat = packFormat(94)
	)
	val jsonNew = json.encodeToString(PackSection.serializer(), sectionNew)
	// Should NOT contain pack_format (unless set) and supported_formats
	jsonNew.contains("\"pack_format\"") assertsIs false
	jsonNew.contains("\"supported_formats\"") assertsIs false

	// Overlay supporting old versions
	val overlayOld = PackOverlayEntry("old", packFormat(40), packFormat(60))
	val jsonOverlayOld = json.encodeToString(PackOverlayEntry.serializer(), overlayOld)
	jsonOverlayOld.contains("\"formats\"") assertsIs true

	// Overlay NOT supporting old versions
	val overlayNew = PackOverlayEntry("new", packFormat(82), packFormat(94))
	val jsonOverlayNew = json.encodeToString(PackOverlayEntry.serializer(), overlayNew)
	jsonOverlayNew.contains("\"formats\"") assertsIs false
}

private fun testPackFormatDecimalCreation() {
	val format = packFormat(94.1)
	format assertsIs PackFormatDecimal(94.1)
	format.major assertsIs 94
}

private fun testPackFormatSerializationDecimal() {
	val format = packFormat(94.1)
	json.encodeToString(PackFormat.serializer(), format) assertsIsJson "94.1"
}
