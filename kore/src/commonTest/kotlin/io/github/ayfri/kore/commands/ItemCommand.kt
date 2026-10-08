package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.*
import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.features.itemmodifiers.functions.Source
import io.github.ayfri.kore.features.itemmodifiers.functions.setLore
import io.github.ayfri.kore.features.slotsources.SlotSourceOrigin
import io.github.ayfri.kore.features.slotsources.inlineSlotSource
import io.github.ayfri.kore.features.slotsources.reference
import io.github.ayfri.kore.features.slotsources.slotRange
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.load
import io.github.ayfri.kore.generated.Items
import io.github.ayfri.kore.generated.arguments.types.ItemModifierArgument
import io.github.ayfri.kore.generated.arguments.types.SlotSourceArgument
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

fun Function.itemTests() {
	items {
		modify(self(), WEAPON, ItemModifierArgument("baz")) assertsIs "item modify entity @s weapon minecraft:baz"
		replace(self(), CONTAINER[0], Items.DIRT) assertsIs "item replace entity @s container.0 with minecraft:dirt"
		replace(self(), ARMOR.HEAD, Items.BOW, 3) assertsIs "item replace entity @s armor.head with minecraft:bow 3"
		replace(
			vec3(),
			ENDERCHEST[2],
			vec3(0, 0, 0),
			WEAPON
		) assertsIs "item replace block ~ ~ ~ enderchest.2 from block 0 0 0 weapon"
	}

	items {
		fill(self(), HOTBAR, Items.BREAD, 16) assertsIs "item fill entity @s hotbar.* with minecraft:bread 16"
		override(self(), ARMOR, vec3(0, 0, 0), CONTAINER) assertsIs "item override entity @s armor.* from block 0 0 0 container.*"
		replace(self(), SlotSourceArgument("hands", "my_pack"), Items.STICK) assertsIs "item replace entity @s my_pack:hands with minecraft:stick"
		fill(self(), inlineSlotSource { slotRange(WEAPON.all()) }, Items.TORCH) assertsIs
			"""item fill entity @s {type:"minecraft:slot_range",slots:"weapon.*"} with minecraft:torch"""
		replace(self(), inlineSlotSource {
			slotRange(SlotSourceOrigin.CONTAINER, HOTBAR)
			reference(SlotSourceArgument("hands", "my_pack"))
		}, Items.STICK) assertsIs
			"""item replace entity @s [{type:"minecraft:slot_range",source:"container",slots:"hotbar.*"},{type:"minecraft:reference",name:"my_pack:hands"}] with minecraft:stick"""
	}

	items.modify(vec3(0.5, 64.2, -0.5), WEAPON.MAINHAND, ItemModifierArgument("baz")).toString() shouldBe
		"item modify block 0 64 -1 weapon.mainhand minecraft:baz"
	items.replace(vec3(1.5, 2.5, 3.5), CONTAINER[0], Items.DIRT).toString() shouldBe
		"item replace block 1 2 3 container.0 with minecraft:dirt"

	itemSlot(self(), WEAPON) {
		modify(ItemModifierArgument("baz")) assertsIs "item modify entity @s weapon minecraft:baz"
		modify {
			setLore(Source.THIS, "Inline Item Modifier")
		} assertsIs "item modify entity @s weapon {modifiers:{function:\"minecraft:set_lore\",entity:\"this\",lore:[\"Inline Item Modifier\"]}}"
		replace(Items.DIRT) assertsIs "item replace entity @s weapon with minecraft:dirt"
		replace(Items.BOW, 3) assertsIs "item replace entity @s weapon with minecraft:bow 3"
		fill(Items.ARROW) assertsIs "item fill entity @s weapon with minecraft:arrow"
		override(self(), HOTBAR) assertsIs "item override entity @s weapon from entity @s hotbar.*"
		replace(vec3(0, 0, 0), WEAPON) assertsIs "item replace entity @s weapon from block 0 0 0 weapon"

		replace(Items.DIRT {
			remove("foo")
		}) assertsIs "item replace entity @s weapon with minecraft:dirt[!foo]"

		replace(self(), SADDLE) {
			setLore(Source.THIS, "Inline Item Modifier")
		} assertsIs "item replace entity @s weapon from entity @s saddle {modifiers:{function:\"minecraft:set_lore\",entity:\"this\",lore:[\"Inline Item Modifier\"]}}"
	}
}

class ItemCommandTests : FunSpec({
	test("item") {
		dataPack("unit_tests") {
			load { itemTests() }
		}
	}
})
