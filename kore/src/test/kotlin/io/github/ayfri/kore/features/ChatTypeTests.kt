package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.features.chattype.*

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
