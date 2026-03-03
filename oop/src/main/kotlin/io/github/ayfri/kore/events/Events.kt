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
import net.benwoodworth.knbt.NbtByte
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

private fun DataPack.hasAdvancement(fileName: String) = advancements.any { it.fileName == fileName }

context(fn: Function)
fun Player.onBlockUse(block: Function.() -> Unit) {
    fn.datapack.advancementEvent(fn.namespace, OopConstants.blockUseEvent, block.hashCode(), block) {
		anyBlockUse("any_block_use")
	}
}

context(fn: Function)
fun Player.onChangeDimension(block: Function.() -> Unit) {
    fn.datapack.advancementEvent(fn.namespace, OopConstants.changeDimensionEvent, block.hashCode(), block) {
        changedDimension("changed_dimension")
    }
}

context(fn: Function)
fun Player.onConsumeItem(block: Function.() -> Unit) {
    fn.datapack.advancementEvent(fn.namespace, OopConstants.consumeItemEvent, block.hashCode(), block) {
		consumeItem("consume_item")
	}
}

context(fn: Function)
fun Player.onConsumeItem(item: ItemArgument, block: Function.() -> Unit) {
    fn.datapack.advancementEventForItem(
        fn.namespace,
        OopConstants.consumeItemEvent,
        item.name.lowercase(),
        block.hashCode(),
        block
    ) {
		consumeItem("consume_item", item)
	}
}

context(fn: Function)
fun Player.onRecipeCrafted(recipe: RecipeArgument, block: Function.() -> Unit) {
    fn.datapack.advancementEvent(fn.namespace, OopConstants.recipeCraftedEvent, block.hashCode(), block) {
        recipeCrafted("recipe_crafted", recipe)
    }
}

context(fn: Function)
fun Entity.onDeath(block: Function.() -> Unit) {
    val dp = fn.datapack
    val ns = fn.namespace

    dp.ensureDeathTriggerSetup(ns)
    dp.addHandler(
        OopConstants.deathHandlersTag,
        ns,
        OopConstants.eventHandlerName(OopConstants.deathEvent, block.hashCode()),
        block
    )

    val entityTypeName = selector.type?.name?.lowercase() ?: "generic"
    val lootTableName = OopConstants.deathTriggerLootTable(entityTypeName)
    if (dp.lootTables.none { it.fileName == lootTableName }) {
        dp.lootTable(lootTableName) {
            pool {
                val itemEntry = io.github.ayfri.kore.features.loottables.entries.Item(
                    name = Items.STRUCTURE_VOID,
                    functions = io.github.ayfri.kore.features.itemmodifiers.ItemModifier(
                        modifiers = listOf(
                            io.github.ayfri.kore.features.itemmodifiers.functions.SetCustomData(
                                tag = net.benwoodworth.knbt.buildNbtCompound {
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

context(fn: Function)
fun Player.onEffectsChanged(block: Function.() -> Unit) {
    fn.datapack.advancementEvent(fn.namespace, OopConstants.effectsChangedEvent, block.hashCode(), block) {
        effectsChanged("effects_changed")
    }
}

context(fn: Function)
fun Player.onEnchantItem(block: Function.() -> Unit) {
    fn.datapack.advancementEvent(fn.namespace, OopConstants.enchantItemEvent, block.hashCode(), block) {
        enchantedItem("enchanted_item")
    }
}

context(fn: Function)
fun Player.onEntityHurtPlayer(block: Function.() -> Unit) {
    fn.datapack.advancementEvent(fn.namespace, OopConstants.entityHurtPlayerEvent, block.hashCode(), block) {
        entityHurtPlayer("entity_hurt_player")
    }
}

context(fn: Function)
fun Player.onFallFromHeight(block: Function.() -> Unit) {
    fn.datapack.advancementEvent(fn.namespace, OopConstants.fallFromHeightEvent, block.hashCode(), block) {
        fallFromHeight("fall_from_height")
    }
}

context(fn: Function)
fun Player.onHurtEntity(block: Function.() -> Unit) {
    fn.datapack.advancementEvent(fn.namespace, OopConstants.hurtEntityEvent, block.hashCode(), block) {
		playerHurtEntity("player_hurt_entity")
	}
}

context(fn: Function)
fun Player.onInventoryChange(block: Function.() -> Unit) {
    fn.datapack.advancementEvent(fn.namespace, OopConstants.inventoryChangeEvent, block.hashCode(), block) {
		inventoryChanged("inventory_changed")
	}
}

context(fn: Function)
fun Player.onItemUsedOnBlock(block: Function.() -> Unit) {
    fn.datapack.advancementEvent(fn.namespace, OopConstants.itemUsedOnBlockEvent, block.hashCode(), block) {
		itemUsedOnBlock("item_used_on_block")
	}
}

context(fn: Function)
fun Player.onKill(block: Function.() -> Unit) {
    fn.datapack.advancementEvent(fn.namespace, OopConstants.killEvent, block.hashCode(), block) {
		playerKilledEntity("kill")
	}
}

context(fn: Function)
fun Player.onPlaceBlock(block: Function.() -> Unit) {
    fn.datapack.advancementEvent(fn.namespace, OopConstants.placeBlockEvent, block.hashCode(), block) {
		placedBlock("placed_block")
	}
}

context(fn: Function)
fun Player.onRightClick(item: ItemArgument, block: Function.() -> Unit) {
    fn.datapack.advancementEventForItem(
        fn.namespace,
        OopConstants.rightClickEvent,
        item.name.lowercase(),
        block.hashCode(),
        block
    ) {
		usingItem("use_item") { this.item = itemStack(item) }
	}
}

context(fn: Function)
fun Player.onSleptInBed(block: Function.() -> Unit) {
    fn.datapack.advancementEvent(fn.namespace, OopConstants.sleptInBedEvent, block.hashCode(), block) {
        sleptInBed("slept_in_bed")
    }
}

context(fn: Function)
fun Player.onStartRiding(block: Function.() -> Unit) {
    fn.datapack.advancementEvent(fn.namespace, OopConstants.startRidingEvent, block.hashCode(), block) {
        startedRiding("started_riding")
    }
}

context(fn: Function)
fun Player.onTameAnimal(block: Function.() -> Unit) {
    fn.datapack.advancementEvent(fn.namespace, OopConstants.tameAnimalEvent, block.hashCode(), block) {
        tameAnimal("tame_animal")
    }
}

context(fn: Function)
fun Player.onTargetHit(block: Function.() -> Unit) {
    fn.datapack.advancementEvent(fn.namespace, OopConstants.targetHitEvent, block.hashCode(), block) {
        targetHit("target_hit")
	}
}
