package io.github.ayfri.kore.features.worldgen

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.ARGB
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.colors.rgb
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.features.worldgen.dimensiontype.attributes
import io.github.ayfri.kore.features.worldgen.dimensiontype.dimensionType
import io.github.ayfri.kore.features.worldgen.environmentattributes.EnvironmentAttributeModifier
import io.github.ayfri.kore.features.worldgen.environmentattributes.types.*
import io.github.ayfri.kore.features.worldgen.intproviders.constant
import io.github.ayfri.kore.generated.Activities
import io.github.ayfri.kore.generated.Particles
import io.github.ayfri.kore.generated.SoundEvents
import io.github.ayfri.kore.generated.Textures

fun DataPack.environmentAttributesTests() {
	dimensionType("env_attr_ambient_particles") {
		attributes {
			ambientParticles(
				Particle(ParticleOptions(Particles.ASH), 0.01f),
			)
		}
		monsterSpawnLightLevel = constant(0)
	}

	dimensionTypes.last() assertsIs """
		{
			"attributes": {
				"minecraft:visual/ambient_particles": [
					{
						"options": {
							"type": "minecraft:ash"
						},
						"probability": 0.01
					}
				]
			},
			"natural": true,
			"has_skylight": true,
			"has_ceiling": false,
			"coordinate_scale": 1.0,
			"ambient_light": 0.0,
			"logical_height": 0,
			"infiniburn": "#minecraft:infiniburn_overworld",
			"min_y": 0,
			"height": 16,
			"monster_spawn_light_level": 0,
			"monster_spawn_block_light_limit": 0
		}
	""".trimIndent()

	dimensionType("env_attr_ambient_sounds") {
		attributes {
			ambientSounds(
				loop = SoundEvents.Ambient.CAVE, mod = EnvironmentAttributeModifier.OVERRIDE
			) {
				mood(
					sound = SoundEvents.Ambient.CAVE,
				)
				addition(SoundEvents.Ambient.CAVE, 0.01f)
			}
		}
		monsterSpawnLightLevel = constant(0)
	}

	dimensionTypes.last() assertsIs """
		{
			"attributes": {
				"minecraft:audio/ambient_sounds": {
					"argument": {
						"additions": [
							{
								"sound": "minecraft:ambient.cave",
								"tick_chance": 0.01
							}
						],
						"loop": "minecraft:ambient.cave",
						"mood": {
							"sound": "minecraft:ambient.cave",
							"tick_delay": 6000,
							"block_search_extent": 8,
							"offset": 2.0
						}
					},
					"modifier": "override"
				}
			},
			"natural": true,
			"has_skylight": true,
			"has_ceiling": false,
			"coordinate_scale": 1.0,
			"ambient_light": 0.0,
			"logical_height": 0,
			"infiniburn": "#minecraft:infiniburn_overworld",
			"min_y": 0,
			"height": 16,
			"monster_spawn_light_level": 0,
			"monster_spawn_block_light_limit": 0
		}
	""".trimIndent()

	dimensionType("env_attr_background_music") {
		attributes {
			backgroundMusic {
				default(
					sound = SoundEvents.Music.CREATIVE,
					minDelay = 100,
					maxDelay = 200,
					replaceCurrentMusic = true,
				)
			}
		}
		monsterSpawnLightLevel = constant(0)
	}

	dimensionTypes.last() assertsIs """
		{
			"attributes": {
				"minecraft:audio/background_music": {
					"default": {
						"sound": "minecraft:music.creative",
						"min_delay": 100,
						"max_delay": 200,
						"replace_current_music": true
					}
				}
			},
			"natural": true,
			"has_skylight": true,
			"has_ceiling": false,
			"coordinate_scale": 1.0,
			"ambient_light": 0.0,
			"logical_height": 0,
			"infiniburn": "#minecraft:infiniburn_overworld",
			"min_y": 0,
			"height": 16,
			"monster_spawn_light_level": 0,
			"monster_spawn_block_light_limit": 0
		}
	""".trimIndent()

	dimensionType("env_attr_bed_rule") {
		attributes {
			bedRule(
				canSleep = BedSleepRule.ALWAYS,
				canSetSpawn = BedSleepRule.WHEN_DARK,
				explodes = false,
				errorMessage = textComponent("Cannot sleep here"),
			)
		}
		monsterSpawnLightLevel = constant(0)
	}

	dimensionTypes.last() assertsIs """
		{
			"attributes": {
				"minecraft:gameplay/bed_rule": {
					"can_sleep": "always",
					"can_set_spawn": "when_dark",
					"explodes": false,
					"error_message": "Cannot sleep here"
				}
			},
			"natural": true,
			"has_skylight": true,
			"has_ceiling": false,
			"coordinate_scale": 1.0,
			"ambient_light": 0.0,
			"logical_height": 0,
			"infiniburn": "#minecraft:infiniburn_overworld",
			"min_y": 0,
			"height": 16,
			"monster_spawn_light_level": 0,
			"monster_spawn_block_light_limit": 0
		}
	""".trimIndent()

	dimensionType("env_attr_boolean_modifier") {
		attributes {
			waterEvaporates(true, EnvironmentAttributeModifier.OR)
		}
		monsterSpawnLightLevel = constant(0)
	}

	dimensionTypes.last() assertsIs """
		{
			"attributes": {
				"minecraft:gameplay/water_evaporates": {
					"argument": true,
					"modifier": "or"
				}
			},
			"natural": true,
			"has_skylight": true,
			"has_ceiling": false,
			"coordinate_scale": 1.0,
			"ambient_light": 0.0,
			"logical_height": 0,
			"infiniburn": "#minecraft:infiniburn_overworld",
			"min_y": 0,
			"height": 16,
			"monster_spawn_light_level": 0,
			"monster_spawn_block_light_limit": 0
		}
	""".trimIndent()

	dimensionType("env_attr_booleans") {
		attributes {
			fireflyBushSounds(true)
			beesStayInHive(true)
			canPillagerPatrolSpawn(false)
			canStartRaid(false)
			creakingActive(true)
			fastLava(true)
			increasedFireBurnout(true)
			monstersBurn(true)
			netherPortalSpawnsPiglin(true)
			piglinsZombify(false)
			respawnAnchorWorks(true)
			snowGolemMelts(true)
			waterEvaporates(true)
		}
		monsterSpawnLightLevel = constant(0)
	}

	dimensionTypes.last() assertsIs """
		{
			"attributes": {
				"minecraft:audio/firefly_bush_sounds": true,
				"minecraft:gameplay/bees_stay_in_hive": true,
				"minecraft:gameplay/can_pillager_patrol_spawn": false,
				"minecraft:gameplay/can_start_raid": false,
				"minecraft:gameplay/creaking_active": true,
				"minecraft:gameplay/fast_lava": true,
				"minecraft:gameplay/increased_fire_burnout": true,
				"minecraft:gameplay/monsters_burn": true,
				"minecraft:gameplay/nether_portal_spawns_piglin": true,
				"minecraft:gameplay/piglins_zombify": false,
				"minecraft:gameplay/respawn_anchor_works": true,
				"minecraft:gameplay/snow_golem_melts": true,
				"minecraft:gameplay/water_evaporates": true
			},
			"natural": true,
			"has_skylight": true,
			"has_ceiling": false,
			"coordinate_scale": 1.0,
			"ambient_light": 0.0,
			"logical_height": 0,
			"infiniburn": "#minecraft:infiniburn_overworld",
			"min_y": 0,
			"height": 16,
			"monster_spawn_light_level": 0,
			"monster_spawn_block_light_limit": 0
		}
	""".trimIndent()

	dimensionType("env_attr_color_modifier") {
		attributes {
			fogColor(rgb(255, 255, 0), EnvironmentAttributeModifier.ADD)
		}
		monsterSpawnLightLevel = constant(0)
	}

	dimensionTypes.last() assertsIs """
		{
			"attributes": {
				"minecraft:visual/fog_color": {
					"argument": 16776960,
					"modifier": "add"
				}
			},
			"natural": true,
			"has_skylight": true,
			"has_ceiling": false,
			"coordinate_scale": 1.0,
			"ambient_light": 0.0,
			"logical_height": 0,
			"infiniburn": "#minecraft:infiniburn_overworld",
			"min_y": 0,
			"height": 16,
			"monster_spawn_light_level": 0,
			"monster_spawn_block_light_limit": 0
		}
	""".trimIndent()

	dimensionType("env_attr_colors") {
		attributes {
			fogColor(rgb(255, 170, 0))
			skyColor(Color.RED)
			skyLightColor(rgb(255, 255, 255))
			waterFogColor(rgb(5, 5, 51))
		}
		monsterSpawnLightLevel = constant(0)
	}

	dimensionTypes.last() assertsIs """
		{
			"attributes": {
				"minecraft:visual/fog_color": 16755200,
				"minecraft:visual/sky_color": 16733525,
				"minecraft:visual/sky_light_color": 16777215,
				"minecraft:visual/water_fog_color": 329011
			},
			"natural": true,
			"has_skylight": true,
			"has_ceiling": false,
			"coordinate_scale": 1.0,
			"ambient_light": 0.0,
			"logical_height": 0,
			"infiniburn": "#minecraft:infiniburn_overworld",
			"min_y": 0,
			"height": 16,
			"monster_spawn_light_level": 0,
			"monster_spawn_block_light_limit": 0
		}
	""".trimIndent()

	dimensionType("env_attr_dripstone_particle") {
		attributes {
			defaultDripstoneParticle(Particles.DRIPPING_DRIPSTONE_WATER)
		}
		monsterSpawnLightLevel = constant(0)
	}

	dimensionTypes.last() assertsIs """
		{
			"attributes": {
				"minecraft:visual/default_dripstone_particle": {
					"type": "minecraft:dripping_dripstone_water"
				}
			},
			"natural": true,
			"has_skylight": true,
			"has_ceiling": false,
			"coordinate_scale": 1.0,
			"ambient_light": 0.0,
			"logical_height": 0,
			"infiniburn": "#minecraft:infiniburn_overworld",
			"min_y": 0,
			"height": 16,
			"monster_spawn_light_level": 0,
			"monster_spawn_block_light_limit": 0
		}
	""".trimIndent()

	dimensionType("env_attr_floats") {
		attributes {
			musicVolume(0.8f)
			catWakingUpGiftChance(0.5f)
			skyLightLevel(15.0f)
			surfaceSlimeSpawnChance(0.1f)
			turtleEggHatchChance(0.05f)
			cloudHeight(192.33f)
			moonAngle(45.0f)
			skyLightFactor(0.9f)
			starAngle(30.0f)
			starBrightness(0.7f)
			sunAngle(90.0f)
		}
		monsterSpawnLightLevel = constant(0)
	}

	dimensionTypes.last() assertsIs """
		{
			"attributes": {
				"minecraft:audio/music_volume": 0.8,
				"minecraft:gameplay/cat_waking_up_gift_chance": 0.5,
				"minecraft:gameplay/sky_light_level": 15.0,
				"minecraft:gameplay/surface_slime_spawn_chance": 0.1,
				"minecraft:gameplay/turtle_egg_hatch_chance": 0.05,
				"minecraft:visual/cloud_height": 192.33,
				"minecraft:visual/moon_angle": 45.0,
				"minecraft:visual/sky_light_factor": 0.9,
				"minecraft:visual/star_angle": 30.0,
				"minecraft:visual/star_brightness": 0.7,
				"minecraft:visual/sun_angle": 90.0
			},
			"natural": true,
			"has_skylight": true,
			"has_ceiling": false,
			"coordinate_scale": 1.0,
			"ambient_light": 0.0,
			"logical_height": 0,
			"infiniburn": "#minecraft:infiniburn_overworld",
			"min_y": 0,
			"height": 16,
			"monster_spawn_light_level": 0,
			"monster_spawn_block_light_limit": 0
		}
	""".trimIndent()

	dimensionType("env_attr_cloud_color") {
		attributes {
			cloudColor(ARGB(255, 128, 64, 32))
		}
		monsterSpawnLightLevel = constant(0)
	}

	dimensionTypes.last() assertsIs """
		{
			"attributes": {
				"minecraft:visual/cloud_color": "#ff804020"
			},
			"natural": true,
			"has_skylight": true,
			"has_ceiling": false,
			"coordinate_scale": 1.0,
			"ambient_light": 0.0,
			"logical_height": 0,
			"infiniburn": "#minecraft:infiniburn_overworld",
			"min_y": 0,
			"height": 16,
			"monster_spawn_light_level": 0,
			"monster_spawn_block_light_limit": 0
		}
	""".trimIndent()

	dimensionType("env_attr_blend_to_gray") {
		attributes {
			fogColor(rgb(255, 170, 0), EnvironmentAttributeModifier.BlendToGray(brightness = 0.5f, factor = 0.8f))
		}
		monsterSpawnLightLevel = constant(0)
	}

	dimensionTypes.last() assertsIs """
		{
			"attributes": {
				"minecraft:visual/fog_color": {
					"argument": 16755200,
					"modifier": {
						"type": "blend_to_gray",
						"brightness": 0.5,
						"factor": 0.8
					}
				}
			},
			"natural": true,
			"has_skylight": true,
			"has_ceiling": false,
			"coordinate_scale": 1.0,
			"ambient_light": 0.0,
			"logical_height": 0,
			"infiniburn": "#minecraft:infiniburn_overworld",
			"min_y": 0,
			"height": 16,
			"monster_spawn_light_level": 0,
			"monster_spawn_block_light_limit": 0
		}
	""".trimIndent()

	dimensionType("env_attr_distance_floats") {
		attributes {
			cloudFogEndDistance(256.0f)
			fogEndDistance(192.0f)
			fogStartDistance(0.0f)
			skyFogEndDistance(320.0f)
			waterFogEndDistance(96.0f)
			waterFogStartDistance(0.0f)
		}
		monsterSpawnLightLevel = constant(0)
	}

	dimensionTypes.last() assertsIs """
		{
			"attributes": {
				"minecraft:visual/cloud_fog_end_distance": 256.0,
				"minecraft:visual/fog_end_distance": 192.0,
				"minecraft:visual/fog_start_distance": 0.0,
				"minecraft:visual/sky_fog_end_distance": 320.0,
				"minecraft:visual/water_fog_end_distance": 96.0,
				"minecraft:visual/water_fog_start_distance": 0.0
			},
			"natural": true,
			"has_skylight": true,
			"has_ceiling": false,
			"coordinate_scale": 1.0,
			"ambient_light": 0.0,
			"logical_height": 0,
			"infiniburn": "#minecraft:infiniburn_overworld",
			"min_y": 0,
			"height": 16,
			"monster_spawn_light_level": 0,
			"monster_spawn_block_light_limit": 0
		}
	""".trimIndent()

	dimensionType("env_attr_eyeblossom_open") {
		attributes {
			eyeblossomOpen(EyeblossomOpenState.TRUE, EnvironmentAttributeModifier.OVERRIDE)
		}
		monsterSpawnLightLevel = constant(0)
	}

	dimensionTypes.last() assertsIs """
		{
			"attributes": {
				"minecraft:gameplay/eyeblossom_open": {
					"argument": "true",
					"modifier": "override"
				}
			},
			"natural": true,
			"has_skylight": true,
			"has_ceiling": false,
			"coordinate_scale": 1.0,
			"ambient_light": 0.0,
			"logical_height": 0,
			"infiniburn": "#minecraft:infiniburn_overworld",
			"min_y": 0,
			"height": 16,
			"monster_spawn_light_level": 0,
			"monster_spawn_block_light_limit": 0
		}
	""".trimIndent()

	dimensionType("env_attr_villager_activity") {
		attributes {
			babyVillagerActivity(Activities.PLAY, EnvironmentAttributeModifier.OVERRIDE)
			villagerActivity(Activities.WORK, EnvironmentAttributeModifier.OVERRIDE)
		}
		monsterSpawnLightLevel = constant(0)
	}

	dimensionTypes.last() assertsIs """
		{
			"attributes": {
				"minecraft:gameplay/baby_villager_activity": {
					"argument": "minecraft:play",
					"modifier": "override"
				},
				"minecraft:gameplay/villager_activity": {
					"argument": "minecraft:work",
					"modifier": "override"
				}
			},
			"natural": true,
			"has_skylight": true,
			"has_ceiling": false,
			"coordinate_scale": 1.0,
			"ambient_light": 0.0,
			"logical_height": 0,
			"infiniburn": "#minecraft:infiniburn_overworld",
			"min_y": 0,
			"height": 16,
			"monster_spawn_light_level": 0,
			"monster_spawn_block_light_limit": 0
		}
	""".trimIndent()

	dimensionType("env_attr_moon_phase") {
		attributes {
			moonPhase(Textures.Environment.Celestial.Moon.FULL_MOON, EnvironmentAttributeModifier.OVERRIDE)
		}
		monsterSpawnLightLevel = constant(0)
	}

	dimensionTypes.last() assertsIs """
		{
			"attributes": {
				"minecraft:visual/moon_phase": {
					"argument": "minecraft:environment/celestial/moon/full_moon",
					"modifier": "override"
				}
			},
			"natural": true,
			"has_skylight": true,
			"has_ceiling": false,
			"coordinate_scale": 1.0,
			"ambient_light": 0.0,
			"logical_height": 0,
			"infiniburn": "#minecraft:infiniburn_overworld",
			"min_y": 0,
			"height": 16,
			"monster_spawn_light_level": 0,
			"monster_spawn_block_light_limit": 0
		}
	""".trimIndent()

	dimensionType("env_attr_sunrise_sunset_color") {
		attributes {
			sunriseSunsetColor(ARGB(128, 255, 128, 0))
		}
		monsterSpawnLightLevel = constant(0)
	}

	dimensionTypes.last() assertsIs """
		{
			"attributes": {
				"minecraft:visual/sunrise_sunset_color": "#80ff8000"
			},
			"natural": true,
			"has_skylight": true,
			"has_ceiling": false,
			"coordinate_scale": 1.0,
			"ambient_light": 0.0,
			"logical_height": 0,
			"infiniburn": "#minecraft:infiniburn_overworld",
			"min_y": 0,
			"height": 16,
			"monster_spawn_light_level": 0,
			"monster_spawn_block_light_limit": 0
		}
	""".trimIndent()
}
