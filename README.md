# Datapack-DSL

A Kotlin DSL to generate Datapacks for Minecraft Java.

It is made for now for 1.19.2, I don't think that I will support older versions.
For now, it's not on Maven Central, but I'll publish it soon.

Exemple of usage :

```kotlin
fun main() {
	val datapack = dataPack("test") {
		function("display_text") {
			tellraw(allEntities(), textComponent("Hello World!"))
		}

		function("tp_random_entity_to_entity") {
			val entityName = "test"
			val entity = allEntities(true) {
				name = entityName
			}
			summon(Entities.CREEPER, vec3(), nbt {
				this["CustomName"] = textComponent("Hello World!")
			})

			execute {
				asTarget(allEntities(true) {
					limit = 3
					sort = Sort.RANDOM
				})

				run {
					teleport(entity)
				}
			}
		}
	}

	pack {
		description = textComponent {
			text = "Datapack test for "
			color = Color.GOLD
		} + textComponent {
			text = "Datapack-DSL"
			color = Color.AQUA
			bold = true
		}
	}

	datapack.generate()
}
```

There are multiple other new features that I want to add to this project like :

- [X] Pre-generating the list of Blocks/Items/Entities/Advancements/... and a lot of other repositories.
- [X] Pre-generating the list of Sounds/Tags & Advancements using the enum tree generation method.
- [ ] Pre-generating the list of BlockStates, BlockNbtTags, EntityNbtTags etc.
- [ ] API for creating all the JSON-base features of Minecraft (Advancements, Loot Tables, Recipes, ...).
- [X] Better methods for creating NBT tags.
- [ ] An OOP way to create the datapacks (like `player.teleportTo(entity)` instead of `teleport(player, entity)` and `player.giveItem(item)`
  etc).
- [ ] Useful libraries made by different people recreated in Kotlin.
- [ ] Maybe a way to import a datapack and create Kotlin equivalent code.

## Contributing

If you want to contribute to this project, you can do it by creating a pull request or by creating an issue.

Instructions to run the project :

1. First, clone the project.
2. Then, run gradle `generation:run` task to generate the auto-generated files.
3. Finally, run gradle `datapackDSL:run` or `oop:run` to run the examples.

> Note : OOP module is not finished yet and experimental, it might change a lot in the future.
