package io.github.ayfri.kore.website.components.playground

/**
 * Starter snippets for the playground. They follow the playground contract: the editor defines
 * `fun playground(): DataPack`, and the hidden harness supplies `main()` around it.
 */
data class PlaygroundExample(
	val title: String,
	val description: String,
	val code: String,
)

val playgroundExamples = listOf(
	PlaygroundExample(
		title = "Hello world",
		description = "A pack with a load function, a tick function and a greeting.",
		code = """
			import io.github.ayfri.kore.arguments.chatcomponents.textComponent
			import io.github.ayfri.kore.arguments.types.literals.allPlayers
			import io.github.ayfri.kore.commands.tellraw
			import io.github.ayfri.kore.dataPack
			import io.github.ayfri.kore.functions.function
			import io.github.ayfri.kore.functions.load
			import io.github.ayfri.kore.pack.pack

			fun playground() = dataPack("starter_kore") {
				pack { description = textComponent("Starter datapack generated with Kore") }

				load("bootstrap") {
					tellraw(allPlayers(), textComponent("[starter_kore] loaded"))
				}

				function("hello") {
					tellraw(allPlayers(), textComponent("Hello from Kore"))
				}
			}
		""".trimIndent(),
	),
	PlaygroundExample(
		title = "Advancement",
		description = "A data-driven feature: JSON generated from typed Kotlin.",
		code = """
			import io.github.ayfri.kore.arguments.chatcomponents.textComponent
			import io.github.ayfri.kore.dataPack
			import io.github.ayfri.kore.features.advancements.AdvancementFrameType
			import io.github.ayfri.kore.features.advancements.advancement
			import io.github.ayfri.kore.features.advancements.display
			import io.github.ayfri.kore.generated.Items
			import io.github.ayfri.kore.pack.pack

			fun playground() = dataPack("advancement_demo") {
				pack { description = textComponent("Advancement demo") }

				advancement("root") {
					display(Items.DIAMOND_SWORD, "Getting started", "Made with Kore") {
						frame = AdvancementFrameType.CHALLENGE
					}
				}
			}
		""".trimIndent(),
	),
)

val defaultExample = playgroundExamples.first()
