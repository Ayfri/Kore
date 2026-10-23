package io.github.ayfri.kore.helpers

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.numbers.seconds
import io.github.ayfri.kore.arguments.numbers.ticks
import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrInt
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.say
import io.github.ayfri.kore.commands.scoreboard.scoreboard
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.generated.arguments.types.DialogArgument
import io.github.ayfri.kore.helpers.conversations.ChoiceStyle
import io.github.ayfri.kore.helpers.conversations.conversation
import io.github.ayfri.kore.helpers.npcs.npc
import io.github.ayfri.kore.utils.pretty
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe

private fun DataPack.generatedFunctionsText(namePrefix: String = "conversation_") =
	generatedFunctions.filter { it.name.startsWith(namePrefix) }.joinToString("\n\n") { "# ${it.name}\n$it" }

class ConversationTests : FunSpec({
	test("a timed pause splits the conversation into segments, a countdown tick and a dispatch") {
		dataPack("conv_tests") {
			pretty()
			conversation("greet") {
				speaker = textComponent("Bob")
				line("Hello!")
				wait(2.seconds)
				line("Bye!")
			}

			generatedFunctionsText() assertsIs """
				# conversation_greet_2
				tellraw @s {type:translatable,translate:"chat.type.text",with:[{type:text,text:Bob},{type:text,text:"Bye!"}]}
				scoreboard players reset @s conv_tests.conversation.greet
				scoreboard players reset @s conv_tests.conversation.greet.wait

				# conversation_greet_1
				tellraw @s {type:translatable,translate:"chat.type.text",with:[{type:text,text:Bob},{type:text,text:"Hello!"}]}
				scoreboard players set @s conv_tests.conversation.greet 2
				scoreboard players set @s conv_tests.conversation.greet.wait 40
				advancement revoke @s only conv_tests:conversation/greet

				# conversation_greet_dispatch
				execute if score @s conv_tests.conversation.greet matches 2 run return run function conv_tests:generated_scopes/conversation_greet_2

				# conversation_greet_tick
				execute unless score @s conv_tests.conversation.greet matches 1.. run return fail
				scoreboard players remove @s conv_tests.conversation.greet.wait 1
				execute if score @s conv_tests.conversation.greet.wait matches 1.. run return run advancement revoke @s only conv_tests:conversation/greet
				function conv_tests:generated_scopes/conversation_greet_dispatch

				# conversation_greet_load
				scoreboard objectives add conv_tests.conversation.greet dummy
				scoreboard objectives add conv_tests.conversation.greet.wait dummy
			""".trimIndent()

			advancements.single().generateJson(this) assertsIs """
				{
					"criteria": {
						"tick": {
							"trigger": "minecraft:tick"
						}
					},
					"rewards": {
						"function": "conv_tests:generated_scopes/conversation_greet_tick"
					}
				}
			""".trimIndent()
		}
	}

	test("only the load tag lists a conversation function, the tick runs from the advancement of the waiting player") {
		dataPack("conv_tests") {
			conversation("greet") {
				line("Hello!")
				wait(2.seconds)
				choice("Pick") { option("A") { } }
			}

			tags.filter { it.type == "function" }.map { it.fileName } shouldBe listOf("load")
		}
	}

	test("a speaker is optional and a line keeps the color of its text") {
		dataPack("conv_tests") {
			conversation("talk") {
				line("Plain")
				speaker = textComponent("Bob", Color.GOLD)
				line("Danger", Color.RED)
			}

			generatedFunctions.first().lines.take(2) shouldBe listOf(
				"""tellraw @s Plain""",
				"""tellraw @s {type:translatable,translate:"chat.type.text",with:[{type:text,color:gold,text:Bob},{type:text,color:red,text:Danger}]}""",
			)
		}
	}

	test("start, advance, resume and cancel are called from the functions of the player") {
		dataPack("conv_tests") {
			val ask = conversation("ask") {
				waitFor()
				choice("Pick") { option("A") { } }
			}
			val plain = conversation("plain") { line("Hi") }

			function("play") {
				ask.start()
				ask.advance()
				ask.resume()
				ask.cancel()
				plain.cancel()
			}.toString() assertsIs """
				execute unless score @s conv_tests.conversation.ask matches 1.. run function conv_tests:generated_scopes/conversation_ask_1
				function conv_tests:generated_scopes/conversation_ask_advance
				function conv_tests:generated_scopes/conversation_ask_resume
				scoreboard players reset @s conv_tests.conversation.ask
				scoreboard players reset @s conv_tests.conversation.ask.choice
				scoreboard players reset @s conv_tests.conversation.plain
			""".trimIndent()

			generatedFunctionsText("conversation_ask_advance") assertsIs """
				# conversation_ask_advance
				execute unless score @s conv_tests.conversation.ask matches 1.. run return run function conv_tests:generated_scopes/conversation_ask_1
				function conv_tests:generated_scopes/conversation_ask_resume
			""".trimIndent()
		}
	}

	test("waitFor needs no tick, advance continues it and a conversation without pauses restarts on advance only") {
		dataPack("conv_tests") {
			val wf = conversation("wf") {
				line("Come back")
				show(DialogArgument("intro", "conv_tests"))
				waitFor()
				step { say("Thanks") }
			}
			val plain = conversation("plain") { line("Hi") }

			function("play") {
				wf.advance()
				plain.advance()
			}

			generatedFunctionsText() assertsIs """
				# conversation_wf_2
				say Thanks
				scoreboard players reset @s conv_tests.conversation.wf

				# conversation_wf_1
				tellraw @s "Come back"
				dialog show @s conv_tests:intro
				scoreboard players set @s conv_tests.conversation.wf 2

				# conversation_wf_load
				scoreboard objectives add conv_tests.conversation.wf dummy

				# conversation_plain_1
				tellraw @s Hi
				scoreboard players reset @s conv_tests.conversation.plain

				# conversation_plain_load
				scoreboard objectives add conv_tests.conversation.plain dummy

				# conversation_wf_resume
				execute if score @s conv_tests.conversation.wf matches 2 run return run function conv_tests:generated_scopes/conversation_wf_2

				# conversation_wf_advance
				execute unless score @s conv_tests.conversation.wf matches 1.. run return run function conv_tests:generated_scopes/conversation_wf_1
				function conv_tests:generated_scopes/conversation_wf_resume

				# conversation_plain_advance
				execute unless score @s conv_tests.conversation.plain matches 1.. run return run function conv_tests:generated_scopes/conversation_plain_1
			""".trimIndent()
			advancements.shouldBeEmpty()
		}
	}

	test("waitUntil guards the segment after it, polling with a one tick countdown") {
		dataPack("conv_tests") {
			conversation("mine") {
				line("Mine 3 blocks")
				waitUntil { score(self(), "mined") greaterThanOrEqualTo 3 }
				line("Done")
			}

			generatedFunctionsText() assertsIs """
				# conversation_mine_2
				execute unless score @s mined matches 3.. run return run advancement revoke @s only conv_tests:conversation/mine
				tellraw @s Done
				scoreboard players reset @s conv_tests.conversation.mine
				scoreboard players reset @s conv_tests.conversation.mine.wait

				# conversation_mine_1
				tellraw @s "Mine 3 blocks"
				scoreboard players set @s conv_tests.conversation.mine 2
				scoreboard players set @s conv_tests.conversation.mine.wait 1
				advancement revoke @s only conv_tests:conversation/mine

				# conversation_mine_dispatch
				execute if score @s conv_tests.conversation.mine matches 2 run return run function conv_tests:generated_scopes/conversation_mine_2

				# conversation_mine_tick
				execute unless score @s conv_tests.conversation.mine matches 1.. run return fail
				scoreboard players remove @s conv_tests.conversation.mine.wait 1
				execute if score @s conv_tests.conversation.mine.wait matches 1.. run return run advancement revoke @s only conv_tests:conversation/mine
				function conv_tests:generated_scopes/conversation_mine_dispatch

				# conversation_mine_load
				scoreboard objectives add conv_tests.conversation.mine dummy
				scoreboard objectives add conv_tests.conversation.mine.wait dummy
			""".trimIndent()
		}
	}

	test("a choice shows a dialog of trigger buttons and a tick runs the clicked option, then goes on") {
		dataPack("conv_tests") {
			pretty()
			conversation("ask") {
				line("Offer")
				choice("Take it?") {
					option("Accept", Color.GREEN) {
						line("Ok")
						wait(1.seconds)
						line("Go")
					}
					option("Decline", Color.RED) { }
				}
				line("Bye")
			}

			generatedFunctionsText() assertsIs """
				# conversation_ask_2
				tellraw @s Bye
				scoreboard players reset @s conv_tests.conversation.ask
				scoreboard players reset @s conv_tests.conversation.ask.wait
				scoreboard players reset @s conv_tests.conversation.ask.choice

				# conversation_ask_4
				tellraw @s Go
				function conv_tests:generated_scopes/conversation_ask_2

				# conversation_ask_3
				scoreboard players reset @s conv_tests.conversation.ask.choice
				tellraw @s Ok
				scoreboard players set @s conv_tests.conversation.ask 4
				scoreboard players set @s conv_tests.conversation.ask.wait 20
				advancement revoke @s only conv_tests:conversation/ask

				# conversation_ask_5
				scoreboard players reset @s conv_tests.conversation.ask.choice
				function conv_tests:generated_scopes/conversation_ask_2

				# conversation_ask_show_2
				scoreboard players enable @s conv_tests.conversation.ask.choice
				scoreboard players set @s conv_tests.conversation.ask.choice 0
				dialog show @s conv_tests:conversation_ask_choice_2

				# conversation_ask_1
				tellraw @s Offer
				scoreboard players set @s conv_tests.conversation.ask 2
				scoreboard players enable @s conv_tests.conversation.ask.choice
				scoreboard players set @s conv_tests.conversation.ask.choice 0
				dialog show @s conv_tests:conversation_ask_choice_2
				advancement revoke @s only conv_tests:conversation/ask

				# conversation_ask_dispatch
				execute if score @s conv_tests.conversation.ask matches 4 run return run function conv_tests:generated_scopes/conversation_ask_4

				# conversation_ask_reenable
				scoreboard players enable @s conv_tests.conversation.ask.choice
				scoreboard players set @s conv_tests.conversation.ask.choice 0

				# conversation_ask_tick
				execute unless score @s conv_tests.conversation.ask matches 1.. run return fail
				execute if score @s conv_tests.conversation.ask matches 2 if score @s conv_tests.conversation.ask.choice matches 1 run return run function conv_tests:generated_scopes/conversation_ask_3
				execute if score @s conv_tests.conversation.ask matches 2 if score @s conv_tests.conversation.ask.choice matches 2 run return run function conv_tests:generated_scopes/conversation_ask_5
				execute if score @s conv_tests.conversation.ask matches 2 unless score @s conv_tests.conversation.ask.choice matches 0 run function conv_tests:generated_scopes/conversation_ask_reenable
				execute if score @s conv_tests.conversation.ask matches 2 run return run advancement revoke @s only conv_tests:conversation/ask
				scoreboard players remove @s conv_tests.conversation.ask.wait 1
				execute if score @s conv_tests.conversation.ask.wait matches 1.. run return run advancement revoke @s only conv_tests:conversation/ask
				function conv_tests:generated_scopes/conversation_ask_dispatch

				# conversation_ask_load
				scoreboard objectives add conv_tests.conversation.ask dummy
				scoreboard objectives add conv_tests.conversation.ask.wait dummy
				scoreboard objectives add conv_tests.conversation.ask.choice trigger
			""".trimIndent()

			dialogs.single().generateJson(this) assertsIs """
				{
					"type": "minecraft:multi_action",
					"title": "Take it?",
					"inputs": [],
					"actions": [
						{
							"action": {
								"type": "minecraft:run_command",
								"command": "trigger conv_tests.conversation.ask.choice set 1"
							},
							"label": {
								"text": "Accept",
								"color": "green",
								"type": "text"
							}
						},
						{
							"action": {
								"type": "minecraft:run_command",
								"command": "trigger conv_tests.conversation.ask.choice set 2"
							},
							"label": {
								"text": "Decline",
								"color": "red",
								"type": "text"
							}
						}
					]
				}
			""".trimIndent()
		}
	}

	test("a chat choice writes clickable options with the same trigger lines as a dialog") {
		dataPack("conv_tests") {
			conversation("ask") {
				speaker = textComponent("Mira", Color.GOLD)
				choiceStyle = ChoiceStyle.CHAT
				choice("Will you help?") {
					option("Accept", Color.GREEN, "Take the pickaxe") { line("Ok") }
					option("Decline", Color.RED) { }
				}
			}

			generatedFunctions.first { it.name == "conversation_ask_show_2" }.lines assertsIs listOf(
				"scoreboard players enable @s conv_tests.conversation.ask.choice",
				"scoreboard players set @s conv_tests.conversation.ask.choice 0",
				"""tellraw @s {type:translatable,translate:"chat.type.text",with:[{type:text,color:gold,text:Mira},{type:text,text:"Will you help?"}]}""",
				"""tellraw @s [{type:text,text:""},{type:text,click_event:{action:run_command,command:"trigger conv_tests.conversation.ask.choice set 1"},extra:[{type:text,color:green,text:"["},{type:text,color:green,text:Accept},{type:text,color:green,text:"]"}],hover_event:{action:show_text,value:"Take the pickaxe"},text:""},{type:text,text:"  "},{type:text,click_event:{action:run_command,command:"trigger conv_tests.conversation.ask.choice set 2"},extra:[{type:text,color:red,text:"["},{type:text,color:red,text:Decline},{type:text,color:red,text:"]"}],text:""}]""",
			)
			dialogs.shouldBeEmpty()
		}
	}

	test("the chat format sets the prompt, the layout and the decoration of the options") {
		dataPack("conv_tests") {
			conversation("ask") {
				choiceStyle = ChoiceStyle.CHAT
				chatFormat = chatFormat.copy(prompt = false)
				choice("Pick") {
					chat {
						decorate = { label -> textComponent("> ") + label }
						separator = null
					}
					option("A") { }
					option("B") { }
				}
			}

			generatedFunctions.first { it.name == "conversation_ask_show_2" }.lines.drop(2) assertsIs listOf(
				"""tellraw @s {type:text,click_event:{action:run_command,command:"trigger conv_tests.conversation.ask.choice set 1"},extra:["> ",A],text:""}""",
				"""tellraw @s {type:text,click_event:{action:run_command,command:"trigger conv_tests.conversation.ask.choice set 2"},extra:["> ",B],text:""}""",
			)
		}
	}

	test("the style of a choice is the default of the conversation unless it gives its own, and the dialog can be tweaked") {
		dataPack("conv_tests") {
			pretty()
			conversation("ask") {
				choiceStyle = ChoiceStyle.CHAT
				choice("Chat") { option("A") { } }
				choice("Dialog", ChoiceStyle.DIALOG) {
					dialog {
						canCloseWithEscape = false
						columns = 1
					}
					option("B", hover = "Tip") { }
				}
			}

			generatedFunctions.first { it.name == "conversation_ask_show_2" }.lines.last().startsWith("tellraw @s ") shouldBe true
			generatedFunctions.first { it.name == "conversation_ask_show_3" }.lines.last() assertsIs "dialog show @s conv_tests:conversation_ask_choice_3"
			dialogs.single().generateJson(this) assertsIs """
				{
					"type": "minecraft:multi_action",
					"title": "Dialog",
					"can_close_with_escape": false,
					"inputs": [],
					"actions": [
						{
							"action": {
								"type": "minecraft:run_command",
								"command": "trigger conv_tests.conversation.ask.choice set 2"
							},
							"label": "B",
							"tooltip": "Tip"
						}
					],
					"columns": 1
				}
			""".trimIndent()
		}
	}

	test("both styles are dispatched by the same tick lines") {
		dataPack("conv_tests") {
			fun DataPack.ask(name: String, style: ChoiceStyle) = conversation(name) {
				choiceStyle = style
				line("Hi")
				choice("Pick") {
					option("A") { line("A") }
					option("B") { line("B") }
				}
			}
			ask("chat_ask", ChoiceStyle.CHAT)
			ask("dialog_ask", ChoiceStyle.DIALOG)

			fun tick(name: String) = generatedFunctions.first { it.name == "conversation_${name}_tick" }.lines.map { it.replace(name, "ask") }
			tick("chat_ask") shouldBe tick("dialog_ask")
		}
	}

	test("a pending choice is shown again by resume, only while it is pending") {
		dataPack("conv_tests") {
			val ask = conversation("ask") { choice("Pick") { option("A") { } } }

			function("play") { ask.resume() }
			generatedFunctionsText("conversation_ask_resume") assertsIs """
				# conversation_ask_resume
				execute if score @s conv_tests.conversation.ask matches 2 run return run function conv_tests:generated_scopes/conversation_ask_show_2
			""".trimIndent()
		}
	}

	test("options with the same body share one function and the tick calls it for both") {
		dataPack("conv_tests") {
			conversation("ask") {
				choice("Pick") {
					option("A") { line("Ok") }
					option("B") { line("Ok") }
				}
			}

			val tick = generatedFunctions.first { it.name == "conversation_ask_tick" }.lines
			tick[1] assertsIs
				"execute if score @s conv_tests.conversation.ask matches 2 if score @s conv_tests.conversation.ask.choice matches 1 run return run function conv_tests:generated_scopes/conversation_ask_3"
			tick[2] assertsIs
				"execute if score @s conv_tests.conversation.ask matches 2 if score @s conv_tests.conversation.ask.choice matches 2 run return run function conv_tests:generated_scopes/conversation_ask_3"
			generatedFunctions.map { it.name }.contains("conversation_ask_4") shouldBe false
		}
	}

	test("a nested choice gets its own dialog and tick lines, with trigger values that no other choice of the conversation uses") {
		dataPack("conv_tests") {
			conversation("deep") {
				choice("First") {
					option("A") { choice("Second") { option("B") { line("Deep") } } }
				}
			}

			dialogs.map { it.fileName } shouldBe listOf("conversation_deep_choice_4", "conversation_deep_choice_2")
			generatedFunctions.first { it.name == "conversation_deep_tick" }.lines.drop(1).map { it.substringBefore(" run ") } shouldBe listOf(
				"execute if score @s conv_tests.conversation.deep matches 2 if score @s conv_tests.conversation.deep.choice matches 1",
				"execute if score @s conv_tests.conversation.deep matches 2 unless score @s conv_tests.conversation.deep.choice matches 0",
				"execute if score @s conv_tests.conversation.deep matches 2",
				"execute if score @s conv_tests.conversation.deep matches 4 if score @s conv_tests.conversation.deep.choice matches 2",
				"execute if score @s conv_tests.conversation.deep matches 4 unless score @s conv_tests.conversation.deep.choice matches 0",
				"execute if score @s conv_tests.conversation.deep matches 4",
			)
			dialogs.map { Regex("choice set (\\d+)").find(it.generateJson(this))!!.groupValues[1] } shouldBe listOf("2", "1")
		}
	}

	test("options are numbered in order across the whole conversation, each choice together and a nested choice before the next step") {
		dataPack("conv_tests") {
			conversation("order") {
				choiceStyle = ChoiceStyle.CHAT
				choice("First?") {
					option("A") { choice("Inner?") { option("X") { }; option("Y") { } } }
					option("B") { }
				}
				choice("Second?", ChoiceStyle.DIALOG) {
					option("C") { }
					option("D") { }
				}
			}

			fun triggers(text: String) = Regex("choice set (\\d+)").findAll(text).map { it.groupValues[1].toInt() }.toList()
			fun show(id: Int) = generatedFunctions.first { it.name == "conversation_order_show_$id" }.lines.joinToString("\n")

			triggers(show(2)) shouldBe listOf(1, 2)
			triggers(show(7)) shouldBe listOf(3, 4)
			triggers(dialogs.single().generateJson(this)) shouldBe listOf(5, 6)
			generatedFunctions.first { it.name == "conversation_order_tick" }.lines.mapNotNull {
				Regex("order matches (\\d+) if score .* matches (\\d+) run").find(it)?.destructured?.toList()
			} shouldBe listOf(
				listOf("2", "1"),
				listOf("2", "2"),
				listOf("3", "5"),
				listOf("3", "6"),
				listOf("7", "3"),
				listOf("7", "4"),
			)
		}
	}

	test("every call of a segment from the dispatch, resume and tick functions is a return run") {
		dataPack("conv_tests") {
			val all = conversation("all") {
				line("Start")
				wait(1.seconds)
				waitUntil { score(self(), "x") greaterThanOrEqualTo 1 }
				waitFor()
				choice("Pick") { option("A") { wait(1.seconds) } }
				line("End")
			}
			function("play") { all.advance() }

			val calls = generatedFunctions.filter { it.name.startsWith("conversation_all_") }
				.flatMap { it.lines }.filter { "run function" in it }
			calls.size shouldBe 8
			calls.filter { " run return run function " !in it }.map { it.substringAfter(" run ") } shouldBe
				listOf("function conv_tests:generated_scopes/conversation_all_reenable")
		}
	}

	test("the state is on the player, no selector or fake player addresses anyone else") {
		dataPack("conv_tests") {
			val all = conversation("all") {
				speaker = textComponent("Bob")
				line("Start")
				wait(1.seconds)
				waitUntil { score(self(), "x") greaterThanOrEqualTo 1 }
				waitFor()
				choice("Pick") { option("A") { step { say("A") } } }
			}
			function("play") {
				all.start()
				all.advance()
				all.resume()
				all.cancel()
			}

			val others = Regex("""@[aepr]\b|\s#\w""")
			generatedFunctions.filter { it.name.startsWith("conversation_all_") }
				.flatMap { it.lines }.filter(others::containsMatchIn).shouldBeEmpty()
			functions.single().lines.filter(others::containsMatchIn).shouldBeEmpty()
		}
	}

	test("wait truncates to whole ticks and refuses less than one") {
		dataPack("conv_tests") {
			conversation("w") {
				wait(0.5.seconds)
				wait(3.ticks)
			}

			generatedFunctions.filter { it.name.startsWith("conversation_w_") }.flatMap { it.lines }
				.filter { it.contains(".wait ") && it.startsWith("scoreboard players set") } shouldBe listOf(
				"scoreboard players set @s conv_tests.conversation.w.wait 3",
				"scoreboard players set @s conv_tests.conversation.w.wait 10",
			)

			shouldThrow<IllegalArgumentException> { conversation("zero") { wait(0.ticks) } }.message shouldBe
				"A conversation wait lasts at least one tick, got 0."
			shouldThrow<IllegalArgumentException> { conversation("short") { wait(0.01.seconds) } }.message shouldBe
				"A conversation wait lasts at least one tick, got 0."
		}
	}

	test("invalid declarations and calls are refused") {
		dataPack("conv_tests") {
			shouldThrow<IllegalArgumentException> { conversation("Bad Name") { } }
			shouldThrow<IllegalArgumentException> { conversation("empty") { choice("Pick") { } } }.message shouldBe
				"A choice of conversation 'empty' needs at least one option."

			val plain = conversation("plain") { line("Hi") }
			shouldThrow<IllegalStateException> { function("play") { plain.resume() } }.message shouldBe
				"Conversation 'plain' has no waitFor or choice to resume."
		}
	}

	test("a conversation name is unique per pack") {
		dataPack("conv_tests") {
			conversation("talk") { line("Hi") }

			shouldThrow<IllegalArgumentException> { conversation("talk") { line("Again") } }.message shouldBe
				"Conversation 'talk' is already declared in this pack."
		}
		dataPack("conv_tests") { conversation("talk") { line("Hi") } }
	}

	test("a quest giver NPC starts the conversation on the first click and answers differently once the quest is accepted") {
		dataPack("conv_tests") {
			npc("quest_giver") {
				val quest = objective("quest")

				val intro = conversation("quest_intro") {
					line("Take this pickaxe?")
					choice("Quest") {
						option("Accept") {
							step { scoreboard.players.set(self(), quest, 1) }
							line("Good luck.")
						}
						option("Decline") { line("Maybe later.") }
					}
				}

				onRightClick {
					execute {
						ifCondition { score(self(), quest, rangeOrInt(1)) }
						run { say("Welcome back") }
					}
					execute {
						unlessCondition { score(self(), quest, rangeOrInt(1)) }
						run { intro.advance() }
					}
				}
			}

			generatedFunctions.first { it.lines.any { line -> "welcome back" in line.lowercase() } }.lines assertsIs listOf(
				"execute if score @s conv_tests.npc.quest_giver.quest matches 1 run say Welcome back",
				"execute unless score @s conv_tests.npc.quest_giver.quest matches 1 run function conv_tests:generated_scopes/conversation_quest_intro_advance",
			)
			generatedFunctions.first { it.name == "conversation_quest_intro_3" }.lines assertsIs listOf(
				"scoreboard players reset @s conv_tests.conversation.quest_intro.choice",
				"scoreboard players set @s conv_tests.npc.quest_giver.quest 1",
				"""tellraw @s "Good luck."""",
				"function conv_tests:generated_scopes/conversation_quest_intro_2",
			)
			generatedFunctions.first { it.name == "conversation_quest_intro_advance" }.lines assertsIs listOf(
				"execute unless score @s conv_tests.conversation.quest_intro matches 1.. run return run function conv_tests:generated_scopes/conversation_quest_intro_1",
				"function conv_tests:generated_scopes/conversation_quest_intro_resume",
			)
		}
	}

	test("builds are reproducible") {
		fun build() = dataPack("conv_tests") {
			val c = conversation("c") {
				speaker = textComponent("Bob")
				line("Hi")
				wait(1.seconds)
				choice("Pick") {
					option("A") { line("Ok") }
					option("B") { line("Ok") }
				}
				waitFor()
			}
			npc("bob") { onRightClick { c.advance() } }
			function("setup") { c.start() }
		}.let {
			it.generatedFunctions.map { f -> "${f.asId()}\n$f" } + it.dialogs.map { d -> d.fileName } + it.advancements.map { a -> a.fileName }
		}

		build() shouldBe build()
	}
})
