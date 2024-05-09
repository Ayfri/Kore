package io.github.ayfri.kore.arguments

import io.github.ayfri.kore.arguments.chatcomponents.text
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.components.types.*
import io.github.ayfri.kore.arguments.enums.Dimension
import io.github.ayfri.kore.arguments.enums.MapDecoration
import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.arguments.types.literals.randomUUID
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.commands.AttributeModifierOperation
import io.github.ayfri.kore.data.item.builders.itemStack
import io.github.ayfri.kore.generated.*
import io.github.ayfri.kore.generated.Enchantments
import io.github.ayfri.kore.utils.set

fun itemsTests() {
	val stoneSword = Items.STONE_SWORD
	val stone = Items.STONE
	val leatherHelmet = Items.LEATHER_HELMET
	val crossbow = Items.CROSSBOW
	val bundle = Items.BUNDLE

	val uuid = randomUUID()
	val attributeModifiersTest = stoneSword {
		attributeModifiers {
			modifier(
				type = Attributes.GENERIC_SCALE,
				amount = 1.0,
				name = "Big!",
				operation = AttributeModifierOperation.ADD_VALUE,
				uuid = uuid,
			)
		}
	}
	attributeModifiersTest.asString() assertsIs """minecraft:stone_sword[attribute_modifiers=[{type:"minecraft:generic.scale",uuid:"${uuid.asString()}",name:"Big!",amount:1.0d,operation:"add_value"}]]"""

	attributeModifiersTest.components!!.attributeModifiers {
		modifier(
			type = Attributes.GENERIC_ATTACK_DAMAGE,
			amount = 1.0,
			name = "Big!",
			operation = AttributeModifierOperation.ADD_VALUE,
			uuid = uuid,
		)
		showInTooltip = true
	}
	attributeModifiersTest.asString() assertsIs """minecraft:stone_sword[attribute_modifiers={modifiers:[{type:"minecraft:generic.attack_damage",uuid:"${uuid.asString()}",name:"Big!",amount:1.0d,operation:"add_value"}],show_in_tooltip:1b}]"""

	val bannerPatternsTest = Items.WHITE_BANNER {
		bannerPatterns {
			pattern(BannerPatterns.CIRCLE, Color.AQUA)
		}
	}
	bannerPatternsTest.asString() assertsIs """minecraft:white_banner[banner_patterns=[{pattern:"minecraft:circle",color:"aqua"}]]"""

	val baseColorTest = stone {
		baseColor(Color.AQUA)
	}
	baseColorTest.asString() assertsIs """minecraft:stone[base_color="aqua"]"""

	val beesTest = Items.BEE_NEST {
		bees {
			bee(ticksInHive = 10, minTicksInHive = 20) {
				entityDataId(EntityTypes.BEE)
				entityData {
					this["test"] = "test"
				}
			}
		}
	}
	beesTest.asString() assertsIs """minecraft:bee_nest[bees=[{entity_data:{id:"minecraft:bee",test:"test"},ticks_in_hive:10,min_ticks_in_hive:20}]]"""

	val blockEntityDataTest = Items.BEE_NEST {
		blockEntityData(Blocks.BEE_NEST) {
			this["test"] = "test"
		}
	}
	blockEntityDataTest.asString() assertsIs """minecraft:bee_nest[block_entity_data={id:"minecraft:bee_nest",test:"test"}]"""

	val blockStateTest = stone {
		blockState {
			this["test"] = "test"
		}
	}
	blockStateTest.asString() assertsIs """minecraft:stone[block_state={test:"test"}]"""

	val bucketEntityDataTest = Items.WATER_BUCKET {
		bucketEntityData {
			this["test"] = "test"
		}
	}
	bucketEntityDataTest.asString() assertsIs """minecraft:water_bucket[bucket_entity_data={test:"test"}]"""

	val bundleContentsTest = bundle {
		bundleContents(Items.DIAMOND, Items.DIRT)
	}
	bundleContentsTest.asString() assertsIs """minecraft:bundle[bundle_contents=[{id:"minecraft:diamond"},{id:"minecraft:dirt"}]]"""

	bundleContentsTest.components!!.bundleContents {
		item(Items.DIAMOND_SWORD, 5) {
			damage(1)
		}
	}
	bundleContentsTest.asString() assertsIs """minecraft:bundle[bundle_contents=[{id:"minecraft:diamond_sword",count:5s,components:{damage:1}}]]"""

	val canBreakTest = stoneSword {
		canBreak(Blocks.DIAMOND_BLOCK)
	}
	canBreakTest.asString() assertsIs """minecraft:stone_sword[can_break={predicates:[{blocks:"minecraft:diamond_block"}]}]"""

	canBreakTest.components!!.canBreak {
		predicate(Blocks.DIAMOND_BLOCK, state = mapOf("test" to "test"))
		predicate(Blocks.STONE, Blocks.ICE)
		showInTooltip = true
	}
	canBreakTest.asString() assertsIs """minecraft:stone_sword[can_break={predicates:[{blocks:"minecraft:diamond_block",state:{test:"test"}},{blocks:["minecraft:stone","minecraft:ice"]}],show_in_tooltip:1b}]"""

	val canPlaceOnTest = stone {
		canPlaceOn(Blocks.DIAMOND_BLOCK)
	}
	canPlaceOnTest.asString() assertsIs """minecraft:stone[can_place_on={predicates:[{blocks:"minecraft:diamond_block"}]}]"""

	canPlaceOnTest.components!!.canPlaceOn {
		predicate(Blocks.DIAMOND_BLOCK, state = mapOf("test" to "test"))
		predicate(Blocks.STONE, Blocks.ICE)
		showInTooltip = true
	}
	canPlaceOnTest.asString() assertsIs """minecraft:stone[can_place_on={predicates:[{blocks:"minecraft:diamond_block",state:{test:"test"}},{blocks:["minecraft:stone","minecraft:ice"]}],show_in_tooltip:1b}]"""

	val chargedProjectilesTest = crossbow {
		chargedProjectile(Items.ARROW)
	}
	chargedProjectilesTest.asString() assertsIs """minecraft:crossbow[charged_projectiles=[{id:"minecraft:arrow"}]]"""

	chargedProjectilesTest.components!!.chargedProjectiles(Items.ARROW, Items.SPECTRAL_ARROW, Items.TIPPED_ARROW)
	chargedProjectilesTest.asString() assertsIs """minecraft:crossbow[charged_projectiles=[{id:"minecraft:arrow"},{id:"minecraft:spectral_arrow"},{id:"minecraft:tipped_arrow"}]]"""

	val containerTest = stone {
		container {
			slot(0, itemStack(Items.DIAMOND))
			slot(1, itemStack(Items.DIRT) {
				damage(1)
			})
			this[10] = itemStack(Items.DIAMOND)
		}
	}
	containerTest.asString() assertsIs """minecraft:stone[container=[{slot:0,item:{id:"minecraft:diamond"}},{slot:1,item:{id:"minecraft:dirt",components:{damage:1}}},{slot:10,item:{id:"minecraft:diamond"}}]]"""

	containerTest.components!!.container {
		slot(CONTAINER[10], itemStack(Items.DIAMOND))
		slot(WEAPON, itemStack(Items.DIRT) {
			damage(1)
		})
	}
	containerTest.asString() assertsIs """minecraft:stone[container=[{slot:10,item:{id:"minecraft:diamond"}},{slot:98,item:{id:"minecraft:dirt",components:{damage:1}}}]]"""

	val containerLootTest = stone {
		containerLoot(LootTables.Gameplay.PANDA_SNEEZE, 123)
	}
	containerLootTest.asString() assertsIs """minecraft:stone[container_loot={loot_table:"minecraft:gameplay/panda_sneeze",seed:123L}]"""

	val customDataTest = stoneSword {
		customData {
			this["test"] = "test"
		}
	}
	customDataTest.asString() assertsIs """minecraft:stone_sword[custom_data={test:"test"}]"""

	val customModelDataTest = stoneSword {
		customModelData(5)
	}
	customModelDataTest.asString() assertsIs """minecraft:stone_sword[custom_model_data=5]"""

	val customNameTest = stoneSword {
		customName(textComponent("test", Color.AQUA))
	}
	customNameTest.asString() assertsIs """minecraft:stone_sword[custom_name='{"text":"test","color":"aqua"}']"""

	customNameTest.components!!.customName(textComponent("test"))
	customNameTest.asString() assertsIs """minecraft:stone_sword[custom_name='"test"']"""

	val damageTest = stoneSword {
		damage(5)
	}
	damageTest.asString() assertsIs """minecraft:stone_sword[damage=5]"""

	val debugStickStateTest = stone {
		debugStickState {
			this[Blocks.DIAMOND_BLOCK] = "test"
		}
	}
	debugStickStateTest.asString() assertsIs """minecraft:stone[debug_stick_state={"minecraft:diamond_block":"test"}]"""

	val dyedColorTest = leatherHelmet {
		dyedColor(Color.AQUA)
	}
	dyedColorTest.asString() assertsIs """minecraft:leather_helmet[dyed_color=5636095]"""

	dyedColorTest.components!!.dyedColor(Color.AQUA, showInTooltip = true)
	dyedColorTest.asString() assertsIs """minecraft:leather_helmet[dyed_color={rgb:5636095,show_in_tooltip:1b}]"""

	val enchantmentsTest = stoneSword {
		enchantments(mapOf(Enchantments.SHARPNESS to 5))
	}
	enchantmentsTest.asString() assertsIs """minecraft:stone_sword[enchantments={"minecraft:sharpness":5}]"""

	enchantmentsTest.components!!.enchantments {
		enchantment(Enchantments.LOOTING, 3)
		showInTooltip = true
	}
	enchantmentsTest.asString() assertsIs """minecraft:stone_sword[enchantments={levels:{"minecraft:looting":3},show_in_tooltip:1b}]"""

	val enchantmentGlintOverrideTest = stoneSword {
		enchantmentGlintOverride(true)
	}
	enchantmentGlintOverrideTest.asString() assertsIs """minecraft:stone_sword[enchantment_glint_override=1b]"""

	val entityDataTest = stoneSword {
		entityData {
			this["test"] = "test"
		}
	}
	entityDataTest.asString() assertsIs """minecraft:stone_sword[entity_data={test:"test"}]"""

	val fireResistantTest = stoneSword {
		fireResistant()
	}
	fireResistantTest.asString() assertsIs """minecraft:stone_sword[fire_resistant={}]"""

	val fireworksTest = Items.FIREWORK_ROCKET {
		fireworks(flightDuration = 1) {
			explosion(FireworkExplosionShape.BURST) {
				colors(Color.AQUA)
				fadeColors(Color.BLACK, Color.WHITE)
				hasTrail = true
				hasFlicker = true
			}

			explosion(FireworkExplosionShape.CREEPER) {
				colors(Color.BLACK)
				fadeColors(Color.WHITE)
				hasTrail = false
				hasFlicker = false
			}
		}
	}
	fireworksTest.asString() assertsIs """minecraft:firework_rocket[fireworks={explosions:[{shape:"burst",colors:[5636095],fade_colors:[0,16777215],has_trail:1b,has_flicker:1b},{shape:"creeper",colors:[0],fade_colors:[16777215],has_trail:0b,has_flicker:0b}],flight_duration:1}]"""

	val fireworkExplosionTest = Items.FIREWORK_STAR {
		fireworkExplosion(FireworkExplosionShape.BURST) {
			colors(Color.AQUA)
			fadeColors(Color.BLACK, Color.WHITE)
			hasTrail = true
			hasFlicker = true
		}
	}
	fireworkExplosionTest.asString() assertsIs """minecraft:firework_star[firework_explosion={shape:"burst",colors:[5636095],fade_colors:[0,16777215],has_trail:1b,has_flicker:1b}]"""

	val foodTest = Items.COOKED_BEEF {
		food(
			nutrition = 10,
			saturationModifier = 1.0f,
		) {
			isMeat = true
			eatSeconds = 0.5f

			effect(
				probability = 1f,
				id = Effects.REGENERATION,
				duration = 100,
				amplifier = 1,
				ambient = true,
				showParticles = true,
				showIcon = true
			)
		}
	}
	foodTest.asString() assertsIs """minecraft:cooked_beef[food={nutrition:10,saturation_modifier:1.0f,is_meat:1b,eat_seconds:0.5f,effects:[{effect:{id:"minecraft:regeneration",duration:100,amplifier:1b,ambient:1b,show_particles:1b,show_icon:1b},probability:1.0f}]}]"""

	val instrumentTest = Items.GOAT_HORN {
		instrument(Instruments.CALL_GOAT_HORN)
	}
	instrumentTest.asString() assertsIs """minecraft:goat_horn[instrument="minecraft:call_goat_horn"]"""

	val intangibleProjectileTest = crossbow {
		intangibleProjectile()
	}
	intangibleProjectileTest.asString() assertsIs """minecraft:crossbow[intangible_projectile={}]"""

	val hideAdditionalTooltipTest = stoneSword {
		hideAdditionalTooltip()
	}
	hideAdditionalTooltipTest.asString() assertsIs """minecraft:stone_sword[hide_additional_tooltip={}]"""

	val hideTooltipTest = stoneSword {
		hideTooltip()
	}
	hideTooltipTest.asString() assertsIs """minecraft:stone_sword[hide_tooltip={}]"""

	val lockTest = stoneSword {
		lock("test")
	}
	lockTest.asString() assertsIs """minecraft:stone_sword[lock="test"]"""

	val lodeStoneTargetTest = Items.LODESTONE {
		lodestoneTarget(10, 10, 10, Dimension.OVERWORLD)
	}
	lodeStoneTargetTest.asString() assertsIs """minecraft:lodestone[lodestone_tracker={pos:[10,10,10],dimension:"minecraft:overworld"}]"""

	lodeStoneTargetTest.components!!.lodestoneTarget(vec3(10.0, 10.0, 10.0), Dimension.OVERWORLD, tracked = true)
	lodeStoneTargetTest.asString() assertsIs """minecraft:lodestone[lodestone_tracker={pos:[10,10,10],dimension:"minecraft:overworld",tracked:1b}]"""

	val loreTest = stoneSword {
		lore("test")
	}
	loreTest.asString() assertsIs """minecraft:stone_sword[lore=['"test"']]"""

	loreTest.components!!.lore(textComponent("test", Color.AQUA) + text("test2", Color.BLACK) + text("a"))
	loreTest.asString() assertsIs """minecraft:stone_sword[lore=['[{"text":"test","color":"aqua"},{"text":"test2","color":"black"},"a"]']]"""

	val mapColorTest = stone {
		mapColor(Color.AQUA.toRGB())
	}
	mapColorTest.asString() assertsIs """minecraft:stone[map_color=5636095]"""

	val mapDecorationsTest = stone {
		mapDecorations {
			decoration("here", MapDecoration.PLAYER, 10.0, 10.0, 5f)
		}
	}
	mapDecorationsTest.asString() assertsIs """minecraft:stone[map_decorations={here:{type:"player",x:10.0d,z:10.0d,rotation:5.0f}}]"""

	val mapIdTest = stone {
		mapId(5)
	}
	mapIdTest.asString() assertsIs """minecraft:stone[map_id=5]"""

	val maxDamageTest = stoneSword {
		maxDamage(5)
	}
	maxDamageTest.asString() assertsIs """minecraft:stone_sword[max_damage=5]"""

	val maxStackSizeTest = stone {
		maxStackSize(5)
	}
	maxStackSizeTest.asString() assertsIs """minecraft:stone[max_stack_size=5]"""

	val noteBlockSoundTest = Items.PLAYER_HEAD {
		noteBlockSound(Sounds.Mob.Cow.SAY1)
	}
	noteBlockSoundTest.asString() assertsIs """minecraft:player_head[note_block_sound="minecraft:mob.cow.say1"]"""

	val potDecorationsTest = stone {
		potDecorations(Items.ARMS_UP_POTTERY_SHERD, Items.SKULL_POTTERY_SHERD, Items.FRIEND_POTTERY_SHERD, Items.BRICK)
	}
	potDecorationsTest.asString() assertsIs """minecraft:stone[pot_decorations=["minecraft:arms_up_pottery_sherd","minecraft:skull_pottery_sherd","minecraft:friend_pottery_sherd","minecraft:brick"]]"""

	val potionContentsTest = Items.POTION {
		potionContents(
			Potions.AWKWARD
		) {
			customColor = Color.AQUA.toRGB()
			customEffect(
				Effects.POISON,
				100,
				1,
				true,
				true,
				true
			)
		}
	}

	potionContentsTest.asString() assertsIs """minecraft:potion[potion_contents={potion:"minecraft:awkward",custom_color:5636095,custom_effects:[{id:"minecraft:poison",duration:100,amplifier:1b,ambient:1b,show_particles:1b,show_icon:1b}]}]"""

	potionContentsTest.components!!.potionContents(Potions.AWKWARD)
	potionContentsTest.asString() assertsIs """minecraft:potion[potion_contents="minecraft:awkward"]"""

	val profileTest = Items.PLAYER_HEAD {
		profile("Notch")
	}
	profileTest.asString() assertsIs """minecraft:player_head[profile="Notch"]"""

	val randomId = randomUUID()
	profileTest.components!!.profile("Notch", id = randomId)
	profileTest.asString() assertsIs """minecraft:player_head[profile={name:"Notch",id:"${randomId.asString()}"}]"""

	profileTest.components!!.profile("Notch") {
		property("test", "test")
		property("test", byteArrayOf('K'.code.toByte(), 'o'.code.toByte(), 'r'.code.toByte(), 'e'.code.toByte()))
	}
	profileTest.asString() assertsIs """minecraft:player_head[profile={name:"Notch",properties:[{name:"test",value:"test"},{name:"test",value:"Kore"}]}]"""

	val rarityTest = stone {
		rarity(Rarities.EPIC)
	}
	rarityTest.asString() assertsIs """minecraft:stone[rarity="epic"]"""

	val recipesTest = stone {
		recipes(Recipes.CRAFTING_TABLE, Recipes.FURNACE)
	}
	recipesTest.asString() assertsIs """minecraft:stone[recipes=["minecraft:crafting_table","minecraft:furnace"]]"""

	recipesTest.components!!.recipes {
		recipe(Recipes.BLAST_FURNACE)
	}
	recipesTest.asString() assertsIs """minecraft:stone[recipes=["minecraft:blast_furnace"]]"""

	val repairCostTest = stoneSword {
		repairCost(5)
	}
	repairCostTest.asString() assertsIs """minecraft:stone_sword[repair_cost=5]"""

	val storedEnchantmentsTest = stoneSword {
		storedEnchantments(mapOf(Enchantments.SHARPNESS to 5))
	}
	storedEnchantmentsTest.asString() assertsIs """minecraft:stone_sword[stored_enchantments={"minecraft:sharpness":5}]"""

	storedEnchantmentsTest.components!!.storedEnchantments {
		enchantment(Enchantments.LOOTING, 3)
		showInTooltip = true
	}
	storedEnchantmentsTest.asString() assertsIs """minecraft:stone_sword[stored_enchantments={levels:{"minecraft:looting":3},show_in_tooltip:1b}]"""

	val suspiciousStewTest = Items.SUSPICIOUS_STEW {
		suspiciousStewEffectsComponent {
			effect(Effects.POISON, 100)
		}
	}
	suspiciousStewTest.asString() assertsIs """minecraft:suspicious_stew[suspicious_stew_effects=[{id:"minecraft:poison",duration:100}]]"""

	val toolTest = stoneSword {
		tool {
			rule(
				speed = 0.5f,
				correctForDrops = true,
				Tags.Blocks.BASE_STONE_OVERWORLD,
				Tags.Blocks.OVERWORLD_CARVER_REPLACEABLES,
				Blocks.HAY_BLOCK
			)
			defaultMiningSpeed = 2.0f
		}
	}
	toolTest.asString() assertsIs """minecraft:stone_sword[tool={rules:[{blocks:["#minecraft:base_stone_overworld","#minecraft:overworld_carver_replaceables","minecraft:hay_block"],speed:0.5f,correct_for_drops:1b}],default_mining_speed:2.0f}]"""

	val trimTest = Items.DIAMOND_CHESTPLATE {
		trim(TrimPatterns.SHAPER, TrimMaterials.DIAMOND)
	}
	trimTest.asString() assertsIs """minecraft:diamond_chestplate[trim={pattern:"minecraft:shaper",material:"minecraft:diamond"}]"""

	val unbreakableTest = stoneSword {
		unbreakable()
	}
	unbreakableTest.asString() assertsIs """minecraft:stone_sword[unbreakable={}]"""

	unbreakableTest.components!!.unbreakable(showInTooltip = true)
	unbreakableTest.asString() assertsIs """minecraft:stone_sword[unbreakable={show_in_tooltip:1b}]"""

	val writableBookTest = Items.WRITABLE_BOOK {
		writableBookContent {
			page("test")
			page("test2", "test3")
		}
	}
	writableBookTest.asString() assertsIs """minecraft:writable_book[writable_book_content={pages:[{text:"test"},{text:"test2",filtered:"test3"}]}]"""

	val writtenBookTest = Items.WRITTEN_BOOK {
		writtenBookContent(
			title = textComponent("test"),
			author = "test",
			generation = 1,
			resolved = true
		) {
			page(textComponent("test", Color.AQUA))
		}
	}
	writtenBookTest.asString() assertsIs """minecraft:written_book[written_book_content={pages:['{"text":"test","color":"aqua"}'],title:'"test"',author:"test",generation:1,resolved:1b}]"""
}
