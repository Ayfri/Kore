package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.components.data.EquipmentSlot
import io.github.ayfri.kore.arguments.maths.Vec3f
import io.github.ayfri.kore.arguments.types.resources.FunctionArgument
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.commands.AttributeModifierOperation
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.features.enchantments.*
import io.github.ayfri.kore.features.enchantments.effects.builders.*
import io.github.ayfri.kore.features.enchantments.effects.entity.*
import io.github.ayfri.kore.features.enchantments.effects.entity.spawnparticles.ParticlePositionType
import io.github.ayfri.kore.features.enchantments.effects.entity.spawnparticles.types.geyserBaseParticleType
import io.github.ayfri.kore.features.enchantments.effects.entity.spawnparticles.types.geyserParticleType
import io.github.ayfri.kore.features.enchantments.effects.entity.spawnparticles.types.geyserPlumeParticleType
import io.github.ayfri.kore.features.enchantments.effects.entity.spawnparticles.types.geyserPoofParticleType
import io.github.ayfri.kore.features.enchantments.effects.special.start
import io.github.ayfri.kore.features.enchantments.effects.value.requirements
import io.github.ayfri.kore.features.enchantments.values.*
import io.github.ayfri.kore.features.predicates.conditions.weatherCheck
import io.github.ayfri.kore.features.worldgen.blockpredicate.matchingBlocks
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.simpleStateProvider
import io.github.ayfri.kore.generated.*
import io.github.ayfri.kore.utils.pretty
import io.kotest.core.spec.style.FunSpec

private const val DUMMY_ENCHANTMENT_CONTENT = """"description": "",
			"supported_items": [],
			"weight": 1,
			"max_level": 1,
			"min_cost": {
				"base": 0,
				"per_level_above_first": 0
			},
			"max_cost": {
				"base": 0,
				"per_level_above_first": 0
			},
			"anvil_cost": 0,
			"slots": [],"""

fun DataPack.enchantmentTests() {
	enchantment("test") {
		description("This is a test enchantment.")
		exclusiveSet(Tags.Enchantment.IN_ENCHANTING_TABLE)
		supportedItems(Items.DIAMOND_SWORD, Items.DIAMOND_AXE)
		primaryItems(Tags.Item.AXES)
		slots(EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND)
	}

	enchantments.last() assertsIs """
		{
			"description": "This is a test enchantment.",
			"exclusive_set": "#minecraft:in_enchanting_table",
			"supported_items": [
				"minecraft:diamond_sword",
				"minecraft:diamond_axe"
			],
			"primary_items": "#minecraft:axes",
			"weight": 1,
			"max_level": 1,
			"min_cost": {
				"base": 0,
				"per_level_above_first": 0
			},
			"max_cost": {
				"base": 0,
				"per_level_above_first": 0
			},
			"anvil_cost": 0,
			"slots": [
				"mainhand",
				"offhand"
			]
		}
	""".trimIndent()

	enchantment("supported_items_tag") {
		supportedItems(Tags.Item.SWORDS)
	}

	enchantments.last() assertsIs """
		{
			"description": "",
			"supported_items": "#minecraft:swords",
			"weight": 1,
			"max_level": 1,
			"min_cost": {
				"base": 0,
				"per_level_above_first": 0
			},
			"max_cost": {
				"base": 0,
				"per_level_above_first": 0
			},
			"anvil_cost": 0,
			"slots": []
		}
	""".trimIndent()

	enchantment("ammo_use") {
		effects {
			ammoUse {
				add(5)
			}
		}
	}

	enchantments.last() assertsIs """
		{
			$DUMMY_ENCHANTMENT_CONTENT
			"effects": {
				"minecraft:ammo_use": [
					{
						"effect": {
							"type": "minecraft:add",
							"value": 5
						}
					}
				]
			}
		}
	""".trimIndent()

	enchantment("armor_effectiveness") {
		effects {
			armorEffectiveness {
				add(5) {
					requirements {
						weatherCheck(raining = true)
					}
				}

				allOf {
					add(5)

					allOf {
						add(5)
					}
				}

				multiply(2)
				removeBinomial(0.25f)
				set(2)
			}
		}
	}

	enchantments.last() assertsIs """
		{
			$DUMMY_ENCHANTMENT_CONTENT
			"effects": {
				"minecraft:armor_effectiveness": [
					{
						"effect": {
							"type": "minecraft:add",
							"value": 5
						},
						"requirements": {
							"condition": "minecraft:weather_check",
							"raining": true
						}
					},
					{
						"effect": {
							"type": "minecraft:all_of",
							"effects": [
								{
									"type": "minecraft:add",
									"value": 5
								},
								{
									"type": "minecraft:all_of",
									"effects": [
										{
											"type": "minecraft:add",
											"value": 5
										}
									]
								}
							]
						}
					},
					{
						"effect": {
							"type": "minecraft:multiply",
							"factor": 2
						}
					},
					{
						"effect": {
							"type": "minecraft:remove_binomial",
							"chance": 0.25
						}
					},
					{
						"effect": {
							"type": "minecraft:set",
							"value": 2
						}
					}
				]
			}
		}
	""".trimIndent()

	enchantment("all_of_requirements") {
		effects {
			damage {
				allOf {
					requirements {
						weatherCheck(raining = true)
					}

					add(2)
				}
			}
		}
	}

	enchantments.last() assertsIs """
		{
			$DUMMY_ENCHANTMENT_CONTENT
			"effects": {
				"minecraft:damage": [
					{
						"effect": {
							"type": "minecraft:all_of",
							"effects": [
								{
									"type": "minecraft:add",
									"value": 2
								}
							]
						},
						"requirements": {
							"condition": "minecraft:weather_check",
							"raining": true
						}
					}
				]
			}
		}
	""".trimIndent()

	enchantment("attributes") {
		effects {
			attributes {
				attribute("my_modifier", name, Attributes.SCALE, AttributeModifierOperation.ADD_VALUE, constantLevelBased(5))
			}
		}
	}

	enchantments.last() assertsIs """
		{
			$DUMMY_ENCHANTMENT_CONTENT
			"effects": {
				"minecraft:attributes": [
					{
						"id": "$name:my_modifier",
						"attribute": "minecraft:scale",
						"operation": "add_value",
						"amount": 5
					}
				]
			}
		}
	""".trimIndent()

	enchantment("block_experience") {
		effects {
			blockExperience {
				allOf {
					add(clampedLevelBased(5, 0f, 10f))
					add(constantLevelBased(5))
					add(exponentLevelBased(1, 5))
					add(fractionLevelBased(1, 5))
					add(levelsSquaredLevelBased(2))
					add(linearLevelBased(2, 2))
					add(lookupLevelBased(2, 2, fallback = 2))
					add(linearLevelBased(1.5f, 0.25f))
				}
			}
		}
	}

	enchantments.last() assertsIs """
		{
			$DUMMY_ENCHANTMENT_CONTENT
			"effects": {
				"minecraft:block_experience": [
					{
						"effect": {
							"type": "minecraft:all_of",
							"effects": [
								{
									"type": "minecraft:add",
									"value": {
										"type": "minecraft:clamped",
										"value": 5,
										"min": 0.0,
										"max": 10.0
									}
								},
								{
									"type": "minecraft:add",
									"value": 5
								},
								{
									"type": "minecraft:add",
									"value": {
										"type": "minecraft:exponent",
										"base": 1,
										"power": 5
									}
								},
								{
									"type": "minecraft:add",
									"value": {
										"type": "minecraft:fraction",
										"numerator": 1,
										"denominator": 5
									}
								},
								{
									"type": "minecraft:add",
									"value": {
										"type": "minecraft:levels_squared",
										"added": 2
									}
								},
								{
									"type": "minecraft:add",
									"value": {
										"type": "minecraft:linear",
										"base": 2,
										"per_level_above_first": 2
									}
								},
								{
									"type": "minecraft:add",
									"value": {
										"type": "minecraft:lookup",
										"values": [
											2,
											2
										],
										"fallback": 2
									}
								},
								{
									"type": "minecraft:add",
									"value": {
										"type": "minecraft:linear",
										"base": 1.5,
										"per_level_above_first": 0.25
									}
								}
							]
						}
					}
				]
			}
		}
	""".trimIndent()

	enchantment("crossbow_charging_sounds") {
		effects {
			crossbowChargingSounds {
				level {
					start(SoundEvents.Item.Crossbow.QUICK_CHARGE_3)
				}
			}
		}
	}

	enchantments.last() assertsIs """
		{
			$DUMMY_ENCHANTMENT_CONTENT
			"effects": {
				"minecraft:crossbow_charging_sounds": [
					{
						"start": "minecraft:item.crossbow.quick_charge_3"
					}
				]
			}
		}
	""".trimIndent()

	enchantment("crossbow_charge_time") {
		effects {
			crossbowChargeTime {
				add(5)
			}
		}
	}

	enchantments.last() assertsIs """
		{
			$DUMMY_ENCHANTMENT_CONTENT
			"effects": {
				"minecraft:crossbow_charge_time": {
					"type": "minecraft:add",
					"value": 5
				}
			}
		}
	""".trimIndent()

	enchantment("damage_immunity") {
		effects {
			damageImmunity {
				requirements {
					weatherCheck(raining = true)
				}

				always()
			}
		}
	}

	enchantments.last() assertsIs """
		{
			$DUMMY_ENCHANTMENT_CONTENT
			"effects": {
				"minecraft:damage_immunity": [
					{
						"effect": {},
						"requirements": {
							"condition": "minecraft:weather_check",
							"raining": true
						}
					},
					{
						"effect": {}
					}
				]
			}
		}
	""".trimIndent()

	enchantment("damage_protection") {
		effects {
			damageProtection {
				add(linearLevelBased(1, 1))
			}
		}
	}

	enchantments.last() assertsIs """
		{
			$DUMMY_ENCHANTMENT_CONTENT
			"effects": {
				"minecraft:damage_protection": [
					{
						"effect": {
							"type": "minecraft:add",
							"value": {
								"type": "minecraft:linear",
								"base": 1,
								"per_level_above_first": 1
							}
						}
					}
				]
			}
		}
	""".trimIndent()

	enchantment("equipment_drops") {
		effects {
			equipmentDrops {
				on(EquipmentDropsSpecifier.ATTACKER) {
					add(5) {
						requirements {
							weatherCheck(raining = true)
						}
					}
				}

				on(EquipmentDropsSpecifier.VICTIM) {
					allOf {
						add(5)

						allOf {
							add(5)
						}
					}
				}
			}
		}
	}

	enchantments.last() assertsIs """
		{
			$DUMMY_ENCHANTMENT_CONTENT
			"effects": {
				"minecraft:equipment_drops": [
					{
						"enchanted": "attacker",
						"effect": {
							"type": "minecraft:add",
							"value": 5
						},
						"requirements": {
							"condition": "minecraft:weather_check",
							"raining": true
						}
					},
					{
						"enchanted": "victim",
						"effect": {
							"type": "minecraft:all_of",
							"effects": [
								{
									"type": "minecraft:add",
									"value": 5
								},
								{
									"type": "minecraft:all_of",
									"effects": [
										{
											"type": "minecraft:add",
											"value": 5
										}
									]
								}
							]
						}
					}
				]
			}
		}
	""".trimIndent()

	enchantment("hit_block") {
		effects {
			hitBlock {
				allOf {
					applyMobEffect(Effects.SPEED) {
						maxDuration(2)
						minAmplifier(1)
					}

					requirements {
						weatherCheck(raining = true)
					}
				}

				applyExhaustion(5)

				damageEntity(DamageTypes.IN_FIRE, 1, 2) {
					requirements {
						weatherCheck(raining = false)
					}
				}

				damageItem(1)

				explode(
					smallParticle = Particles.GUST_EMITTER_SMALL,
					largeParticle = Particles.GUST_EMITTER_LARGE,
					sound = SoundEvents.Entity.WindCharge.WIND_BURST,
				) {
					blockInteraction = BlockInteraction.TRIGGER
					radius(3.5f)
					knockbackMultiplier(2)

					blockParticles {
						particle(2, Particles.ASH, scaling = 0.5f)
					}
				}

				ignite(2)
				playSound(SoundEvents.Entity.FireworkRocket.LAUNCH, 5f)

				replaceBlock {
					blockState = simpleStateProvider(Blocks.DIAMOND_BLOCK)
					offset(5, 5, 5)
					triggerGameEvent = GameEvents.BLOCK_PLACE
				}

				replaceDisk {
					blockState = simpleStateProvider(Blocks.DIAMOND_BLOCK)
					radius(5)
					height(2)
				}

				runFunction(FunctionArgument("function", "namespace"))

				setBlockProperties {
					properties {
						this["test"] = "test"
					}
				}

				spawnParticles(
					Particles.ANGRY_VILLAGER,
					horizontalPositionType = ParticlePositionType.IN_BOUNDING_BOX,
					verticalPositionType = ParticlePositionType.IN_BOUNDING_BOX,
				) {
					horizontalVelocity(base = 2.5f, movementScale = 1.2f)
				}

				summonEntity(EntityTypes.AREA_EFFECT_CLOUD)
			}
		}
	}

	enchantments.last() assertsIs """
		{
			$DUMMY_ENCHANTMENT_CONTENT
			"effects": {
				"minecraft:hit_block": [
					{
						"effect": {
							"type": "minecraft:all_of",
							"effects": [
								{
									"type": "minecraft:apply_mob_effect",
									"to_apply": "minecraft:speed",
									"min_duration": 0,
									"max_duration": 2,
									"min_amplifier": 1,
									"max_amplifier": 0
								}
							]
						},
						"requirements": {
							"condition": "minecraft:weather_check",
							"raining": true
						}
					},
					{
						"effect": {
							"type": "minecraft:apply_exhaustion",
							"amount": 5
						}
					},
					{
						"effect": {
							"type": "minecraft:damage_entity",
							"damage_type": "minecraft:in_fire",
							"min_damage": 1,
							"max_damage": 2
						},
						"requirements": {
							"condition": "minecraft:weather_check",
							"raining": false
						}
					},
					{
						"effect": {
							"type": "minecraft:damage_item",
							"amount": 1
						}
					},
					{
						"effect": {
							"type": "minecraft:explode",
							"large_particle": {
								"type": "minecraft:gust_emitter_large"
							},
							"small_particle": {
								"type": "minecraft:gust_emitter_small"
							},
							"sound": "minecraft:entity.wind_charge.wind_burst",
							"attribute_to_user": false,
							"block_interaction": "trigger",
							"block_particles": [
								{
									"weight": 2,
									"particle": {
										"type": "minecraft:ash"
									},
									"scaling": 0.5
								}
							],
							"create_fire": false,
							"knockback_multiplier": 2,
							"radius": 3.5
						}
					},
					{
						"effect": {
							"type": "minecraft:ignite",
							"duration": 2
						}
					},
					{
						"effect": {
							"type": "minecraft:play_sound",
							"sound": {
								"sound_id": "minecraft:entity.firework_rocket.launch",
								"range": 5.0
							},
							"volume": 1.0,
							"pitch": 1.0
						}
					},
					{
						"effect": {
							"type": "minecraft:replace_block",
							"block_state": {
								"type": "minecraft:simple_state_provider",
								"state": {
									"Name": "minecraft:diamond_block"
								}
							},
							"offset": [
								5,
								5,
								5
							],
							"trigger_game_event": "minecraft:block_place"
						}
					},
					{
						"effect": {
							"type": "minecraft:replace_disk",
							"block_state": {
								"type": "minecraft:simple_state_provider",
								"state": {
									"Name": "minecraft:diamond_block"
								}
							},
							"radius": 5,
							"height": 2
						}
					},
					{
						"effect": {
							"type": "minecraft:run_function",
							"function": "namespace:function"
						}
					},
					{
						"effect": {
							"type": "minecraft:set_block_properties",
							"properties": {
								"test": "test"
							}
						}
					},
					{
						"effect": {
							"type": "minecraft:spawn_particles",
							"particle": {
								"type": "minecraft:angry_villager"
							},
							"horizontal_position": {
								"type": "in_bounding_box"
							},
							"vertical_position": {
								"type": "in_bounding_box"
							},
							"horizontal_velocity": {
								"base": 2.5,
								"movement_scale": 1.2
							},
							"vertical_velocity": {}
						}
					},
					{
						"effect": {
							"type": "minecraft:summon_entity",
							"entity": "minecraft:area_effect_cloud"
						}
					}
				]
			}
		}
	""".trimIndent()

	enchantment("knockback") {
		effects {
			knockback {
				add(5)
			}
		}
	}

	enchantments.last() assertsIs """
		{
			$DUMMY_ENCHANTMENT_CONTENT
			"effects": {
				"minecraft:knockback": [
					{
						"effect": {
							"type": "minecraft:add",
							"value": 5
						}
					}
				]
			}
		}
	""".trimIndent()

	enchantment("location_changed") {
		effects {
			locationChanged {
				replaceDisk {
					blockState = simpleStateProvider(Blocks.FROSTED_ICE)
					radius = clampedLevelBased(linearLevelBased(3, 1), 0f, 16f)
					height(1)
					offset(0, -1, 0)
					triggerGameEvent = GameEvents.BLOCK_PLACE

					predicate {
						matchingBlocks(Blocks.WATER)
					}
				}
			}
		}
	}

	enchantments.last() assertsIs """
		{
			$DUMMY_ENCHANTMENT_CONTENT
			"effects": {
				"minecraft:location_changed": [
					{
						"effect": {
							"type": "minecraft:replace_disk",
							"block_state": {
								"type": "minecraft:simple_state_provider",
								"state": {
									"Name": "minecraft:frosted_ice"
								}
							},
							"radius": {
								"type": "minecraft:clamped",
								"value": {
									"type": "minecraft:linear",
									"base": 3,
									"per_level_above_first": 1
								},
								"min": 0.0,
								"max": 16.0
							},
							"height": 1,
							"offset": [
								0,
								-1,
								0
							],
							"predicate": {
								"type": "minecraft:matching_blocks",
								"blocks": "minecraft:water"
							},
							"trigger_game_event": "minecraft:block_place"
						}
					}
				]
			}
		}
	""".trimIndent()

	enchantment("item_damage") {
		effects {
			itemDamage {
				add(5)
			}
		}
	}

	enchantments.last() assertsIs """
		{
			$DUMMY_ENCHANTMENT_CONTENT
			"effects": {
				"minecraft:item_damage": [
					{
						"effect": {
							"type": "minecraft:add",
							"value": 5
						}
					}
				]
			}
		}
	""".trimIndent()

	enchantment("mob_experience") {
		effects {
			mobExperience {
				add(5)
			}
		}
	}

	enchantments.last() assertsIs """
		{
			$DUMMY_ENCHANTMENT_CONTENT
			"effects": {
				"minecraft:mob_experience": [
					{
						"effect": {
							"type": "minecraft:add",
							"value": 5
						}
					}
				]
			}
		}
	""".trimIndent()

	enchantment("post_attack") {
		effects {
			postAttack {
				on(PostAttackSpecifier.ATTACKER, PostAttackSpecifier.DAMAGING_ENTITY) {
					applyMobEffect(Effects.SPEED) {
						requirements {
							weatherCheck(raining = true)
						}
					}
				}

				on(PostAttackSpecifier.VICTIM, PostAttackSpecifier.DAMAGING_ENTITY) {
					damageEntity(DamageTypes.IN_FIRE, 1, 2)
				}
			}
		}
	}

	enchantments.last() assertsIs """
		{
			$DUMMY_ENCHANTMENT_CONTENT
			"effects": {
				"minecraft:post_attack": [
					{
						"enchanted": "attacker",
						"affected": "damaging_entity",
						"effect": {
							"type": "minecraft:apply_mob_effect",
							"to_apply": "minecraft:speed",
							"min_duration": 0,
							"max_duration": 0,
							"min_amplifier": 0,
							"max_amplifier": 0
						},
						"requirements": {
							"condition": "minecraft:weather_check",
							"raining": true
						}
					},
					{
						"enchanted": "victim",
						"affected": "damaging_entity",
						"effect": {
							"type": "minecraft:damage_entity",
							"damage_type": "minecraft:in_fire",
							"min_damage": 1,
							"max_damage": 2
						}
					}
				]
			}
		}
	""".trimIndent()

	enchantment("post_piercing_attack") {
		effects {
			postPiercingAttack {
				applyMobEffect(Effects.SLOWNESS) {
					minDuration(2)
					maxDuration(4)
				}
			}
		}
	}

	enchantments.last() assertsIs """
		{
			$DUMMY_ENCHANTMENT_CONTENT
			"effects": {
				"minecraft:post_piercing_attack": [
					{
						"effect": {
							"type": "minecraft:apply_mob_effect",
							"to_apply": "minecraft:slowness",
							"min_duration": 2,
							"max_duration": 4,
							"min_amplifier": 0,
							"max_amplifier": 0
						}
					}
				]
			}
		}
	""".trimIndent()

	enchantment("apply_impulse") {
		effects {
			hitBlock {
				applyImpulse(
					coordinateScale = Vec3f(1f, 1f, 1f),
					direction = Vec3f(y = 1f),
					magnitude = constantLevelBased(2)
				)
			}
		}
	}

	enchantments.last() assertsIs """
		{
			$DUMMY_ENCHANTMENT_CONTENT
			"effects": {
				"minecraft:hit_block": [
					{
						"effect": {
							"type": "minecraft:apply_impulse",
							"coordinate_scale": [
								1.0,
								1.0,
								1.0
							],
							"direction": [
								0.0,
								1.0,
								0.0
							],
							"magnitude": 2
						}
					}
				]
			}
		}
	""".trimIndent()

	enchantment("prevent_armor_change") {
		effects {
			preventArmorChange()
		}
	}

	enchantments.last() assertsIs """
		{
			$DUMMY_ENCHANTMENT_CONTENT
			"effects": {
				"minecraft:prevent_armor_change": {}
			}
		}
	""".trimIndent()

	enchantment("prevent_equipment_drop") {
		effects {
			preventEquipmentDrop()
		}
	}

	enchantments.last() assertsIs """
		{
			$DUMMY_ENCHANTMENT_CONTENT
			"effects": {
				"minecraft:prevent_equipment_drop": {}
			}
		}
	""".trimIndent()

	enchantment("projectile_count") {
		effects {
			projectileCount {
				add(5)
			}
		}
	}

	enchantments.last() assertsIs """
		{
			$DUMMY_ENCHANTMENT_CONTENT
			"effects": {
				"minecraft:projectile_count": [
					{
						"effect": {
							"type": "minecraft:add",
							"value": 5
						}
					}
				]
			}
		}
	""".trimIndent()

	enchantment("projectile_piercing") {
		effects {
			projectilePiercing {
				add(5)
			}
		}
	}

	enchantments.last() assertsIs """
		{
			$DUMMY_ENCHANTMENT_CONTENT
			"effects": {
				"minecraft:projectile_piercing": [
					{
						"effect": {
							"type": "minecraft:add",
							"value": 5
						}
					}
				]
			}
		}
	""".trimIndent()

	enchantment("projectile_spawned") {
		effects {
			projectileSpawned {
				applyMobEffect(Effects.SPEED) {
					maxDuration(2)
					minAmplifier(1)
				}
			}
		}
	}

	enchantments.last() assertsIs """
		{
			$DUMMY_ENCHANTMENT_CONTENT
			"effects": {
				"minecraft:projectile_spawned": [
					{
						"effect": {
							"type": "minecraft:apply_mob_effect",
							"to_apply": "minecraft:speed",
							"min_duration": 0,
							"max_duration": 2,
							"min_amplifier": 1,
							"max_amplifier": 0
						}
					}
				]
			}
		}
	""".trimIndent()

	enchantment("projectile_spread") {
		effects {
			projectileSpread {
				add(5)
			}
		}
	}

	enchantments.last() assertsIs """
		{
			$DUMMY_ENCHANTMENT_CONTENT
			"effects": {
				"minecraft:projectile_spread": [
					{
						"effect": {
							"type": "minecraft:add",
							"value": 5
						}
					}
				]
			}
		}
	""".trimIndent()

	enchantment("repair_with_xp") {
		effects {
			repairWithXp {
				add(5)
			}
		}
	}

	enchantments.last() assertsIs """
		{
			$DUMMY_ENCHANTMENT_CONTENT
			"effects": {
				"minecraft:repair_with_xp": [
					{
						"effect": {
							"type": "minecraft:add",
							"value": 5
						}
					}
				]
			}
		}
	""".trimIndent()

	enchantment("smash_damage_per_fallen_block") {
		effects {
			smashDamagePerFallenBlock {
				add(5)
			}
		}
	}

	enchantments.last() assertsIs """
		{
			$DUMMY_ENCHANTMENT_CONTENT
			"effects": {
				"minecraft:smash_damage_per_fallen_block": [
					{
						"effect": {
							"type": "minecraft:add",
							"value": 5
						}
					}
				]
			}
		}
	""".trimIndent()

	enchantment("tick") {
		effects {
			tick {
				damageEntity(DamageTypes.IN_FIRE, 1, 2)
			}
		}
	}

	enchantments.last() assertsIs """
		{
			$DUMMY_ENCHANTMENT_CONTENT
			"effects": {
				"minecraft:tick": [
					{
						"effect": {
							"type": "minecraft:damage_entity",
							"damage_type": "minecraft:in_fire",
							"min_damage": 1,
							"max_damage": 2
						}
					}
				]
			}
		}
	""".trimIndent()

	enchantment("trident_return_acceleration") {
		effects {
			tridentReturnAcceleration {
				add(5)
			}
		}
	}

	enchantments.last() assertsIs """
		{
			$DUMMY_ENCHANTMENT_CONTENT
			"effects": {
				"minecraft:trident_return_acceleration": [
					{
						"effect": {
							"type": "minecraft:add",
							"value": 5
						}
					}
				]
			}
		}
	""".trimIndent()

	enchantment("trident_spin_attack_strength") {
		effects {
			tridentSpinAttackStrength {
				add(5)
			}
		}
	}

	enchantments.last() assertsIs """
		{
			$DUMMY_ENCHANTMENT_CONTENT
			"effects": {
				"minecraft:trident_spin_attack_strength": {
					"type": "minecraft:add",
					"value": 5
				}
			}
		}
	""".trimIndent()

	enchantment("trident_sound") {
		effects {
			tridentSound {
				sound(SoundEvents.Entity.FireworkRocket.LAUNCH, 5f)
			}
		}
	}

	enchantments.last() assertsIs """
		{
			$DUMMY_ENCHANTMENT_CONTENT
			"effects": {
				"minecraft:trident_sound": [
					{
						"sound_id": "minecraft:entity.firework_rocket.launch",
						"range": 5.0
					}
				]
			}
		}
	""".trimIndent()

	enchantment("geyser_particles") {
		effects {
			hitBlock {
				allOf {
					spawnParticles(
						geyserParticleType(Particles.GEYSER, waterBlocks = 3),
						horizontalPositionType = ParticlePositionType.ENTITY_POSITION,
						verticalPositionType = ParticlePositionType.ENTITY_POSITION,
					)

					spawnParticles(
						geyserBaseParticleType(Particles.GEYSER_BASE, burstImpulseBase = 0.5f, waterBlocks = 3),
						horizontalPositionType = ParticlePositionType.ENTITY_POSITION,
						verticalPositionType = ParticlePositionType.ENTITY_POSITION,
					)

					spawnParticles(
						geyserPlumeParticleType(Particles.GEYSER_PLUME, waterBlocks = 4),
						horizontalPositionType = ParticlePositionType.ENTITY_POSITION,
						verticalPositionType = ParticlePositionType.ENTITY_POSITION,
					)

					spawnParticles(
						geyserPoofParticleType(Particles.GEYSER_POOF, burstImpulseBase = 1.5f, waterBlocks = 4),
						horizontalPositionType = ParticlePositionType.ENTITY_POSITION,
						verticalPositionType = ParticlePositionType.ENTITY_POSITION,
					)
				}
			}
		}
	}

	enchantments.last() assertsIs """
		{
			$DUMMY_ENCHANTMENT_CONTENT
			"effects": {
				"minecraft:hit_block": [
					{
						"effect": {
							"type": "minecraft:all_of",
							"effects": [
								{
									"type": "minecraft:spawn_particles",
									"particle": {
										"type": "minecraft:geyser",
										"water_blocks": 3
									},
									"horizontal_position": {
										"type": "entity_position"
									},
									"vertical_position": {
										"type": "entity_position"
									},
									"horizontal_velocity": {},
									"vertical_velocity": {}
								},
								{
									"type": "minecraft:spawn_particles",
									"particle": {
										"type": "minecraft:geyser_base",
										"burst_impulse_base": 0.5,
										"water_blocks": 3
									},
									"horizontal_position": {
										"type": "entity_position"
									},
									"vertical_position": {
										"type": "entity_position"
									},
									"horizontal_velocity": {},
									"vertical_velocity": {}
								},
								{
									"type": "minecraft:spawn_particles",
									"particle": {
										"type": "minecraft:geyser_plume",
										"water_blocks": 4
									},
									"horizontal_position": {
										"type": "entity_position"
									},
									"vertical_position": {
										"type": "entity_position"
									},
									"horizontal_velocity": {},
									"vertical_velocity": {}
								},
								{
									"type": "minecraft:spawn_particles",
									"particle": {
										"type": "minecraft:geyser_poof",
										"burst_impulse_base": 1.5,
										"water_blocks": 4
									},
									"horizontal_position": {
										"type": "entity_position"
									},
									"vertical_position": {
										"type": "entity_position"
									},
									"horizontal_velocity": {},
									"vertical_velocity": {}
								}
							]
						}
					}
				]
			}
		}
	""".trimIndent()

	enchantment("round_trip_simple_enchantment") {
		supportedItems(Items.DIAMOND_SWORD)
		slots(EquipmentSlot.MAINHAND)
		minCost(1, 10)
		maxCost(21, 10)
	}
	roundTrip(enchantments.last())
}

class EnchantmentTests : FunSpec({
	test("enchantment") {
		dataPack("enchantment") {
			pretty()
			enchantmentTests()
		}
	}
})
