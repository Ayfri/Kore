package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.components.data.EquipmentSlot
import io.github.ayfri.kore.arguments.types.resources.FunctionArgument
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.commands.AttributeModifierOperation
import io.github.ayfri.kore.features.enchantments.*
import io.github.ayfri.kore.features.enchantments.effects.builders.*
import io.github.ayfri.kore.features.enchantments.effects.entity.*
import io.github.ayfri.kore.features.enchantments.effects.entity.spawnparticles.ParticlePositionType
import io.github.ayfri.kore.features.enchantments.effects.special.requirements
import io.github.ayfri.kore.features.enchantments.effects.special.start
import io.github.ayfri.kore.features.enchantments.values.*
import io.github.ayfri.kore.features.predicates.conditions.weatherCheck
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.simpleStateProvider
import io.github.ayfri.kore.generated.*
import io.github.ayfri.kore.helpers.displays.maths.Vec3f

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
				removeBinomial(2)
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
							"chance": 2
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

	enchantment("attributes") {
		effects {
			attributes {
				attribute("my_modifier", name, Attributes.SCALE, AttributeModifierOperation.ADD_VALUE, 5)
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
					add(clampedLevelBased(5, 0.0, 10.0))
					add(constantLevelBased(5))
					add(fractionLevelBased(1, 5))
					add(levelsSquaredLevelBased(2))
					add(linearLevelBased(2, 2))
					add(lookupLevelBased(2, 2, fallback = 2))
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
				crossbowChargingSound {
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
						"start": "minecraft:item.crossbow.quick_charge_3",
						"mid": ":",
						"end": ":"
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
				"minecraft:crossbow_charge_time": [
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

	enchantment("damage") {
		effects {
			damage {
				add(5)
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
							"type": "minecraft:add",
							"value": 5
						}
					}
				]
			}
		}
	""".trimIndent()

	enchantment("damage_immunity") {
		effects {
			damageImmunity {
				sound {
					requirements {
						weatherCheck(raining = true)
					}
				}
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
					}
				]
			}
		}
	""".trimIndent()

	enchantment("equipment_drops") {
		effects {
			equipmentDrops {
				add(EquipmentDropsSpecifier.ATTACKER, 5) {
					requirements {
						weatherCheck(raining = true)
					}
				}

				allOf(EquipmentDropsSpecifier.VICTIM) {
					add(5)

					allOf {
						add(5)
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

	enchantment("fishing_luck_bonus") {
		effects {
			fishingLuckBonus {
				add(5)
			}
		}
	}

	enchantments.last() assertsIs """
		{
			$DUMMY_ENCHANTMENT_CONTENT
			"effects": {
				"minecraft:fishing_luck_bonus": [
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

	enchantment("fishing_time_reduction") {
		effects {
			fishingTimeReduction {
				add(5)
			}
		}
	}

	enchantments.last() assertsIs """
		{
			$DUMMY_ENCHANTMENT_CONTENT
			"effects": {
				"minecraft:fishing_time_reduction": [
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

				damageEntity(DamageTypes.IN_FIRE, 1, 2) {
					requirements {
						weatherCheck(raining = false)
					}
				}

				changeItemDamage(1)
				explode(
					attributeToUser = true,
					createFire = true,
					blockInteraction = BlockInteraction.TNT,
					smallParticle = Particles.ANGRY_VILLAGER,
					largeParticle = Particles.ANGRY_VILLAGER,
					sound = Sounds.Random.FUSE
				) {
					radius(2)
				}

				ignite(2)
				playSound(SoundEvents.Entity.FireworkRocket.LAUNCH, 5f)
				replaceBlock(simpleStateProvider(Blocks.DIAMOND_BLOCK)) {
					offset(5, 5, 5)
					triggerGameEvent = GameEvents.BLOCK_PLACE
				}

				replaceDisk(simpleStateProvider(Blocks.DIAMOND_BLOCK)) {
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
							"attribute_to_user": true,
							"block_interaction": "tnt",
							"create_fire": true,
							"large_particle": "minecraft:angry_villager",
							"radius": 2,
							"small_particle": "minecraft:angry_villager",
							"sound": "minecraft:random/fuse"
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
							"volume": 1.0E-5,
							"pitch": 1.0E-5
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
				applyMobEffect(PostAttackSpecifier.ATTACKER, PostAttackSpecifier.DAMAGING_ENTITY, Effects.SPEED) {
					requirements {
						weatherCheck(raining = true)
					}
				}

				damageEntity(PostAttackSpecifier.VICTIM, PostAttackSpecifier.DAMAGING_ENTITY, DamageTypes.IN_FIRE, 1, 2)
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
				applyMobEffect(PostAttackSpecifier.ATTACKER, PostAttackSpecifier.VICTIM, Effects.SLOWNESS) {
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
						"enchanted": "attacker",
						"affected": "victim",
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
				"minecraft:trident_spin_attack_strength": [
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
}
