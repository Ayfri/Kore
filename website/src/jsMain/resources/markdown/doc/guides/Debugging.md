---
root: .components.layouts.MarkdownLayout
title: Debugging Kore Datapacks - From In-Game Errors Back to Kotlin
nav-title: Debugging
description: Trace an in-game datapack error back to the Kotlin that wrote it, read generated function names, validate the output with Spyglass and catch load errors in CI.
keywords: kore debugging, datapack debugging, minecraft function error, failed to load function, spyglass datapack, packtest, debug function, generated_scopes
date-created: 2026-10-07
date-modified: 2026-10-07
routeOverride: /docs/guides/debugging
position: 10
---

# Debugging a Kore datapack

Kore writes plain `.mcfunction` and JSON files, so everything that works on a hand-written pack works on a Kore pack:
the game log, `/debug function`, Spyglass, PackTest. This page shows how to use them, and how to get from a file of the
generated pack back to the Kotlin that wrote it.

## Make the output readable first

The defaults aim at releases. While debugging, turn on pretty-printed JSON and write the pack to a folder instead of a
zip, so you can open and search it:

```kotlin
dataPack("my_pack") {
	configuration {
		prettyPrint = true
	}
	// ...
}.generate()
```

[Configuration](/docs/guides/configuration#development-vs-release-setups) shows how to switch it from a build
constant.

## Reading generated function names

The functions you name with `function("give_reward")` keep their name. The ones Kore creates for you land in
`data/<namespace>/function/generated_scopes/`, named after a hash of their body:

| Created by                                   | File name                     |
|----------------------------------------------|-------------------------------|
| `execute { run { ... } }` with several lines | `generated_<hash>`            |
| `load { }` / `tick { }` without a name       | `load_<hash>` / `tick_<hash>` |
| `schedule` with a block                      | `schedule_<hash>`             |
| `hashedGeneratedFunction("on_click") { }`    | `on_click_<hash>`             |

The hash only depends on the commands inside, so the same body gets the same name on every build, and two places
producing the same body share one file. Changing one command changes the name.

To find who calls a generated function, search the pack for its name: the caller holds the
`function my_pack:generated_scopes/generated_<hash>` line. When a generated function keeps coming up, give it a real
name instead:

```kotlin
execute {
	asTarget(allPlayers())
	run("give_reward") {
		give(self(), Items.DIAMOND)
		tellraw(self(), textComponent("Reward!"))
	}
}
// data/my_pack/function/generated_scopes/give_reward.mcfunction
```

## From an in-game error back to Kotlin

When a function contains a command the game can't parse, `/reload` skips that whole function and writes the reason to
the game log (`logs/latest.log`, or the launcher's log window):

```
Failed to load function my_pack:shop/buy
... Whilst parsing command on line 4: Unknown or incomplete command ...
```

1. Open `data/my_pack/function/shop/buy.mcfunction` in the generated pack and go to line 4.
2. `shop/buy` is the name you gave `function("buy", directory = "shop")`, so that is the Kotlin block to look at. For
   a `generated_scopes/` file, find its caller first, as shown above.
3. Search your Kotlin sources for a literal of that line (a score name, a tag, a text) to land on the call.

JSON resources fail the same way, with the resource path (`my_pack:loot_table/chest_reward`) and the field the game
refused.

Most errors never get that far: Kore's builders are typed against the targeted Minecraft version, and generation
throws when two resources write one path with different contents:

```
Two resources of datapack 'my_pack' write 'data/my_pack/function/init.mcfunction' with different contents, rename one of them.
```

A pack that loads in one game version and fails in another usually targets a different version than the game, see
[Version Support](/docs/guides/version-support#one-minecraft-version-per-release).

## Validating the output with Spyglass

[Spyglass](https://spyglassmc.com/) checks every command and JSON file against the game's data, the same checks the game
runs on `/reload`, but inside your editor. Open the generated pack folder (the one holding `pack.mcmeta`) in VS Code with
the [Spyglass extension](https://marketplace.visualstudio.com/items?itemName=SPGoding.datapack-language-server), and
pin the game version in a `spyglass.json` at its root:

```json
{
	"env": {
		"gameVersion": "26.2"
	}
}
```

Use the version in your Kore artifact (`MINECRAFT_VERSION` at runtime). With `"Auto"`, Spyglass reads it from
`pack.mcmeta`.

## Tracing a function at runtime

Kore's [`debug` helpers](/docs/commands/functions#debugging) print each command to the chat as it runs:

```kotlin
function("buy") {
	debug {
		scoreboard.players.remove(self(), "coins", 10)
		give(self(), Items.DIAMOND)
	}
}
```

The vanilla `/debug function my_pack:shop/buy` goes further: it runs the function once and writes a trace of every
command it ran, nested calls included, to a file in the `debug` folder of the game directory (the server folder on a
dedicated server). Generated functions show up in it under their `generated_scopes/` name.

## Catching errors in CI

[PackTest](https://github.com/misode/packtest) is a Fabric mod that runs datapack tests on a headless server. Started
with `-Dpacktest.auto -Dpacktest.auto.annotations`, it exits with the number of failed tests and reports every
resource load error as a GitHub annotation, so a pack that no longer loads fails the build. Its README holds a
ready-to-copy workflow: point its `cp -r datapack ...` step at your Kore output folder.

For gameplay tests, Kore generates the vanilla GameTest registries, see [Test Features](/docs/advanced/test-features).

## Common errors

| Symptom                                                             | Cause                                                           | Fix                                                                                                       |
|---------------------------------------------------------------------|-----------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------|
| The pack shows as incompatible in `/datapack list`                  | The game version differs from the one your Kore release targets | Use the Kore release for that game version, see [Version Support](/docs/guides/version-support)           |
| `Two resources of datapack ... write ... with different contents`   | Two `function("init")`, or two resources with one file name     | Rename one of them                                                                                        |
| `The pack format range of the other pack is different` when merging | The merged pack targets another game version                    | See [Pack format compatibility](/docs/guides/creating-a-datapack#pack-format-compatibility)               |
| A change doesn't show up in game                                    | The game still runs the previous files                          | Run `/reload`, or use [`koreRun --continuous`](/docs/guides/gradle-plugin#the-development-loop) with RCON |
| `Unknown function` when calling a function                          | The function failed to load                                     | Check the game log for `Failed to load function`                                                          |

## What to read next

- [Functions](/docs/commands/functions) - generated functions and the `debug` helpers
- [Configuration](/docs/guides/configuration) - development and release settings
- [Version Support](/docs/guides/version-support) - which game version a Kore release targets
- [Known Issues](/docs/advanced/known-issues) - limits Kore documents
