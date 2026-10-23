package io.github.ayfri.kore.serialization

import io.github.ayfri.kore.arguments.numbers.toCanonicalString
import io.github.ayfri.kore.arguments.numbers.toStringWithDecimal
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.features.instruments.instrument
import io.github.ayfri.kore.features.predicates.providers.ConstantContextFloatProvider
import io.github.ayfri.kore.features.predicates.providers.ContextFloatProvider
import io.github.ayfri.kore.features.worldgen.dimensiontype.attributes
import io.github.ayfri.kore.features.worldgen.dimensiontype.dimensionType
import io.github.ayfri.kore.features.worldgen.environmentattributes.types.cloudHeight
import io.github.ayfri.kore.utils.pretty
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer
import kotlin.random.Random

private const val EXPECTED_SAMPLE_HASH = 2113338408

class NumberTextTests : FunSpec({
	test("toStringWithDecimal is plain text with a decimal point on every platform") {
		listOf(1f to "1.0", -2f to "-2.0", 0.5f to "0.5", 0.1f to "0.1", 1e-5f to "0.00001", 3e7f to "30000000.0", 123456.7f to "123456.7", 16777217f to "16777216.0")
			.forEach { (value, text) -> value.toStringWithDecimal shouldBe text }

		listOf(1.0 to "1.0", -2.0 to "-2.0", 0.1 to "0.1", 1e-5 to "0.00001", 3e7 to "30000000.0", 1e20 to "100000000000000000000.0", 16777217.0 to "16777217.0")
			.forEach { (value, text) -> value.toStringWithDecimal shouldBe text }
	}

	test("floats and doubles have Java's text on every platform") {
		listOf(
			1f to "1.0", -2f to "-2.0", 0.5f to "0.5", 0.1f to "0.1", 1e-5f to "1.0E-5", 3e7f to "3.0E7", 123456.7f to "123456.7",
			16777217f to "1.6777216E7", 1e-3f to "0.001", 9.999999e6f to "9999999.0", 1e7f to "1.0E7", 1.17549435E-38f to "1.1754944E-38",
			Float.MAX_VALUE to "3.4028235E38", 0f to "0.0", -1.5e-4f to "-1.5E-4", 100f to "100.0", 1234567.9f to "1234567.9", 3202600.25f to "3202600.2", -3202600.75f to "-3202600.8",
			Float.MIN_VALUE to "1.4E-45", Float.fromBits(2) to "2.8E-45", Float.fromBits(3) to "4.2E-45",
		).forEach { (value, text) -> value.toCanonicalString() shouldBe text }

		listOf(
			1.0 to "1.0", -2.0 to "-2.0", 0.1 to "0.1", 1e-5 to "1.0E-5", 3e7 to "3.0E7", 1e20 to "1.0E20", 1e21 to "1.0E21",
			16777217.0 to "1.6777217E7", 1e-3 to "0.001", 9999999.5 to "9999999.5", 1e7 to "1.0E7", 2.2250738585072014E-308 to "2.2250738585072014E-308",
			Double.MAX_VALUE to "1.7976931348623157E308", 123456789.123 to "1.23456789123E8", 0.30000000000000004 to "0.30000000000000004",
			Double.MIN_VALUE to "4.9E-324", Double.fromBits(2) to "9.9E-324", Double.fromBits(4) to "2.0E-323",
		).forEach { (value, text) -> value.toCanonicalString() shouldBe text }

		(-0f).toCanonicalString() shouldBe "-0.0"
		(-0.0).toCanonicalString() shouldBe "-0.0"
		listOf(Float.NaN to "NaN", Float.POSITIVE_INFINITY to "Infinity", Float.NEGATIVE_INFINITY to "-Infinity")
			.forEach { (value, text) -> value.toCanonicalString() shouldBe text }
		listOf(Double.NaN to "NaN", Double.POSITIVE_INFINITY to "Infinity", Double.NEGATIVE_INFINITY to "-Infinity")
			.forEach { (value, text) -> value.toCanonicalString() shouldBe text }
	}

	test("a seeded sample of floats and doubles has the JVM text on every platform") {
		val random = Random(2026)
		var hash = 0
		repeat(4000) {
			val texts = listOf(
				Float.fromBits(random.nextInt()).toCanonicalString(),
				Double.fromBits(random.nextLong()).toCanonicalString(),
				random.nextInt(-20_000_000, 20_000_000).toFloat().toCanonicalString(),
				(random.nextLong(-20_000_000_000, 20_000_000_000) / 1000.0).toCanonicalString(),
				Float.fromBits(((127 + random.nextInt(-30, 40)) shl 23) or (random.nextInt() ushr 9)).toCanonicalString(),
				Double.fromBits(((1023 + random.nextLong(-40, 80)) shl 52) or (random.nextLong() ushr 12)).toCanonicalString(),
			)
			texts.forEach { hash = hash * 31 + it.hashCode() }
		}
		hash shouldBe EXPECTED_SAMPLE_HASH
	}

	test("a data pack file writes whole floats and doubles with a decimal point") {
		dataPack("number_text") {
			instrument("horn", range = 1f, useDuration = 14.5f)
			dimensionType("scaled") {
				ambientLight = 1f
				attributes { cloudHeight(128f) }
				coordinateScale = 8.0
			}

			val horn = instruments.single().generateJson(this)
			horn shouldContain "\"range\":1.0,\"use_duration\":14.5"

			val dimension = dimensionTypes.single().generateJson(this)
			dimension shouldContain "\"coordinate_scale\":8.0"
			dimension shouldContain "\"ambient_light\":1.0"
			dimension shouldContain "\"minecraft:visual/cloud_height\":128.0"
		}
	}

	test("pretty printing keeps the decimal point and the exponent") {
		dataPack("number_text_pretty") {
			pretty()
			instrument("horn", range = 2f, useDuration = 0.00001f)

			val horn = instruments.single().generateJson(this)
			horn shouldContain "\"range\": 2.0"
			horn shouldContain "\"use_duration\": 1.0E-5"
		}
	}

	test("floats written outside a class keep their decimal point") {
		dataPack("number_text_values") {
			jsonEncoder.encodeToString(ListSerializer(Float.serializer()), listOf(1f, 2.5f, 1e-5f)) shouldBe "[1.0,2.5,1.0E-5]"
			jsonEncoder.encodeToString(ListSerializer(Double.serializer().nullable), listOf(3.0, null, 3e7)) shouldBe "[3.0,null,3.0E7]"
			jsonEncoder.encodeToString(ContextFloatProvider.serializer(), ConstantContextFloatProvider(5f)) shouldBe "5.0"
		}
	}
})
