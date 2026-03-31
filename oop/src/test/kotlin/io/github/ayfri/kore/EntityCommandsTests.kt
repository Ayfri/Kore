package io.github.ayfri.kore

import io.github.ayfri.kore.arguments.enums.Gamemode
import io.github.ayfri.kore.arguments.numbers.Xp
import io.github.ayfri.kore.arguments.types.resources.SoundArgument
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.commands.say
import io.github.ayfri.kore.entities.*
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.generated.EntityTypes
import io.github.ayfri.kore.generated.Items
import io.github.ayfri.kore.generated.arguments.types.DamageTypeArgument
import io.github.ayfri.kore.utils.testDataPack

fun entityCommandsTests() = testDataPack("entity_commands_tests") {
	val player = player("TestPlayer")
	val zombie = entity {
		type = EntityTypes.ZOMBIE
	}

	function("test_add_tag") {
		player.addTag("vip") assertsIs "tag @e[limit=1,name=TestPlayer,type=minecraft:player] add vip"
		lines.size assertsIs 1
	}

	function("test_batch") {
		player.batch("player_setup") {
			say("Hello from batch!")
			say("Second command!")
		}
		lines[0] assertsIs "execute as @e[limit=1,name=TestPlayer,type=minecraft:player] run function entity_commands_tests:generated_scopes/batch_player_setup"
		lines.size assertsIs 1
	}

	function("test_clear_items") {
		player.clearItems() assertsIs "clear @e[limit=1,name=TestPlayer,type=minecraft:player]"
		lines.size assertsIs 1
	}

	function("test_clear_specific_item") {
		player.clearItems(
			Items.DIAMOND,
			5
		) assertsIs "clear @e[limit=1,name=TestPlayer,type=minecraft:player] minecraft:diamond 5"
		lines.size assertsIs 1
	}

	function("test_damage") {
		zombie.damage(5f) assertsIs "damage @e[limit=1,type=minecraft:zombie] 5"
		lines.size assertsIs 1
	}

	function("test_damage_with_type") {
		zombie.damage(
			3f,
			DamageTypeArgument("fire")
		) assertsIs "damage @e[limit=1,type=minecraft:zombie] 3 minecraft:fire"
		lines.size assertsIs 1
	}

	function("test_dismount") {
		player.dismount() assertsIs "ride @e[limit=1,name=TestPlayer,type=minecraft:player] dismount"
		lines.size assertsIs 1
	}

	function("test_gamemode") {
		player.setGamemode(Gamemode.CREATIVE) assertsIs "gamemode creative @e[limit=1,name=TestPlayer,type=minecraft:player]"
		lines.size assertsIs 1
	}

	function("test_give_xp") {
		player.giveXp(Xp(10)) assertsIs "experience add @e[limit=1,name=TestPlayer,type=minecraft:player] 10"
		lines.size assertsIs 1
	}

	function("test_kill") {
		player.kill() assertsIs "kill @e[limit=1,name=TestPlayer,type=minecraft:player]"
		lines.size assertsIs 1
	}

	function("test_mount") {
		player.mount(zombie) assertsIs "ride @e[limit=1,name=TestPlayer,type=minecraft:player] mount @e[limit=1,type=minecraft:zombie]"
		lines.size assertsIs 1
	}

	function("test_play_sound") {
		player.playSound(SoundArgument("entity.experience_orb.pickup")) assertsIs "playsound minecraft:entity.experience_orb.pickup @e[limit=1,name=TestPlayer,type=minecraft:player]"
		lines.size assertsIs 1
	}

	function("test_remove_tag") {
		player.removeTag("vip") assertsIs "tag @e[limit=1,name=TestPlayer,type=minecraft:player] remove vip"
		lines.size assertsIs 1
	}

	function("test_send_message") {
		player.sendMessage("Hello!") assertsIs """tellraw @e[limit=1,name=TestPlayer,type=minecraft:player] "Hello!""""
		lines.size assertsIs 1
	}

	function("test_set_xp") {
		player.setXp(Xp(5)) assertsIs "experience set @e[limit=1,name=TestPlayer,type=minecraft:player] 5"
		lines.size assertsIs 1
	}

	function("test_show_title") {
		player.showTitle("Welcome")
		lines[0] assertsIs """title @e[limit=1,name=TestPlayer,type=minecraft:player] title "Welcome""""
		lines.size assertsIs 1
	}

	function("test_show_title_with_subtitle") {
		player.showTitle("Welcome", "to the game")
		lines[0] assertsIs """title @e[limit=1,name=TestPlayer,type=minecraft:player] title "Welcome""""
		lines[1] assertsIs """title @e[limit=1,name=TestPlayer,type=minecraft:player] subtitle "to the game""""
		lines.size assertsIs 2
	}

	function("test_action_bar") {
		player.showActionBar("Press F to interact") assertsIs """title @e[limit=1,name=TestPlayer,type=minecraft:player] actionbar "Press F to interact""""
		lines.size assertsIs 1
	}
}.apply {
	generate()
}
