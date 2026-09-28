package io.github.ayfri.kore.website.playground

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.website.playground.snippets.advancement.playground as advancement
import io.github.ayfri.kore.website.playground.snippets.customitem.playground as customItem
import io.github.ayfri.kore.website.playground.snippets.helloworld.playground as helloWorld
import io.github.ayfri.kore.website.playground.snippets.loadandtick.playground as loadAndTick
import io.github.ayfri.kore.website.playground.snippets.loottable.playground as lootTable
import io.github.ayfri.kore.website.playground.snippets.macros.playground as macros
import io.github.ayfri.kore.website.playground.snippets.recipes.playground as recipes
import io.github.ayfri.kore.website.playground.snippets.scheduling.playground as scheduling
import io.github.ayfri.kore.website.playground.snippets.selectors.playground as selectors
import io.github.ayfri.kore.website.playground.snippets.tags.playground as tags

enum class Category(val label: String) {
	BASICS("Basics"),
	DATA_DRIVEN("Data-driven"),
	GAMEPLAY("Gameplay"),
}

/**
 * A starter snippet of the playground, following its contract: the editor defines `fun playground(): DataPack`.
 *
 * [slug] locates the source, `hello-world` being `HelloWorld.kt` in package `snippets.helloworld`, and names the
 * generated pack the page fetches. Snippets only use `kore`, the one module the compile backend puts on the classpath.
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
	PlaygroundExample("advancement", "Advancement", "A trigger and a reward, JSON generated from typed Kotlin.", Category.DATA_DRIVEN, ::advancement),
	PlaygroundExample("recipes", "Recipes", "A shaped crafting recipe and a blasting recipe.", Category.DATA_DRIVEN, ::recipes),
	PlaygroundExample("loot-table", "Loot table", "Pools, entries and conditions, rolled from a function.", Category.DATA_DRIVEN, ::lootTable),
	PlaygroundExample("tags", "Tags", "Block and item tags, then a check under each player's feet.", Category.DATA_DRIVEN, ::tags),
	PlaygroundExample("custom-item", "Custom item", "Item components, and the check that recognizes the item in hand.", Category.GAMEPLAY, ::customItem),
	PlaygroundExample("scheduling", "Scheduling", "Telegraph an action now, run it a few seconds later.", Category.GAMEPLAY, ::scheduling),
	PlaygroundExample("macros", "Macros", "A function parameterized at runtime, with typed macro names.", Category.GAMEPLAY, ::macros),
)
