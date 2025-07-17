package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.enums.Difficulty
import io.github.ayfri.kore.arguments.enums.Gamemode
import io.github.ayfri.kore.arguments.maths.Vec3
import io.github.ayfri.kore.arguments.types.EntityArgument
import io.github.ayfri.kore.arguments.types.literals.RotationArgument
import io.github.ayfri.kore.arguments.types.literals.all
import io.github.ayfri.kore.arguments.types.literals.int
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.arguments.types.resources.EnchantmentArgument
import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.arguments.types.resources.SoundArgument
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.generated.arguments.worldgen.types.BiomeArgument
import io.github.ayfri.kore.utils.asArg

fun Function.debugStart() = addLine(command("debug", literal("start")))
fun Function.debugStop() = addLine(command("debug", literal("stop")))

fun Function.defaultGamemode(mode: Gamemode) = addLine(command("defaultgamemode", literal(mode.asArg())))

fun Function.difficulty(difficulty: Difficulty? = null) = addLine(command("difficulty", literal(difficulty?.asArg())))

fun Function.enchant(target: EntityArgument, enchantment: EnchantmentArgument, level: Int? = null) =
	addLine(command("enchant", target, enchantment, int(level)))

fun Function.fillbiome(from: Vec3, to: Vec3, biome: BiomeArgument) = addLine(command("fillbiome", from, to, biome))
fun Function.fillbiome(from: Vec3, to: Vec3, biome: BiomeArgument, filter: BiomeArgument) =
	addLine(command("fillbiome", from, to, biome, literal("replace"), filter))

fun Function.gamemode(gamemode: Gamemode, target: EntityArgument? = null) =
	addLine(command("gamemode", literal(gamemode.asArg()), target))

fun Function.give(target: EntityArgument, item: ItemArgument, count: Int? = null) =
	addLine(command("give", target, item, int(count)))

fun Function.help(command: String? = null) = addLine(command("help", literal(command)))
fun Function.help(command: Command) = addLine(command("help", literal(command.name)))

fun Function.jfr(block: Function.() -> Unit) {
	jfrStart()
	block()
	jfrStop()
}

fun Function.jfrStart() = addLine(command("jfr", literal("start")))
fun Function.jfrStop() = addLine(command("jfr", literal("stop")))

fun Function.kill(targets: EntityArgument? = null) = addLine(command("kill", targets))

fun Function.list(uuids: Boolean = false) = addLine(command("list", literal(if (uuids) "uuids" else null)))

fun Function.me(message: String) = addLine(command("me", literal(message)))

fun Function.msg(target: EntityArgument, message: String) = addLine(command("msg", target, literal(message)))
fun Function.tell(targets: EntityArgument, message: String) = msg(targets, message)
fun Function.w(target: EntityArgument, message: String) = msg(target, message)

fun Function.perf(block: Function.() -> Unit) {
	perfStart()
	block()
	perfStop()
}

fun Function.perfStart() = addLine(command("perf", literal("start")))
fun Function.perfStop() = addLine(command("perf", literal("stop")))

fun Function.say(message: String) = addLine(command("say", literal(message)))

fun Function.seed() = addLine(command("seed"))

fun Function.setWorldSpawn(pos: Vec3? = null, angle: RotationArgument? = null) = addLine(command("setworldspawn", pos, angle))

fun Function.spawnPoint(target: EntityArgument? = null, pos: Vec3? = null, angle: RotationArgument? = null) =
	addLine(command("spawnpoint", target, pos, angle))

fun Function.spectate(target: EntityArgument? = null, player: EntityArgument? = null) =
	addLine(command("spectate", target, player))

fun Function.stopSound(
	targets: EntityArgument,
	source: PlaySoundMixer? = null,
	sound: SoundArgument? = null,
) = addLine(command("stopsound", targets, literal(source?.asArg()), sound))

fun Function.stopSoundAllSources(
	targets: EntityArgument,
	sound: SoundArgument? = null,
) = addLine(command("stopsound", targets, all(), sound))

fun Function.teamMsg(message: String) = addLine(command("teammsg", literal(message)))
fun Function.tm(message: String) = teamMsg(message)
