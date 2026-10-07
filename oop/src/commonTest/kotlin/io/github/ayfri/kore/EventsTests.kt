package io.github.ayfri.kore

import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.commands.say
import io.github.ayfri.kore.entities.entity
import io.github.ayfri.kore.entities.player
import io.github.ayfri.kore.events.*
import io.github.ayfri.kore.exportAsStrings
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.generated.EntityTypes
import io.github.ayfri.kore.generated.Items
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec

fun eventsTests() = dataPack("events_tests") {
	val player = player("TestPlayer")
	val zombie = entity(EntityTypes.ZOMBIE)

	function("test_events") {
		player.onBlockUse { say("Used a block!") }
		player.onBredAnimals { say("Bred animals!") }
		player.onBrewedPotion { say("Brewed a potion!") }
		player.onChangeDimension { say("Changed dimension!") }
		player.onConsumeItem { say("Consumed an item!") }
		player.onConsumeItem(Items.GOLDEN_APPLE) { say("Consumed a golden apple!") }
		player.onEffectsChanged { say("Effects changed!") }
		player.onEnchantItem { say("Enchanted an item!") }
		player.onEntityHurtPlayer { say("Entity hurt me!") }
		player.onFallFromHeight { say("Fell from height!") }
		player.onFilledBucket { say("Filled a bucket!") }
		player.onFishingRodHooked { say("Hooked something!") }
		player.onHurtEntity { say("Hurt an entity!") }
		player.onInteractWithEntity { say("Interacted with an entity!") }
		player.onInventoryChange { say("Inventory changed!") }
		player.onItemUsedOnBlock { say("Used item on block!") }
		player.onKilledByArrow { say("Killed by arrow!") }
		player.onPlaceBlock { say("Placed a block!") }
		player.onRightClick(Items.STICK) { say("Right clicked with stick!") }
		player.onShotCrossbow { say("Shot a crossbow!") }
		player.onSleptInBed { say("Slept in bed!") }
		player.onStartRiding { say("Started riding!") }
		player.onTameAnimal { say("Tamed an animal!") }
		player.onTargetHit { say("Hit a target!") }
		player.onTick { say("Tick!") }
		player.onUsedEnderEye { say("Used an ender eye!") }
		player.onUsedTotem { say("Used a totem!") }
		zombie.onDeath { self -> say("A ${self.type?.asString()} died!") }
	}

	player.onKill { self -> say("${self.name} killed an entity!") }

	val expectedEvents = listOf(
		"on_block_use", "on_bred_animals", "on_brewed_potion", "on_change_dimension", "on_consume_item",
		"on_effects_changed", "on_enchant_item", "on_entity_hurt_player",
		"on_fall_from_height", "on_filled_bucket", "on_fishing_rod_hooked", "on_hurt_entity",
		"on_interact_with_entity", "on_inventory_change", "on_item_used_on_block", "on_kill", "on_killed_by_arrow",
		"on_place_block", "on_shot_crossbow", "on_slept_in_bed", "on_start_riding", "on_tame_animal",
		"on_target_hit", "on_tick", "on_used_ender_eye", "on_used_totem",
	)

	for (event in expectedEvents) {
		advancements.any { it.fileName.endsWith(event) } assertsIs true
		generatedFunctions.any { it.name == OopConstants.dispatchFunctionName(event) } assertsIs true
		functions.any { it.name.startsWith("${event}_handler_") } assertsIs true
	}

	advancements.any { it.fileName.endsWith("on_consume_item_golden_apple") } assertsIs true
	advancements.any { it.fileName.endsWith("on_right_click_stick") } assertsIs true
	generatedFunctions.any {
		it.name == OopConstants.dispatchFunctionNameForItem(
			"on_consume_item",
			"golden_apple"
		)
	} assertsIs true
	generatedFunctions.any {
		it.name == OopConstants.dispatchFunctionNameForItem(
			"on_right_click",
			"stick"
		)
	} assertsIs true

	generatedFunctions.first { it.name == OopConstants.dispatchFunctionName("on_block_use") }.lines.first() assertsIs
		"advancement revoke @s only events_tests:${OopConstants.advancementName("on_block_use")}"
	generatedFunctions.first { it.name == OopConstants.dispatchFunctionNameForItem("on_right_click", "stick") }.lines.first() assertsIs
		"advancement revoke @s only events_tests:${OopConstants.advancementNameForItem("on_right_click", "stick")}"

	lootTables.any { it.fileName.endsWith("death_trigger_zombie") } assertsIs true
	generatedFunctions.any { it.name == OopConstants.deathDispatcherFunction } assertsIs true
}

class EventsTests : FunSpec({
	test("events") {
		eventsTests()
	}

	test("death events track their entities and run only their own handlers") {
		val files = dataPack("death_tests") {
			entity(EntityTypes.ZOMBIE).onDeath { say("zombie died") }
			entity(EntityTypes.ZOMBIE, limitToOne = false) { tag = "boss" }.onDeath { say("boss died") }
		}.exportAsStrings()

		val fn = "data/death_tests/function/generated_scopes"
		val tracker = files.getValue("$fn/kore_oop_death_c3f864bc_tracker.mcfunction")
		tracker.substringBefore(" run ") assertsIs "execute as @e[tag=!kore_oop_death_c3f864bc,type=minecraft:zombie]"
		files.getValue("$fn/kore_oop_death_13e63c2c_tracker.mcfunction").substringBefore(" run ") assertsIs
			"execute as @e[tag=boss,tag=!kore_oop_death_13e63c2c,type=minecraft:zombie]"
		files["data/death_tests/function/${tracker.substringAfter("run function death_tests:")}.mcfunction"] assertsIs """
			tag @s add kore_oop_death_c3f864bc
			data modify entity @s DeathLootTable set value "death_tests:kore_oop/death_trigger_zombie"
		""".trimIndent()
		files["$fn/kore_oop_death_dispatcher.mcfunction"] assertsIs
			"execute as @e[type=minecraft:item] if items entity @s contents minecraft:structure_void[custom_data~{kore_oop_death:1b}] at @s run function death_tests:generated_scopes/kore_oop_death_handle"
		files["$fn/kore_oop_death_handle.mcfunction"] assertsIs """
			execute if items entity @s contents *[custom_data~{tags:["kore_oop_death_c3f864bc"]}] run function #death_tests:kore_oop_death_c3f864bc
			execute if items entity @s contents *[custom_data~{tags:["kore_oop_death_13e63c2c"]}] run function #death_tests:kore_oop_death_13e63c2c
			kill @s
		""".trimIndent()
		files["data/death_tests/loot_table/kore_oop/death_trigger_zombie.json"] assertsIs
			"""{"pools":[{"rolls":1.0,"entries":[{"type":"minecraft:loot_table","value":"minecraft:entities/zombie"}]},{"rolls":1.0,"entries":[{"type":"minecraft:item","name":"minecraft:structure_void","functions":[{"function":"minecraft:set_custom_data","tag":{"kore_oop_death":true}},{"function":"minecraft:copy_custom_data","source":{"type":"minecraft:context","target":"this"},"ops":[{"op":"replace","source":"Tags","target":"tags"}]}]}]}]}"""
	}

	test("death events refuse players and untyped entities") {
		dataPack("death_errors") {
			shouldThrow<IllegalArgumentException> { player("Steve").onDeath { say("died") } }
			shouldThrow<IllegalArgumentException> { entity { tag = "x" }.onDeath { say("died") } }
		}
	}
})
