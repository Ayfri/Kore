package io.github.ayfri.kore.serialization

import io.github.ayfri.kore.arguments.actions.runCommand
import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.clickEvent
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.data.sound.SoundEvent
import io.github.ayfri.kore.serializers.JsonSerialName
import io.github.ayfri.kore.utils.decodeFromSnbt
import io.github.ayfri.kore.utils.encodeToSnbt
import io.github.ayfri.kore.utils.nbt
import io.github.ayfri.kore.utils.set
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.NbtCompound

@Serializable
private data class Inner(val innerValue: Int)

@Serializable
private data class Fixture(
	val camelCase: Int,
	@JsonSerialName("KeptName") val keptName: String,
	val nestedClass: Inner,
	val listOfClasses: List<Inner>,
	val mapOfClasses: Map<String, Inner>,
	val rawCompound: NbtCompound,
	val missingValue: String? = null,
	val byteArray: ByteArray,
	val intArray: IntArray,
)

@Serializable
private data class Holder(val optionalInner: Inner? = null)

class SnakeCaseNbtTests : FunSpec({
	val fixture = Fixture(
		camelCase = 1,
		keptName = "a",
		nestedClass = Inner(2),
		listOfClasses = listOf(Inner(3)),
		mapOfClasses = mapOf("camelKey" to Inner(4)),
		rawCompound = nbt { this["CustomKey"] = 5 },
		byteArray = byteArrayOf(6, 7),
		intArray = intArrayOf(8, 9),
	)

	test("class properties are written in snake_case") {
		encodeToSnbt(Inner(1)) assertsIs "{inner_value:1}"
	}

	test("JsonSerialName names, map keys and raw compounds are kept, nested classes renamed, nulls and arrays untouched") {
		encodeToSnbt(fixture) assertsIs "{camel_case:1,KeptName:a,nested_class:{inner_value:2},list_of_classes:[{inner_value:3}]," +
			"map_of_classes:{camelKey:{inner_value:4}},raw_compound:{CustomKey:5},byte_array:[B;6B,7B],int_array:[I;8,9]}"
	}

	test("class properties are read from snake_case") {
		decodeFromSnbt<Inner>("{inner_value:1}") shouldBe Inner(1)
	}

	test("a class decodes back to the value it was encoded from") {
		val decoded = decodeFromSnbt<Fixture>(encodeToSnbt(fixture))

		decoded.nestedClass shouldBe Inner(2)
		decoded.listOfClasses shouldBe listOf(Inner(3))
		decoded.mapOfClasses shouldBe mapOf("camelKey" to Inner(4))
		decoded.missingValue shouldBe null
		encodeToSnbt(decoded) assertsIs encodeToSnbt(fixture)
	}

	test("a nullable class property decodes when present and when absent") {
		decodeFromSnbt<Holder>("{optional_inner:{inner_value:1}}") shouldBe Holder(Inner(1))
		decodeFromSnbt<Holder>("{}") shouldBe Holder()
	}

	test("camelCase names are unknown keys") {
		runCatching { decodeFromSnbt<Inner>("{innerValue:1}") }.isFailure shouldBe true
	}

	test("a chat component reads its snake_case click event") {
		decodeFromSnbt<ChatComponents>("{text:\"a\",click_event:{action:\"run_command\",command:\"/x\"}}") shouldBe
			textComponent("a") { clickEvent { runCommand("/x") } }
	}

	test("a sound event reads its inlined sound and its snake_case form") {
		val inlined = decodeFromSnbt<SoundEvent>("\"minecraft:entity.cat.ambient\"")
		inlined.soundId.asString() assertsIs "minecraft:entity.cat.ambient"
		inlined.range shouldBe null

		val detailed = decodeFromSnbt<SoundEvent>("{sound_id:\"minecraft:entity.cat.ambient\",range:2.0f}")
		detailed.soundId.asString() assertsIs "minecraft:entity.cat.ambient"
		detailed.range shouldBe 2f
	}
})
