package commands

import arguments.enums.Difficulty
import arguments.enums.Gamemode
import arguments.maths.Vec2
import arguments.maths.Vec3
import arguments.maths.vec3
import arguments.types.EntityArgument
import arguments.types.literals.*
import arguments.types.resources.BlockArgument
import arguments.types.resources.EnchantmentArgument
import arguments.types.resources.EntityTypeArgument
import arguments.types.resources.ItemArgument
import arguments.types.resources.worldgen.BiomeArgument
import commands.execute.Anchor
import functions.Function
import net.benwoodworth.knbt.NbtCompound
import net.benwoodworth.knbt.NbtCompoundBuilder
import serializers.LowercaseSerializer
import utils.asArg
import utils.nbt
import utils.nbtArg
import kotlinx.serialization.Serializable

fun Function.clear(targets: EntityArgument? = null, item: ItemArgument? = null, maxCount: Int? = null) =
	addLine(command("clear", targets, item, int(maxCount)))

fun Function.debugStart() = addLine(command("debug", literal("start")))
fun Function.debugStop() = addLine(command("debug", literal("stop")))

fun Function.defaultGamemode(mode: Gamemode) = addLine(command("defaultgamemode", literal(mode.asArg())))

fun Function.difficulty(difficulty: Difficulty? = null) = addLine(command("difficulty", literal(difficulty?.asArg())))

fun Function.enchant(target: EntityArgument, enchantment: EnchantmentArgument, level: Int? = null) =
	addLine(command("enchant", target, enchantment, int(level)))

@Serializable(FillOption.Companion.FillOptionSerializer::class)
enum class FillOption {
	DESTROY,
	HOLLOW,
	KEEP,
	OUTLINE;

	companion object {
		data object FillOptionSerializer : LowercaseSerializer<FillOption>(entries)
	}
}

fun Function.fill(from: Vec3, to: Vec3, block: BlockArgument, fillOption: FillOption? = null) =
	addLine(command("fill", from, to, block, literal(fillOption?.asArg())))

fun Function.fill(from: Vec3, to: Vec3, block: BlockArgument, filter: BlockArgument) =
	addLine(command("fill", from, to, block, literal("replace"), filter))

fun Function.fillbiome(from: Vec3, to: Vec3, biome: BiomeArgument) = addLine(command("fillbiome", from, to, biome))
fun Function.fillbiome(from: Vec3, to: Vec3, biome: BiomeArgument, filter: BiomeArgument) =
	addLine(command("fillbiome", from, to, biome, literal("replace"), filter))

fun Function.gamemode(gamemode: Gamemode, target: EntityArgument? = null) =
	addLine(command("gamemode", literal(gamemode.asArg()), target))

fun Function.give(target: EntityArgument, item: ItemArgument, count: Int? = null) =
	addLine(command("give", target, item, int(count)))

fun Function.help(command: String? = null) = addLine(command("help", literal(command)))

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

fun Function.perf(block: Function.() -> Unit) {
	perfStart()
	block()
	perfStop()
}

fun Function.perfStart() = addLine(command("perf", literal("start")))
fun Function.perfStop() = addLine(command("perf", literal("stop")))

@Serializable(PlaySoundSource.Companion.PlaySoundSourceSerializer::class)
enum class PlaySoundSource {
	MASTER,
	MUSIC,
	RECORD,
	WEATHER,
	BLOCK,
	HOSTILE,
	NEUTRAL,
	PLAYER,
	AMBIENT,
	VOICE;

	companion object {
		data object PlaySoundSourceSerializer : LowercaseSerializer<PlaySoundSource>(entries)
	}
}

fun Function.playSound(
	sound: String,
	source: PlaySoundSource,
	target: EntityArgument,
	pos: Vec3? = null,
	volume: Double? = null,
	pitch: Double? = null,
	minVolume: Double? = null,
) = addLine(command("playsound", literal(sound), literal(source.asArg()), target, pos, float(volume), float(pitch), float(minVolume)))

fun Function.say(message: String) = addLine(command("say", literal(message)))

fun Function.seed() = addLine(command("seed"))

@Serializable(SetBlockMode.Companion.SetBlockModeSerializer::class)
enum class SetBlockMode {
	REPLACE,
	DESTROY,
	KEEP;

	companion object {
		data object SetBlockModeSerializer : LowercaseSerializer<SetBlockMode>(entries)
	}
}

fun Function.setBlock(pos: Vec3, block: BlockArgument, mode: SetBlockMode? = null) =
	addLine(command("setblock", pos, block, literal(mode?.asArg())))

fun Function.setWorldSpawn(pos: Vec3? = null, angle: RotationArgument) = addLine(command("setworldspawn", pos, angle))

fun Function.spawnPoint(target: EntityArgument? = null, pos: Vec3? = null, angle: RotationArgument? = null) =
	addLine(command("spawnpoint", target, pos, angle))

fun Function.spectate(target: EntityArgument, player: EntityArgument? = null) =
	addLine(command("spectate", target, player))

fun Function.spectate() = addLine(command("spectate"))

fun Function.spreadPlayers(
	center: Vec2,
	spreadDistance: Double,
	maxRange: Double,
	respectTeams: Boolean,
	targets: EntityArgument,
) = addLine(command("spreadplayers", center, float(spreadDistance), float(maxRange), bool(respectTeams), targets))

fun Function.spreadPlayers(
	center: Vec2,
	spreadDistance: Double,
	maxRange: Double,
	maxHeight: Int,
	respectTeams: Boolean,
	targets: EntityArgument,
) = addLine(command("spreadplayers", center, float(spreadDistance), float(maxRange), int(maxHeight), bool(respectTeams), targets))

fun Function.stopSound(
	targets: EntityArgument,
	source: PlaySoundSource? = null,
	sound: String? = null,
) = addLine(command("stopsound", targets, literal(source?.asArg()), literal(sound)))

fun Function.summon(entity: String, pos: Vec3? = null, nbt: NbtCompound?) = addLine(command("summon", literal(entity), pos, nbt(nbt)))
fun Function.summon(entity: String, pos: Vec3? = null, nbt: (NbtCompoundBuilder.() -> Unit)? = null) =
	addLine(command("summon", literal(entity), pos, nbt?.let { nbtArg(nbt) }))

fun Function.summon(entity: EntityTypeArgument, pos: Vec3, nbt: NbtCompound?) = summon(entity.asArg(), pos, nbt)
fun Function.summon(entity: EntityTypeArgument, pos: Vec3 = vec3(), nbt: (NbtCompoundBuilder.() -> Unit)? = null) =
	addLine(command("summon", entity, pos, nbt?.let { nbtArg(nbt) }))

fun Function.teamMsg(message: String) = addLine(command("teammsg", literal(message)))

fun Function.teleport(destination: EntityArgument) = addLine(command("teleport", destination))
fun Function.teleport(target: EntityArgument, destination: EntityArgument) =
	addLine(command("teleport", target, destination))

fun Function.teleport(location: Vec3) = addLine(command("teleport", location))
fun Function.teleport(target: EntityArgument, location: Vec3, rotation: RotationArgument? = null) =
	addLine(command("teleport", target, location, rotation))

fun Function.teleport(target: EntityArgument, location: Vec3, facing: Vec3) =
	addLine(command("teleport", target, location, literal("facing"), facing))

fun Function.teleport(
	target: EntityArgument,
	location: Vec3,
	facing: EntityArgument,
	facingAnchor: Anchor,
) = addLine(command("teleport", target, location, literal("facing"), facing, literal(facingAnchor.asArg())))

fun Function.tell(targets: EntityArgument, message: String) = addLine(command("tell", targets, literal(message)))

fun Function.tm(message: String) = addLine(command("tm", literal(message)))

fun Function.tp(destination: EntityArgument) = addLine(command("tp", destination))
fun Function.tp(target: EntityArgument, destination: EntityArgument) = addLine(command("tp", target, destination))
fun Function.tp(location: Vec3) = addLine(command("tp", location))
fun Function.tp(target: EntityArgument, location: Vec3, rotation: RotationArgument? = null) =
	addLine(command("tp", target, location, rotation))

fun Function.tp(target: EntityArgument, location: Vec3, facing: Vec3) =
	addLine(command("tp", target, location, literal("facing"), facing))

fun Function.tp(
	target: EntityArgument,
	location: Vec3,
	facing: EntityArgument,
	facingAnchor: Anchor,
) = addLine(command("tp", target, location, literal("facing"), facing, literal(facingAnchor.asArg())))

fun Function.w(message: String) = addLine(command("w", literal(message)))
