package io.github.ayfri.kore.events

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.OopConstants
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
import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.itemmodifiers.functions.SetCustomData
import io.github.ayfri.kore.features.loottables.entries.Item
import io.github.ayfri.kore.features.loottables.lootTable
import io.github.ayfri.kore.features.loottables.pool
import io.github.ayfri.kore.features.predicates.sub.itemStack
import io.github.ayfri.kore.features.tags.functionTag
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.generatedFunction
import io.github.ayfri.kore.functions.tick
import io.github.ayfri.kore.generated.EntityTypes
import io.github.ayfri.kore.generated.Items
import io.github.ayfri.kore.generated.arguments.types.RecipeArgument
import io.github.ayfri.kore.utils.nbt
import net.benwoodworth.knbt.NbtByte
import io.github.ayfri.kore.commands.function as functionCommand
import io.github.ayfri.kore.functions.function as dpFunction

private val initializedDeathDispatch = mutableSetOf<String>()

internal fun DataPack.addHandler(
	tagName: String,
	ns: String,
	handlerName: String,
	block: Function.() -> Unit,
): FunctionArgument {
	val fn = dpFunction(handlerName, ns, block = block)
	functionTag(tagName, namespace = ns) { add(fn.asId()) }
	return fn
}

private fun DataPack.advancementEvent(
	ns: String,
	event: String,
	hashCode: Int,
	block: Function.() -> Unit,
	criteriaSetup: AdvancementCriteria.() -> Unit,
) {
	val tagName = OopConstants.eventTagName(event)
	addHandler(tagName, ns, OopConstants.eventHandlerName(event, hashCode), block)

	val advName = OopConstants.advancementName(event)
	if (!hasAdvancement(advName)) {
		val dispatchFn = generatedFunction(OopConstants.dispatchFunctionName(event)) {
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
	val tagName = OopConstants.eventTagNameForItem(event, itemName)
	addHandler(tagName, ns, OopConstants.eventHandlerNameForItem(event, itemName, hashCode), block)

	val advName = OopConstants.advancementNameForItem(event, itemName)
	if (!hasAdvancement(advName)) {
		val dispatchFn = generatedFunction(OopConstants.dispatchFunctionNameForItem(event, itemName)) {
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
		buildPartial(OopConstants.deathTriggerKey) { put(OopConstants.deathTriggerKey, NbtByte(1)) }
	}

	tick(OopConstants.deathDispatcherFunction) {
		execute {
			asTarget(allEntities { type = EntityTypes.ITEM })
			ifCondition { items(self(), CONTENTS, deathPredicate) }
			at(self())
			run {
				functionCommand(FunctionTagArgument(OopConstants.deathHandlersTag, ns))
				kill(self())
			}
		}
	}
}

private fun DataPack.registerDeathEvent(entity: Entity, ns: String, hashCode: Int, block: Function.() -> Unit) {
	ensureDeathTriggerSetup(ns)
	addHandler(
		OopConstants.deathHandlersTag,
		ns,
		OopConstants.eventHandlerName(OopConstants.deathEvent, hashCode),
		block
	)

	val entityTypeName = entity.selector.type?.name?.lowercase() ?: "generic"
	val lootTableName = OopConstants.deathTriggerLootTable(entityTypeName)
	if (lootTables.none { it.fileName == lootTableName }) {
		lootTable(lootTableName) {
			pool {
				val itemEntry = Item(
					name = Items.STRUCTURE_VOID,
					functions = ItemModifier(
						modifiers = listOf(
							SetCustomData(
								tag = nbt {
									put(OopConstants.deathTriggerKey, NbtByte(1))
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

private fun DataPack.hasAdvancement(fileName: String) = advancements.any { it.fileName == fileName }

context(dp: DataPack)
fun Player.onBlockUse(block: Function.(Player) -> Unit) {
	val self = this
	dp.advancementEvent(dp.name, OopConstants.blockUseEvent, block.hashCode(), { block(self) }) {
		anyBlockUse("any_block_use")
	}
}

context(dp: DataPack)
fun Player.onBredAnimals(block: Function.(Player) -> Unit) {
	val self = this
	dp.advancementEvent(dp.name, OopConstants.bredAnimalsEvent, block.hashCode(), { block(self) }) {
		bredAnimals("bred_animals")
	}
}

context(dp: DataPack)
fun Player.onBrewedPotion(block: Function.(Player) -> Unit) {
	val self = this
	dp.advancementEvent(dp.name, OopConstants.brewedPotionEvent, block.hashCode(), { block(self) }) {
		brewedPotion("brewed_potion")
	}
}

context(dp: DataPack)
fun Player.onChangeDimension(block: Function.(Player) -> Unit) {
	val self = this
	dp.advancementEvent(dp.name, OopConstants.changeDimensionEvent, block.hashCode(), { block(self) }) {
		changedDimension("changed_dimension")
	}
}

context(dp: DataPack)
fun Player.onConsumeItem(block: Function.(Player) -> Unit) {
	val self = this
	dp.advancementEvent(dp.name, OopConstants.consumeItemEvent, block.hashCode(), { block(self) }) {
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
		block.hashCode(),
		{ block(self) }) {
		consumeItem("consume_item", item)
	}
}

context(dp: DataPack)
fun Player.onEffectsChanged(block: Function.(Player) -> Unit) {
	val self = this
	dp.advancementEvent(dp.name, OopConstants.effectsChangedEvent, block.hashCode(), { block(self) }) {
		effectsChanged("effects_changed")
	}
}

context(dp: DataPack)
fun Player.onEnchantItem(block: Function.(Player) -> Unit) {
	val self = this
	dp.advancementEvent(dp.name, OopConstants.enchantItemEvent, block.hashCode(), { block(self) }) {
		enchantedItem("enchanted_item")
	}
}

context(dp: DataPack)
fun Player.onEntityHurtPlayer(block: Function.(Player) -> Unit) {
	val self = this
	dp.advancementEvent(dp.name, OopConstants.entityHurtPlayerEvent, block.hashCode(), { block(self) }) {
		entityHurtPlayer("entity_hurt_player")
	}
}

context(dp: DataPack)
fun Player.onFallFromHeight(block: Function.(Player) -> Unit) {
	val self = this
	dp.advancementEvent(dp.name, OopConstants.fallFromHeightEvent, block.hashCode(), { block(self) }) {
		fallFromHeight("fall_from_height")
	}
}

context(dp: DataPack)
fun Player.onFilledBucket(block: Function.(Player) -> Unit) {
	val self = this
	dp.advancementEvent(dp.name, OopConstants.filledBucketEvent, block.hashCode(), { block(self) }) {
		filledBucket("filled_bucket")
	}
}

context(dp: DataPack)
fun Player.onFishingRodHooked(block: Function.(Player) -> Unit) {
	val self = this
	dp.advancementEvent(dp.name, OopConstants.fishingRodHookedEvent, block.hashCode(), { block(self) }) {
		fishingRodHooked("fishing_rod_hooked")
	}
}

context(dp: DataPack)
fun Player.onHurtEntity(block: Function.(Player) -> Unit) {
	val self = this
	dp.advancementEvent(dp.name, OopConstants.hurtEntityEvent, block.hashCode(), { block(self) }) {
		playerHurtEntity("player_hurt_entity")
	}
}

context(dp: DataPack)
fun Player.onInteractWithEntity(block: Function.(Player) -> Unit) {
	val self = this
	dp.advancementEvent(dp.name, OopConstants.interactWithEntityEvent, block.hashCode(), { block(self) }) {
		playerInteractedWithEntity("player_interacted_with_entity")
	}
}

context(dp: DataPack)
fun Player.onInventoryChange(block: Function.(Player) -> Unit) {
	val self = this
	dp.advancementEvent(dp.name, OopConstants.inventoryChangeEvent, block.hashCode(), { block(self) }) {
		inventoryChanged("inventory_changed")
	}
}

context(dp: DataPack)
fun Player.onItemUsedOnBlock(block: Function.(Player) -> Unit) {
	val self = this
	dp.advancementEvent(dp.name, OopConstants.itemUsedOnBlockEvent, block.hashCode(), { block(self) }) {
		itemUsedOnBlock("item_used_on_block")
	}
}

context(dp: DataPack)
fun Player.onKill(block: Function.(Player) -> Unit) {
	val self = this
	dp.advancementEvent(dp.name, OopConstants.killEvent, block.hashCode(), { block(self) }) {
		playerKilledEntity("kill")
	}
}

context(dp: DataPack)
fun Player.onKilledByArrow(block: Function.(Player) -> Unit) {
	val self = this
	dp.advancementEvent(dp.name, OopConstants.killedByArrowEvent, block.hashCode(), { block(self) }) {
		killedByArrow("killed_by_arrow")
	}
}

context(dp: DataPack)
fun Player.onPlaceBlock(block: Function.(Player) -> Unit) {
	val self = this
	dp.advancementEvent(dp.name, OopConstants.placeBlockEvent, block.hashCode(), { block(self) }) {
		placedBlock("placed_block")
	}
}

context(dp: DataPack)
fun Player.onRecipeCrafted(recipe: RecipeArgument, block: Function.(Player) -> Unit) {
	val self = this
	dp.advancementEvent(dp.name, OopConstants.recipeCraftedEvent, block.hashCode(), { block(self) }) {
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
		block.hashCode(),
		{ block(self) }) {
		usingItem("use_item") { this.item = itemStack(item) }
	}
}

context(dp: DataPack)
fun Player.onShotCrossbow(block: Function.(Player) -> Unit) {
	val self = this
	dp.advancementEvent(dp.name, OopConstants.shotCrossbowEvent, block.hashCode(), { block(self) }) {
		shotCrossbow("shot_crossbow")
	}
}

context(dp: DataPack)
fun Player.onSleptInBed(block: Function.(Player) -> Unit) {
	val self = this
	dp.advancementEvent(dp.name, OopConstants.sleptInBedEvent, block.hashCode(), { block(self) }) {
		sleptInBed("slept_in_bed")
	}
}

context(dp: DataPack)
fun Player.onStartRiding(block: Function.(Player) -> Unit) {
	val self = this
	dp.advancementEvent(dp.name, OopConstants.startRidingEvent, block.hashCode(), { block(self) }) {
		startedRiding("started_riding")
	}
}

context(dp: DataPack)
fun Player.onTameAnimal(block: Function.(Player) -> Unit) {
	val self = this
	dp.advancementEvent(dp.name, OopConstants.tameAnimalEvent, block.hashCode(), { block(self) }) {
		tameAnimal("tame_animal")
	}
}

context(dp: DataPack)
fun Player.onTargetHit(block: Function.(Player) -> Unit) {
	val self = this
	dp.advancementEvent(dp.name, OopConstants.targetHitEvent, block.hashCode(), { block(self) }) {
		targetHit("target_hit")
	}
}

context(dp: DataPack)
fun Player.onTick(block: Function.(Player) -> Unit) {
	val self = this
	dp.advancementEvent(dp.name, OopConstants.tickEvent, block.hashCode(), { block(self) }) {
		tick("tick")
	}
}

context(dp: DataPack)
fun Player.onUsedEnderEye(block: Function.(Player) -> Unit) {
	val self = this
	dp.advancementEvent(dp.name, OopConstants.usedEnderEyeEvent, block.hashCode(), { block(self) }) {
		usedEnderEye("used_ender_eye")
	}
}

context(dp: DataPack)
fun Player.onUsedTotem(block: Function.(Player) -> Unit) {
	val self = this
	dp.advancementEvent(dp.name, OopConstants.usedTotemEvent, block.hashCode(), { block(self) }) {
		usedTotem("used_totem")
	}
}

context(dp: DataPack)
fun Entity.onDeath(block: Function.(Entity) -> Unit) {
	val self = this
	dp.registerDeathEvent(this, dp.name, block.hashCode()) { block(self) }
}

