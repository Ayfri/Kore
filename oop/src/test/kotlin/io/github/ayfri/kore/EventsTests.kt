package io.github.ayfri.kore

import io.github.ayfri.kore.assertions.assertFileGenerated
import io.github.ayfri.kore.assertions.assertGeneratorsGenerated
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
		player.onConsumeItem { say("Consumed an item!") }
		player.onConsumeItem(Items.GOLDEN_APPLE) { say("Consumed a golden apple!") }
		player.onHurtEntity { say("Hurt an entity!") }
		player.onInventoryChange { say("Inventory changed!") }
		player.onItemUsedOnBlock { say("Used item on block!") }
		player.onKill { say("Killed an entity!") }
		player.onPlaceBlock { say("Placed a block!") }
		player.onRightClick(Items.STICK) { say("Right clicked with stick!") }
		zombie.onDeath { say("Zombie died!") }
	}

	advancements.any { it.fileName.contains("on_block_use") } assertsIs true
	advancements.any { it.fileName.contains("on_consume_item") } assertsIs true
	advancements.any { it.fileName.contains("on_consume_item_golden_apple") } assertsIs true
	advancements.any { it.fileName.contains("on_hurt_entity") } assertsIs true
	advancements.any { it.fileName.contains("on_inventory_change") } assertsIs true
	advancements.any { it.fileName.contains("on_item_used_on_block") } assertsIs true
	advancements.any { it.fileName.contains("on_kill") } assertsIs true
	advancements.any { it.fileName.contains("on_place_block") } assertsIs true
	advancements.any { it.fileName.contains("on_right_click") } assertsIs true

	generatedFunctions.any { it.name.contains("dispatch_on_block_use") } assertsIs true
	generatedFunctions.any { it.name.contains("dispatch_on_consume_item") } assertsIs true
	generatedFunctions.any { it.name.contains("dispatch_on_hurt_entity") } assertsIs true
	generatedFunctions.any { it.name.contains("dispatch_on_inventory_change") } assertsIs true
	generatedFunctions.any { it.name.contains("dispatch_on_item_used_on_block") } assertsIs true
	generatedFunctions.any { it.name.contains("dispatch_on_kill") } assertsIs true
	generatedFunctions.any { it.name.contains("dispatch_on_place_block") } assertsIs true
	generatedFunctions.any { it.name.contains("dispatch_on_right_click") } assertsIs true

	functions.any { it.name.contains("on_block_use") && it.name.contains("handler") } assertsIs true
	functions.any { it.name.contains("on_consume_item") && it.name.contains("handler") } assertsIs true
	functions.any { it.name.contains("on_hurt_entity") && it.name.contains("handler") } assertsIs true
	functions.any { it.name.contains("on_inventory_change") && it.name.contains("handler") } assertsIs true
	functions.any { it.name.contains("on_item_used_on_block") && it.name.contains("handler") } assertsIs true
	functions.any { it.name.contains("on_kill") && it.name.contains("handler") } assertsIs true
	functions.any { it.name.contains("on_place_block") && it.name.contains("handler") } assertsIs true
	functions.any { it.name.contains("on_right_click") && it.name.contains("handler") } assertsIs true

	lootTables.any { it.fileName.contains("death_trigger_zombie") } assertsIs true
	generatedFunctions.any { it.name == OopConstants.Events.DEATH_DISPATCHER } assertsIs true
}.apply {
	val n = "events_tests"
	val d = "$n/data/$n"
	DataPack.DEFAULT_GENERATED_FUNCTIONS_FOLDER

	assertFileGenerated("$d/function/test_events.mcfunction")
	assertFileGenerated("$d/advancement/kore_oop/on_block_use.json")
	assertFileGenerated("$d/advancement/kore_oop/on_consume_item.json")
	assertFileGenerated("$d/advancement/kore_oop/on_consume_item_golden_apple.json")
	assertFileGenerated("$d/advancement/kore_oop/on_hurt_entity.json")
	assertFileGenerated("$d/advancement/kore_oop/on_inventory_change.json")
	assertFileGenerated("$d/advancement/kore_oop/on_item_used_on_block.json")
	assertFileGenerated("$d/advancement/kore_oop/on_kill.json")
	assertFileGenerated("$d/advancement/kore_oop/on_place_block.json")
	assertFileGenerated("$d/advancement/kore_oop/on_right_click_stick.json")
	assertFileGenerated("$d/loot_table/kore_oop/death_trigger_zombie.json")
	assertGeneratorsGenerated()
	generate()
}
