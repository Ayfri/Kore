package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.WEAPON
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.colors.FormattingColor
import io.github.ayfri.kore.arguments.components.data.EquipmentSlot
import io.github.ayfri.kore.arguments.components.item.*
import io.github.ayfri.kore.arguments.components.matchers.exists
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.arguments.types.literals.uuid
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.commands.items
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.features.itemmodifiers.formulas.BinomialWithBonusCount
import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.itemmodifiers.functions.*
import io.github.ayfri.kore.features.itemmodifiers.itemModifier
import io.github.ayfri.kore.features.itemmodifiers.types.Mode
import io.github.ayfri.kore.features.loottables.entries.item
import io.github.ayfri.kore.features.predicates.conditions.randomChance
import io.github.ayfri.kore.features.predicates.conditions.weatherCheck
import io.github.ayfri.kore.features.predicates.providers.add
import io.github.ayfri.kore.features.predicates.providers.constant
import io.github.ayfri.kore.features.predicates.providers.enchantmentLevel
import io.github.ayfri.kore.features.predicates.providers.fromFloat
import io.github.ayfri.kore.features.predicates.providers.fromInt
import io.github.ayfri.kore.features.predicates.providers.reference
import io.github.ayfri.kore.features.predicates.providers.score
import io.github.ayfri.kore.features.predicates.providers.uniform
import io.github.ayfri.kore.features.predicates.sub.predicates
import io.github.ayfri.kore.features.predicates.types.EntityTarget
import io.github.ayfri.kore.functions.load
import io.github.ayfri.kore.generated.*
import io.github.ayfri.kore.generated.Enchantments
import io.github.ayfri.kore.generated.arguments.tagged.ItemModifierTagArgument
import io.github.ayfri.kore.generated.arguments.types.BlockTransformerArgument
import io.github.ayfri.kore.generated.arguments.types.ContextIntProviderArgument
import io.github.ayfri.kore.generated.arguments.types.ItemModifierArgument
import io.github.ayfri.kore.generated.arguments.types.PredicateArgument
import io.github.ayfri.kore.utils.pretty
import io.github.ayfri.kore.utils.set
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.types.shouldBeInstanceOf

private fun DataPack.roundTripFile(modifier: ItemModifier) {
	val json = modifier.generateJson(this)
	val decoded = jsonEncoder.decodeFromString(ItemFunction.serializer(), json)

	jsonEncoder.encodeToString(ItemFunction.serializer(), decoded) assertsIs json
}

fun DataPack.itemModifierTests() {
	itemModifier("apply_bonus") {
		applyBonus(Enchantments.SHARPNESS, BinomialWithBonusCount(extra = 1, probability = 0.5f))
	}

	itemModifiers.last() assertsIs """
		{
			"type": "minecraft:apply_bonus",
			"enchantment": "minecraft:sharpness",
			"formula": "minecraft:binomial_with_bonus_count",
			"parameters": {
				"extra": 1,
				"probability": 0.5
			}
		}
	""".trimIndent()

	itemModifier("copy_components") {
		copyComponents {
			exclude(ItemComponentTypes.FOOD, ItemComponentTypes.FIREWORKS)
			include(ItemComponentTypes.BLOCK_ENTITY_DATA)
		}
	}

	itemModifiers.last() assertsIs """
		{
			"type": "minecraft:copy_components",
			"source": "block_entity",
			"include": [
				"minecraft:block_entity_data"
			],
			"exclude": [
				"minecraft:food",
				"minecraft:fireworks"
			]
		}
	""".trimIndent()

	itemModifier("copy_custom_data") {
		copyCustomData("kore:test") {
			append("foo", "bar")
			merge("baz", "qux")
			replace("quux", "corge")
		}
	}

	itemModifiers.last() assertsIs """
		{
			"type": "minecraft:copy_custom_data",
			"source": {
				"type": "minecraft:storage",
				"source": "kore:test"
			},
			"ops": [
				{
					"op": "append",
					"source": "foo",
					"target": "bar"
				},
				{
					"op": "merge",
					"source": "baz",
					"target": "qux"
				},
				{
					"op": "replace",
					"source": "quux",
					"target": "corge"
				}
			]
		}
	""".trimIndent()

	itemModifier("copy_custom_data_context") {
		copyCustomData(Source.THIS) {
			replace("Tags", "tags")
		}
	}

	itemModifiers.last() assertsIs """
		{
			"type": "minecraft:copy_custom_data",
			"source": {
				"type": "minecraft:context",
				"target": "this"
			},
			"ops": [
				{
					"op": "replace",
					"source": "Tags",
					"target": "tags"
				}
			]
		}
	""".trimIndent()

	itemModifier("copy_name") {
		copyName(Source.ATTACKING_PLAYER)
	}

	itemModifiers.last() assertsIs """
		{
			"type": "minecraft:copy_name",
			"source": "attacking_player"
		}
	""".trimIndent()

	itemModifier("copy_state") {
		copyState(Blocks.OAK_LOG, "axis", "waterlogged")
	}

	itemModifiers.last() assertsIs """
		{
			"type": "minecraft:copy_state",
			"block": "minecraft:oak_log",
			"properties": [
				"axis",
				"waterlogged"
			]
		}
	""".trimIndent()

	itemModifier("discard") {
		discard()
	}

	itemModifiers.last() assertsIs """
		{
			"type": "minecraft:discard"
		}
	""".trimIndent()

	itemModifier("enchanted_count_increase") {
		enchantedCountIncrease(Enchantments.SHARPNESS, 1f, limit = 5)
	}

	itemModifiers.last() assertsIs """
		{
			"type": "minecraft:enchanted_count_increase",
			"enchantment": "minecraft:sharpness",
			"count": 1.0,
			"limit": 5
		}
	""".trimIndent()

	itemModifier("enchant_randomly") {
		enchantRandomly(Enchantments.SHARPNESS, Enchantments.LOOTING, onlyCompatible = true)
	}

	itemModifiers.last() assertsIs """
		{
			"type": "minecraft:enchant_randomly",
			"options": [
				"minecraft:sharpness",
				"minecraft:looting"
			],
			"only_compatible": true
		}
	""".trimIndent()

	itemModifier("enchant_randomly_any") {
		enchantRandomly()
	}

	itemModifiers.last() assertsIs """
		{
			"type": "minecraft:enchant_randomly"
		}
	""".trimIndent()

	itemModifier("enchant_randomly_with_additional_cost") {
		enchantRandomly(Enchantments.SHARPNESS) {
			includeAdditionalCostComponent = true
		}
	}

	itemModifiers.last() assertsIs """
		{
			"type": "minecraft:enchant_randomly",
			"include_additional_cost_component": true,
			"options": "minecraft:sharpness"
		}
	""".trimIndent()

	itemModifier("enchant_with_levels") {
		enchantWithLevels(Enchantments.SHARPNESS, levels = constant(5))
	}

	itemModifiers.last() assertsIs """
		{
			"type": "minecraft:enchant_with_levels",
			"levels": 5,
			"options": "minecraft:sharpness"
		}
	""".trimIndent()

	itemModifier("enchant_with_levels_with_additional_cost") {
		enchantWithLevels(levels = constant(30)) {
			includeAdditionalCostComponent = false
		}
	}

	itemModifiers.last() assertsIs """
		{
			"type": "minecraft:enchant_with_levels",
			"levels": 30,
			"include_additional_cost_component": false
		}
	""".trimIndent()

	itemModifier("exploration_map") {
		explorationMap(Tags.Worldgen.Structure.MINESHAFT) {
			decoration = MapDecorationTypes.FRAME
			searchRadius = 20
			skipExistingChunks = true
			zoom = 1
		}
	}

	itemModifiers.last() assertsIs """
		{
			"type": "minecraft:exploration_map",
			"decoration": "minecraft:frame",
			"destination": "#minecraft:mineshaft",
			"search_radius": 20,
			"skip_existing_chunks": true,
			"zoom": 1
		}
	""".trimIndent()
	roundTrip(itemModifiers.last())

	itemModifier("exploration_map_shipwreck") {
		explorationMap(
			Tags.Worldgen.Structure.ON_TREASURE_MAPS,
			decoration = MapDecorationTypes.RED_X,
			skipExistingChunks = false,
			zoom = 1,
		)
		setName("Map", color = Color.BLACK)
	}

	itemModifiers.last() assertsIs """
		{
			"type": "minecraft:sequence",
			"functions": [
				{
					"type": "minecraft:exploration_map",
					"decoration": "minecraft:red_x",
					"destination": "#minecraft:on_treasure_maps",
					"skip_existing_chunks": false,
					"zoom": 1
				},
				{
					"type": "minecraft:set_name",
					"name": {
						"text": "Map",
						"color": "black",
						"type": "text"
					}
				}
			]
		}
	""".trimIndent()

	itemModifier("exploration_map_cartographer") {
		explorationMap(
			Tags.Worldgen.Structure.ON_OCEAN_MONUMENT_MAPS,
			decoration = MapDecorationTypes.MONUMENT,
			searchRadius = 100,
		)
	}

	itemModifiers.last() assertsIs """
		{
			"type": "minecraft:exploration_map",
			"decoration": "minecraft:monument",
			"destination": "#minecraft:on_ocean_monument_maps",
			"search_radius": 100
		}
	""".trimIndent()

	itemModifier("exploration_map_two_structures") {
		explorationMap(ConfiguredStructures.MINESHAFT, ConfiguredStructures.MONUMENT)
	}

	itemModifiers.last() assertsIs """
		{
			"type": "minecraft:exploration_map",
			"destination": [
				"minecraft:mineshaft",
				"minecraft:monument"
			]
		}
	""".trimIndent()
	roundTrip(itemModifiers.last())

	itemModifier("exploration_map_discard_without_structure") {
		explorationMap(Tags.Worldgen.Structure.ON_TREASURE_MAPS, decoration = MapDecorationTypes.RED_X, zoom = 1)
		filtered {
			itemFilter { predicates { exists(ItemComponentTypes.MAP_ID) } }
			onFail { discard() }
		}
	}

	itemModifiers.last() assertsIs """
		{
			"type": "minecraft:sequence",
			"functions": [
				{
					"type": "minecraft:exploration_map",
					"decoration": "minecraft:red_x",
					"destination": "#minecraft:on_treasure_maps",
					"zoom": 1
				},
				{
					"type": "minecraft:filtered",
					"item_filter": {
						"predicates": {
							"minecraft:map_id": {}
						}
					},
					"on_fail": {
						"type": "minecraft:discard"
					}
				}
			]
		}
	""".trimIndent()

	itemModifier("explosion_decay") {
		explosionDecay()
	}

	itemModifiers.last() assertsIs """
		{
			"type": "minecraft:explosion_decay"
		}
	""".trimIndent()

	itemModifier("fill_player_head") {
		fillPlayerHead(Source.THIS)
	}

	itemModifiers.last() assertsIs """
		{
			"type": "minecraft:fill_player_head",
			"entity": "this"
		}
	""".trimIndent()

	itemModifier("filtered") {
		filtered {
			itemFilter(Items.APPLE)

			onPass {
				setName("Test")
			}

			onFail {
				discard()
			}
		}
	}

	itemModifiers.last() assertsIs """
		{
			"type": "minecraft:filtered",
			"item_filter": {
				"items": "minecraft:apple"
			},
			"on_fail": {
				"type": "minecraft:discard"
			},
			"on_pass": {
				"type": "minecraft:set_name",
				"name": "Test"
			}
		}
	""".trimIndent()

	itemModifier("furnace_smelt") {
		furnaceSmelt()
	}

	itemModifiers.last() assertsIs """
		{
			"type": "minecraft:furnace_smelt"
		}
	""".trimIndent()

	itemModifier("limit_count") {
		limitCount(
			min = score("my_score", EntityTarget.THIS, constant(4)),
			max = fromFloat(enchantmentLevel(5))
		)
	}

	itemModifiers.last() assertsIs """
		{
			"type": "minecraft:limit_count",
			"limit": {
				"max": {
					"type": "minecraft:from_float",
					"input": {
						"type": "minecraft:enchantment_level",
						"amount": 5
					}
				},
				"min": {
					"type": "minecraft:score",
					"fallback": 4,
					"score": "my_score",
					"target": {
						"type": "minecraft:context",
						"target": "this"
					}
				}
			}
		}
	""".trimIndent()
	roundTrip(itemModifiers.last())

	itemModifier("limit_count_range") {
		limitCount(1..32)
	}

	itemModifiers.last() assertsIs """
		{
			"type": "minecraft:limit_count",
			"limit": {
				"max": 32,
				"min": 1
			}
		}
	""".trimIndent()
	roundTrip(itemModifiers.last())

	itemModifier("limit_count_max") {
		limitCount(max = constant(64))
	}

	itemModifiers.last() assertsIs """
		{
			"type": "minecraft:limit_count",
			"limit": {
				"max": 64
			}
		}
	""".trimIndent()
	roundTrip(itemModifiers.last())

	itemModifier("modify_contents") {
		modifyContents(ContentComponentTypes.BUNDLE_CONTENTS) {
			modifier {
				setName("Test")
			}
		}
	}

	itemModifiers.last() assertsIs """
		{
			"type": "minecraft:modify_contents",
			"component": "bundle_contents",
			"modifier": {
				"type": "minecraft:set_name",
				"name": "Test"
			}
		}
	""".trimIndent()

	itemModifier("reference") {
		reference(ItemModifierArgument("my_modifier", "kore"))
	}

	itemModifiers.last() assertsIs """
		{
			"type": "minecraft:sequence",
			"functions": "kore:my_modifier"
		}
	""".trimIndent()
	roundTrip(itemModifiers.last())
	roundTripFile(itemModifiers.last())

	itemModifier("reference_tag") {
		reference(ItemModifierTagArgument("my_tag", "kore"))
	}

	itemModifiers.last() assertsIs """
		{
			"type": "minecraft:sequence",
			"functions": "#kore:my_tag"
		}
	""".trimIndent()

	itemModifier("two_references") {
		reference(ItemModifierArgument("a", "kore"))
		reference(ItemModifierArgument("b", "kore"))
	}

	itemModifiers.last() assertsIs """
		{
			"type": "minecraft:sequence",
			"functions": [
				"kore:a",
				"kore:b"
			]
		}
	""".trimIndent()
	roundTripFile(itemModifiers.last())

	itemModifier("reference_mixed") {
		reference(ItemModifierArgument("a", "kore"))
		setCount(1)
	}

	itemModifiers.last() assertsIs """
		{
			"type": "minecraft:sequence",
			"functions": [
				"kore:a",
				{
					"type": "minecraft:set_count",
					"count": 1
				}
			]
		}
	""".trimIndent()
	roundTripFile(itemModifiers.last())

	itemModifier("reference_modify_contents") {
		modifyContents(ContentComponentTypes.BUNDLE_CONTENTS) {
			modifier { reference(ItemModifierTagArgument("my_tag", "kore")) }
		}
	}

	itemModifiers.last() assertsIs """
		{
			"type": "minecraft:modify_contents",
			"component": "bundle_contents",
			"modifier": "#kore:my_tag"
		}
	""".trimIndent()

	itemModifier("modify_contents_reference_id") {
		modifyContents(ContentComponentTypes.CONTAINER) {
			modifier { reference(ItemModifierArgument("my_modifier", "kore")) }
		}
	}

	itemModifiers.last() assertsIs """
		{
			"type": "minecraft:modify_contents",
			"component": "container",
			"modifier": "kore:my_modifier"
		}
	""".trimIndent()
	roundTrip(itemModifiers.last())

	itemModifier("sequence_condition") {
		sequence {
			setItem(Items.APPLE)
			explosionDecay()
		}.condition { randomChance(0.5f) }
	}

	itemModifiers.last() assertsIs """
		{
			"type": "minecraft:sequence",
			"condition": {
				"type": "minecraft:random_chance",
				"chance": 0.5
			},
			"functions": [
				{
					"type": "minecraft:set_item",
					"item": "minecraft:apple"
				},
				{
					"type": "minecraft:explosion_decay"
				}
			]
		}
	""".trimIndent()
	roundTrip(itemModifiers.last())

	itemModifier("reference_sequence") {
		sequence {
			reference(ItemModifierArgument("a", "kore"))
		}
	}

	itemModifiers.last() assertsIs """
		{
			"type": "minecraft:sequence",
			"functions": "kore:a"
		}
	""".trimIndent()
	roundTrip(itemModifiers.last())

	itemModifier("sequence") {
		sequence {
			setItem(Items.APPLE)
			setName("Test", color = Color.BLACK)
		}
	}

	itemModifiers.last() assertsIs """
		{
			"type": "minecraft:sequence",
			"functions": [
				{
					"type": "minecraft:set_item",
					"item": "minecraft:apple"
				},
				{
					"type": "minecraft:set_name",
					"name": {
						"text": "Test",
						"color": "black",
						"type": "text"
					}
				}
			]
		}
	""".trimIndent()

	itemModifier("set_attributes") {
		setAttributes {
			attribute(Attributes.SCALE, "test", 0.5f, slot = listOf(EquipmentSlot.MAINHAND))
			replace = false
		}
	}

	itemModifiers.last() assertsIs """
		{
			"type": "minecraft:set_attributes",
			"modifiers": [
				{
					"attribute": "minecraft:scale",
					"amount": 0.5,
					"id": "test",
					"operation": "add_value",
					"slot": "mainhand"
				}
			],
			"replace": false
		}
	""".trimIndent()

	itemModifier("set_banner_pattern") {
		setBannerPattern(append = true) {
			bannerPattern(BannerPatterns.CREEPER, FormattingColor.BLACK)
		}
	}

	itemModifiers.last() assertsIs """
		{
			"type": "minecraft:set_banner_pattern",
			"patterns": [
				{
					"pattern": "minecraft:creeper",
					"color": "black"
				}
			],
			"append": true
		}
	""".trimIndent()

	itemModifier("set_book_cover") {
		setBookCover("Test Title", author = "Test Author", generation = 2)
	}

	itemModifiers.last() assertsIs """
		{
			"type": "minecraft:set_book_cover",
			"title": "Test Title",
			"author": "Test Author",
			"generation": 2
		}
	""".trimIndent()

	itemModifier("set_components") {
		setComponents {
			customName("Test", color = Color.BLACK)
			!damage(5)
			!"test"
			setToRemove("test2")
		}
	}

	itemModifiers.last() assertsIs """
		{
			"type": "minecraft:set_components",
			"components": {
				"custom_name": {
					"text": "Test",
					"color": "black"
				},
				"!damage": {},
				"!test": {},
				"!test2": {}
			}
		}
	""".trimIndent()

	itemModifier("set_components_block_transformer") {
		setComponents {
			blockTransformer(BlockTransformerArgument("flatten", "mypack"))
		}
	}

	itemModifiers.last() assertsIs """
		{
			"type": "minecraft:set_components",
			"components": {
				"block_transformer": "mypack:flatten"
			}
		}
	""".trimIndent()

	itemModifier("set_components_player_profile") {
		setComponents {
			playerProfile(id = uuid("8667ba71-b85a-4004-af54-457a9734eed7"))
		}
	}

	itemModifiers.last() assertsIs """
		{
			"type": "minecraft:set_components",
			"components": {
				"profile": {
					"id": [
						-2040022415,
						-1202044924,
						-1353431686,
						-1758138665
					]
				}
			}
		}
	""".trimIndent()
	roundTrip(PlayerProfile(id = uuid("8667ba71-b85a-4004-af54-457a9734eed7")))

	itemModifier("set_contents") {
		setContents(ContentComponentTypes.BUNDLE_CONTENTS) {
			entries {
				item(Items.APPLE) {
					weight = 1
				}
			}
		}
	}

	itemModifiers.last() assertsIs """
		{
			"type": "minecraft:set_contents",
			"component": "bundle_contents",
			"entries": [
				{
					"type": "minecraft:item",
					"name": "minecraft:apple",
					"weight": 1
				}
			]
		}
	""".trimIndent()

	itemModifier("set_count") {
		setCount(5, add = true)
	}

	itemModifiers.last() assertsIs """
		{
			"type": "minecraft:set_count",
			"count": 5,
			"add": true
		}
	""".trimIndent()

	itemModifier("set_custom_data") {
		setCustomData {
			this["test"] = 1
		}
	}

	itemModifiers.last() assertsIs """
		{
			"type": "minecraft:set_custom_data",
			"tag": {
				"test": 1
			}
		}
	""".trimIndent()

	itemModifier("set_custom_model_data") {
		setCustomModelData(
			colors = listOf(Color.RED, Color.BLUE),
			flags = listOf(true, false),
			floats = listOf(1.0f, 2.0f),
			strings = listOf("test1", "test2")
		)
	}

	itemModifiers.last() assertsIs """
		{
			"type": "minecraft:set_custom_model_data",
			"colors": {
				"values": [
					16733525,
					5592575
				],
				"mode": "replace_all"
			},
			"flags": {
				"values": [
					true,
					false
				],
				"mode": "replace_all"
			},
			"floats": {
				"values": [
					1.0,
					2.0
				],
				"mode": "replace_all"
			},
			"strings": {
				"values": [
					"test1",
					"test2"
				],
				"mode": "replace_all"
			}
		}
	""".trimIndent()

	itemModifier("set_custom_model_data_with_mode") {
		setCustomModelData(
			colors = listOf(Color.RED),
			flags = listOf(true)
		) {
			colors?.mode(Mode.INSERT, 1)
			flags?.mode(Mode.APPEND)
		}
	}

	itemModifiers.last() assertsIs """
		{
			"type": "minecraft:set_custom_model_data",
			"colors": {
				"values": [
					16733525
				],
				"mode": "insert",
				"offset": 1
			},
			"flags": {
				"values": [
					true
				],
				"mode": "append"
			}
		}
	""".trimIndent()

	itemModifier("set_custom_model_data_providers") {
		setCustomModelData {
			floats(add(fromInt(score("x", "#a")), constant(0.5f)))
		}
	}

	itemModifiers.last() assertsIs """
		{
			"type": "minecraft:set_custom_model_data",
			"floats": {
				"values": [
					{
						"type": "minecraft:add",
						"inputs": [
							{
								"type": "minecraft:from_int",
								"input": {
									"type": "minecraft:score",
									"score": "x",
									"target": {
										"type": "minecraft:fixed",
										"name": "#a"
									}
								}
							},
							0.5
						]
					}
				],
				"mode": "replace_all"
			}
		}
	""".trimIndent()

	itemModifier("set_custom_model_data_color_providers") {
		setCustomModelData {
			colors(score("tint", "#a"))
		}
	}

	itemModifiers.last() assertsIs """
		{
			"type": "minecraft:set_custom_model_data",
			"colors": {
				"values": [
					{
						"type": "minecraft:score",
						"score": "tint",
						"target": {
							"type": "minecraft:fixed",
							"name": "#a"
						}
					}
				],
				"mode": "replace_all"
			}
		}
	""".trimIndent()

	roundTrip(itemModifiers.last())

	itemModifier("set_custom_model_data_color_builder") {
		setCustomModelData {
			colors(Color.RED, Color.BLUE)
		}
	}

	itemModifiers.last() assertsIs """
		{
			"type": "minecraft:set_custom_model_data",
			"colors": {
				"values": [
					16733525,
					5592575
				],
				"mode": "replace_all"
			}
		}
	""".trimIndent()

	itemModifier("set_damage") {
		setDamage(0.5f, add = true)
	}

	itemModifiers.last() assertsIs """
		{
			"type": "minecraft:set_damage",
			"damage": 0.5,
			"add": true
		}
	""".trimIndent()

	itemModifier("set_enchantments") {
		setEnchantments(add = true) {
			this[Enchantments.SHARPNESS] = constant(2)
		}
	}

	itemModifiers.last() assertsIs """
		{
			"type": "minecraft:set_enchantments",
			"enchantments": {
				"minecraft:sharpness": 2
			},
			"add": true
		}
	""".trimIndent()

	itemModifier("set_firework_explosion") {
		setFireworkExplosion(FireworkExplosionShape.STAR) {
			colors = listOf(Color.RED.toRGB())
			fadeColors = listOf(Color.BLUE.toRGB())
			hasTwinkle = true
			hasTrail = true
		}
	}

	itemModifiers.last() assertsIs """
		{
			"type": "minecraft:set_firework_explosion",
			"shape": "star",
			"colors": [
				16733525
			],
			"fade_colors": [
				5592575
			],
			"has_trail": true,
			"has_twinkle": true
		}
	""".trimIndent()

	itemModifier("set_fireworks") {
		setFireworks {
			flightDuration = 5
			explosions {
				explosion(FireworkExplosionShape.BURST) {
					colors(Color.RED)
					fadeColors(Color.BLUE)
					hasTrail = true
					hasTwinkle = true
				}

				mode(Mode.REPLACE_ALL)
			}
		}
	}

	itemModifiers.last() assertsIs """
		{
			"type": "minecraft:set_fireworks",
			"flight_duration": 5,
			"explosions": {
				"values": [
					{
						"shape": "burst",
						"colors": [
							16733525
						],
						"fade_colors": [
							5592575
						],
						"has_trail": true,
						"has_twinkle": true
					}
				],
				"mode": "replace_all"
			}
		}
	""".trimIndent()

	itemModifier("set_instrument_list") {
		setInstrument(Instruments.ADMIRE_GOAT_HORN, Instruments.SING_GOAT_HORN)
	}

	itemModifiers.last() assertsIs """
		{
			"type": "minecraft:set_instrument",
			"options": [
				"minecraft:admire_goat_horn",
				"minecraft:sing_goat_horn"
			]
		}
	""".trimIndent()

	itemModifier("set_item") {
		setItem(Items.APPLE)
	}

	itemModifiers.last() assertsIs """
		{
			"type": "minecraft:set_item",
			"item": "minecraft:apple"
		}
	""".trimIndent()

	itemModifier("set_loot_table") {
		setLootTable(LootTables.Chests.SIMPLE_DUNGEON, seed = 42)
	}

	itemModifiers.last() assertsIs """
		{
			"type": "minecraft:set_loot_table",
			"loot_table_id": "minecraft:chests/simple_dungeon",
			"seed": 42
		}
	""".trimIndent()

	itemModifier("set_lore") {
		setLore {
			lore("Test", color = Color.BLACK)
			lore("Test2")
			mode(Mode.INSERT, 0)
		}
	}

	itemModifiers.last() assertsIs """
		{
			"type": "minecraft:set_lore",
			"lore": [
				{
					"text": "Test",
					"color": "black",
					"type": "text"
				},
				"Test2"
			],
			"mode": "insert",
			"offset": 0
		}
	""".trimIndent()

	itemModifier("set_name") {
		setName("Test", color = Color.BLACK) {
			target = SetNameTarget.CUSTOM_NAME
			entity = Source.THIS
		}
	}

	itemModifiers.last() assertsIs """
		{
			"type": "minecraft:set_name",
			"entity": "this",
			"name": {
				"text": "Test",
				"color": "black",
				"type": "text"
			},
			"target": "custom_name"
		}
	""".trimIndent()

	itemModifier("set_name_components") {
		setName(textComponent("Legendary ", Color.GOLD) + textComponent("Sword"))
	}

	itemModifiers.last() assertsIs """
		{
			"type": "minecraft:set_name",
			"name": [
				"",
				{
					"text": "Legendary ",
					"color": "gold",
					"type": "text"
				},
				"Sword"
			]
		}
	""".trimIndent()

	itemModifier("set_ominous_bottle_amplifier") {
		setOminousBottleAmplifier(5)
	}

	itemModifiers.last() assertsIs """
		{
			"type": "minecraft:set_ominous_bottle_amplifier",
			"amplifier": 5
		}
	""".trimIndent()

	itemModifier("set_ominous_bottle_amplifier_provider") {
		setOminousBottleAmplifier(uniform(2, 4))
	}

	itemModifiers.last() assertsIs """
		{
			"type": "minecraft:set_ominous_bottle_amplifier",
			"amplifier": {
				"type": "minecraft:uniform",
				"max": 4,
				"min": 2
			}
		}
	""".trimIndent()

	itemModifier("set_ominous_bottle_amplifier_reference") {
		setOminousBottleAmplifier(reference(ContextIntProviderArgument("amplifier", "my_pack")))
	}

	itemModifiers.last() assertsIs """
		{
			"type": "minecraft:set_ominous_bottle_amplifier",
			"amplifier": "my_pack:amplifier"
		}
	""".trimIndent()

	itemModifier("set_count_reference") {
		setCount(reference(ContextIntProviderArgument("n", "my_pack")))
	}

	itemModifiers.last() assertsIs """
		{
			"type": "minecraft:set_count",
			"count": "my_pack:n"
		}
	""".trimIndent()

	itemModifier("set_potion") {
		setPotion(Potions.HEALING)
	}

	itemModifiers.last() assertsIs """
		{
			"type": "minecraft:set_potion",
			"id": "minecraft:healing"
		}
	""".trimIndent()

	itemModifier("set_random_dyes") {
		setRandomDyes(3)
	}

	itemModifiers.last() assertsIs """
		{
			"type": "minecraft:set_random_dyes",
			"number_of_dyes": 3
		}
	""".trimIndent()

	itemModifier("set_random_potion") {
		setRandomPotion(Potions.HEALING, Potions.SWIFTNESS)
	}

	itemModifiers.last() assertsIs """
		{
			"type": "minecraft:set_random_potion",
			"options": [
				"minecraft:healing",
				"minecraft:swiftness"
			]
		}
	""".trimIndent()

	itemModifier("set_stew_effect") {
		setStewEffect {
			potionEffect(Effects.NIGHT_VISION, constant(200))
		}
	}

	itemModifiers.last() assertsIs """
		{
			"type": "minecraft:set_stew_effect",
			"effects": [
				{
					"type": "minecraft:night_vision",
					"duration": 200
				}
			]
		}
	""".trimIndent()

	itemModifier("set_stew_effect_mob_effects") {
		setStewEffect {
			potionEffect(Effects.NIGHT_VISION, uniform(7, 10))
			potionEffect(Effects.JUMP_BOOST, uniform(4, 7))
		}
	}

	itemModifiers.last() assertsIs """
		{
			"type": "minecraft:set_stew_effect",
			"effects": [
				{
					"type": "minecraft:night_vision",
					"duration": {
						"type": "minecraft:uniform",
						"max": 10,
						"min": 7
					}
				},
				{
					"type": "minecraft:jump_boost",
					"duration": {
						"type": "minecraft:uniform",
						"max": 7,
						"min": 4
					}
				}
			]
		}
	""".trimIndent()

	itemModifier("set_writable_book_pages") {
		setWritableBookPages {
			page("test", filtered = "filtered")
			mode(Mode.INSERT, 1)
		}
	}

	itemModifiers.last() assertsIs """
		{
			"type": "minecraft:set_writable_book_pages",
			"pages": [
				{
					"raw": "test",
					"filtered": "filtered"
				}
			],
			"mode": "insert",
			"offset": 1
		}
	""".trimIndent()

	itemModifier("set_written_book_pages") {
		setWrittenBookPages {
			page("test", filtered = "test2")
		}
	}

	itemModifiers.last() assertsIs """
		{
			"type": "minecraft:set_written_book_pages",
			"pages": [
				{
					"raw": "test",
					"filtered": "test2"
				}
			],
			"mode": "replace_all"
		}
	""".trimIndent()

	val modifier = itemModifier("test_modifier") {
		explorationMap(Tags.Worldgen.Structure.MINESHAFT) {
			decoration = MapDecorationTypes.BANNER_BLACK
			skipExistingChunks = true

			condition {
				randomChance(0.5f)
				weatherCheck(true)
			}
		}

		setInstrument(Tags.Instrument.GOAT_HORNS)
	}

	itemModifiers.last() assertsIs """
		{
			"type": "minecraft:sequence",
			"functions": [
				{
					"type": "minecraft:exploration_map",
					"condition": {
						"type": "minecraft:all_of",
						"terms": [
							{
								"type": "minecraft:random_chance",
								"chance": 0.5
							},
							{
								"type": "minecraft:weather_check",
								"raining": true
							}
						]
					},
					"decoration": "minecraft:banner_black",
					"destination": "#minecraft:mineshaft",
					"skip_existing_chunks": true
				},
				{
					"type": "minecraft:set_instrument",
					"options": "#minecraft:goat_horns"
				}
			]
		}
	""".trimIndent()

	load {
		items {
			modify(self(), WEAPON.MAINHAND, modifier)
		}
	}

	itemModifier("single_condition") {
		setCount(1) {
			condition {
				randomChance(0.5f)
			}
		}
	}

	itemModifiers.last() assertsIs """
		{
			"type": "minecraft:set_count",
			"condition": {
				"type": "minecraft:random_chance",
				"chance": 0.5
			},
			"count": 1
		}
	""".trimIndent()

	itemModifier("condition_predicate_id") {
		setCount(1) {
			condition(PredicateArgument("is_night", "my_pack"))
		}
	}

	itemModifiers.last() assertsIs """
		{
			"type": "minecraft:set_count",
			"condition": "my_pack:is_night",
			"count": 1
		}
	""".trimIndent()
	roundTrip(itemModifiers.last())

	itemModifier("single_function_sequence") {
		sequence {
			setItem(Items.APPLE)
		}
	}

	itemModifiers.last() assertsIs """
		{
			"type": "minecraft:sequence",
			"functions": {
				"type": "minecraft:set_item",
				"item": "minecraft:apple"
			}
		}
	""".trimIndent()

	itemModifier("two_functions") {
		setItem(Items.APPLE)
		explosionDecay()
	}

	itemModifiers.last() assertsIs """
		{
			"type": "minecraft:sequence",
			"functions": [
				{
					"type": "minecraft:set_item",
					"item": "minecraft:apple"
				},
				{
					"type": "minecraft:explosion_decay"
				}
			]
		}
	""".trimIndent()

	itemModifier("toggle_tooltips") {
		toggleTooltips {
			toggle(true, ItemComponentTypes.TRIM, ItemComponentTypes.CAN_PLACE_ON)
			toggles(ItemComponentTypes.DYED_COLOR to false)
		}
	}

	itemModifiers.last() assertsIs """
		{
			"type": "minecraft:toggle_tooltips",
			"toggles": {
				"minecraft:trim": true,
				"minecraft:can_place_on": true,
				"minecraft:dyed_color": false
			}
		}
	""".trimIndent()

	val id = jsonEncoder.decodeFromString(ItemFunction.serializer(), "\"my_pack:m\"")
		.shouldBeInstanceOf<ItemModifierReference>().id
	id.shouldBeInstanceOf<ItemModifierArgument>().asString() assertsIs "my_pack:m"

	val tag = jsonEncoder.decodeFromString(ItemFunction.serializer(), "\"#my_pack:t\"")
		.shouldBeInstanceOf<ItemModifierReference>().id
	tag.shouldBeInstanceOf<ItemModifierTagArgument>().asString() assertsIs "#my_pack:t"

	shouldThrowAny { jsonEncoder.decodeFromString(ItemFunction.serializer(), "5.0") }
	shouldThrowAny { jsonEncoder.decodeFromString(ItemFunction.serializer(), """{"type":"minecraft:reference","name":"my_pack:m"}""") }
}

class ItemModifierTests : FunSpec({
	test("item modifier") {
		dataPack("itemModifier") {
			pretty()
			itemModifierTests()
		}
	}

	test("item modifier file without function") {
		dataPack("itemModifier") {
			itemModifier("empty") {}

			shouldThrow<IllegalArgumentException> { itemModifiers.last().generateJson(this) }
		}
	}
})
