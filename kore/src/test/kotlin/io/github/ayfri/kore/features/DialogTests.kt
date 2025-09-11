package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.actions.openUrl
import io.github.ayfri.kore.arguments.actions.runCommand
import io.github.ayfri.kore.arguments.actions.suggestChatMessage
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.commands.say
import io.github.ayfri.kore.features.dialogs.action.action
import io.github.ayfri.kore.features.dialogs.action.tooltip
import io.github.ayfri.kore.features.dialogs.body.item
import io.github.ayfri.kore.features.dialogs.body.plainMessage
import io.github.ayfri.kore.features.dialogs.control.multiline
import io.github.ayfri.kore.features.dialogs.control.numberRange
import io.github.ayfri.kore.features.dialogs.control.text
import io.github.ayfri.kore.features.dialogs.dialogBuilder
import io.github.ayfri.kore.features.dialogs.types.*
import io.github.ayfri.kore.generated.Items
import io.github.ayfri.kore.generated.Tags

fun DataPack.dialogTests() {
	dialogBuilder.confirmation("test", "title") {
		externalTitle("yop", Color.RED)
		yes("yes") {
			action {
				runCommand {
					say("yay")
				}
			}
		}

		no("no") {
			action {
				suggestChatMessage("no")
			}
		}
	}

	dialogs.last() assertsIs """
		{
			"type": "minecraft:confirmation",
			"title": "title",
			"external_title": {
				"text": "yop",
				"color": "red",
				"type": "text"
			},
			"yes": {
				"action": {
					"type": "minecraft:run_command",
					"command": "say yay"
				},
				"label": "yes"
			},
			"no": {
				"action": {
					"type": "minecraft:suggest_command",
					"command": "no"
				},
				"label": "no"
			}
		}
	""".trimIndent()

	dialogBuilder.dialogList("test_list", "list") {
		dialogs(Tags.Dialog.PAUSE_SCREEN_ADDITIONS)
		columns = 4
		buttonWidth = 100
		exitAction("open_url") {
			action {
				openUrl("https://minecraft.net")
			}
		}
	}

	dialogs.last() assertsIs """
		{
			"type": "minecraft:dialog_list",
			"title": "list",
			"dialogs": "#minecraft:pause_screen_additions",
			"exit_action": {
				"action": {
					"type": "minecraft:open_url",
					"url": "https://minecraft.net"
				},
				"label": "open_url"
			},
			"columns": 4,
			"button_width": 100
		}
	""".trimIndent()

	dialogBuilder.multiAction("test_multi_action", "Multi Action") {
		columns = 3

		inputs {
			text("username", "Username") {
				maxLength = 20
			}
			text("description", "Description") {
				multiline(maxLines = 5, height = 100)
			}
			numberRange("level", "Level", range = 1..100, initial = 1) {
				step = 1f
			}
		}

		actions {
			action("My Submit") {
				action {
					runCommand {
						say("Submitted!")
					}
				}
			}
		}
	}

	dialogs.last() assertsIs """
		{
			"type": "minecraft:multi_action",
			"title": "Multi Action",
			"actions": [
				{
					"action": {
						"type": "minecraft:run_command",
						"command": "say Submitted!"
					},
					"label": "My Submit"
				}
			],
			"columns": 3,
			"inputs": [
				{
					"type": "minecraft:text",
					"key": "username",
					"label": "Username",
					"max_length": 20
				},
				{
					"type": "minecraft:text",
					"key": "description",
					"label": "Description",
					"multiline": {
						"max_lines": 5,
						"height": 100
					}
				},
				{
					"type": "minecraft:number_range",
					"key": "level",
					"label": "Level",
					"start": 1.0,
					"end": 100.0,
					"step": 1.0,
					"initial": 1.0
				}
			]
		}
	""".trimIndent()

	dialogBuilder.notice("test_notice", "title") {
		bodies {
			item(Items.FLINT_AND_STEEL)
			plainMessage("yay")
		}

		action("lol") {
			tooltip("here")
			action {
				runCommand {
					say("hihi")
				}
			}
		}
	}

	dialogs.last() assertsIs """
		{
			"type": "minecraft:notice",
			"title": "title",
			"body": [
				{
					"type": "minecraft:item",
					"item": {
						"id": "minecraft:flint_and_steel"
					}
				},
				{
					"type": "minecraft:plain_message",
					"contents": "yay"
				}
			],
			"action": {
				"action": {
					"type": "minecraft:run_command",
					"command": "say hihi"
				},
				"label": "lol",
				"tooltip": "here"
			}
		}
	""".trimIndent()

	dialogBuilder.serverLinks("test_links", "links") {
		buttonWidth = 100
		columns = 5
		exitAction("cancelled") {
			action {
				suggestChatMessage("cancelled")
			}
		}
	}

	dialogs.last() assertsIs """
		{
			"type": "minecraft:server_links",
			"title": "links",
			"button_width": 100,
			"columns": 5,
			"exit_action": {
				"action": {
					"type": "minecraft:suggest_command",
					"command": "cancelled"
				},
				"label": "cancelled"
			}
		}
	""".trimIndent()
}
