package generators

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import generateEnum
import generatePathEnumTree
import getFromCacheOrDownloadTxt
import overrides

suspend fun launchAllSimpleGenerators() {
	val txtListsListGenerators = listOf(
		gen("BannerPatterns", "banner_patterns"),
		gen("CatVariants", "cats"),
		gen("ChickenVariants", "chickens"),
		gen("CowVariants", "cows"),
		gen("DamageTypes", "damage_types"),
		gen("DimensionTypes", "dimension_types"),
		gen("Dimensions", "dimensions"),
		gen("Enchantments", "enchantments"),
		gen("FrogVariants", "frogs"),
		gen("Instruments", "instruments"),
		gen("JukeboxSongs", "jukebox_songs"),
		gen("PaintingVariants", "paintings"),
		gen("PigVariants", "pigs"),
		gen("Recipes", "recipes"),
		gen("TestEnvironments", "test_environment"),
		gen("TestInstances", "test_instance"),
		gen("TrimMaterials", "trim_materials"),
		gen("TrimPatterns", "trim_patterns"),
		gen("PigVariants", "pigs"),
		gen("WaypointStyles", "waypoint_style"),
		gen("WolfSoundVariants", "wolf_sounds"),
		gen("WolfVariants", "wolfs"),

		gen("Biomes", "worldgen/biome"),
		gen("BiomePresets", "worldgen/multi_noise_biome_source_parameter_list") {
			argumentClassName = "MultiNoiseBiomeSourceParameterList"
		},
		gen("ConfiguredFeatures", "worldgen/configured_feature"),
		gen("FlatLevelGeneratorPresets", "worldgen/flat_level_generator_preset"),
		gen("Noises", "worldgen/noise"),
		gen("NoiseSettings", "worldgen/noise_settings") {
			argumentClassName = "NoiseSettings"
		},
		gen("PlacedFeatures", "worldgen/placed_feature"),
		gen("ProcessorLists", "worldgen/processor_list"),
		gen("ConfiguredStructures", "worldgen/structure"),
		gen("StructureSets", "worldgen/structure_set"),
	).transformRemoveJSONSuffix()

	val txtListsTreeGenerators = listOf(
		gen("Advancements", "advancements"),
		gen("LootTables", "loot_tables"),
		gen("Models", "models") {
			argumentClassName = "Model M"
		},
		gen("Sounds", "sounds") {
			argumentClassName = "Sound M"
			transform { it.removeSuffix(".ogg") }
		},
		gen("Structures", "structures") {
			argumentClassName = "worldgen.Structure"
			transform { it.removeSuffix(".nbt") }
		},
		gen("Tags", "tags") {
			argumentClassName = "Tag M"
			tagsParents = mapOf(
				"banner_pattern" to "BannerPatternTag",
				"block" to "BlockTag M",
				"cat_variant" to "CatVariantTag",
				"damage_type" to "DamageTypeTag",
				"enchantment" to "EnchantmentTag",
				"entity_type" to "EntityTypeTag",
				"fluid" to "FluidTag",
				"frog_variant" to "FrogVariantTag",
				"game_event" to "GameEventTag",
				"instrument" to "InstrumentTag",
				"item" to "ItemTag M",
				"painting_variant" to "PaintingVariantTag",
				"point_of_interest_type" to "PointOfInterestTypeTag",
				"pig_variant" to "PigVariantTag",
				"wolf_variant" to "WolfVariantTag",
				"worldgen/biome" to "worldgen.BiomeTag",
				"worldgen/flat_level_generator_preset" to "worldgen.FlatLevelGeneratorPresetTag",
				"worldgen/structure" to "worldgen.ConfiguredStructureTag",
				"worldgen/world_preset" to "worldgen.WorldPresetTag",
			)
		},
		gen("Textures", "textures") {
			argumentClassName = "Model M"
			transform { it.removeSuffix(".png") }
			subInterfacesParents("ColorPalettes" to "arguments.types.TrimColorPaletteArgument")
		},
		gen("DensityFunctions", "worldgen/density_function"),
		gen("TemplatePools", "worldgen/template_pool"),
	).transformRemoveJSONSuffix().map { gen ->
		gen.apply { enumTree = true }
	}

	val txtRegistriesListGenerators = listOf(
		gen("Attributes", "attribute"),
		gen("BlockEntityTypes", "block_entity_type"),
		gen("Blocks", "block") {
			argumentClassName = "Block M"
			transform { it.removePrefix("minecraft:") }
			additionalCode {
				addProperty(
					PropertySpec.builder("nbtData", ClassName("net.benwoodworth.knbt", "NbtCompound").copy(nullable = true))
						.overrides()
						.mutable()
						.initializer("null")
						.build()
				)

				val stringTypeName = String::class.asTypeName()
				addProperty(
					PropertySpec.builder("states", MUTABLE_MAP.parameterizedBy(stringTypeName, stringTypeName))
						.overrides()
						.mutable()
						.initializer("mutableMapOf()")
						.build()
				)
			}
		},
		gen("CustomStats", "custom_stat"),
		gen("Effects", "mob_effect") {
			argumentClassName = "MobEffect"
		},
		gen("EnchantmentEffectComponents", "enchantment_effect_component_type") {
			argumentClassName = ""
			additionalCode {
				addFunction(
					FunSpec.builder("asId")
						.addStatement($"return \"minecraft:\${name.lowercase()}\"")
						.returns(String::class)
						.build()
				)
			}
		},
		gen("EntityTypes", "entity_type"),
		gen("Fluids", "fluid"),
		gen("GameEvents", "game_event"),
		gen("Items", "item") {
			argumentClassName = "Item M"
			additionalCode {
				addProperty(
					PropertySpec.builder(
						"components",
						ClassName("io.github.ayfri.kore.arguments.components", "ComponentsPatch").copy(nullable = true)
					)
						.overrides()
						.mutable()
						.initializer("null")
						.build()
				)

				addFunction(
					FunSpec.builder(
						"invoke",
					)
						.addParameter(
							"block", LambdaTypeName.get(
								receiver = ClassName("io.github.ayfri.kore.arguments.components", "ComponentsPatch"),
								returnType = Unit::class.asTypeName()
							)
						)
						.overrides()
						.addStatement("return ItemArgument.invoke(name.lowercase(), namespace, ComponentsPatch().apply(block))")
						.returns(ClassName("io.github.ayfri.kore.arguments.types.resources", "ItemArgument"))
						.build()
				)
			}
		},
		gen("MapDecorationTypes", "map_decoration_type"),
		gen("Particles", "particle_type") {
			argumentClassName = "ParticleType"
		},
		gen("Potions", "potion"),
		gen("StatisticTypes", "stat_type") {
			argumentClassName = "StatType"
			transform { it.removePrefix("minecraft:") }
		},
		gen("VillagerProfessions", "villager_profession"),
		gen("VillagerTypes", "villager_type"),
		gen("Carvers", "worldgen/carver"),
	)

	val txtRegistriesTreeGenerators = listOf(
		gen("SoundEvents", "sound_event") {
			argumentClassName = "SoundEvent"
		},
	).transformRemoveMinecraftPrefix().map { gen ->
		gen.apply {
			enumTree = true
			separator = "."
		}
	}

	val allListGenerators = txtListsListGenerators.setUrlWithType("lists") +
		txtListsTreeGenerators.setUrlWithType("lists") +
		txtRegistriesListGenerators.setUrlWithType("registries") +
		txtRegistriesTreeGenerators.setUrlWithType("registries")

	allListGenerators.sortedBy { it.fileName }.forEach { gen ->
		val url = gen.url
		val list = getFromCacheOrDownloadTxt(gen.fileName, url).lines()
		val parentArgumentType = gen.getParentArgumentType()

		when {
			gen.enumTree -> {
				generatePathEnumTree(
					paths = list.mapIfNotNull(gen.transform),
					gen
				)

				gen.extractEnums?.forEach { (prefix, enumName) ->
					// Get the values prefixed by it
					val values = list.filter { it.startsWith(prefix) }
					generateEnum(
						values = values.map { it.removePrefix(prefix).removePrefix(gen.separator) }
							.mapIfNotNull(gen.transform),
						name = enumName,
						sourceUrl = url,
						asString = gen.asString,
						additionalEnumCode = gen.additionalCode
					)
				}
			}

			else -> generateEnum(
				values = list.mapIfNotNull(gen.transform),
				name = gen.name,
				sourceUrl = url,
				parentArgumentType = parentArgumentType,
				asString = gen.asString,
				additionalEnumCode = gen.additionalCode
			)
		}
	}
}
