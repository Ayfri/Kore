<h1 align="center"> Kore </h1>
<p align="center">A Kotlin library to write Datapacks for Minecraft.</p>
<p align="center">
	<a href="https://github.com/Ayfri/Kore">
		<img src="https://img.shields.io/github/stars/Ayfri/Kore?color=darkcyan&logo=github&style=flat-square" title="Stars" alt="Stars"/>
		<img alt="GitHub" src="https://img.shields.io/github/license/Ayfri/Kore?style=flat-square">
		<img alt="GitHub issues" src="https://img.shields.io/github/issues-raw/Ayfri/Kore?color=dark_green&logo=github&style=flat-square">
		<img alt="GitHub closed issues" src="https://img.shields.io/github/issues-closed-raw/Ayfri/Kore?color=blue&logo=github&style=flat-square">
		<img alt="Maven Central" src="https://img.shields.io/maven-central/v/io.github.ayfri.kore/kore?style=flat-square&logo=gradle&label=latest%20version">
	</a>
	<a href="https://kotlinlang.slack.com/archives/C066G9BF66A">
		<img src="https://img.shields.io/badge/%23kore-4A154B?logo=slack&logoColor=white&style=flat-square" alt="Kotlin Slack">
	</a>
</p>
<hr>

<p align="center">
	<img src="kore typography 1200x600.png" title="kore typography" alt="kore typography"/>
</p>

This library is compatible and made for Minecraft Java 1.20.2, I don't think I will support older versions nor Bedrock Edition.<br>
You can still create your own fork and make it compatible with older versions.<br>
I will accept pull requests for older versions on a separate branch.

## Getting Started

Install the library with Gradle, with Kotlin DSL:

```kotlin
implementation("io.github.ayfri.kore:kore:VERSION")
```

Or with Groovy DSL:

```groovy
implementation 'io.github.ayfri.kore:kore:VERSION'
```

Then activate the `-Xcontext-receivers` compiler option:

```kotlin
kotlin {
	compilerOptions {
		freeCompilerArgs.add("-Xcontext-receivers")
	}
}
```

You should also use Java 17 or higher:

```kotlin
kotlin {
	jvmToolchain(17)
}
```

Then create a `Main.kt` file and start writing your datapacks.
See the [documentation](https://github.com/Ayfri/Kore/wiki/Creating-a-Datapack) for more information.

## Example

```kotlin
fun main() {
	val datapack = dataPack("test") {
		function("display_text") {
			tellraw(allPlayers(), textComponent("Hello World!"))
		}

		recipes {
			craftingShaped("enchanted_golden_apple") {
				pattern(
					"GGG",
					"GAG",
					"GGG"
				)

				key("G", Items.GOLD_BLOCK)
				key("A", Items.APPLE)

				result(Items.ENCHANTED_GOLDEN_APPLE)
			}
		}

		function("tp_random_entity_to_entity") {
			val entityName = "test"
			val entity = allEntities(limitToOne = true) {
				name = entityName
			}

			summon(Entities.CREEPER, vec3(), nbt {
				this["CustomName"] = textComponent("Hello World!")
			})

			execute {
				asTarget(allEntities {
					limit = 3
					sort = Sort.RANDOM
				})

				ifCondition {
					score(self(), "test") lessThan 10
				}

				run {
					teleport(entity)
				}
			}
		}

		pack {
			description = textComponent("Datapack test for ", Color.GOLD) + text("Kore", Color.AQUA) { bold = true }
		}
	}

	datapack.generateZip()
}
```

## Features

- DataPack generation.
- Function generation.
- All commands with all subcommands and multiple syntaxes.
- Generation of all JSON-based features of Minecraft (Advancements, Loot Tables, Recipes, ...).
- Selectors.
- NBT tags.
- Chat components.
- Lists for all registries (Blocks, Items, Entities, Advancements, ...).
- Colors/Vector/Rotation/... classes with common operations.
- Macros support.
- Inventory/Scheduler managers.
- Scoreboard display manager (like on servers).
- Debugging system inside commands or functions.
- Common Nbt tags generation (blocks, items, entities, ...).
- OOP module (experimental).

> Note: All APIs for commands, selectors, NBT tags, ... are public, so you can use them to create your own features.

## Contributing

If you want to contribute to this project, you can follow these steps:

1. Fork the repository.
2. Run gradle `kore:run` to run the tests.
3. Make your changes.
4. Create a pull request and wait for it to be reviewed and merged.

You can also create an issue if you find a bug or if you want to suggest a new feature.

## Support

If you want to support the project, please consider donating !

<a href="https://www.buymeacoffee.com/ayfri" target="_blank"><img src="https://www.buymeacoffee.com/assets/img/custom_images/orange_img.png" alt="Buy Me A Coffee" style="height: 41px !important;width: 174px !important;box-shadow: 0px 3px 2px 0px rgba(190, 190, 190, 0.5) !important;-webkit-box-shadow: 0px 3px 2px 0px rgba(190, 190, 190, 0.5) !important;" ></a>

## License

This project is licensed under the GNU 3.0 License – see the [LICENSE](LICENSE) file for details.
