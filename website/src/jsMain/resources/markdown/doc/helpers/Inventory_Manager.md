---
root: .components.layouts.MarkdownLayout
title: Inventory Manager
nav-title: Inventory Manager
description: Listen to slot events and control containers (players, blocks) with Kore's Inventory Manager.
keywords: minecraft, datapack, kore, inventory, container, slots, events, gui
date-created: 2025-08-11
date-modified: 2025-08-11
routeOverride: /docs/helpers/inventory-manager
---

# Inventory Manager

Kore’s Inventory Manager lets you declaratively control inventories on entities or blocks and react to slot events.

- Manage items in any `ContainerArgument` (players, entities, or block containers).
- Register slot listeners that fire when an item is taken from a slot.
- Keep a slot populated, clear other slots, or run custom logic every tick.
- Auto-generate the required load/tick functions and minimal scoreboards to drive the listeners.

## Quick start

```kotlin
import io.github.ayfri.kore.arguments.types.literals.nearestPlayer
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.commands.TitleLocation
import io.github.ayfri.kore.generated.Items
import io.github.ayfri.kore.helpers.inventorymanager.inventoryManager

function("inventory_demo") {
    val playerInv = inventoryManager(nearestPlayer())

    playerInv.slotEvent(HOTBAR[0], Items.NETHER_STAR {
        this["display"] = nbt {
            this["Name"] = textComponent("Do not move me", color = Color.RED).toJsonString()
        }
    }) {
        onTake {
            title(self(), TitleLocation.ACTIONBAR, textComponent("Stop taking me!", color = Color.RED))
        }

        duringTake { setItemInSlot() }
        onTick { clearAllItemsNotInSlot(); killAllItemsNotInSlot() }

        setItemInSlot() // seed the slot once
    }

    // Start listeners for this manager
    playerInv.generateSlotsListeners()
}
```

Tip: Use the builder variant to both declare listeners and auto-generate them in one go:

```kotlin
inventoryManager(nearestPlayer()) {
    slotEvent(HOTBAR[0], Items.DIAMOND) { setItemInSlot() }
    generateSlotsListeners()
}
```

## Block containers

You can target block inventories as well by passing a `Vec3` and (optionally) placing a container block first:

```kotlin
val chestPos = vec3(0, -59, 0)

inventoryManager(chestPos) {
    setBlock(Blocks.CHEST)

    slotEvent(CONTAINER[0], Items.DIAMOND_SWORD) {
        onTake { tellraw(allPlayers(), text("You took the sword!", Color.RED)) }
        duringTake { setItemInSlot() }
        onTick { clearAllItemsNotInSlot(); killAllItemsNotInSlot() }
        setItemInSlot()
    }

    generateSlotsListeners()
}
```

## API overview

- `inventoryManager(container)` – Create a manager for any `ContainerArgument` (`EntityArgument` or `Vec3`).
- `slotEvent(slot, expectedItem) { … }` – Register handlers for a single slot.
  - `onTake { … }` – Fires once when the slot transitions from expected item to something else.
  - `duringTake { … }` – Fires every tick while the slot is not holding the expected item; useful to enforce state.
  - `onTick { … }` – Runs every tick regardless of state; convenient for housekeeping.
  - Helpers inside the scope:
    - `setItemInSlot()` – Put back the expected item into the slot.
    - `clearAllItemsNotInSlot([targets])` – Clear all other slots.
    - `killAllItemsNotInSlot()` – Remove dropped items that don’t belong to this slot.
- `generateSlotsListeners()` – Emits the `load`/`tick` functions and scoreboard wiring for all registered listeners.
- `setBlock(block)` – When the container is a position, place a block (e.g., a chest) before managing its contents.
- `clear(slot)`, `clearAll()`, `clearAll(item)` – Utilities to wipe inventory content.

Internally, Inventory Manager relies on a scoreboard objective and a tiny helper marker entity (for non-entity containers) to detect state transitions. Names are auto-namespaced and unique per datapack.

## Removing detectors

To clean up objectives created by Inventory Manager across runs:

```kotlin
dataPack("my_dp") {
    InventoryManager.removeClickDetectors()
}
```

## Full example

This combines a player inventory policy with messaging and a chest that constantly re-seeds its first slot. It mirrors the test coverage used in Kore’s own suite.

```kotlin
fun Function.inventoryManagerTests() {
    val counter = "take_counter"
    val playerInv = inventoryManager(nearestPlayer())

    playerInv.slotEvent(HOTBAR[0], Items.NETHER_STAR) {
        onTake {
            title(self(), TitleLocation.ACTIONBAR, text("Don’t take me", Color.RED))
            scoreboard.players.add(self(), counter, 1)
        }
        duringTake { setItemInSlot() }
        onTick { clearAllItemsNotInSlot(); killAllItemsNotInSlot() }
        setItemInSlot()
    }

    playerInv.generateSlotsListeners()

    datapack.load {
        scoreboard.objectives.add(counter)
        scoreboard.players.set(playerInv.container as ScoreHolderArgument, counter, 0)
    }

    inventoryManager(vec3(0, -59, 0)) {
        setBlock(Blocks.CHEST)
        slotEvent(CONTAINER[0], Items.DIAMOND_SWORD) {
            onTake { tellraw(allPlayers(), text("You took the diamond sword from the chest", Color.RED)) }
            duringTake { setItemInSlot() }
            onTick { clearAllItemsNotInSlot(); killAllItemsNotInSlot() }
            setItemInSlot()
        }
        generateSlotsListeners()
    }
}
```

## See also

- [Scheduler](./scheduler) – Run repeated or delayed logic that complements inventory policies.
- [Components](../components) – Define complex items (names, lore, enchantments) you can enforce in slots.
- [Predicates](../predicates) – Validate component-based item properties in other contexts.
- [Scoreboards](../scoreboards) – Background knowledge on objectives used under the hood.
