package io.github.ayfri.kore.bindings

import io.github.ayfri.kore.bindings.api.RemappingState
import io.github.ayfri.kore.commands.say
import io.github.ayfri.kore.features.advancements.advancement
import io.github.ayfri.kore.features.advancements.criteria
import io.github.ayfri.kore.features.advancements.triggers.tick
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.pack.packFormat
import io.kotest.core.spec.style.FunSpec
import kotlin.io.path.readText

fun writerTests() {
	testPackMetadataGenerationUsesPackHelpers()
	testNamespaceWithDotsInMultiNamespace()
	testNamespaceWithHyphensInMultiNamespace()
	testNamespaceWithMixedSpecialCharsInMultiNamespace()
	testNamespaceRemappingRenamesObject()
	testObjectNameAndNamespaceRemappingCombined()
}

fun testPackMetadataGenerationUsesPackHelpers() = newTest("pack_metadata_generation") {
	val pack = createDataPack("pack_metadata_generation") {
		function("fn", namespace = "packmeta") { say("metadata") }

		pack.apply {
			minFormat = packFormat(95)
			maxFormat = packFormat(95, 1)
			@Suppress("DEPRECATION")
			run {
				packFormat = packFormat(90)
			}
		}
	}

	pack.generate()

	val explored = explore(pack.path.toString())
	val srcDir = srcDir()
	writeFiles(explored, srcDir)

	val content = srcDir.resolve("PackMetadataGeneration.kt").readText()

	content.contains("import io.github.ayfri.kore.arguments.chatcomponents.textComponent") assertsIs true
	content.contains("import io.github.ayfri.kore.pack.packFormat") assertsIs true
	content.contains("val pack: PackSection = PackSection(") assertsIs true
	content.contains("description = textComponent(\"Imported datapack\")") assertsIs true
	content.contains("minFormat = packFormat(95)") assertsIs true
	content.contains("maxFormat = packFormat(95, 1)") assertsIs true
	content.contains("packFormat = packFormat(90)") assertsIs true
	content.contains("supportedFormats =") assertsIs false
}

fun testNamespaceWithDotsInMultiNamespace() = newTest("ns_normalize_dots") {
	val pack = createDataPack("ns_normalize_dots") {
		function("fn1", namespace = "my.ns.one") { say("1") }
		function("fn2", namespace = "my.ns.two") { say("2") }
	}

	pack.generate()

	val explored = explore(pack.path.toString())
	val srcDir = srcDir()
	writeFiles(explored, srcDir)

	val content = srcDir.resolve("NsNormalizeDots.kt").readText()

	// Dots are valid in Minecraft namespaces but not in Kotlin identifiers, writer must normalize them
	content.contains("data object my.ns.one") assertsIs false
	content.contains("data object my.ns.two") assertsIs false
	content.contains("data object MyNsOne") assertsIs true
	content.contains("data object MyNsTwo") assertsIs true
}

fun testNamespaceWithHyphensInMultiNamespace() = newTest("ns_normalize_hyphens") {
	val pack = createDataPack("ns_normalize_hyphens") {
		function("fn1", namespace = "some-ns") { say("1") }
		function("fn2", namespace = "another-ns") { say("2") }
	}

	pack.generate()

	val explored = explore(pack.path.toString())
	val srcDir = srcDir()
	writeFiles(explored, srcDir)

	val content = srcDir.resolve("NsNormalizeHyphens.kt").readText()

	// Hyphens are valid in Minecraft namespaces but not in Kotlin identifiers, writer must normalize them
	content.contains("data object some-ns") assertsIs false
	content.contains("data object another-ns") assertsIs false
	content.contains("data object SomeNs") assertsIs true
	content.contains("data object AnotherNs") assertsIs true
}

fun testNamespaceWithMixedSpecialCharsInMultiNamespace() = newTest("ns_normalize_mixed") {
	val pack = createDataPack("ns_normalize_mixed") {
		function("fn1", namespace = "foo.bar-baz_qux") { say("a") }
		function("fn2", namespace = "hello.world") { say("b") }
	}

	pack.generate()

	val explored = explore(pack.path.toString())
	val srcDir = srcDir()
	writeFiles(explored, srcDir)

	val content = srcDir.resolve("NsNormalizeMixed.kt").readText()

	content.contains("data object foo.bar-baz_qux") assertsIs false
	content.contains("data object hello.world") assertsIs false
	content.contains("data object FooBarBazQux") assertsIs true
	content.contains("data object HelloWorld") assertsIs true
}

fun testNamespaceRemappingRenamesObject() = newTest("remapping_namespace") {
	val pack = createDataPack("remap_ns_test") {
		function("fn1", namespace = "internal_ns") { say("1") }
		function("fn2", namespace = "other_ns") { say("2") }
	}

	pack.generate()

	val explored = explore(pack.path.toString())
	val srcDir = srcDir()

	writeFiles(
		explored,
		srcDir,
		RemappingState(
			namespaces = mapOf(
				"internal_ns" to "PublicApi",
				"other_ns" to "PrivateImpl",
			)
		)
	)

	val content = srcDir.resolve("RemapNsTest.kt").readText()

	content.contains("data object PublicApi") assertsIs true
	content.contains("data object PrivateImpl") assertsIs true

	// Default PascalCase-of-namespace names must not appear
	content.contains("data object InternalNs") assertsIs false
	content.contains("data object OtherNs") assertsIs false

	// NAMESPACE constants must still hold the original namespace strings
	val publicApiSection = content.substringAfter("data object PublicApi")
	publicApiSection.contains("const val NAMESPACE: String = \"internal_ns\"") assertsIs true

	val privateImplSection = content.substringAfter("data object PrivateImpl")
	privateImplSection.contains("const val NAMESPACE: String = \"other_ns\"") assertsIs true
}

fun testObjectNameAndNamespaceRemappingCombined() = newTest("remapping_combined") {
	val pack = createDataPack("combined_remap") {
		function("fn1", namespace = "ns_a") { say("a") }
		function("fn2", namespace = "ns_b") { say("b") }

		advancement("adv1") {
			namespace = "ns_a"
			criteria { tick("test") }
		}
	}

	pack.generate()

	val explored = explore(pack.path.toString())
	val srcDir = srcDir()

	writeFiles(
		explored,
		srcDir,
		RemappingState(
			namespaces = mapOf("ns_a" to "CoreFeatures", "ns_b" to "CompatLayer"),
			objectName = "MyDatapack"
		)
	)

	val generatedFile = srcDir.resolve("MyDatapack.kt")
	generatedFile.toFile().exists() assertsIs true

	val content = generatedFile.readText()

	content.contains("data object MyDatapack") assertsIs true
	content.contains("data object CoreFeatures") assertsIs true
	content.contains("data object CompatLayer") assertsIs true
	content.contains("data object NsA") assertsIs false
	content.contains("data object NsB") assertsIs false
	content.contains("data object CombinedRemap") assertsIs false

	// NAMESPACE constants must preserve the original namespace strings
	val coreFeaturesSection = content.substringAfter("data object CoreFeatures")
	coreFeaturesSection.contains("const val NAMESPACE: String = \"ns_a\"") assertsIs true

	val compatLayerSection = content.substringAfter("data object CompatLayer")
	compatLayerSection.contains("const val NAMESPACE: String = \"ns_b\"") assertsIs true
}

class WriterTests : FunSpec({
	test("writer") {
		writerTests()
	}
})
