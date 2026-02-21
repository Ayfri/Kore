package io.github.ayfri.kore

data object OopConstants {
	const val ADVANCEMENT_PREFIX = "kore_oop"
	const val LOOT_TABLE_PREFIX = "kore_oop"

	data object Cooldown {
		fun initFunctionName(name: String) = "cooldown_${name}_init"
		fun readyHandlerName(name: String, hashCode: Int) = "cooldown_${name}_ready_$hashCode"
		fun tickFunctionName(name: String) = "cooldown_${name}_tick"
	}

	data object Events {
		const val BLOCK_USE = "on_block_use"
		const val CONSUME_ITEM = "on_consume_item"
		const val DEATH = "on_death"
		const val DEATH_DISPATCHER = "kore_oop_death_dispatcher"
		const val DEATH_HANDLERS_TAG = "on_death_handlers"
		const val DEATH_TRIGGER_KEY = "kore_oop_death"
		const val HURT_ENTITY = "on_hurt_entity"
		const val INVENTORY_CHANGE = "on_inventory_change"
		const val ITEM_USED_ON_BLOCK = "on_item_used_on_block"
		const val KILL = "on_kill"
		const val PLACE_BLOCK = "on_place_block"
		const val RIGHT_CLICK = "on_right_click"

		fun advancementName(event: String) = "$ADVANCEMENT_PREFIX/$event"
		fun advancementNameForItem(event: String, itemName: String) = "$ADVANCEMENT_PREFIX/${event}_$itemName"
		fun deathTriggerLootTable(entityTypeName: String) = "$LOOT_TABLE_PREFIX/death_trigger_$entityTypeName"
		fun dispatchFunctionName(event: String) = "dispatch_$event"
		fun dispatchFunctionNameForItem(event: String, itemName: String) = "dispatch_${event}_$itemName"
		fun handlerName(event: String, hashCode: Int) = "${event}_handler_$hashCode"
		fun handlerNameForItem(event: String, itemName: String, hashCode: Int) =
			"${event}_${itemName}_handler_$hashCode"

		fun tagName(event: String) = event
		fun tagNameForItem(event: String, itemName: String) = "${event}_$itemName"
	}

	data object Math {
		const val INIT_FUNCTION = "kore_math_init"
		const val OBJECTIVE = "kore_math"
		const val CONST_2 = "#2"
		const val CONST_360 = "#360"
		const val CONST_SCALE = "#scale"
		const val SQRT_ITERATIONS = 8
	}

	data object Vfx {
		fun shapeFunctionName(name: String) = "vfx_${name}"
	}

	data object Raycast {
		const val INIT_FUNCTION = "kore_raycast_init"
		const val OBJECTIVE = "kore_raycast"
		const val TAG = "kore_raycasting"

		fun hitFunctionName(name: String) = "raycast_${name}_hit"
		fun maxFunctionName(name: String) = "raycast_${name}_max"
		fun startFunctionName(name: String) = "raycast_${name}_start"
		fun stepFunctionName(name: String) = "raycast_${name}_step"
	}

}
