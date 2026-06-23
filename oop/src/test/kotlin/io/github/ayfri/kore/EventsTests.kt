package io.github.ayfri.kore

import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.commands.say
import io.github.ayfri.kore.entities.entity
import io.github.ayfri.kore.entities.player
import io.github.ayfri.kore.events.*
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.generated.EntityTypes
import io.github.ayfri.kore.generated.Items
import io.github.ayfri.kore.utils.testDataPack
import io.kotest.core.spec.style.FunSpec

fun eventsTests() = testDataPack("events_tests") {
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
		zombie.onDeath { self -> say("A ${self.type?.name} died!") }
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

	lootTables.any { it.fileName.endsWith("death_trigger_zombie") } assertsIs true
	generatedFunctions.any { it.name == OopConstants.deathDispatcherFunction } assertsIs true
}.apply {
	generate()
}

class EventsTests : FunSpec({
	test("events") {
		eventsTests()
	}
})
