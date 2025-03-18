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
	<a href="https://discord.gg/BySjRNQ9Je">
		<img src="https://img.shields.io/badge/Discord-5865F2?logo=discord&logoColor=white&style=flat-square" alt="Discord">
	</a>
</p>
<hr>

<p align="center">
	<img src="kore typography 1200x600.png" title="kore typography" alt="kore typography"/>
</p>

This library is compatible and made for Minecraft Java 1.20 and later versions, I don't think I will support older versions nor Bedrock
Edition.<br>
You can still create your own fork and make it compatible with older versions.<br>
I will accept pull requests for older versions on a separate branch.

## Getting Started

You can use the [Kore Template](https://github.com/Ayfri/Kore-Template) to start a new project with Kore.

Or install the library by hand with Gradle.

With Kotlin DSL:

```kotlin
implementation("io.github.ayfri.kore:kore:VERSION")
```

With Groovy DSL:

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

You should also use Java 21 or higher:

```kotlin
kotlin {
	jvmToolchain(21)
}
```

Then create a `Main.kt` file and start writing your datapacks. See the [documentation](https://kore.ayfri.com/docs/home) for more
information.

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

## Community Creations

-   [SimplEnergyKore](https://github.com/e-psi-lon/SimplEnergyKore)
-   [Kore-Bindings](https://github.com/e-psi-lon/Kore-Bindings)
-   [OreCrops](https://github.com/e-psi-lon/OreCrops)
-   [realms-map](https://github.com/Aeltumn/realms-maps)

_How to add your project to the list ?_

-   Create an issue or contact me on [Discord](https://discord.gg/BySjRNQ9Je).

## Features

-   Datapack generation as files or zips or jar files for mod-loaders.
-   Function generation.
-   All commands with all subcommands and multiple syntaxes.
-   Generation of all JSON-based features of Minecraft (Advancements, Loot Tables, Recipes, ...).
-   Selectors.
-   NBT tags.
-   Chat components.
-   Lists for all registries (Blocks, Items, Entities, Advancements, ...).
-   Colors/Vector/Rotation/... classes with common operations.
-   Macros support.
-   Inventory/Scheduler managers.
-   Merging datapacks, even with existing zips.
-   Scoreboard display manager (like on servers).
-   Debugging system inside commands or functions.
-   Common Nbt tags generation (blocks, items, entities, ...).
-   OOP module (experimental).

> Note: All APIs for commands, selectors, NBT tags, ... are public, so you can use them to create your own features.


## Star History

<a href="https://www.star-history.com/#Ayfri/Kore&Date">
 <picture>
   <source media="(prefers-color-scheme: dark)" srcset="https://api.star-history.com/svg?repos=Ayfri/Kore&type=Date&theme=dark" />
   <source media="(prefers-color-scheme: light)" srcset="https://api.star-history.com/svg?repos=Ayfri/Kore&type=Date" />
   <img alt="Star History Chart" src="https://api.star-history.com/svg?repos=Ayfri/Kore&type=Date" />
 </picture>
</a>

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
