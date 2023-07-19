package commands

import arguments.ARMOR
import arguments.CONTAINER
import arguments.ENDERCHEST
import arguments.WEAPON
import arguments.maths.vec3
import arguments.types.literals.self
import arguments.types.resources.ItemModifierArgument
import functions.Function
import generated.Items
import utils.assertsIs

fun Function.itemTests() {
	items {
		modify(self(), WEAPON, ItemModifierArgument("baz")) assertsIs "item modify entity @s weapon minecraft:baz"
		replace(self(), CONTAINER[0], Items.DIRT) assertsIs "item replace entity @s container.0 with minecraft:dirt"
		replace(self(), ARMOR.HEAD, Items.BOW, 3) assertsIs "item replace entity @s armor.head with minecraft:bow 3"
		replace(vec3(), ENDERCHEST[2], vec3(0, 0, 0), WEAPON) assertsIs "item replace block ~ ~ ~ enderchest.2 from block 0 0 0 weapon"
	}

	itemSlot(self(), WEAPON) {
		modify(ItemModifierArgument("baz")) assertsIs "item modify entity @s weapon minecraft:baz"
		replace(Items.DIRT) assertsIs "item replace entity @s weapon with minecraft:dirt"
		replace(Items.BOW, 3) assertsIs "item replace entity @s weapon with minecraft:bow 3"
		replace(vec3(0, 0, 0), WEAPON) assertsIs "item replace entity @s weapon from block 0 0 0 weapon"
	}
}
