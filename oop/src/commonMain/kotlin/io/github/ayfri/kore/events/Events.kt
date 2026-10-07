package io.github.ayfri.kore.events

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.DataPackStateKey
import io.github.ayfri.kore.OopConstants
import io.github.ayfri.kore.arguments.CONTENTS
import io.github.ayfri.kore.arguments.components.buildPartial
import io.github.ayfri.kore.arguments.components.predicate
import io.github.ayfri.kore.arguments.types.ResourceLocationArgument
import io.github.ayfri.kore.arguments.types.literals.allEntities
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.arguments.types.resources.FunctionArgument
import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.arguments.types.resources.tagged.FunctionTagArgument
import io.github.ayfri.kore.commands.advancements
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.function as functionCommand
import io.github.ayfri.kore.commands.kill
import io.github.ayfri.kore.entities.Entity
import io.github.ayfri.kore.entities.Player
import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.advancement
import io.github.ayfri.kore.features.advancements.criteria
import io.github.ayfri.kore.features.advancements.rewards
import io.github.ayfri.kore.features.advancements.triggers.*
import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.itemmodifiers.functions.SetCustomData
import io.github.ayfri.kore.features.loottables.entries.Item
import io.github.ayfri.kore.features.loottables.lootTable
import io.github.ayfri.kore.features.loottables.pool
import io.github.ayfri.kore.features.predicates.sub.itemStackPredicate
import io.github.ayfri.kore.features.tags.functionTag
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.function as dpFunction
import io.github.ayfri.kore.functions.generatedFunction
import io.github.ayfri.kore.functions.generatedFunctionName
import io.github.ayfri.kore.functions.tick
import io.github.ayfri.kore.generated.EntityTypes
import io.github.ayfri.kore.generated.Items
import io.github.ayfri.kore.generated.arguments.types.AdvancementArgument
import io.github.ayfri.kore.generated.arguments.types.RecipeArgument
import io.github.ayfri.kore.utils.nbt
import io.github.ayfri.kore.arguments.components.itemPredicate
import io.github.ayfri.kore.commands.data
import io.github.ayfri.kore.commands.tag
import io.github.ayfri.kore.features.itemmodifiers.functions.CopyCustomData
import io.github.ayfri.kore.features.itemmodifiers.functions.CopyCustomDataOperationType
import io.github.ayfri.kore.features.itemmodifiers.functions.CopyNbtContext
import io.github.ayfri.kore.features.itemmodifiers.functions.CopyNbtOperation
import io.github.ayfri.kore.features.itemmodifiers.functions.Source
import io.github.ayfri.kore.features.loottables.entries.LootTable as NestedLootTable
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.generated.LootTables
import io.github.ayfri.kore.generated.arguments.types.LootTableArgument
import io.github.ayfri.kore.utils.nbtListOf
import net.benwoodworth.knbt.NbtByte

private val deathHandleFunctions = DataPackStateKey<MutableMap<String, Function>>("oop.deathDispatch")
private val trackedDeaths = DataPackStateKey<MutableSet<String>>("oop.trackedDeaths")
private val registeredHandlers = DataPackStateKey<MutableMap<String, FunctionArgument>>("oop.handlers")

/** Registers [block] in the [tagName] function tag, named after [handlerPrefix] and a hash of its body, so the same body registers once. */
internal fun DataPack.addHandler(
	tagName: String,
	ns: String,
	handlerPrefix: String,
	block: Function.() -> Unit,
): FunctionArgument {
	val body = Function(handlerPrefix, ns, datapack = this).apply(block).lines
	val name = generatedFunctionName(handlerPrefix, body)
	return state(registeredHandlers) { mutableMapOf() }.getOrPut("$ns:$name") {
		dpFunction(name, ns) { lines += body }.also { fn -> functionTag(tagName, namespace = ns) { add(fn.asId()) } }
	}
}

private fun DataPack.advancementEvent(
	ns: String,
	event: String,
	block: Function.() -> Unit,
	criteriaSetup: AdvancementCriteria.() -> Unit,
) {
	val tagName = OopConstants.eventTagName(event)
	addHandler(tagName, ns, OopConstants.eventHandlerPrefix(event), block)

	val advName = OopConstants.advancementName(event)
	if (!hasAdvancement(advName)) {
		val advancement = AdvancementArgument(advName, name)
		val dispatchFn = generatedFunction(OopConstants.dispatchFunctionName(event)) {
			advancements.revoke(self(), advancement)
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
	block: Function.() -> Unit,
	criteriaSetup: AdvancementCriteria.() -> Unit,
) {
	val tagName = OopConstants.eventTagNameForItem(event, itemName)
	addHandler(tagName, ns, OopConstants.eventHandlerPrefixForItem(event, itemName), block)

	val advName = OopConstants.advancementNameForItem(event, itemName)
	if (!hasAdvancement(advName)) {
		val advancement = AdvancementArgument(advName, name)
		val dispatchFn = generatedFunction(OopConstants.dispatchFunctionNameForItem(event, itemName)) {
			advancements.revoke(self(), advancement)
			functionCommand(FunctionTagArgument(tagName, ns))
		}
		advancement(advName) {
			criteria(criteriaSetup)
			rewards { function = dispatchFn }
		}
	}
}

/** The function each death trigger item runs: one line per tracked selector calling its handlers, then `kill @s`. */
private fun DataPack.deathHandleFunction(ns: String) = state(deathHandleFunctions) { mutableMapOf() }.getOrPut(ns) {
	val handle = generatedFunction(OopConstants.deathHandleFunction, ns) { kill(self()) } as Function
	tick(OopConstants.deathDispatcherFunction, ns) {
		execute {
			asTarget(allEntities { type = EntityTypes.ITEM })
			ifCondition {
				items(self(), CONTENTS, Items.STRUCTURE_VOID.predicate {
					buildPartial(ItemComponentTypes.CUSTOM_DATA) { put(OopConstants.deathTriggerKey, NbtByte(1)) }
				})
			}
			at(self())
			run(handle)
		}
	}
	handle
}

/** Drops the type's vanilla loot plus a structure void carrying the trigger key and the dying entity's `Tags`. */
private fun DataPack.deathLootTable(type: String) = OopConstants.deathTriggerLootTable(type).also { name ->
	if (lootTables.any { it.fileName == name }) return@also
	lootTable(name) {
		if (LootTables.Entities.entries.any { it.name.equals(type, ignoreCase = true) }) pool {
			entries = listOf(NestedLootTable(LootTableArgument("entities/$type")))
		}
		pool {
			entries = listOf(
				Item(
					name = Items.STRUCTURE_VOID,
					functions = ItemModifier(
						modifiers = listOf(
							SetCustomData(tag = nbt { put(OopConstants.deathTriggerKey, NbtByte(1)) }),
							CopyCustomData(
								source = CopyNbtContext(Source.THIS),
								ops = listOf(CopyNbtOperation(CopyCustomDataOperationType.REPLACE, "Tags", "tags")),
							),
						)
					)
				)
			)
		}
	}
}

private fun DataPack.registerDeathEvent(entity: Entity, ns: String, block: Function.() -> Unit) {
	require(!entity.isPlayer) { "onDeath doesn't work on players, they have no death loot table." }
	val type = requireNotNull((entity.type as? ResourceLocationArgument)?.name?.lowercase()) {
		"onDeath needs an entity with a type, the death trigger is dropped through the loot table of that type."
	}

	val key = generatedFunctionName(OopConstants.deathTriggerKey, listOf(entity.asSelector(limitToOne = false).asString()))
	addHandler(key, ns, OopConstants.eventHandlerPrefix(OopConstants.deathEvent), block)
	if (!state(trackedDeaths) { mutableSetOf() }.add("$ns:$key")) return

	val lootTableId = "$name:${deathLootTable(type)}"
	tick(OopConstants.deathTrackerFunctionName(key), ns) {
		execute {
			asTarget(entity.asSelector(limitToOne = false) { tag = !key })
			run {
				tag(self()) { add(key) }
				data(self()) { modify("DeathLootTable", lootTableId) }
			}
		}
	}

	val handle = deathHandleFunction(ns)
	val call = Function("", ns, datapack = this).apply {
		execute {
			ifCondition { items(self(), CONTENTS, itemPredicate { buildPartial(ItemComponentTypes.CUSTOM_DATA) { put("tags", nbtListOf(key)) } }) }
			run { functionCommand(FunctionTagArgument(key, ns)) }
		}
	}.lines
	handle.lines.addAll(handle.lines.size - 1, call)
}

private fun DataPack.hasAdvancement(fileName: String) = advancements.any { it.fileName == fileName }

context(dp: DataPack)
fun Player.onBlockUse(block: Function.(Player) -> Unit) {
	val self = this
	dp.advancementEvent(dp.name, OopConstants.blockUseEvent, { block(self) }) {
		anyBlockUse("any_block_use")
	}
}

context(dp: DataPack)
fun Player.onBredAnimals(block: Function.(Player) -> Unit) {
	val self = this
	dp.advancementEvent(dp.name, OopConstants.bredAnimalsEvent, { block(self) }) {
		bredAnimals("bred_animals")
	}
}

context(dp: DataPack)
fun Player.onBrewedPotion(block: Function.(Player) -> Unit) {
	val self = this
	dp.advancementEvent(dp.name, OopConstants.brewedPotionEvent, { block(self) }) {
		brewedPotion("brewed_potion")
	}
}

context(dp: DataPack)
fun Player.onChangeDimension(block: Function.(Player) -> Unit) {
	val self = this
	dp.advancementEvent(dp.name, OopConstants.changeDimensionEvent, { block(self) }) {
		changedDimension("changed_dimension")
	}
}

context(dp: DataPack)
fun Player.onConsumeItem(block: Function.(Player) -> Unit) {
	val self = this
	dp.advancementEvent(dp.name, OopConstants.consumeItemEvent, { block(self) }) {
		consumeItem("consume_item")
	}
}

context(dp: DataPack)
fun Player.onConsumeItem(item: ItemArgument, block: Function.(Player) -> Unit) {
	val self = this
	dp.advancementEventForItem(
		dp.name,
		OopConstants.consumeItemEvent,
		item.name.lowercase(),
		{ block(self) }) {
		consumeItem("consume_item", item)
	}
}

context(dp: DataPack)
fun Player.onEffectsChanged(block: Function.(Player) -> Unit) {
	val self = this
	dp.advancementEvent(dp.name, OopConstants.effectsChangedEvent, { block(self) }) {
		effectsChanged("effects_changed")
	}
}

context(dp: DataPack)
fun Player.onEnchantItem(block: Function.(Player) -> Unit) {
	val self = this
	dp.advancementEvent(dp.name, OopConstants.enchantItemEvent, { block(self) }) {
		enchantedItem("enchanted_item")
	}
}

context(dp: DataPack)
fun Player.onEntityHurtPlayer(block: Function.(Player) -> Unit) {
	val self = this
	dp.advancementEvent(dp.name, OopConstants.entityHurtPlayerEvent, { block(self) }) {
		entityHurtPlayer("entity_hurt_player")
	}
}

context(dp: DataPack)
fun Player.onFallFromHeight(block: Function.(Player) -> Unit) {
	val self = this
	dp.advancementEvent(dp.name, OopConstants.fallFromHeightEvent, { block(self) }) {
		fallFromHeight("fall_from_height")
	}
}

context(dp: DataPack)
fun Player.onFilledBucket(block: Function.(Player) -> Unit) {
	val self = this
	dp.advancementEvent(dp.name, OopConstants.filledBucketEvent, { block(self) }) {
		filledBucket("filled_bucket")
	}
}

context(dp: DataPack)
fun Player.onFishingRodHooked(block: Function.(Player) -> Unit) {
	val self = this
	dp.advancementEvent(dp.name, OopConstants.fishingRodHookedEvent, { block(self) }) {
		fishingRodHooked("fishing_rod_hooked")
	}
}

context(dp: DataPack)
fun Player.onHurtEntity(block: Function.(Player) -> Unit) {
	val self = this
	dp.advancementEvent(dp.name, OopConstants.hurtEntityEvent, { block(self) }) {
		playerHurtEntity("player_hurt_entity")
	}
}

context(dp: DataPack)
fun Player.onInteractWithEntity(block: Function.(Player) -> Unit) {
	val self = this
	dp.advancementEvent(dp.name, OopConstants.interactWithEntityEvent, { block(self) }) {
		playerInteractedWithEntity("player_interacted_with_entity")
	}
}

context(dp: DataPack)
fun Player.onInventoryChange(block: Function.(Player) -> Unit) {
	val self = this
	dp.advancementEvent(dp.name, OopConstants.inventoryChangeEvent, { block(self) }) {
		inventoryChanged("inventory_changed")
	}
}

context(dp: DataPack)
fun Player.onItemUsedOnBlock(block: Function.(Player) -> Unit) {
	val self = this
	dp.advancementEvent(dp.name, OopConstants.itemUsedOnBlockEvent, { block(self) }) {
		itemUsedOnBlock("item_used_on_block")
	}
}

context(dp: DataPack)
fun Player.onKill(block: Function.(Player) -> Unit) {
	val self = this
	dp.advancementEvent(dp.name, OopConstants.killEvent, { block(self) }) {
		playerKilledEntity("kill")
	}
}

context(dp: DataPack)
fun Player.onKilledByArrow(block: Function.(Player) -> Unit) {
	val self = this
	dp.advancementEvent(dp.name, OopConstants.killedByArrowEvent, { block(self) }) {
		killedByArrow("killed_by_arrow")
	}
}

context(dp: DataPack)
fun Player.onPlaceBlock(block: Function.(Player) -> Unit) {
	val self = this
	dp.advancementEvent(dp.name, OopConstants.placeBlockEvent, { block(self) }) {
		placedBlock("placed_block")
	}
}

context(dp: DataPack)
fun Player.onRecipeCrafted(recipe: RecipeArgument, block: Function.(Player) -> Unit) {
	val self = this
	dp.advancementEvent(dp.name, OopConstants.recipeCraftedEvent, { block(self) }) {
		recipeCrafted("recipe_crafted", recipe)
	}
}

context(dp: DataPack)
fun Player.onRightClick(item: ItemArgument, block: Function.(Player) -> Unit) {
	val self = this
	dp.advancementEventForItem(
		dp.name,
		OopConstants.rightClickEvent,
		item.name.lowercase(),
		{ block(self) }) {
		usingItem("use_item") { this.item = itemStackPredicate(item) }
	}
}

context(dp: DataPack)
fun Player.onShotCrossbow(block: Function.(Player) -> Unit) {
	val self = this
	dp.advancementEvent(dp.name, OopConstants.shotCrossbowEvent, { block(self) }) {
		shotCrossbow("shot_crossbow")
	}
}

context(dp: DataPack)
fun Player.onSleptInBed(block: Function.(Player) -> Unit) {
	val self = this
	dp.advancementEvent(dp.name, OopConstants.sleptInBedEvent, { block(self) }) {
		sleptInBed("slept_in_bed")
	}
}

context(dp: DataPack)
fun Player.onStartRiding(block: Function.(Player) -> Unit) {
	val self = this
	dp.advancementEvent(dp.name, OopConstants.startRidingEvent, { block(self) }) {
		startedRiding("started_riding")
	}
}

context(dp: DataPack)
fun Player.onTameAnimal(block: Function.(Player) -> Unit) {
	val self = this
	dp.advancementEvent(dp.name, OopConstants.tameAnimalEvent, { block(self) }) {
		tameAnimal("tame_animal")
	}
}

context(dp: DataPack)
fun Player.onTargetHit(block: Function.(Player) -> Unit) {
	val self = this
	dp.advancementEvent(dp.name, OopConstants.targetHitEvent, { block(self) }) {
		targetHit("target_hit")
	}
}

context(dp: DataPack)
fun Player.onTick(block: Function.(Player) -> Unit) {
	val self = this
	dp.advancementEvent(dp.name, OopConstants.tickEvent, { block(self) }) {
		tick("tick")
	}
}

context(dp: DataPack)
fun Player.onUsedEnderEye(block: Function.(Player) -> Unit) {
	val self = this
	dp.advancementEvent(dp.name, OopConstants.usedEnderEyeEvent, { block(self) }) {
		usedEnderEye("used_ender_eye")
	}
}

context(dp: DataPack)
fun Player.onUsedTotem(block: Function.(Player) -> Unit) {
	val self = this
	dp.advancementEvent(dp.name, OopConstants.usedTotemEvent, { block(self) }) {
		usedTotem("used_totem")
	}
}

context(dp: DataPack)
fun Entity.onDeath(block: Function.(Entity) -> Unit) {
	val self = this
	dp.registerDeathEvent(this, dp.name) { block(self) }
}

