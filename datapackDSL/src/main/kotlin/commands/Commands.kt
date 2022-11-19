package commands

import arguments.*
import functions.Function
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonPrimitive
import net.benwoodworth.knbt.NbtCompound
import net.benwoodworth.knbt.NbtTag
import serializers.LowercaseSerializer

internal val json = Json { ignoreUnknownKeys = true }
internal inline fun <reified T : @Serializable Any> T.asArg() = json.encodeToJsonElement(this).jsonPrimitive.content

fun Function.attribute(targets: Argument.Selector, attribute: Argument.Attribute, base: Boolean = false) =
	addLine(command("attribute", targets, attribute, if (base) literal("base get") else literal("get")))

fun Function.attribute(targets: Argument.Selector, attribute: Argument.Attribute, amount: Argument.Float) = addLine(command("attribute", targets, attribute, literal("base set"), amount))

fun Function.clear(targets: Argument.Selector? = null, item: Argument.Item? = null, maxCount: Int? = null) = addLine(command("clear", targets, item, int(maxCount)))

fun Function.debugStart() = addLine(command("debug", literal("start")))
fun Function.debugStop() = addLine(command("debug", literal("stop")))

fun Function.defaultGamemode(mode: Gamemode) = addLine(command("defaultgamemode", literal(mode.asArg())))

fun Function.difficulty(difficulty: Difficulty? = null) = addLine(command("difficulty", literal(difficulty?.asArg())))

class Effect(private val fn: Function, val target: Argument.Entity) {
	fun clear(effect: String? = null) = fn.addLine(command("effect", literal("clear"), target, literal(effect)))
	fun give(effect: String, duration: Int? = null, amplifier: Int? = null, hideParticles: Boolean? = null) = fn.addLine(
		command(
			"effect", literal("give"), target, literal(effect), int(duration), int(amplifier), bool(hideParticles)
		)
	)
}

fun Function.effect(target: Argument.Entity, block: Effect.() -> Unit) = Effect(this, target).apply(block)

fun Function.enchant(enchantment: String, level: Int? = null) = addLine(command("enchant", literal(enchantment), int(level)))

@Serializable(FillOption.Companion.FillOptionSerializer::class)
enum class FillOption {
	DESTROY,
	HOLLOW,
	KEEP,
	OUTLINE;
	
	companion object {
		val values = values()
		
		object FillOptionSerializer : LowercaseSerializer<FillOption>(values)
	}
}

fun Function.fill(from: Argument.Coordinate, to: Argument.Coordinate, block: Argument.Block, fillOption: FillOption? = null) = addLine(command("fill", from, to, block, literal(fillOption?.asArg())))
fun Function.fill(from: Argument.Coordinate, to: Argument.Coordinate, block: Argument.Block, filter: Argument.Block) = addLine(command("fill", from, to, block, literal("replace"), filter))

fun Function.function(name: String, group: Boolean = false) = addLine(command("function", tag(name, group)))
fun Function.function(namespace: String, name: String, group: Boolean = false) = addLine(command("function", tag(name, namespace, group)))

fun Function.gamemode(gamemode: Gamemode, target: Argument.Entity? = null) = addLine(command("gamemode", literal(gamemode.asArg()), target))

fun Function.gamerule(rule: String, value: Boolean? = null) = addLine(command("gamerule", literal(rule), bool(value)))
fun Function.gamerule(rule: String, value: Int) = addLine(command("gamerule", literal(rule), int(value)))

fun Function.give(target: Argument.Entity, item: Argument.Item, count: Int? = null) = addLine(command("give", target, item, int(count)))

fun Function.help(command: String? = null) = addLine(command("help", literal(command)))

fun Function.jfrStart() = addLine(command("jfr", literal("start")))
fun Function.jfrStop() = addLine(command("jfr", literal("stop")))

fun Function.kill(targets: Argument.Selector? = null) = addLine(command("kill", targets))

fun Function.list(uuids: Boolean = false) = addLine(command("list", literal(if (uuids) "uuids" else null)))

fun Function.locateStructure(structure: String) = addLine(command("locate", literal("structure"), literal(structure)))
fun Function.locateBiome(biome: String) = addLine(command("locate", literal("biome"), literal(biome)))
fun Function.locatePointOfInterest(pointOfInterest: String) = addLine(command("locate", literal("poi"), literal(pointOfInterest)))

fun Function.me(message: String) = addLine(command("me", literal(message)))

fun Function.msg(target: Argument.Entity, message: String) = addLine(command("msg", target, literal(message)))

fun Function.particule(name: String, pos: Argument.Coordinate? = null) = addLine(command("particle", literal(name), pos))

@Serializable(ParticleMode.Companion.ParticleModeSerializer::class)
enum class ParticleMode {
	NORMAL,
	FORCE;
	
	companion object {
		val values = values()
		
		object ParticleModeSerializer : LowercaseSerializer<ParticleMode>(values)
	}
}

fun Function.particule(
	name: String,
	pos: Argument.Coordinate,
	delta: Argument.Coordinate,
	speed: Double,
	count: Int,
	mode: ParticleMode,
	viewers: Argument.Entity? = null,
) = addLine(command("particle", literal(name), pos, delta, float(speed), int(count), literal(mode.asArg()), viewers))

fun Function.perfStart() = addLine(command("perf", literal("start")))
fun Function.perfStop() = addLine(command("perf", literal("stop")))

fun Function.placeFeature(feature: String, pos: Argument.Coordinate) = addLine(command("place", literal("feature"), literal(feature), pos))
fun Function.placeJigsaw(
	jigsaw: String,
	target: String,
	maxDepth: Int,
	pos: Argument.Coordinate? = null,
) = addLine(command("place", literal("jigsaw"), literal(jigsaw), literal(target), int(maxDepth), pos))

fun Function.placeStructure(structure: String, pos: Argument.Coordinate) = addLine(command("place", literal("structure"), literal(structure), pos))
fun Function.placeTemplate(
	template: String,
	pos: Argument.Coordinate? = null,
	rotation: Argument.Rotation? = null,
	mirror: Boolean? = null,
	seed: Long? = null,
) = addLine(command("place", literal("template"), literal(template), pos, rotation, literal(mirror?.asArg()), int(seed)))

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
		val values = values()
		
		object PlaySoundSourceSerializer : LowercaseSerializer<PlaySoundSource>(values)
	}
}

fun Function.playSound(
	sound: String,
	source: PlaySoundSource,
	target: Argument.Entity,
	pos: Argument.Coordinate? = null,
	volume: Double? = null,
	pitch: Double? = null,
	minVolume: Double? = null,
) = addLine(command("playsound", literal(sound), literal(source.asArg()), target, pos, float(volume), float(pitch), float(minVolume)))

fun Function.publish() = addLine(command("publish"))

fun Function.recipeGive(target: Argument.Entity, recipe: String) = addLine(command("recipe", literal("give"), target, literal(recipe)))
fun Function.recipeGiveAll(target: Argument.Entity) = addLine(command("recipe", literal("give"), target, literal("*")))
fun Function.recipeTake(target: Argument.Entity, recipe: String) = addLine(command("recipe", literal("take"), target, literal(recipe)))
fun Function.recipeTakeAll(target: Argument.Entity) = addLine(command("recipe", literal("take"), target, literal("*")))

fun Function.say(message: String) = addLine(command("say", literal(message)))

fun Function.seed() = addLine(command("seed"))

@Serializable(SetBlockMode.Companion.SetBlockModeSerializer::class)
enum class SetBlockMode {
	REPLACE,
	DESTROY,
	KEEP;
	
	companion object {
		val values = values()
		
		object SetBlockModeSerializer : LowercaseSerializer<SetBlockMode>(values)
	}
}

fun Function.setBlock(pos: Argument.Coordinate, block: Argument.Block, mode: SetBlockMode? = null) = addLine(command("setblock", pos, block, literal(mode?.asArg())))

fun Function.setWorldSpawn(pos: Argument.Coordinate? = null, angle: Argument.Rotation) = addLine(command("setworldspawn", pos, angle))

fun Function.spawnPoint(target: Argument.Entity? = null, pos: Argument.Coordinate? = null, angle: Argument.Rotation? = null) = addLine(command("spawnpoint", target, pos, angle))

fun Function.spectate(target: Argument.Entity, player: Argument.Entity? = null) = addLine(command("spectate", target, player))
fun Function.spectate() = addLine(command("spectate"))

fun Function.spreadPlayers(
	center: Argument.Coordinate,
	spreadDistance: Double,
	maxRange: Double,
	respectTeams: Boolean,
	targets: Argument.Selector,
) = addLine(command("spreadplayers", center, float(spreadDistance), float(maxRange), bool(respectTeams), targets))

fun Function.spreadPlayers(
	center: Argument.Coordinate,
	spreadDistance: Double,
	maxRange: Double,
	maxHeight: Int,
	respectTeams: Boolean,
	targets: Argument.Selector,
) = addLine(command("spreadplayers", center, float(spreadDistance), float(maxRange), int(maxHeight), bool(respectTeams), targets))

fun Function.stopSound(
	targets: Argument.Entity,
	source: PlaySoundSource? = null,
	sound: String? = null,
) = addLine(command("stopsound", targets, literal(source?.asArg()), literal(sound)))

fun Function.summon(entity: Argument.Entity, pos: Argument.Coordinate? = null, nbt: NbtCompound) = addLine(command("summon", entity, pos, nbt(nbt)))

fun Function.teamMsg(message: String) = addLine(command("teammsg", literal(message)))

fun Function.teleport(destination: Argument.Entity) = addLine(command("teleport", destination))
fun Function.teleport(target: Argument.Entity, destination: Argument.Entity) = addLine(command("teleport", target, destination))
fun Function.teleport(location: Argument.Coordinate) = addLine(command("teleport", location))
fun Function.teleport(target: Argument.Entity, location: Argument.Coordinate, rotation: Argument.Rotation? = null) = addLine(command("teleport", target, location, rotation))
fun Function.teleport(target: Argument.Entity, location: Argument.Coordinate, facing: Argument.Coordinate) = addLine(command("teleport", target, location, literal("facing"), facing))
fun Function.teleport(
	target: Argument.Entity,
	location: Argument.Coordinate,
	facing: Argument.Entity,
	facingAnchor: Anchor,
) = addLine(command("teleport", target, location, literal("facing"), facing, literal(facingAnchor.asArg())))

fun Function.tell(targets: Argument.Entity, message: String) = addLine(command("tell", targets, literal(message)))

fun Function.tellraw(targets: Argument.Entity, message: NbtTag) = addLine(command("tellraw", targets, nbt(message)))

@Serializable(TitleAction.Companion.TitleActionSerializer::class)
enum class TitleAction {
	CLEAR,
	RESET;
	
	companion object {
		val values = values()
		
		object TitleActionSerializer : LowercaseSerializer<TitleAction>(values)
	}
}

@Serializable(TitleLocation.Companion.TitleLocationSerializer::class)
enum class TitleLocation {
	TITLE,
	SUBTITLE,
	ACTIONBAR;
	
	companion object {
		val values = values()
		
		object TitleLocationSerializer : LowercaseSerializer<TitleLocation>(values)
	}
}

fun Function.title(targets: Argument.Entity, action: TitleAction) = addLine(command("title", targets, literal(action.asArg())))
fun Function.title(targets: Argument.Entity, location: TitleLocation, message: NbtTag) = addLine(command("title", targets, literal(location.asArg()), nbt(message)))
fun Function.title(targets: Argument.Entity, fadeIn: Int, stay: Int, fadeOut: Int) = addLine(command("title", targets, literal("times"), int(fadeIn), int(stay), int(fadeOut)))

fun Function.tm(message: String) = addLine(command("tm", literal(message)))

fun Function.tp(destination: Argument.Entity) = addLine(command("tp", destination))
fun Function.tp(target: Argument.Entity, destination: Argument.Entity) = addLine(command("tp", target, destination))
fun Function.tp(location: Argument.Coordinate) = addLine(command("tp", location))
fun Function.tp(target: Argument.Entity, location: Argument.Coordinate, rotation: Argument.Rotation? = null) = addLine(command("tp", target, location, rotation))
fun Function.tp(target: Argument.Entity, location: Argument.Coordinate, facing: Argument.Coordinate) = addLine(command("tp", target, location, literal("facing"), facing))
fun Function.tp(
	target: Argument.Entity,
	location: Argument.Coordinate,
	facing: Argument.Entity,
	facingAnchor: Anchor,
) = addLine(command("tp", target, location, literal("facing"), facing, literal(facingAnchor.asArg())))

fun Function.w(message: String) = addLine(command("w", literal(message)))

fun Function.weatherClear(duration: Int? = null) = addLine(command("weather", literal("clear"), int(duration)))
fun Function.weatherRain(duration: Int? = null) = addLine(command("weather", literal("rain"), int(duration)))
fun Function.weatherThunder(duration: Int? = null) = addLine(command("weather", literal("thunder"), int(duration)))
