package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.assertions.assertGeneratorsGenerated
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.features.chattypes.*
import io.github.ayfri.kore.utils.pretty
import io.github.ayfri.kore.utils.testDataPack
import io.kotest.core.spec.style.FunSpec

fun DataPack.chatTypeTests() {
	chatType("test_chat_type") {
		chat {
			parameters(
				ChatTypeParameter.SENDER,
				ChatTypeParameter.CONTENT,
			)

			style {
				color = Color.RED
				bold = true
			}
		}

		narration {
			parameters(
				ChatTypeParameter.SENDER,
				ChatTypeParameter.CONTENT,
			)

			style {
				bold = true
				italic = true
			}
		}
	}
	chatTypes.last() assertsIs """
		{
			"chat": {
				"translation_key": "chat.type.text",
				"parameters": [
					"sender",
					"content"
				],
				"style": {
					"color": "red",
					"bold": true
				}
			},
			"narration": {
				"translation_key": "chat.type.text.narrate",
				"parameters": [
					"sender",
					"content"
				],
				"style": {
					"bold": true,
					"italic": true
				}
			}
		}
	""".trimIndent()
}

class ChatTypeTests : FunSpec({
	test("chat type") {
		testDataPack("chatType") {
			pretty()
			chatTypeTests()
		}.apply {
			assertGeneratorsGenerated()
			generate()
		}
	}
})
