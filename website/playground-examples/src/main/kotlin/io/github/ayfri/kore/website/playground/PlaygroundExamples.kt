package io.github.ayfri.kore.website.playground

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.website.playground.snippets.advancement.playground as advancement
import io.github.ayfri.kore.website.playground.snippets.cooldowns.playground as cooldowns
import io.github.ayfri.kore.website.playground.snippets.customenchantment.playground as customEnchantment
import io.github.ayfri.kore.website.playground.snippets.customitem.playground as customItem
import io.github.ayfri.kore.website.playground.snippets.datastorage.playground as dataStorage
import io.github.ayfri.kore.website.playground.snippets.dialogmenu.playground as dialogMenu
import io.github.ayfri.kore.website.playground.snippets.dialogs.playground as dialogs
import io.github.ayfri.kore.website.playground.snippets.displayentities.playground as displayEntities
import io.github.ayfri.kore.website.playground.snippets.events.playground as events
import io.github.ayfri.kore.website.playground.snippets.executechains.playground as executeChains
import io.github.ayfri.kore.website.playground.snippets.helloworld.playground as helloWorld
import io.github.ayfri.kore.website.playground.snippets.itemmodifiers.playground as itemModifiers
import io.github.ayfri.kore.website.playground.snippets.loadandtick.playground as loadAndTick
import io.github.ayfri.kore.website.playground.snippets.loottable.playground as lootTable
import io.github.ayfri.kore.website.playground.snippets.macros.playground as macros
import io.github.ayfri.kore.website.playground.snippets.predicates.playground as predicates
import io.github.ayfri.kore.website.playground.snippets.raycast.playground as raycast
import io.github.ayfri.kore.website.playground.snippets.recipes.playground as recipes
import io.github.ayfri.kore.website.playground.snippets.scheduling.playground as scheduling
import io.github.ayfri.kore.website.playground.snippets.scoreboards.playground as scoreboards
import io.github.ayfri.kore.website.playground.snippets.selectors.playground as selectors
import io.github.ayfri.kore.website.playground.snippets.sidebar.playground as sidebar
import io.github.ayfri.kore.website.playground.snippets.tags.playground as tags
import io.github.ayfri.kore.website.playground.snippets.teams.playground as teams
import io.github.ayfri.kore.website.playground.snippets.textcomponents.playground as textComponents
import io.github.ayfri.kore.website.playground.snippets.timerbossbar.playground as timerBossBar
import io.github.ayfri.kore.website.playground.snippets.villagertrades.playground as villagerTrades

enum class Category(val label: String) {
	BASICS("Basics"),
	DATA_DRIVEN("Data-driven"),
	GAMEPLAY("Gameplay"),
}

/**
 * A starter snippet of the playground, following its contract: the editor defines `fun playground(): DataPack`.
 *
 * [slug] locates the source, `hello-world` being `HelloWorld.kt` in package `snippets.helloworld`, and names the
 * generated pack the page fetches. Snippets use `kore`, `oop` and `helpers`, the modules the compile backend puts on the classpath.
 */
class PlaygroundExample(
	val slug: String,
	val title: String,
	val description: String,
	val category: Category,
	val build: () -> DataPack,
) {
	val fileName = slug.split('-').joinToString("", postfix = ".kt") { part -> part.replaceFirstChar(Char::uppercaseChar) }
	val packageName = "io.github.ayfri.kore.website.playground.snippets.${slug.replace("-", "")}"
}

/** In picker order: categories show up in the order their first example does. */
val playgroundExamples = listOf(
	PlaygroundExample("hello-world", "Hello world", "A pack with a load function and a greeting.", Category.BASICS, ::helloWorld),
	PlaygroundExample("load-and-tick", "Load and tick", "One-time setup on load, and a rule checked every tick.", Category.BASICS, ::loadAndTick),
	PlaygroundExample("selectors", "Selectors", "Name a selector once, reuse it everywhere.", Category.BASICS, ::selectors),
	PlaygroundExample("text-components", "Text components", "Colored chat with hover text and a clickable button.", Category.BASICS, ::textComponents),
	PlaygroundExample("scoreboards", "Scoreboards", "Objectives, a sidebar display and score checks.", Category.BASICS, ::scoreboards),
	PlaygroundExample("execute-chains", "Execute chains", "Target, position and condition in one typed execute block.", Category.BASICS, ::executeChains),
	PlaygroundExample("data-storage", "Data storage", "Keep values in command storage and read them back.", Category.BASICS, ::dataStorage),
	PlaygroundExample("advancement", "Advancement", "A trigger and a reward, JSON generated from typed Kotlin.", Category.DATA_DRIVEN, ::advancement),
	PlaygroundExample("recipes", "Recipes", "A shaped crafting recipe and a blasting recipe.", Category.DATA_DRIVEN, ::recipes),
	PlaygroundExample("loot-table", "Loot table", "Pools, entries and conditions, rolled from a function.", Category.DATA_DRIVEN, ::lootTable),
	PlaygroundExample("tags", "Tags", "Block and item tags, then a check under each player's feet.", Category.DATA_DRIVEN, ::tags),
	PlaygroundExample("predicates", "Predicates", "Named weather and chance checks used from commands.", Category.DATA_DRIVEN, ::predicates),
	PlaygroundExample("item-modifiers", "Item modifiers", "Rename, repair and enchant the item in hand.", Category.DATA_DRIVEN, ::itemModifiers),
	PlaygroundExample("dialogs", "Dialogs", "A confirmation screen whose buttons run functions.", Category.DATA_DRIVEN, ::dialogs),
	PlaygroundExample("villager-trades", "Villager trades", "Custom trades, written as typed Kotlin.", Category.DATA_DRIVEN, ::villagerTrades),
	PlaygroundExample("custom-enchantment", "Custom enchantment", "A new enchantment with its own effect.", Category.DATA_DRIVEN, ::customEnchantment),
	PlaygroundExample("custom-item", "Custom item", "Item components, and the check that recognizes the item in hand.", Category.GAMEPLAY, ::customItem),
	PlaygroundExample("scheduling", "Scheduling", "Telegraph an action now, run it a few seconds later.", Category.GAMEPLAY, ::scheduling),
	PlaygroundExample("macros", "Macros", "A function parameterized at runtime, with typed macro names.", Category.GAMEPLAY, ::macros),
	PlaygroundExample("sidebar", "Sidebar", "A live scoreboard sidebar from the helpers module.", Category.GAMEPLAY, ::sidebar),
	PlaygroundExample("raycast", "Raycast", "A particle laser that stops at the first block it hits.", Category.GAMEPLAY, ::raycast),
	PlaygroundExample("timer-boss-bar", "Timer with boss bar", "A round timer whose progress shows in a boss bar.", Category.GAMEPLAY, ::timerBossBar),
	PlaygroundExample("cooldowns", "Cooldowns", "An ability usable once every few seconds.", Category.GAMEPLAY, ::cooldowns),
	PlaygroundExample("teams", "Teams", "Two teams with colors, prefixes and friendly fire off.", Category.GAMEPLAY, ::teams),
	PlaygroundExample("display-entities", "Display entities", "A floating text and a scaled block display.", Category.GAMEPLAY, ::displayEntities),
	PlaygroundExample("events", "Events", "React to a stick click, a golden apple, a kill and a boss death.", Category.GAMEPLAY, ::events),
	PlaygroundExample("dialog-menu", "Dialog menu", "A kit picker built with the helpers menu DSL.", Category.GAMEPLAY, ::dialogMenu),
)
