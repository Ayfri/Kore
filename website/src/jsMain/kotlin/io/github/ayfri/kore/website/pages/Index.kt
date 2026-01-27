package io.github.ayfri.kore.website.pages

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.core.Page
import io.github.ayfri.kore.website.components.index.*
import io.github.ayfri.kore.website.components.layouts.PageLayout
import io.github.ayfri.kore.website.utils.smMax
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.css.StyleSheet
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.fontSize

@Page
@Composable
fun HomePage() = PageLayout("Home") {
	Style(HomePageStyle)

	// Hero section with code examples
	HeroSection()

	// Quick installation guide
	InstallationSection()

	// Core features section
	FeaturesSection()

	// Masonry grid with code examples
	Masonry(masonryItems)

	// Community platforms section
	CommunitySection()

	// FAQ section
	FaqSection()

	// Call to action section
	CtaSection()
}

object HomePageStyle : StyleSheet() {
	init {
		smMax(child(className("code-toolbar"), type("pre"))) {
			fontSize(0.8.cssRem)
		}
	}
}

// language=kotlin
private const val SIMPLE_API_CODE = """
	fun myDatapack() = dataPack("my_datapack") {
		function("test") {
			say("Hello World!")
		}

		function("gamemode_creative") {
			gamemode(Gamemode.CREATIVE)
			tellraw(
				targets = allPlayers(),
				message = "You are now in creative mode!",
				color = Color.GREEN
			)
		}
	}
"""

// language=kotlin
private const val JSON_LESS_CODE = """
	fun myDatapack() = dataPack("my_datapack") {
		val myRecipe = recipesBuilder.smithingTransform(
			name = "diamond_to_netherite"
		) {
			template(Items.DIAMOND_BLOCK)
			base(Items.DIAMOND_SWORD)
			addition(Items.NETHERITE_INGOT)
			result(Items.NETHERITE_SWORD)
		}

		function("give_recipe") {
			recipe(allPlayers()).give(myRecipe)
		}
	}
"""

// language=kotlin
private const val BIG_PROJECTS_CODE = """
	fun myDatapack() = dataPack("my_datapack") {
		function("execute_my_function") {
			execute {
				ifCondition(myPredicate)

				run {
					function(myFunction)
					scoreboard.player(self()) {
						add(myScore, 1)
					}
				}
			}
		}
	}
"""

// language=kotlin
private const val TYPE_SAFE_CODE = """
	fun myDatapack() = dataPack("my_datapack") {
		function("kore_is_great") {
			advancement(allPlayers {
				gamemode = !Gamemode.CREATIVE
				nbt = nbt {
					this["using_kore"] = true
				}
			}) {
				grant(Advancements.Story.SHINY_GEAR)
			}

			playSound(
				sound = Sounds.Block.EnchantmentTable,
				source = PlaySoundSource.MASTER,
				target = allEntities()
			)
		}
	}
"""

private val masonryItems = listOf(
	MasonryItem(
		"Simple APIs",
		"Kore provides simple and intuitive APIs to create Minecraft datapacks and call commands. Almost all the lists from the game are available as enums, so you are always sure to use the right value.",
		SIMPLE_API_CODE.trimIndent()
	),
	MasonryItem(
		"JSON-less",
		"By using Kore, you don't have to write a single line of JSON ever. You only have one language to learn and maintain. Feature-complete code completion and documentation are available in your IDE.",
		JSON_LESS_CODE.trimIndent()
	),
	MasonryItem(
		"Perfect for big projects",
		"Kore is perfect for big projects, as it allows you to split your code into multiple files and use the full power of Kotlin to manage your code.",
		BIG_PROJECTS_CODE.trimIndent()
	),
	MasonryItem(
		"Type-safe",
		"We crafted generators for dozens of Minecraft lists, so you can be sure to use the right value at the right place. No more typos in your commands, JSON files, or functions.",
		TYPE_SAFE_CODE.trimIndent()
	),
)
