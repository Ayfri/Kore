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
		gen("DamageTypes", "damage_types"),
		gen("DimensionTypes", "dimension_types", "worldgen.DimensionType"),
		gen("Enchantments", "enchantments"),
		gen("Instruments", "instruments"),
		gen("JukeboxSongs", "jukebox_songs"),
		gen("PaintingVariants", "painting_variants"),
		gen("Recipes", "recipes"),
		gen("TrimMaterials", "trim_materials"),
		gen("TrimPatterns", "trim_patterns"),
		gen("WolfVariants", "wolf_variants"),
		gen("Biomes", "worldgen/biome"),
		gen("BiomePresets", "worldgen/multi_noise_biome_source_parameter_list"),
		gen("ConfiguredFeatures", "worldgen/configured_feature"),
		gen("FlatLevelGeneratorPresets", "worldgen/flat_level_generator_preset"),
		gen("Noises", "worldgen/noise"),
		gen("NoiseSettings", "worldgen/noise_settings", "NoiseSettings"),
		gen("PlacedFeatures", "worldgen/placed_feature"),
		gen("ProcessorLists", "worldgen/processor_list"),
		gen("ConfiguredStructures", "worldgen/structure"),
		gen("StructureSets", "worldgen/structure_set"),
	).transformRemoveJSONSuffix()

	val txtListsTreeGenerators = listOf(
		gen("Advancements", "advancements"),
		gen("LootTables", "loot_tables"),
		gen("Sounds", "sounds") { it.removeSuffix(".ogg") },
		gen("Structures", "structures", "worldgen.Structure") { it.removeSuffix(".nbt") },
		gen(
			"Tags", "tags", argumentClassName = "", tagsParents = mapOf(
				"block" to "BlockTag",
				"cat_variant" to "CatVariantTag",
				"damage_type" to "DamageTypeTag",
				"enchantment" to "EnchantmentTag",
				"fluid" to "FluidTag",
				"frog_variant" to "FrogVariantTag",
				"instrument" to "InstrumentTag",
				"item" to "ItemTag",
				"painting_variant" to "PaintingVariantTag",
				"wolf_variant" to "WolfVariantTag",
				"worldgen/biome" to "BiomeTag",
				"worldgen/structure" to "ConfiguredStructureTag",
			)
		),
		gen("DensityFunctions", "worldgen/density_function"),
		gen("TemplatePools", "worldgen/template_pool"),
	).transformRemoveJSONSuffix().map { gen ->
		gen.apply { enumTree = true }
	}

	val txtRegistriesListGenerators = listOf(
		gen("Attributes", "attribute"),
		gen("BlockEntityTypes", "block_entity_type"),
		gen("Blocks", "block", additionalCode = {
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
		}) { it.removePrefix("minecraft:") },
		gen("CatVariants", "cat_variant"),
		gen("ComponentTypes", "data_component_type", argumentClassName = ""),
		gen("CustomStats", "custom_stat"),
		gen("Effects", "mob_effect"),
		gen("EnchantmentEffectComponents", "enchantment_effect_component_type", argumentClassName = "", additionalCode = {
			addFunction(
				FunSpec.builder("asId")
					.addStatement("return \"minecraft:\" + name.lowercase()")
					.returns(String::class)
					.build()
			)
		}),
		gen("EntityTypes", "entity_type"),
		gen("Fluids", "fluid"),
		gen("FrogVariants", "frog_variant"),
		gen("GameEvents", "game_event"),
		gen("Items", "item", additionalCode = {
			addProperty(
				PropertySpec.builder(
					"components",
					ClassName("io.github.ayfri.kore.arguments.components", "ComponentsRemovables").copy(nullable = true)
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
							receiver = ClassName("io.github.ayfri.kore.arguments.components", "ComponentsRemovables"),
							returnType = Unit::class.asTypeName()
						)
					)
					.overrides()
					.addStatement("return ItemArgument.invoke(name.lowercase(), namespace, ComponentsRemovables().apply(block))")
					.returns(ClassName("io.github.ayfri.kore.arguments.types.resources", "ItemArgument"))
					.build()
			)
		}),
		gen("Particles", "particle_type"),
		gen("Potions", "potion"),
		gen("StatisticTypes", "stat_type") { it.removePrefix("minecraft:") },
		gen("VillagerProfessions", "villager_profession"),
		gen("VillagerTypes", "villager_type"),
		gen("Carvers", "worldgen/carver"),
	)

	val txtRegistriesTreeGenerators = listOf(
		gen("SoundEvents", "sound_event", "SoundEvent"),
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
		when {
			gen.enumTree -> generatePathEnumTree(
				paths = list.mapIfNotNull(gen.transform),
				name = gen.name,
				sourceUrl = url,
				parentArgumentType = gen.argumentName,
				separator = gen.separator,
				tagsParents = gen.tagsParents
			)

			else -> generateEnum(
				values = list.mapIfNotNull(gen.transform),
				name = gen.name,
				sourceUrl = url,
				parentArgumentType = gen.argumentName,
				asString = gen.asString,
				additionalEnumCode = gen.additionalCode
			)
		}
	}
}
