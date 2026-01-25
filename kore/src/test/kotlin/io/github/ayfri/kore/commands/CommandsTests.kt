package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.enums.Difficulty
import io.github.ayfri.kore.arguments.enums.Gamemode
import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.arguments.types.literals.rotation
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.arguments.types.literals.uuid
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.generated.Enchantments
import io.github.ayfri.kore.generated.Items
import io.github.ayfri.kore.generated.Sounds
import net.benwoodworth.knbt.NbtInt

fun Function.commandsTests() {
	debugStart() assertsIs "debug start"
	debugStop() assertsIs "debug stop"

	defaultGamemode(Gamemode.CREATIVE) assertsIs "defaultgamemode creative"

	difficulty() assertsIs "difficulty"
	difficulty(Difficulty.EASY) assertsIs "difficulty easy"

	enchant(self(), Enchantments.MENDING) assertsIs "enchant @s minecraft:mending"
	enchant(self(), Enchantments.MENDING, 1) assertsIs "enchant @s minecraft:mending 1"

	fetchprofile("Ayfri") assertsIs "fetchprofile name Ayfri"
	fetchprofile(uuid("00000000-0000-0000-0000-000000000000")) assertsIs "fetchprofile id 00000000-0000-0000-0000-000000000000"

	gamemode(Gamemode.CREATIVE) assertsIs "gamemode creative"
	gamemode(Gamemode.CREATIVE, self()) assertsIs "gamemode creative @s"

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
	spawnPoint(self(), vec3(), rotation(90, 50)) assertsIs "spawnpoint @s ~ ~ ~ 90 50"

	spectate() assertsIs "spectate"
	spectate(self()) assertsIs "spectate @s"
	spectate(self(), self()) assertsIs "spectate @s @s"

	stopSound(self()) assertsIs "stopsound @s"
	stopSound(self(), PlaySoundMixer.MASTER) assertsIs "stopsound @s master"
	stopSound(
		self(),
		PlaySoundMixer.MASTER,
		Sounds.Mob.Bat.TAKEOFF
	) assertsIs "stopsound @s master minecraft:mob/bat/takeoff"
	stopSoundAllSources(self()) assertsIs "stopsound @s *"
	stopSoundAllSources(self(), Sounds.Mob.Bat.TAKEOFF) assertsIs "stopsound @s * minecraft:mob/bat/takeoff"

	teamMsg("test") assertsIs "teammsg test"
	tm("test") assertsIs "teammsg test"

	version() assertsIs "version"
}
