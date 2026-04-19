package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.enums.Difficulty
import io.github.ayfri.kore.arguments.enums.Gamemode
import io.github.ayfri.kore.arguments.maths.Vec3
import io.github.ayfri.kore.arguments.types.EntityArgument
import io.github.ayfri.kore.arguments.types.literals.*
import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.arguments.types.resources.SoundArgument
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.generated.arguments.types.EnchantmentArgument
import io.github.ayfri.kore.generated.arguments.worldgen.types.BiomeArgument
import io.github.ayfri.kore.utils.asArg

/** Starts the debug profiler. */
fun Function.debugStart() = addLine(command("debug", literal("start")))

/** Stops the debug profiler. */
fun Function.debugStop() = addLine(command("debug", literal("stop")))

/** Sets the default gamemode. */
fun Function.defaultGamemode(mode: Gamemode) = addLine(command("defaultgamemode", literal(mode.asArg())))

/** Sets the world difficulty. */
fun Function.difficulty(difficulty: Difficulty? = null) = addLine(command("difficulty", literal(difficulty?.asArg())))

/** Enchants [target] with [enchantment] at the optional [level]. */
fun Function.enchant(target: EntityArgument, enchantment: EnchantmentArgument, level: Int? = null) =
	addLine(command("enchant", target, enchantment, int(level)))

/** Looks up a profile by player [name]. */
fun Function.fetchprofile(name: String) = addLine(command("fetchprofile", literal("name"), literal(name)))

/** Looks up a profile by UUID [id]. */
fun Function.fetchprofile(id: UUIDArgument) = addLine(command("fetchprofile", literal("id"), id))

/** Fills the selected area with [biome]. */
fun Function.fillbiome(from: Vec3, to: Vec3, biome: BiomeArgument) = addLine(command("fillbiome", from, to, biome))

/** Replaces the selected area with [biome] where [filter] matches. */
fun Function.fillbiome(from: Vec3, to: Vec3, biome: BiomeArgument, filter: BiomeArgument) =
	addLine(command("fillbiome", from, to, biome, literal("replace"), filter))

/** Sets the current gamemode for [target] or the command source. */
fun Function.gamemode(gamemode: Gamemode, target: EntityArgument? = null) =
	addLine(command("gamemode", literal(gamemode.asArg()), target))

/** Gives [target] [item] with an optional [count]. */
fun Function.give(target: EntityArgument, item: ItemArgument, count: Int? = null) =
	addLine(command("give", target, item, int(count)))

/** Shows the help entry for the given command name. */
fun Function.help(command: String? = null) = addLine(command("help", literal(command)))

/** Shows the help entry for [command]. */
fun Function.help(command: Command) = addLine(command("help", literal(command.name)))

/** Runs a `jfr start` / `jfr stop` block around [block]. */
fun Function.jfr(block: Function.() -> Unit) {
	jfrStart()
	block()
	jfrStop()
}

/** Starts Java Flight Recorder tracing. */
fun Function.jfrStart() = addLine(command("jfr", literal("start")))

/** Stops Java Flight Recorder tracing. */
fun Function.jfrStop() = addLine(command("jfr", literal("stop")))

/** Kills [targets], or the command source when omitted. */
fun Function.kill(targets: EntityArgument? = null) = addLine(command("kill", targets))

/** Lists the players currently on the server. */
fun Function.list(uuids: Boolean = false) = addLine(command("list", literal(if (uuids) "uuids" else null)))

/** Makes the command source perform the action described by [message]. */
fun Function.me(message: String) = addLine(command("me", literal(message)))

/** Sends a private message to [target]. */
fun Function.msg(target: EntityArgument, message: String) = addLine(command("msg", target, literal(message)))

/** Alias of [msg]. */
fun Function.tell(targets: EntityArgument, message: String) = msg(targets, message)

/** Alias of [msg]. */
fun Function.w(target: EntityArgument, message: String) = msg(target, message)

/** Runs a `perf start` / `perf stop` block around [block]. */
fun Function.perf(block: Function.() -> Unit) {
	perfStart()
	block()
	perfStop()
}

/** Starts the performance profiler. */
fun Function.perfStart() = addLine(command("perf", literal("start")))

/** Stops the performance profiler. */
fun Function.perfStop() = addLine(command("perf", literal("stop")))

/** Sends a chat message as the command source. */
fun Function.say(message: String) = addLine(command("say", literal(message)))

/** Returns the world seed. */
fun Function.seed() = addLine(command("seed"))

/** Sets the world spawn point and optional rotation. */
fun Function.setWorldSpawn(pos: Vec3? = null, rotation: RotationArgument? = null) =
	addLine(command("setworldspawn", pos, rotation))

/** Sets the spawn point for [target] or the command source. */
fun Function.spawnPoint(target: EntityArgument? = null, pos: Vec3? = null, rotation: RotationArgument? = null) =
	addLine(command("spawnpoint", target, pos, rotation))

/** Makes [target] spectate [player], or stops spectating when [player] is omitted. */
fun Function.spectate(target: EntityArgument? = null, player: EntityArgument? = null) =
	addLine(command("spectate", target, player))

/** Stops [sound] from playing for [targets] on the selected [source]. */
fun Function.stopSound(
	targets: EntityArgument,
	source: PlaySoundMixer? = null,
	sound: SoundArgument? = null,
) = addLine(command("stopsound", targets, literal(source?.asArg()), sound))

/** Stops [sound] from every source for [targets]. */
fun Function.stopSoundAllSources(
	targets: EntityArgument,
	sound: SoundArgument? = null,
) = addLine(command("stopsound", targets, all(), sound))

/** Sends a team message as the command source. */
fun Function.teamMsg(message: String) = addLine(command("teammsg", literal(message)))

/** Alias of [teamMsg]. */
fun Function.tm(message: String) = teamMsg(message)

/** Prints the game version. */
fun Function.version() = addLine(command("version"))
