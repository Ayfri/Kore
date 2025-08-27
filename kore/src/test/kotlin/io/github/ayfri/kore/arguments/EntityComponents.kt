package io.github.ayfri.kore.arguments

import io.github.ayfri.kore.arguments.components.entity.*
import io.github.ayfri.kore.arguments.enums.*
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.generated.*

fun entityComponentsTests() {
	Items.AXOLOTL_BUCKET {
		axolotlVariant(AxolotlVariants.BLUE)
	} assertsIs "minecraft:axolotl_bucket[axolotl_variant=\"blue\"]"

	Items.CAT_SPAWN_EGG {
		catCollar(DyeColors.RED)
	} assertsIs "minecraft:cat_spawn_egg[cat_collar=\"red\"]"

	Items.CAT_SPAWN_EGG {
		catVariant(CatVariants.TABBY)
	} assertsIs "minecraft:cat_spawn_egg[cat_variant=\"minecraft:tabby\"]"

	Items.CHICKEN_SPAWN_EGG {
		chickenVariant(ChickenVariants.COLD)
	} assertsIs "minecraft:chicken_spawn_egg[chicken_variant=\"minecraft:cold\"]"

	Items.COW_SPAWN_EGG {
		cowVariant(CowVariants.WARM)
	} assertsIs "minecraft:cow_spawn_egg[cow_variant=\"minecraft:warm\"]"

	Items.FOX_SPAWN_EGG {
		foxVariant(FoxVariants.SNOW)
	} assertsIs "minecraft:fox_spawn_egg[fox_variant=\"snow\"]"

	Items.FROG_SPAWN_EGG {
		frogVariant(FrogVariants.COLD)
	} assertsIs "minecraft:frog_spawn_egg[frog_variant=\"minecraft:cold\"]"

	Items.HORSE_SPAWN_EGG {
		horseVariant(HorseVariants.GRAY)
	} assertsIs "minecraft:horse_spawn_egg[horse_variant=\"gray\"]"

	Items.LLAMA_SPAWN_EGG {
		llamaVariant(LlamaVariants.BROWN)
	} assertsIs "minecraft:llama_spawn_egg[llama_variant=\"brown\"]"

	Items.MOOSHROOM_SPAWN_EGG {
		mooshroomVariant(MooshroomVariants.RED)
	} assertsIs "minecraft:mooshroom_spawn_egg[mooshroom_variant=\"red\"]"

	Items.PAINTING {
		paintingVariant(PaintingVariants.KEBAB)
	} assertsIs "minecraft:painting[painting_variant=\"minecraft:kebab\"]"

	Items.PARROT_SPAWN_EGG {
		parrotVariant(ParrotVariants.BLUE)
	} assertsIs "minecraft:parrot_spawn_egg[parrot_variant=\"blue\"]"

	Items.PIG_SPAWN_EGG {
		pigVariant(PigVariants.COLD)
	} assertsIs "minecraft:pig_spawn_egg[pig_variant=\"minecraft:cold\"]"

	Items.RABBIT_SPAWN_EGG {
		rabbitVariant(RabbitVariants.EVIL)
	} assertsIs "minecraft:rabbit_spawn_egg[rabbit_variant=\"evil\"]"

	Items.SALMON_BUCKET {
		salmonSize(SalmonSizes.LARGE)
	} assertsIs "minecraft:salmon_bucket[salmon_size=\"large\"]"

	Items.SHEEP_SPAWN_EGG {
		sheepColor(DyeColors.LIME)
	} assertsIs "minecraft:sheep_spawn_egg[sheep_color=\"lime\"]"

	Items.SHULKER_SPAWN_EGG {
		shulkerColor(DyeColors.PURPLE)
	} assertsIs "minecraft:shulker_spawn_egg[shulker_color=\"purple\"]"

	Items.TROPICAL_FISH_BUCKET {
		tropicalFishBaseColor(DyeColors.YELLOW)
	} assertsIs "minecraft:tropical_fish_bucket[tropical_fish_base_color=\"yellow\"]"

	Items.TROPICAL_FISH_BUCKET {
		tropicalFishPattern(TropicalFishPatterns.BETTY)
	} assertsIs "minecraft:tropical_fish_bucket[tropical_fish_pattern=\"betty\"]"

	Items.TROPICAL_FISH_BUCKET {
		tropicalFishPatternColor(DyeColors.CYAN)
	} assertsIs "minecraft:tropical_fish_bucket[tropical_fish_pattern_color=\"cyan\"]"

	Items.VILLAGER_SPAWN_EGG {
		villagerVariant(VillagerVariants.SAVANNA)
	} assertsIs "minecraft:villager_spawn_egg[villager_variant=\"savanna\"]"

	Items.WOLF_SPAWN_EGG {
		wolfCollar(DyeColors.BLACK)
	} assertsIs "minecraft:wolf_spawn_egg[wolf_collar=\"black\"]"

	Items.WOLF_SPAWN_EGG {
		wolfSoundVariant(WolfSoundVariants.CUTE)
	} assertsIs "minecraft:wolf_spawn_egg[wolf_sound_variant=\"minecraft:cute\"]"

	Items.WOLF_SPAWN_EGG {
		wolfVariant(WolfVariants.SNOWY)
	} assertsIs "minecraft:wolf_spawn_egg[wolf_variant=\"minecraft:snowy\"]"
}
