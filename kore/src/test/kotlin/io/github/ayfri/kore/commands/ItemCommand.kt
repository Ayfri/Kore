package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.ARMOR
import io.github.ayfri.kore.arguments.CONTAINER
import io.github.ayfri.kore.arguments.ENDERCHEST
import io.github.ayfri.kore.arguments.WEAPON
import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.generated.Items
import io.github.ayfri.kore.generated.arguments.types.ItemModifierArgument

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

		replace(Items.DIRT {
			remove("foo")
		}) assertsIs "item replace entity @s weapon with minecraft:dirt[!foo]"
	}
}
