package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.chatcomponents.click.openUrl
import io.github.ayfri.kore.arguments.chatcomponents.click.runCommand
import io.github.ayfri.kore.arguments.chatcomponents.click.suggestChatMessage
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.commands.say
import io.github.ayfri.kore.features.dialogs.action.onClick
import io.github.ayfri.kore.features.dialogs.action.onSubmit
import io.github.ayfri.kore.features.dialogs.action.submit.commandTemplate
import io.github.ayfri.kore.features.dialogs.action.tooltip
import io.github.ayfri.kore.features.dialogs.body.item
import io.github.ayfri.kore.features.dialogs.body.plainMessage
import io.github.ayfri.kore.features.dialogs.control.boolean
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
			onClick {
				runCommand {
					say("yay")
				}
			}
		}

		no("no") {
			onClick {
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
				"label": "yes",
				"on_click": {
					"action": "run_command",
					"command": "say yay"
				}
			},
			"no": {
				"label": "no",
				"on_click": {
					"action": "suggest_command",
					"command": "no"
				}
			}
		}
	""".trimIndent()

	dialogBuilder.dialogList("test_list", "list") {
		dialogs(Tags.Dialog.PAUSE_SCREEN_ADDITIONS)
		columns = 4
		buttonWidth = 100
		onCancel {
			openUrl("https://minecraft.net")
		}
	}

	dialogs.last() assertsIs """
		{
			"type": "minecraft:dialog_list",
			"title": "list",
			"dialogs": "#minecraft:pause_screen_additions",
			"on_cancel": {
				"action": "open_url",
				"url": "https://minecraft.net"
			},
			"columns": 4,
			"button_width": 100
		}
	""".trimIndent()

	dialogBuilder.notice("test_notice", "title") {
		bodies {
			item(Items.FLINT_AND_STEEL)
			plainMessage("yay")
		}

		action("lol") {
			tooltip("here")
			onClick {
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
				"label": "lol",
				"tooltip": "here",
				"on_click": {
					"action": "run_command",
					"command": "say hihi"
				}
			}
		}
	""".trimIndent()

	dialogBuilder.serverLinks("test_links", "links") {
		columns = 5
		buttonWidth = 100
		onCancel {
			suggestChatMessage("cancelled")
		}
	}

	dialogs.last() assertsIs """
		{
			"type": "minecraft:server_links",
			"title": "links",
			"on_cancel": {
				"action": "suggest_command",
				"command": "cancelled"
			},
			"columns": 5,
			"button_width": 100
		}
	""".trimIndent()

	dialogBuilder.simpleInputForm("test_input", "input") {
		action("submit", "input_id") {
			onSubmit {
				commandTemplate {
					say("hey")
				}
			}
		}

		inputs {
			text("text", "yes", initial = "no")
			numberRange("range", "no", range = 1..10, initial = 4)
			boolean("bool", "maybe", onTrue = "lol")
		}
	}

	dialogs.last() assertsIs """
		{
			"type": "minecraft:simple_input_form",
			"title": "input",
			"inputs": [
				{
					"type": "minecraft:text",
					"key": "text",
					"label": "yes",
					"initial": "no"
				},
				{
					"type": "minecraft:number_range",
					"key": "range",
					"label": "no",
					"start": 1.0,
					"end": 10.0,
					"initial": 4.0
				},
				{
					"type": "minecraft:boolean",
					"key": "bool",
					"label": "maybe",
					"on_true": "lol"
				}
			],
			"action": {
				"label": "submit",
				"id": "input_id",
				"on_submit": {
					"type": "minecraft:command_template",
					"template": "say hey"
				}
			}
		}
	""".trimIndent()
}
