# Datapack-DSL

A Kotlin DSL to generate Datapacks for Minecraft Java.

For now, this library is compatible and made for Minecraft 1.20.1, I don't think that I will support older versions nor Bedrock Edition.
I have plans to maybe create an optional library to create Resource Packs too, but it's not my priority for now.
You can't currently download the library because it's not finished yet, but you can still clone the repository and use it as a local
library.

Example of usage :

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
			description = textComponent("Datapack test for ", Color.GOLD) + text("Datapack-DSL", Color.AQUA) { bold = true }
		}
	}

	datapack.generate()
}
```

There are multiple other new features that I want to add to this project like :

- [X] Pre-generating the list of Blocks/Items/Entities/Advancements/... and a lot of other repositories.
- [X] Pre-generating the list of Sounds/Tags & Advancements using the enum tree generation method.
- [ ] Pre-generating the list of BlockStates, BlockNbtTags, EntityNbtTags etc.
- [X] API for creating all the JSON-based features of Minecraft (Advancements, Loot Tables, Recipes, ...).
- [X] Better methods for creating NBT tags.
- [ ] An OOP way to create the datapacks (like `player.teleportTo(entity)` instead of `teleport(player, entity)` and `player.giveItem(item)`
  etc).
- [ ] Useful libraries made by different people recreated in Kotlin.

## How to run the project

1. First, run gradle `generation:run` task to generate the auto-generated files.
2. Finally, run gradle `datapackDSL:run` or `oop:run` to run the examples.

> Note: OOP module is not finished yet and experimental, it might change a lot in the future.
