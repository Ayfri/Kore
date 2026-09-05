package io.github.ayfri.kore.arguments

import io.github.ayfri.kore.assertions.assertsIs
import io.kotest.core.spec.style.FunSpec

fun itemSlotTests() {
	ARMOR.BODY.asString() assertsIs "armor.body"

	CONTAINER[20].asString() assertsIs "container.20"

	MOB.INVENTORY.all() assertsIs "mob.inventory.*"
	MOB.INVENTORY[0].asString() assertsIs "mob.inventory.0"
	MOB.INVENTORY[7].asString() assertsIs "mob.inventory.7"

	PLAYER.CURSOR.asString() assertsIs "player.cursor"
	PLAYER.CRAFTING[0].asString() assertsIs "player.crafting.0"
	PLAYER.CRAFTING.all() assertsIs "player.crafting.*"


	ARMOR.BODY.asIndex() assertsIs 105
	CONTAINER[7].asIndex() assertsIs 7
	MOB.INVENTORY[3].asIndex() assertsIs 303
	PLAYER.CRAFTING[3].asIndex() assertsIs 503

	ItemSlotType.fromIndex(105).asString() assertsIs ARMOR.BODY.asString()
	ItemSlotType.fromIndex(7).asString() assertsIs CONTAINER[7].asString()
	ItemSlotType.fromIndex(7, fromEntity = true).asString() assertsIs HOTBAR[7].asString()
	ItemSlotType.fromIndex(300).asString() assertsIs MOB.INVENTORY[0].asString()
	ItemSlotType.fromIndex(305).asString() assertsIs MOB.INVENTORY[5].asString()
	ItemSlotType.fromIndex(503, fromPlayer = true).asString() assertsIs PLAYER.CRAFTING[3].asString()
}

class ItemSlotTests : FunSpec({
	test("item slot") {
		itemSlotTests()
	}
})
