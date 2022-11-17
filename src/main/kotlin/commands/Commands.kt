package commands

import arguments.*
import functions.Function
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonPrimitive
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
	fun clear(effect: String? = null) = fn.addLine(command("effect", fn.literal("clear"), target, fn.literal(effect)))
	fun give(effect: String, duration: Int? = null, amplifier: Int? = null, hideParticles: Boolean? = null) = fn.addLine(
		command(
			"effect", fn.literal("give"), target, fn.literal(effect), fn.int(duration), fn.int(amplifier), fn.bool(hideParticles)
		)
	)
}

fun Function.effect(target: Argument.Entity, block: Effect.() -> Unit) = Effect(this, target).apply(block)

fun Function.enchant(enchantment: String, level: Int? = null) = addLine(command("enchant", literal(enchantment), int(level)))

fun Function.experienceAdd(target: Argument.Entity, amount: Int, type: ExperienceType = ExperienceType.POINTS) =
	addLine(command("experience", literal("add"), target, int(amount), literal(type.asArg())))

fun Function.experienceAdd(target: Argument.Entity, amount: XpNumber) = addLine(command("experience", literal("add"), target, int(amount.value), literal(amount.typeString)))

fun Function.experienceQuery(target: Argument.Entity, type: ExperienceType = ExperienceType.POINTS) = addLine(command("experience", literal("query"), target, literal(type.asArg())))

fun Function.experienceSet(target: Argument.Entity, amount: Int, type: ExperienceType = ExperienceType.POINTS) =
	addLine(command("experience", literal("set"), target, int(amount), literal(type.asArg())))

fun Function.experienceSet(target: Argument.Entity, amount: XpNumber) = addLine(command("experience", literal("set"), target, int(amount.value), literal(amount.typeString)))

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
