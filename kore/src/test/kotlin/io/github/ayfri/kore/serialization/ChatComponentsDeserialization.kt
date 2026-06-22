package io.github.ayfri.kore.serialization

import io.github.ayfri.kore.arguments.actions.openUrl
import io.github.ayfri.kore.arguments.chatcomponents.*
import io.github.ayfri.kore.arguments.chatcomponents.hover.showText
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.arguments.types.resources.storage
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.generated.Atlases
import io.github.ayfri.kore.generated.Textures
import io.kotest.core.spec.style.FunSpec

private fun ChatComponents.assertsRoundTrips() {
	val encoded = toJsonString()
	val decoded = json.decodeFromString(ChatComponents.serializer(), encoded)
	decoded.toJsonString() assertsIs encoded
}

fun chatComponentsDeserializationTests() {
	textComponent("Hello, world!").assertsRoundTrips()

	(textComponent("Hello, ") + text("world!") {
		color = Color.RED
		bold = true
	}).assertsRoundTrips()

	entityComponent(self(), separator = " ").assertsRoundTrips()
	keybindComponent("key.sprint").assertsRoundTrips()
	nbtComponent("test", self()).assertsRoundTrips()
	nbtComponent("test", storage("my_storage")).assertsRoundTrips()
	objectComponent(Textures.Block.ACACIA_LOG, Atlases.BLOCKS).assertsRoundTrips()
	objectComponent(Textures.Block.ACACIA_LOG).assertsRoundTrips()
	scoreComponent("my_objective", self()).assertsRoundTrips()
	translatedTextComponent("block.minecraft.stone").assertsRoundTrips()
	translatedTextComponent("chat.type.advancement.goal", listOf("Kore", "The best library!")).assertsRoundTrips()

	translatedTextComponent(
		"chat.type.advancement.goal", listOf(
			textComponent("Kore", color = Color.AQUA),
			scoreComponent("kills", self()),
		)
	).assertsRoundTrips()

	textComponent("Hover me!") {
		hoverEvent {
			showText("Hello, world!")
		}
	}.assertsRoundTrips()

	textComponent("Hello, world!") {
		bold = true
		italic = true
		underlined = true
		color = Color.RED
		shadowColor = Color.BLUE
		insertion = "insertion"
	}.assertsRoundTrips()

	textComponent("Click me!") {
		clickEvent {
			openUrl("https://kore.ayfri.com")
		}
	}.assertsRoundTrips()

	val decodedString = json.decodeFromString(ChatComponents.serializer(), "\"plain\"")
	decodedString.toComponent().text assertsIs "plain"
}

class ChatComponentsDeserializationTests : FunSpec({
	test("chat components deserialization") {
		chatComponentsDeserializationTests()
	}
})
