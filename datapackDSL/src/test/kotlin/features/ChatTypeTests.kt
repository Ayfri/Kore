package features

import DataPack
import arguments.Color
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
}
