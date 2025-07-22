package io.github.ayfri.kore.arguments

import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.components.contains
import io.github.ayfri.kore.arguments.components.countElement
import io.github.ayfri.kore.arguments.components.data.EquipmentSlot
import io.github.ayfri.kore.arguments.components.matchers.*
import io.github.ayfri.kore.arguments.components.item.FireworkExplosionShape
import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrInt
import io.github.ayfri.kore.assertions.assertsIsJson
import io.github.ayfri.kore.commands.AttributeModifierOperation
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.features.predicates.sub.item.ItemStackSubPredicates
import io.github.ayfri.kore.features.predicates.sub.itemStack
import io.github.ayfri.kore.generated.*
import io.github.ayfri.kore.generated.Enchantments
import io.github.ayfri.kore.utils.pretty
import io.github.ayfri.kore.utils.set

fun componentsMatchersTests() = dataPack("componentsMatchersTests") {
	pretty()

	val attributeModifiers = ItemStackSubPredicates().apply {
		attributeModifiers {
			modifiers {
				contains {
					modifier(
						type = Attributes.SCALE,
						slot = EquipmentSlot.ANY,
						name = "test",
						amount = 1.0,
						operation = AttributeModifierOperation.ADD_MULTIPLIED_BASE
					)
				}
			}
		}
	}

	jsonEncoder.encodeToString(attributeModifiers) assertsIsJson """
		{
			"minecraft:attribute_modifiers": {
				"modifiers": {
					"contains": [
						{
							"type": "minecraft:scale",
							"slot": "any",
							"id": "minecraft:test",
							"amount": 1.0,
							"operation": "add_multiplied_base"
						}
					]
				}
			}
		}
	""".trimIndent()

	val bundlerContents = ItemStackSubPredicates().apply {
		bundlerContents {
			items {
				countElement(size = rangeOrInt(1), test = itemStack(Items.CAKE))
				contains(itemStack(Items.APPLE))
			}
		}
	}

	jsonEncoder.encodeToString(bundlerContents) assertsIsJson """
		{
			"minecraft:bundler_contents": {
				"items": {
					"contains": [
						{
							"items": "minecraft:apple"
						}
					],
					"count": [
						{
							"count": 1,
							"test": {
								"items": "minecraft:cake"
							}
						}
					]
				}
			}
		}
	""".trimIndent()

	val container = ItemStackSubPredicates().apply {
		container {
			items {
				countElement(size = rangeOrInt(1), test = itemStack(Items.CAKE))
				contains(itemStack(Items.APPLE))
			}
		}
	}

	jsonEncoder.encodeToString(container) assertsIsJson """
		{
			"minecraft:container": {
				"items": {
					"contains": [
						{
							"items": "minecraft:apple"
						}
					],
					"count": [
						{
							"count": 1,
							"test": {
								"items": "minecraft:cake"
							}
						}
					]
				}
			}
		}
	""".trimIndent()

	val customData = ItemStackSubPredicates().apply {
		customData {
			this["test"] = 1
		}
	}

	jsonEncoder.encodeToString(customData) assertsIsJson """
		{
			"minecraft:custom_data": {
				"test": 1
			}
		}
	""".trimIndent()

	val damage = ItemStackSubPredicates().apply {
		damage {
			durability(1)
			damage = rangeOrInt(4..5)
		}
	}

	jsonEncoder.encodeToString(damage) assertsIsJson """
		{
			"minecraft:damage": {
				"durability": 1,
				"damage": {
					"min": 4,
					"max": 5
				}
			}
		}
	""".trimIndent()

	val enchantment = ItemStackSubPredicates().apply {
		enchantments {
			enchantment(Enchantments.SHARPNESS, level = 3)
		}
	}

	jsonEncoder.encodeToString(enchantment) assertsIsJson """
		{
			"minecraft:enchantments": [
				{
					"enchantments": "minecraft:sharpness",
					"levels": 3
				}
			]
		}
	""".trimIndent()

	val fireworkExplosion = ItemStackSubPredicates().apply {
		fireworkExplosion {
			shape = FireworkExplosionShape.LARGE_BALL
			hasTrail = true
			hasTwinkle = false
		}
	}

	jsonEncoder.encodeToString(fireworkExplosion) assertsIsJson """
		{
			"minecraft:firework_explosion": {
				"shape": "large_ball",
				"has_trail": true,
				"has_twinkle": false
			}
		}
	""".trimIndent()

	val fireworks = ItemStackSubPredicates().apply {
		fireworks {
			explosions {
				countElement(size = rangeOrInt(1), test = fireworkExplosion.matchers.first() as FireworkExplosionComponentMatcher)
			}
			flightDuration = rangeOrInt(1..2)
		}
	}

	jsonEncoder.encodeToString(fireworks) assertsIsJson """
		{
			"minecraft:fireworks": {
				"explosions": {
					"count": [
						{
							"count": 1,
							"test": {
								"shape": "large_ball",
								"has_trail": true,
								"has_twinkle": false
							}
						}
					]
				},
				"flight_duration": {
					"min": 1,
					"max": 2
				}
			}
		}
	""".trimIndent()

	val jukeboxPlayable = ItemStackSubPredicates().apply {
		jukeboxPlayable {
			songs(JukeboxSongs.OTHERSIDE, JukeboxSongs.CREATOR)
		}
	}

	jsonEncoder.encodeToString(jukeboxPlayable) assertsIsJson """
		{
			"minecraft:jukebox_playable": {
				"song": [
					"minecraft:otherside",
					"minecraft:creator"
				]
			}
		}
	""".trimIndent()


	val potionContents = ItemStackSubPredicates().apply {
		potionContents(Effects.HASTE, Effects.SPEED)
	}

	jsonEncoder.encodeToString(potionContents) assertsIsJson """
		{
			"minecraft:potion_contents": [
				"minecraft:haste",
				"minecraft:speed"
			]
		}
	""".trimIndent()

	val storedEnchantment = ItemStackSubPredicates().apply {
		storedEnchantments {
			enchantment(Enchantments.SHARPNESS, level = 3)
		}
	}

	jsonEncoder.encodeToString(storedEnchantment) assertsIsJson """
		{
			"minecraft:stored_enchantments": [
				{
					"enchantments": "minecraft:sharpness",
					"levels": 3
				}
			]
		}
	""".trimIndent()

	val trim = ItemStackSubPredicates().apply {
		trim {
			materials(TrimMaterials.IRON)
			patterns(TrimPatterns.WARD, TrimPatterns.HOST)
		}
	}

	jsonEncoder.encodeToString(trim) assertsIsJson """
		{
			"minecraft:trim": {
				"material": "minecraft:iron",
				"pattern": [
					"minecraft:ward",
					"minecraft:host"
				]
			}
		}
	""".trimIndent()

	val writableBookContent = ItemStackSubPredicates().apply {
		writableBookContent {
			pages {
				contains {
					add("Hello, world!")
				}
			}
		}
	}

	jsonEncoder.encodeToString(writableBookContent) assertsIsJson """
		{
			"minecraft:writable_book_content": {
				"pages": {
					"contains": [
						"Hello, world!"
					]
				}
			}
		}
	""".trimIndent()

	val writtenBookContent = ItemStackSubPredicates().apply {
		writtenBookContent {
			pages {
				contains {
					this += textComponent("Hello, world!")
				}
			}
			generation(1)
			resolved = true
		}
	}

	jsonEncoder.encodeToString(writtenBookContent) assertsIsJson """
		{
			"minecraft:written_book_content": {
				"pages": {
					"contains": [
						"Hello, world!"
					]
				},
				"generation": 1,
				"resolved": true
			}
		}
	""".trimIndent()
}
