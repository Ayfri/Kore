package features

import DataPack
import arguments.colors.Color
import assertions.assertsIs
import features.chattype.*

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
