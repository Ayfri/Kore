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
			summon("minecraft:creeper", coordinate(0, 0, 0), nbt {
				put("CustomName", textComponent("Hello World!").asArg()) // API will maybe change about this
			})
			
			execute {
				asTarget(allEntities(true) {
					sort = Sort.RANDOM
				})
				
				run {
					teleport(entity)
				}
			}
		}
	}
	
	datapack.generate()
}
```

There are multiple other new features that I want to add to this project like :

- [ ] Pre-generating the list of Blocks/Items/Entities/Advancements/... and a lot of other repositories.
- [ ] Pre-generating the list of BlockStates, BlockNbtTags, EntityNbtTags etc.
- [ ] API for creating all the JSON-base features of Minecraft (Advancements, Loot Tables, Recipes, ...).
- [ ] Better methods for creating NBT tags.
- [ ] An OOP way to create the datapacks (like `player.teleportTo(entity)` instead of `teleport(player, entity)` and `player.giveItem(item)` etc).
- [ ] Useful libraries made by different people recreated in Kotlin.
- [ ] Maybe a way to import a datapack and create Kotlin equivalent code.
