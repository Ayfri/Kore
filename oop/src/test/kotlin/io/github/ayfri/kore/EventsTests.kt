package io.github.ayfri.kore

import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.commands.say
import io.github.ayfri.kore.entities.Entity
import io.github.ayfri.kore.entities.player
import io.github.ayfri.kore.events.*
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.generated.EntityTypes
import io.github.ayfri.kore.generated.Items
import io.github.ayfri.kore.utils.testDataPack

fun eventsTests() = testDataPack("events_tests") {
	val player = player("TestPlayer")
	val zombie = Entity().apply { selector.type = EntityTypes.ZOMBIE }

	function("test_events") {
		player.onBlockUse { say("Used a block!") }
		player.onChangeDimension { say("Changed dimension!") }
		player.onConsumeItem { say("Consumed an item!") }
		player.onConsumeItem(Items.GOLDEN_APPLE) { say("Consumed a golden apple!") }
		player.onEffectsChanged { say("Effects changed!") }
		player.onEnchantItem { say("Enchanted an item!") }
		player.onEntityHurtPlayer { say("Entity hurt me!") }
		player.onFallFromHeight { say("Fell from height!") }
		player.onHurtEntity { say("Hurt an entity!") }
		player.onInventoryChange { say("Inventory changed!") }
		player.onItemUsedOnBlock { say("Used item on block!") }
		player.onKill { say("Killed an entity!") }
		player.onPlaceBlock { say("Placed a block!") }
		player.onRightClick(Items.STICK) { say("Right clicked with stick!") }
		player.onSleptInBed { say("Slept in bed!") }
		player.onStartRiding { say("Started riding!") }
		player.onTameAnimal { say("Tamed an animal!") }
		player.onTargetHit { say("Hit a target!") }
		zombie.onDeath { say("Zombie died!") }
	}

	val expectedEvents = listOf(
		"on_block_use", "on_change_dimension", "on_consume_item",
		"on_effects_changed", "on_enchant_item", "on_entity_hurt_player",
		"on_fall_from_height", "on_hurt_entity", "on_inventory_change",
		"on_item_used_on_block", "on_kill", "on_place_block",
		"on_slept_in_bed", "on_start_riding", "on_tame_animal", "on_target_hit",
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

	lootTables.any { it.fileName.endsWith("death_trigger_zombie") } assertsIs true
	generatedFunctions.any { it.name == OopConstants.deathDispatcherFunction } assertsIs true
}.apply {
	generate()
}
