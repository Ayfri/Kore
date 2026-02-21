package io.github.ayfri.kore.events

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.CONTENTS
import io.github.ayfri.kore.arguments.components.buildPartial
import io.github.ayfri.kore.arguments.components.predicate
import io.github.ayfri.kore.arguments.types.literals.allEntities
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.arguments.types.resources.FunctionArgument
import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.arguments.types.resources.tagged.FunctionTagArgument
import io.github.ayfri.kore.commands.advancements
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.kill
import io.github.ayfri.kore.entities.Entity
import io.github.ayfri.kore.entities.Player
import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.advancement
import io.github.ayfri.kore.features.advancements.criteria
import io.github.ayfri.kore.features.advancements.rewards
import io.github.ayfri.kore.features.advancements.triggers.*
import io.github.ayfri.kore.features.loottables.lootTable
import io.github.ayfri.kore.features.loottables.pool
import io.github.ayfri.kore.features.predicates.sub.itemStack
import io.github.ayfri.kore.features.tags.functionTag
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.generatedFunction
import io.github.ayfri.kore.functions.tick
import io.github.ayfri.kore.generated.EntityTypes
import io.github.ayfri.kore.generated.Items
import net.benwoodworth.knbt.NbtByte
import io.github.ayfri.kore.OopConstants.Events as C
import io.github.ayfri.kore.commands.function as functionCommand
import io.github.ayfri.kore.functions.function as dpFunction

private val initializedDeathDispatch = mutableSetOf<String>()

private fun DataPack.addHandler(
	tagName: String,
	ns: String,
	handlerName: String,
	block: Function.() -> Unit,
): FunctionArgument {
	val fn = dpFunction(handlerName, ns, block = block)
	functionTag(tagName, namespace = ns) { add(fn.asId()) }
	return fn
}

private fun DataPack.hasAdvancement(fileName: String) = advancements.any { it.fileName == fileName }

private fun DataPack.advancementEvent(
	ns: String,
	event: String,
	hashCode: Int,
	block: Function.() -> Unit,
	criteriaSetup: AdvancementCriteria.() -> Unit,
) {
	val tagName = C.tagName(event)
	addHandler(tagName, ns, C.handlerName(event, hashCode), block)

	val advName = C.advancementName(event)
	if (!hasAdvancement(advName)) {
		val dispatchFn = generatedFunction(C.dispatchFunctionName(event)) {
			advancements.revokeEverything(self())
			functionCommand(FunctionTagArgument(tagName, ns))
		}
		advancement(advName) {
			criteria(criteriaSetup)
			rewards { function = dispatchFn }
		}
	}
}

private fun DataPack.advancementEventForItem(
	ns: String,
	event: String,
	itemName: String,
	hashCode: Int,
	block: Function.() -> Unit,
	criteriaSetup: AdvancementCriteria.() -> Unit,
) {
	val tagName = C.tagNameForItem(event, itemName)
	addHandler(tagName, ns, C.handlerNameForItem(event, itemName, hashCode), block)

	val advName = C.advancementNameForItem(event, itemName)
	if (!hasAdvancement(advName)) {
		val dispatchFn = generatedFunction(C.dispatchFunctionNameForItem(event, itemName)) {
			advancements.revokeEverything(self())
			functionCommand(FunctionTagArgument(tagName, ns))
		}
		advancement(advName) {
			criteria(criteriaSetup)
			rewards { function = dispatchFn }
		}
	}
}

internal fun DataPack.ensureDeathTriggerSetup(ns: String) {
	val key = "$name:$ns"
	if (key in initializedDeathDispatch) return
	initializedDeathDispatch += key

	val deathPredicate = Items.STRUCTURE_VOID.predicate {
		buildPartial(C.DEATH_TRIGGER_KEY) { put(C.DEATH_TRIGGER_KEY, NbtByte(1)) }
	}

	tick(C.DEATH_DISPATCHER) {
		execute {
			asTarget(allEntities { type = EntityTypes.ITEM })
			ifCondition { items(self(), CONTENTS, deathPredicate) }
			at(self())
			run {
				functionCommand(FunctionTagArgument(C.DEATH_HANDLERS_TAG, ns))
				kill(self())
			}
		}
	}
}

/**
 * Triggers when the player interacts with any block (right-click).
 *
 * Uses an `any_block_use` advancement trigger.
 */
context(fn: Function)
fun Player.onBlockUse(block: Function.() -> Unit) {
	fn.datapack.advancementEvent(fn.namespace, C.BLOCK_USE, block.hashCode(), block) {
		anyBlockUse("any_block_use")
	}
}

/**
 * Triggers when the player consumes any item (food, potion, etc.).
 *
 * Uses a `consume_item` advancement trigger.
 */
context(fn: Function)
fun Player.onConsumeItem(block: Function.() -> Unit) {
	fn.datapack.advancementEvent(fn.namespace, C.CONSUME_ITEM, block.hashCode(), block) {
		consumeItem("consume_item")
	}
}

/**
 * Triggers when the player consumes a specific [item].
 *
 * Uses a `consume_item` advancement trigger filtered by item type.
 */
context(fn: Function)
fun Player.onConsumeItem(item: ItemArgument, block: Function.() -> Unit) {
	fn.datapack.advancementEventForItem(fn.namespace, C.CONSUME_ITEM, item.name.lowercase(), block.hashCode(), block) {
		consumeItem("consume_item", item)
	}
}

/**
 * Triggers when the player hurts an entity (melee or ranged).
 *
 * Uses a `player_hurt_entity` advancement trigger.
 */
context(fn: Function)
fun Player.onHurtEntity(block: Function.() -> Unit) {
	fn.datapack.advancementEvent(fn.namespace, C.HURT_ENTITY, block.hashCode(), block) {
		playerHurtEntity("player_hurt_entity")
	}
}

/**
 * Triggers when the player's inventory changes.
 *
 * Uses an `inventory_changed` advancement trigger.
 */
context(fn: Function)
fun Player.onInventoryChange(block: Function.() -> Unit) {
	fn.datapack.advancementEvent(fn.namespace, C.INVENTORY_CHANGE, block.hashCode(), block) {
		inventoryChanged("inventory_changed")
	}
}

/**
 * Triggers when the player uses an item on a block (e.g. flint & steel, bone meal).
 *
 * Uses an `item_used_on_block` advancement trigger.
 */
context(fn: Function)
fun Player.onItemUsedOnBlock(block: Function.() -> Unit) {
	fn.datapack.advancementEvent(fn.namespace, C.ITEM_USED_ON_BLOCK, block.hashCode(), block) {
		itemUsedOnBlock("item_used_on_block")
	}
}

/**
 * Triggers when the player kills an entity.
 *
 * Uses a `player_killed_entity` advancement trigger.
 */
context(fn: Function)
fun Player.onKill(block: Function.() -> Unit) {
	fn.datapack.advancementEvent(fn.namespace, C.KILL, block.hashCode(), block) {
		playerKilledEntity("kill")
	}
}

/**
 * Triggers when the player places a block.
 *
 * Uses a `placed_block` advancement trigger.
 */
context(fn: Function)
fun Player.onPlaceBlock(block: Function.() -> Unit) {
	fn.datapack.advancementEvent(fn.namespace, C.PLACE_BLOCK, block.hashCode(), block) {
		placedBlock("placed_block")
	}
}

/**
 * Triggers when the player right-clicks with [item].
 *
 * Uses a `using_item` advancement trigger, dispatches via a function tag
 * so multiple handlers for the same item all fire together.
 */
context(fn: Function)
fun Player.onRightClick(item: ItemArgument, block: Function.() -> Unit) {
	fn.datapack.advancementEventForItem(fn.namespace, C.RIGHT_CLICK, item.name.lowercase(), block.hashCode(), block) {
		usingItem("use_item") { this.item = itemStack(item) }
	}
}

/**
 * Triggers when this entity type dies.
 *
 * On death the entity drops a hidden `structure_void` tagged with custom data.
 * A tick dispatcher detects the dropped item, runs all death handlers, then removes it.
 */
context(fn: Function)
fun Entity.onDeath(block: Function.() -> Unit) {
	val dp = fn.datapack
	val ns = fn.namespace

	dp.ensureDeathTriggerSetup(ns)
	dp.addHandler(C.DEATH_HANDLERS_TAG, ns, C.handlerName(C.DEATH, block.hashCode()), block)

	val entityTypeName = selector.type?.name?.lowercase() ?: "generic"
	val lootTableName = C.deathTriggerLootTable(entityTypeName)
	if (dp.lootTables.none { it.fileName == lootTableName }) {
		dp.lootTable(lootTableName) {
			pool {
				val itemEntry = io.github.ayfri.kore.features.loottables.entries.Item(
					name = Items.STRUCTURE_VOID,
					functions = io.github.ayfri.kore.features.itemmodifiers.ItemModifier(
						modifiers = listOf(
							io.github.ayfri.kore.features.itemmodifiers.functions.SetCustomData(
								tag = net.benwoodworth.knbt.buildNbtCompound {
									put(C.DEATH_TRIGGER_KEY, NbtByte(1))
								}
							)
						)
					)
				)
				entries = listOf(itemEntry)
			}
		}
	}
}
