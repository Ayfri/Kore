<h1 align="center"> Kore </h1>
<p align="center">A Kotlin library to write Datapacks for Minecraft.</p>
<p align="center">
	<a href="https://github.com/Ayfri/Kore">
		<img src="https://img.shields.io/github/stars/Ayfri/Kore?color=darkcyan&logo=github&style=flat-square" title="Stars" alt="Stars"/>
		<img alt="GitHub" src="https://img.shields.io/github/license/Ayfri/Kore?style=flat-square">
		<img alt="GitHub issues" src="https://img.shields.io/github/issues-raw/Ayfri/Kore?color=dark_green&logo=github&style=flat-square">
		<img alt="GitHub closed issues" src="https://img.shields.io/github/issues-closed-raw/Ayfri/Kore?color=blue&logo=github&style=flat-square">
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

You can't currently download the library because it's not finished yet, but you can still clone the repository and use it as a local
library.

Then, you can create a new project and add the library as a dependency.

## Example

```kotlin
fun main() {
	val datapack = dataPack("test") {
		function("display_text") {
			tellraw(allEntities(), textComponent("Hello World!"))
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

	datapack.generate()
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
2. Run gradle `generation:run` task to generate the auto-generated files.
3. Run gradle `kore:run` to run the tests.
4. Make your changes.
5. Create a pull request and wait for it to be reviewed and merged.

You can also create an issue if you find a bug or if you want to suggest a new feature.

## License

This project is licensed under the GNU 3.0 License – see the [LICENSE](LICENSE) file for details.
