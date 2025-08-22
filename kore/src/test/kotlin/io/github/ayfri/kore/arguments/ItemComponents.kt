package io.github.ayfri.kore.arguments

import io.github.ayfri.kore.arguments.chatcomponents.text
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.components.consumable.applyEffects
import io.github.ayfri.kore.arguments.components.consumable.playSound
import io.github.ayfri.kore.arguments.components.consumable.teleportRandomly
import io.github.ayfri.kore.arguments.components.item.*
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

fun itemComponentsTests() {
	val a = Items.AIR {
		itemName("a")
	}

	val b = Items.AIR {
		copyFrom(a)
	}
	b.asString() assertsIs "minecraft:air[item_name=\"a\"]"

	val stoneSword = Items.STONE_SWORD
	val stone = Items.STONE
	val leatherHelmet = Items.LEATHER_HELMET
	val crossbow = Items.CROSSBOW
	val bundle = Items.BUNDLE

	val attributeModifiersTest = stoneSword {
		attributeModifiers {
			modifier(
				type = Attributes.SCALE,
				amount = 1.0,
				name = "big",
				operation = AttributeModifierOperation.ADD_VALUE,
			)
		}
	}
	attributeModifiersTest.asString() assertsIs """minecraft:stone_sword[attribute_modifiers=[{type:"minecraft:scale",id:"minecraft:big",amount:1.0d,operation:"add_value"}]]"""

	attributeModifiersTest.components!!.attributeModifiers {
		modifier(
			type = Attributes.ATTACK_DAMAGE,
			amount = 1.0,
			name = "big",
			namespace = "my_namespace",
			operation = AttributeModifierOperation.ADD_VALUE,
		)
	}
	attributeModifiersTest.asString() assertsIs """minecraft:stone_sword[attribute_modifiers=[{type:"minecraft:attack_damage",id:"my_namespace:big",amount:1.0d,operation:"add_value"}]]"""

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

	val blocksAttacksTest = Items.DIAMOND_SWORD {
		blocksAttacks {
			damageReduction(base = 0f, factor = 0.5f, Tags.DamageType.IS_PLAYER_ATTACK)
			itemDamage(base = 1f, factor = 0f, threshold = 0f)
		}
	}
	blocksAttacksTest.asString() assertsIs """minecraft:diamond_sword[blocks_attacks={damage_reductions:[{base:0.0f,factor:0.5f,type:"#minecraft:is_player_attack"}],item_damage:{base:1.0f,factor:0.0f,threshold:0.0f}}]"""

	val blockStateTest = stone {
		blockState {
			this["test"] = "test"
		}
	}
	blockStateTest.asString() assertsIs """minecraft:stone[block_state={test:"test"}]"""

	val breakSoundTest = Items.DIAMOND_SWORD {
		breakSound(SoundEvents.Ambient.CAVE)
	}
	breakSoundTest.asString() assertsIs """minecraft:diamond_sword[break_sound="minecraft:ambient.cave"]"""

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
	canBreakTest.asString() assertsIs """minecraft:stone_sword[can_break=[{blocks:"minecraft:diamond_block"}]]"""

	canBreakTest.components!!.canBreak {
		predicate(Blocks.DIAMOND_BLOCK, state = mapOf("test" to "test"))
		predicate(Blocks.STONE, Blocks.ICE)
	}
	canBreakTest.asString() assertsIs """minecraft:stone_sword[can_break=[{blocks:"minecraft:diamond_block",state:{test:"test"}},{blocks:["minecraft:stone","minecraft:ice"]}]]"""

	val canPlaceOnTest = stone {
		canPlaceOn(Blocks.DIAMOND_BLOCK)
	}
	canPlaceOnTest.asString() assertsIs """minecraft:stone[can_place_on=[{blocks:"minecraft:diamond_block"}]]"""

	canPlaceOnTest.components!!.canPlaceOn {
		predicate(Blocks.DIAMOND_BLOCK, state = mapOf("test" to "test"))
		predicate(Blocks.STONE, Blocks.ICE)
	}
	canPlaceOnTest.asString() assertsIs """minecraft:stone[can_place_on=[{blocks:"minecraft:diamond_block",state:{test:"test"}},{blocks:["minecraft:stone","minecraft:ice"]}]]"""

	val chargedProjectilesTest = crossbow {
		chargedProjectile(Items.ARROW)
	}
	chargedProjectilesTest.asString() assertsIs """minecraft:crossbow[charged_projectiles=[{id:"minecraft:arrow"}]]"""

	chargedProjectilesTest.components!!.chargedProjectiles(Items.ARROW, Items.SPECTRAL_ARROW, Items.TIPPED_ARROW)
	chargedProjectilesTest.asString() assertsIs """minecraft:crossbow[charged_projectiles=[{id:"minecraft:arrow"},{id:"minecraft:spectral_arrow"},{id:"minecraft:tipped_arrow"}]]"""

	val consumableTest = Items.POTION {
		consumable(
			consumeSeconds = 1.5f,
			animation = ConsumeAnimation.DRINK,
			sound = SoundEvents.Item.Bottle.EMPTY,
		) {
			onConsumeEffects {
				applyEffects(
					probability = 1.0f, Effect(
						id = Effects.POISON,
						duration = 100,
						amplifier = 1,
						ambient = true,
						showParticles = true,
						showIcon = true
					)
				)
				teleportRandomly(diameter = 2.5f)
				playSound(SoundEvents.Entity.Cow.STEP)
			}
		}
	}
	consumableTest.asString() assertsIs """minecraft:potion[consumable={consume_seconds:1.5f,animation:"drink",sound:"minecraft:item.bottle.empty",has_consume_particles:1b,on_consume_effects:{"minecraft:apply_effects":{effects:[{id:"minecraft:poison",duration:100,amplifier:1b,ambient:1b,show_particles:1b,show_icon:1b}],probability:1.0f},"minecraft:teleport_randomly":{diameter:2.5f},"minecraft:play_sound":{sound:"minecraft:entity.cow.step"}}}]"""

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
		customModelData(
			colors = listOf(Color.RED, Color.BLUE),
			flags = listOf(true, false),
			floats = listOf(1.0f, 2.0f),
			strings = listOf("test1", "test2")
		)
	}
	customModelDataTest.asString() assertsIs """minecraft:stone_sword[custom_model_data={colors:[16733525,5592575],flags:[1b,0b],floats:[1.0f,2.0f],strings:["test1","test2"]}]"""

	val customNameTest = stoneSword {
		customName(textComponent("test", Color.AQUA))
	}
	customNameTest.asString() assertsIs "minecraft:stone_sword[custom_name={type:\"text\",color:\"aqua\",text:\"test\"}]"

	customNameTest.components!!.customName(textComponent("test"))
	customNameTest.asString() assertsIs "minecraft:stone_sword[custom_name=\"test\"]"

	val damageTest = stoneSword {
		damage(5)
	}
	damageTest.asString() assertsIs """minecraft:stone_sword[damage=5]"""

	val damageResistantTest = stoneSword {
		damageResistant(Tags.DamageType.DAMAGES_HELMET)
	}
	damageResistantTest.asString() assertsIs """minecraft:stone_sword[damage_resistant={types:"#minecraft:damages_helmet"}]"""

	val deathProtectionTest = stoneSword {
		deathProtection {
			effects(Effect(Effects.POISON, 100, 1, ambient = true, showParticles = true, showIcon = true))
		}
	}
	deathProtectionTest.asString() assertsIs """minecraft:stone_sword[death_protection={death_effects:[{id:"minecraft:poison",duration:100,amplifier:1b,ambient:1b,show_particles:1b,show_icon:1b}]}]"""

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

	val enchantableTest = stoneSword {
		enchantable(10)
	}
	enchantableTest.asString() assertsIs """minecraft:stone_sword[enchantable={value:10}]"""

	val enchantmentsTest = stoneSword {
		enchantments(mapOf(Enchantments.SHARPNESS to 5))
	}
	enchantmentsTest.asString() assertsIs """minecraft:stone_sword[enchantments={"minecraft:sharpness":5}]"""

	enchantmentsTest.components!!.enchantments {
		enchantment(Enchantments.LOOTING, 3)
	}
	enchantmentsTest.asString() assertsIs """minecraft:stone_sword[enchantments={"minecraft:looting":3}]"""

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

	val equippableTest = stoneSword {
		equippable(EquipmentSlot.HEAD, "model") {
			allowedEntities(EntityTypes.PLAYER)
			damageOnHurt = true
			dispensable = true
			equipOnInteract = true
			equipSound = SoundEvents.Item.Armor.EQUIP_IRON
		}
	}
	equippableTest.asString() assertsIs """minecraft:stone_sword[equippable={slot:"head",asset_id:"minecraft:model",allowed_entities:"minecraft:player",damage_on_hurt:1b,dispensable:1b,equip_on_interact:1b,equip_sound:"minecraft:item.armor.equip_iron"}]"""

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
			nutrition = 10f,
			saturation = 1.0f,
			canAlwaysEat = true
		)
	}
	foodTest.asString() assertsIs """minecraft:cooked_beef[food={nutrition:10.0f,saturation:1.0f,can_always_eat:1b}]"""

	val gliderTest = Items.ELYTRA {
		glider()
	}
	gliderTest.asString() assertsIs """minecraft:elytra[glider={}]"""

	val instrumentTest = Items.GOAT_HORN {
		instrument(Instruments.CALL_GOAT_HORN)
	}
	instrumentTest.asString() assertsIs """minecraft:goat_horn[instrument="minecraft:call_goat_horn"]"""

	val intangibleProjectileTest = crossbow {
		intangibleProjectile()
	}
	intangibleProjectileTest.asString() assertsIs """minecraft:crossbow[intangible_projectile={}]"""

	val itemModelTest = stone {
		itemModel("test")
	}
	itemModelTest.asString() assertsIs """minecraft:stone[item_model="minecraft:test"]"""
	itemModelTest.components!!.itemModel(Items.DIAMOND)
	itemModelTest.asString() assertsIs """minecraft:stone[item_model="minecraft:diamond"]"""

	val itemNameTest = stoneSword {
		itemName(textComponent("test"))
	}
	itemNameTest.asString() assertsIs "minecraft:stone_sword[item_name=\"test\"]"
	itemNameTest.components!!.itemName("test", Color.AQUA)
	itemNameTest.asString() assertsIs "minecraft:stone_sword[item_name={type:\"text\",color:\"aqua\",text:\"test\"}]"

	val jukeboxPlayableTest = Items.JUKEBOX {
		jukeboxPlayable(JukeboxSongs.OTHERSIDE)
	}
	jukeboxPlayableTest.asString() assertsIs """minecraft:jukebox[jukebox_playable="minecraft:otherside"]"""

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
	loreTest.asString() assertsIs "minecraft:stone_sword[lore=\"test\"]"
	loreTest.components!!.lore(textComponent("test", Color.AQUA) + text("test2", Color.BLACK) + text("a"))
	loreTest.asString() assertsIs "minecraft:stone_sword[lore=[{type:\"text\",color:\"aqua\",text:\"test\"},{type:\"text\",color:\"black\",text:\"test2\"},{type:\"text\",text:\"a\"}]]"

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

	val ominousBottleAmplifierTest = Items.OMINOUS_BOTTLE {
		ominousBottleAmplifier(5)
	}
	ominousBottleAmplifierTest.asString() assertsIs """minecraft:ominous_bottle[ominous_bottle_amplifier=5]"""

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
			customName = "test"
		}
	}
	potionContentsTest.asString() assertsIs """minecraft:potion[potion_contents={potion:"minecraft:awkward",custom_color:5636095,custom_effects:[{id:"minecraft:poison",duration:100,amplifier:1b,ambient:1b,show_particles:1b,show_icon:1b}],custom_name:"test"}]"""

	potionContentsTest.components!!.potionContents(Potions.AWKWARD)
	potionContentsTest.asString() assertsIs """minecraft:potion[potion_contents="minecraft:awkward"]"""

	val potionDurationScaleTest = Items.POTION {
		potionDurationScale(0.5f)
	}
	potionDurationScaleTest.asString() assertsIs """minecraft:potion[potion_duration_scale=0.5f]"""

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

	val providesBannerPatternsTest = Items.PLAYER_HEAD {
		providesBannerPatterns(Tags.BannerPattern.PatternItem.CREEPER)
	}
	providesBannerPatternsTest.asString() assertsIs """minecraft:player_head[provides_banner_patterns="#minecraft:pattern_item/creeper"]"""

	val providerTrimMaterialTest = Items.PLAYER_HEAD {
		providesTrimMaterial(TrimMaterials.DIAMOND)
	}
	providerTrimMaterialTest.asString() assertsIs """minecraft:player_head[provides_trim_material="minecraft:diamond"]"""

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

	val repairableTest = stoneSword {
		repairable(Items.DIAMOND)
	}
	repairableTest.asString() assertsIs """minecraft:stone_sword[repairable={items:"minecraft:diamond"}]"""

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
	}
	storedEnchantmentsTest.asString() assertsIs """minecraft:stone_sword[stored_enchantments={"minecraft:looting":3}]"""

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
				Tags.Block.BASE_STONE_OVERWORLD,
				Tags.Block.OVERWORLD_CARVER_REPLACEABLES,
				Blocks.HAY_BLOCK
			)
			defaultMiningSpeed = 2.0f
		}
	}
	toolTest.asString() assertsIs """minecraft:stone_sword[tool={rules:[{blocks:["#minecraft:base_stone_overworld","#minecraft:overworld_carver_replaceables","minecraft:hay_block"],speed:0.5f,correct_for_drops:1b}],default_mining_speed:2.0f}]"""

	val tooltipDisplayTest = stoneSword {
		tooltipDisplay(false, ItemComponentTypes.BANNER_PATTERNS, ItemComponentTypes.INSTRUMENT)
	}
	tooltipDisplayTest.asString() assertsIs """minecraft:stone_sword[tooltip_display={hidden_components:["minecraft:banner_patterns","minecraft:instrument"]}]"""

	val tooltipStyleTest = stoneSword {
		tooltipStyle("model")
	}
	tooltipStyleTest.asString() assertsIs """minecraft:stone_sword[tooltip_style="minecraft:model"]"""

	val trimTest = Items.DIAMOND_CHESTPLATE {
		trim(TrimPatterns.SHAPER, TrimMaterials.DIAMOND)
	}
	trimTest.asString() assertsIs """minecraft:diamond_chestplate[trim={pattern:"minecraft:shaper",material:"minecraft:diamond"}]"""

	val unbreakableTest = stoneSword {
		unbreakable()
	}
	unbreakableTest.asString() assertsIs """minecraft:stone_sword[unbreakable={}]"""

	unbreakableTest.components!!.unbreakable()
	unbreakableTest.asString() assertsIs """minecraft:stone_sword[unbreakable={}]"""

	val useCooldownTest = Items.ENDER_PEARL {
		useCooldown(
			seconds = 2.0f,
			cooldownGroup = "ender_pearl"
		)
	}
	useCooldownTest.asString() assertsIs """minecraft:ender_pearl[use_cooldown={seconds:2.0f,cooldown_group:"minecraft:ender_pearl"}]"""

	val useRemainderTest = Items.POTION {
		useRemainder(itemStack(Items.GLASS_BOTTLE))
	}
	useRemainderTest.asString() assertsIs """minecraft:potion[use_remainder={item:{id:"minecraft:glass_bottle"}}]"""

	val writableBookTest = Items.WRITABLE_BOOK {
		writableBookContent {
			page("test")
			page("test2", "test3")
		}
	}
	writableBookTest.asString() assertsIs """minecraft:writable_book[writable_book_content={pages:[{raw:"test"},{raw:"test2",filtered:"test3"}]}]"""

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
	writtenBookTest.asString() assertsIs "minecraft:written_book[written_book_content={pages:['{type:\"text\",color:\"aqua\",text:\"test\"}'],title:'\"test\"',author:\"test\",generation:1,resolved:1b}]"

	val weaponTest = stoneSword {
		weapon(itemDamagePerAttack = 5, disableBlockingForSeconds = 5f)
	}
	weaponTest.asString() assertsIs """minecraft:stone_sword[weapon={item_damage_per_attack:5,disable_blocking_for_seconds:5.0f}]"""

	val weaponDefaultTest = stoneSword {
		weapon()
	}
	weaponDefaultTest.asString() assertsIs """minecraft:stone_sword[weapon={}]"""
}
