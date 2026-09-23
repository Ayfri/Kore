---
root: .components.layouts.MarkdownLayout
title: Minecraft Datapack Menus - Interactive Dialog GUIs with Kore
nav-title: Menus
description: "Build interactive Minecraft menus from dialogs with Kore: buttons running functions for any player, sub-pages, links, pause screen and Smithed menu."
keywords: minecraft menu, datapack menu, dialog menu, datapack gui, minecraft dialog, trigger command, smithed data pack menu, pause screen, kore menu
date-created: 2026-09-23
date-modified: 2026-09-23
routeOverride: /docs/helpers/menus
---

# Menus

A `Menu` is an in-game GUI built from [dialogs](/docs/data-driven/dialogs): pages of buttons, where each button runs
Kotlin-declared commands on the server. Buttons work for every player, operator or not.

```kotlin
import io.github.ayfri.kore.helpers.menus.menu

val kits = menu("kits", "Choose your kit", Color.GOLD) {
	text("Your inventory is replaced by the kit.")

	button("Warrior") {
		clear(self())
		give(self(), Items.IRON_SWORD)
		give(self(), Items.SHIELD)
	}

	button("Archer") {
		clear(self())
		give(self(), Items.BOW)
		give(self(), Items.ARROW, 32)
	}

	page("Help") {
		text("Pick a new kit after each death.")
		link("Rules", "https://example.net/rules")
	}

	pauseScreen = true
}

function("open_kits") { kits.open() }
```

## Buttons

`button(label) { ... }` runs its block as the clicking player, at their position. `reopen = true` shows the page again
afterwards, handy for counters or toggles.

```kotlin
menu("settings", "Settings") {
	button("Toggle night vision", reopen = true) {
		function("my_pack:toggle_night_vision")
	}
}
```

`tooltip` sets the text shown on hover, `buttonWidth` and `columns` control the page layout.

## Pages and links

`page(title) { ... }` adds a button opening a sub-page. Its exit button goes back to the parent page, the root page exit
button closes the menu. `link(label, url)` opens a website after the game's confirmation screen.

```kotlin
menu("guide", "Adventure Guide") {
	page("Bosses") {
		text("The Warden sleeps in the Deep Dark.")
		link("Warden on the wiki", "https://minecraft.wiki/w/Warden")
	}
}
```

Pages are generated at `data/<namespace>/dialog/<menu>.json` and `data/<namespace>/dialog/<menu>_<page>.json`.

## Opening a menu

`open(targets)` shows the root page, to `@s` by default. Each page's `dialog` works anywhere a dialog is expected, such
as `dialogShow` or a text click event.

```kotlin
tellraw(allPlayers(), textComponent("[Choose a kit]", Color.GREEN) {
	clickEvent { showDialog(kits.dialog) }
})
```

## Pause screen and Smithed menu

`pauseScreen = true` adds the menu to the pause screen through the `#minecraft:pause_screen_additions` dialog tag.

`smithed = true` lists the menu in the shared data pack menu of the
[Smithed convention](https://docs.smithed.dev/conventions/data-pack-menu/), so every compatible pack sits behind a
single "Data Packs..." pause screen button. The root exit button then goes back to that list.

```kotlin
menu("about", "Sky Wars") {
	text("Version 1.2.0, by Ayfri")
	link("Report a bug", "https://github.com/Ayfri/Kore/issues")
	smithed = true
}
```

## How it works

A dialog button can only run commands with the clicking player's permissions, and `/function` needs operator rights. So
every button runs `trigger <namespace>.menu.<name> set <n>` instead, which any player can use. The menu generates:

- one function per button,
- a `load` function creating the trigger objective,
- a `tick` function enabling the trigger for all players and running the matching button function for each player who
  clicked, then resetting their score.

A menu without buttons, only pages and links, generates no function at all.
