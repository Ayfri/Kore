package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.arguments.types.literals.rotation
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.generated.Items
import net.benwoodworth.knbt.NbtInt

fun Function.commandsTests() {
	clear() assertsIs "clear"
	clear(self()) assertsIs "clear @s"
	clear(self(), Items.STONE) assertsIs "clear @s minecraft:stone"
	clear(self(), Items.STONE, 1) assertsIs "clear @s minecraft:stone 1"

	debugStart() assertsIs "debug start"
	debugStop() assertsIs "debug stop"

	defaultGamemode(io.github.ayfri.kore.arguments.enums.Gamemode.CREATIVE) assertsIs "defaultgamemode creative"

	difficulty() assertsIs "difficulty"
	difficulty(io.github.ayfri.kore.arguments.enums.Difficulty.EASY) assertsIs "difficulty easy"

	enchant(self(), io.github.ayfri.kore.generated.Enchantments.MENDING) assertsIs "enchant @s minecraft:mending"
	enchant(self(), io.github.ayfri.kore.generated.Enchantments.MENDING, 1) assertsIs "enchant @s minecraft:mending 1"

	gamemode(io.github.ayfri.kore.arguments.enums.Gamemode.CREATIVE) assertsIs "gamemode creative"
	gamemode(io.github.ayfri.kore.arguments.enums.Gamemode.CREATIVE, self()) assertsIs "gamemode creative @s"

	give(self(), Items.STONE) assertsIs "give @s minecraft:stone"
	give(self(), Items.STONE, 1) assertsIs "give @s minecraft:stone 1"
	give(self(), Items.STONE {
		addComponent("test", NbtInt(1))
	}) assertsIs "give @s minecraft:stone[test=1]"

	help() assertsIs "help"
	help("test") assertsIs "help test"
	help(help()) assertsIs "help help"

	jfrStart() assertsIs "jfr start"
	jfrStop() assertsIs "jfr stop"

	kill() assertsIs "kill"
	kill(self()) assertsIs "kill @s"

	list() assertsIs "list"
	list(true) assertsIs "list uuids"

	me("test") assertsIs "me test"

	msg(self(), "test") assertsIs "msg @s test"
	tell(self(), "test") assertsIs "msg @s test"
	w(self(), "test") assertsIs "msg @s test"

	perfStart() assertsIs "perf start"
	perfStop() assertsIs "perf stop"

	say("test") assertsIs "say test"

	seed() assertsIs "seed"

	setWorldSpawn() assertsIs "setworldspawn"
	setWorldSpawn(vec3(1, 2, 3)) assertsIs "setworldspawn 1 2 3"
	setWorldSpawn(vec3(), rotation()) assertsIs "setworldspawn ~ ~ ~ ~ ~"

	spawnPoint() assertsIs "spawnpoint"
	spawnPoint(self()) assertsIs "spawnpoint @s"
	spawnPoint(self(), vec3(1, 2, 3)) assertsIs "spawnpoint @s 1 2 3"
	spawnPoint(self(), vec3(), rotation()) assertsIs "spawnpoint @s ~ ~ ~ ~ ~"

	spectate() assertsIs "spectate"
	spectate(self()) assertsIs "spectate @s"
	spectate(self(), self()) assertsIs "spectate @s @s"

	stopSound(self()) assertsIs "stopsound @s"
	stopSound(self(), PlaySoundSource.MASTER) assertsIs "stopsound @s master"
	stopSound(
		self(),
		PlaySoundSource.MASTER,
		io.github.ayfri.kore.generated.Sounds.Mob.Bat.TAKEOFF
	) assertsIs "stopsound @s master minecraft:mob/bat/takeoff"
	stopSoundAllSources(self()) assertsIs "stopsound @s *"
	stopSoundAllSources(self(), io.github.ayfri.kore.generated.Sounds.Mob.Bat.TAKEOFF) assertsIs "stopsound @s * minecraft:mob/bat/takeoff"

	teamMsg("test") assertsIs "teammsg test"
	tm("test") assertsIs "teammsg test"
}
