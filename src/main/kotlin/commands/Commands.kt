package commands

import arguments.Axes
import arguments.BossBarColor
import arguments.DataType
import arguments.Difficulty
import arguments.Dimension
import arguments.Gamemode
import arguments.RangeOrInt
import arguments.Relation
import arguments.RelationBlock
import functions.Function
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonPrimitive
import serializers.LowercaseSerializer

internal val json = Json { ignoreUnknownKeys = true }

@Serializable(AdvancementAction.Companion.AdvancementActionSerializer::class)
enum class AdvancementAction {
	GRANT,
	REVOKE;
	
	companion object {
		val values = values()
		
		object AdvancementActionSerializer : LowercaseSerializer<AdvancementAction>(values)
	}
}

@Serializable(AdvancementRoute.Companion.AdvancementRouteSerializer::class)
enum class AdvancementRoute {
	ONLY,
	THROUGH,
	UNTIL;
	
	companion object {
		val values = values()
		
		object AdvancementRouteSerializer : LowercaseSerializer<AdvancementRoute>(values)
	}
}

private inline fun <reified T : @Serializable Any> T.asArg() = json.encodeToJsonElement(this).jsonPrimitive.content

fun Function.advancement(action: AdvancementAction, targets: Argument.Selector, advancement: Argument.Advancement, criterion: String = "") =
	addLine(command("advancement", literal(action.asArg()), targets, literal("only"), advancement, literal(criterion)))

fun Function.advancement(action: AdvancementAction, targets: Argument.Selector, route: AdvancementRoute, advancement: Argument.Advancement, criterion: String = "") =
	addLine(command("advancement", literal(action.asArg()), targets, literal(route.asArg()), advancement, literal(criterion)))

fun Function.advancement(action: AdvancementAction, targets: Argument.Selector) = addLine(command("advancement", literal(action.asArg()), targets, literal("everything")))

fun Function.attribute(targets: Argument.Selector, attribute: Argument.Attribute, base: Boolean = false) =
	addLine(command("attribute", targets, attribute, if (base) literal("base get") else literal("get")))

fun Function.attribute(targets: Argument.Selector, attribute: Argument.Attribute, amount: Argument.Float) = addLine(command("attribute", targets, attribute, literal("base set"), amount))

@Serializable(BossBarGetResult.Companion.BossBarActionSerializer::class)
enum class BossBarGetResult {
	MAX,
	PLAYERS,
	VALUE,
	VISIBLE;
	
	companion object {
		val values = values()
		
		object BossBarActionSerializer : LowercaseSerializer<BossBarGetResult>(values)
	}
}

@Serializable(BossBarStyle.Companion.BossBarStyleSerializer::class)
enum class BossBarStyle {
	NOTCHED_6,
	NOTCHED_10,
	NOTCHED_12,
	NOTCHED_20,
	PROGRESS;
	
	companion object {
		val values = values()
		
		object BossBarStyleSerializer : LowercaseSerializer<BossBarStyle>(values)
	}
}

class BossBar(private val fn: Function, val id: String) {
	fun add(name: String) = fn.addLine(command("bossbar", fn.literal("add"), fn.literal(id), fn.literal(name)))
	fun get(result: BossBarGetResult) = fn.addLine(command("bossbar", fn.literal("get"), fn.literal(id), fn.literal(result.asArg())))
	fun remove() = fn.addLine(command("bossbar", fn.literal("remove"), fn.literal(id)))
	fun setColor(action: BossBarColor) = fn.addLine(command("bossbar", fn.literal("set"), fn.literal(id), fn.literal(action.asArg())))
	fun setMax(max: Int) = fn.addLine(command("bossbar", fn.literal("set"), fn.literal(id), fn.literal("max"), fn.int(max)))
	fun setName(name: String) = fn.addLine(command("bossbar", fn.literal("set"), fn.literal(id), fn.literal("name"), fn.literal(name)))
	fun setPlayers(targets: Argument.Selector) = fn.addLine(command("bossbar", fn.literal("set"), fn.literal(id), fn.literal("players"), targets))
	fun setStyle(style: BossBarStyle) = fn.addLine(command("bossbar", fn.literal("set"), fn.literal(id), fn.literal("style"), fn.literal(style.asArg())))
	fun setValue(value: Int) = fn.addLine(command("bossbar", fn.literal("set"), fn.literal(id), fn.literal("value"), fn.int(value)))
	fun setVisible(visible: Boolean) = fn.addLine(command("bossbar", fn.literal("set"), fn.literal(id), fn.literal("visible"), fn.bool(visible)))
}

class BossBars(private val fn: Function) {
	fun list() = fn.addLine(command("bossbar", fn.literal("list")))
	fun get(id: String) = BossBar(fn, id)
}

fun Function.bossbar(name: String, block: BossBar.() -> Unit = {}) = BossBar(this, name).apply(block)
val Function.bossbars get() = BossBars(this)

fun Function.clear(targets: Argument.Selector? = null, item: Argument.Item? = null, maxCount: Int? = null) = addLine(command("clear", targets, item, int(maxCount)))

@Serializable(CloneMode.Companion.CloneModeSerializer::class)
enum class CloneMode {
	MASKED,
	NOT,
	ONLY;
	
	companion object {
		val values = values()
		
		object CloneModeSerializer : LowercaseSerializer<CloneMode>(values)
	}
}

fun Function.clone(begin: Argument.Coordinate, end: Argument.Coordinate, destination: Argument.Coordinate) = addLine(command("clone", begin, end, destination))
fun Function.cloneFiltered(begin: Argument.Coordinate, end: Argument.Coordinate, destination: Argument.Coordinate, filter: Argument.BlockOrTag, mode: CloneMode) =
	addLine(command("clone", begin, end, destination, literal("filtered"), filter, literal(mode.asArg())))

fun Function.cloneMasked(begin: Argument.Coordinate, end: Argument.Coordinate, destination: Argument.Coordinate, mode: CloneMode) =
	addLine(command("clone", begin, end, destination, literal("masked"), literal(mode.asArg())))

fun Function.cloneReplace(begin: Argument.Coordinate, end: Argument.Coordinate, destination: Argument.Coordinate, mode: CloneMode) =
	addLine(command("clone", begin, end, destination, literal("replace"), literal(mode.asArg())))

class DataModifyOperation(private val fn: Function) {
	fun append(from: Argument.Data, path: String) = listOf(fn.literal("append"), fn.literal("from"), fn.literal(from.literalName), from, fn.literal("path"), fn.literal(path))
	fun append(value: Argument) = listOf(fn.literal("append"), fn.literal("value"), value)
	
	fun insert(index: Int, from: Argument.Data, path: String) = listOf(fn.literal("insert"), fn.int(index), fn.literal("from"), fn.literal(from.literalName), from, fn.literal(path))
	fun insert(index: Int, value: Argument) = listOf(fn.literal("insert"), fn.int(index), fn.literal("value"), value)
	
	fun merge(from: Argument.Data, path: String) = listOf(fn.literal("merge"), fn.literal("from"), fn.literal(from.literalName), from, fn.literal("path"), fn.literal(path))
	fun merge(value: Argument) = listOf(fn.literal("merge"), fn.literal("value"), value)
	
	fun prepend(from: Argument.Data, path: String) = listOf(fn.literal("prepend"), fn.literal("from"), fn.literal(from.literalName), from, fn.literal("path"), fn.literal(path))
	fun prepend(value: Argument) = listOf(fn.literal("prepend"), fn.literal("value"), value)
	
	fun set(from: Argument.Data, path: String) = listOf(fn.literal("set"), fn.literal("from"), fn.literal(from.literalName), from, fn.literal("path"), fn.literal(path))
	fun set(value: Argument) = listOf(fn.literal("set"), fn.literal("value"), value)
	
	fun remove(path: String) = listOf(fn.literal("remove"), fn.literal(path))
}

class Data(private val fn: Function, val target: Argument.Data) {
	fun get(path: String, scale: Double? = null) = fn.addLine(
		command(
			"data",
			fn.literal("get"),
			fn.literal(target.literalName),
			target,
			fn.literal(path),
			fn.float(scale)
		)
	)
	
	fun modify(path: String, value: DataModifyOperation.() -> List<Argument>) = fn.addLine(
		command(
			"data",
			fn.literal("modify"),
			fn.literal(target.literalName),
			target,
			fn.literal(path),
			*DataModifyOperation(fn).value().toTypedArray()
		)
	)
	
	fun merge(from: Argument.Data, path: String) = fn.addLine(
		command(
			"data",
			fn.literal("merge"),
			fn.literal(target.literalName),
			target,
			fn.literal("from"),
			fn.literal(from.literalName),
			from,
			fn.literal(path)
		)
	)
	
	fun remove(path: String) = fn.addLine(
		command(
			"data",
			fn.literal("remove"),
			fn.literal(target.literalName),
			target,
			fn.literal(path)
		)
	)
}

fun Function.data(target: Argument.Data, block: Data.() -> Unit) = Data(this, target).apply(block)

@Serializable(DatapackPriority.Companion.DatapackPrioritySerializer::class)
enum class DatapackPriority {
	FIRST,
	LAST;
	
	companion object {
		val values = values()
		
		object DatapackPrioritySerializer : LowercaseSerializer<DatapackPriority>(values)
	}
}

class Datapack(private val fn: Function, val name: String) {
	fun disable() = fn.addLine(command("datapack", fn.literal("disable"), fn.literal(name)))
	fun enable(priority: DatapackPriority? = null) = fn.addLine(command("datapack", fn.literal("enable"), fn.literal(name), fn.literal(priority?.asArg())))
	fun enableFirst() = fn.addLine(command("datapack", fn.literal("enable"), fn.literal("first"), fn.literal(name)))
	fun enableLast() = fn.addLine(command("datapack", fn.literal("enable"), fn.literal("last"), fn.literal(name)))
	fun enableBefore(name: String) = fn.addLine(command("datapack", fn.literal("enable"), fn.literal("before"), fn.literal(name), fn.literal(name)))
	fun enableAfter(name: String) = fn.addLine(command("datapack", fn.literal("enable"), fn.literal("after"), fn.literal(name), fn.literal(name)))
}

class Datapacks(private val fn: Function) {
	fun available() = fn.addLine(command("datapack", fn.literal("available")))
	fun enabled() = fn.addLine(command("datapack", fn.literal("enabled")))
	fun list() = fn.addLine(command("datapack", fn.literal("list")))
}

fun Function.datapack(name: String, block: Datapack.() -> Unit) = Datapack(this, name).apply(block)
val Function.datapacks get() = Datapacks(this)

fun Function.debugStart() = addLine(command("debug", literal("start")))
fun Function.debugStop() = addLine(command("debug", literal("stop")))

fun Function.defaultGamemode(mode: Gamemode) = addLine(command("defaultgamemode", literal(mode.asArg())))

fun Function.difficulty(difficulty: Difficulty? = null) = addLine(command("difficulty", literal(difficulty?.asArg())))

class Effect(private val fn: Function, val target: Argument.Entity) {
	fun clear(effect: String? = null) = fn.addLine(command("effect", fn.literal("clear"), target, fn.literal(effect)))
	fun give(effect: String, duration: Int? = null, amplifier: Int? = null, hideParticles: Boolean? = null) = fn.addLine(
		command(
			"effect",
			fn.literal("give"),
			target,
			fn.literal(effect),
			fn.int(duration),
			fn.int(amplifier),
			fn.bool(hideParticles)
		)
	)
}

fun Function.effect(target: Argument.Entity, block: Effect.() -> Unit) = Effect(this, target).apply(block)

fun Function.enchant(enchantment: String, level: Int? = null) = addLine(command("enchant", literal(enchantment), int(level)))

@Serializable(Anchor.Companion.AnchorSerializer::class)
enum class Anchor {
	FEET,
	EYES;
	
	companion object {
		val values = values()
		
		object AnchorSerializer : LowercaseSerializer<Anchor>(values)
	}
}

@Serializable(BlocksTestMode.Companion.FillModeSerializer::class)
enum class BlocksTestMode {
	ALL,
	MASKED;
	
	companion object {
		val values = values()
		
		object FillModeSerializer : LowercaseSerializer<BlocksTestMode>(values)
	}
}

class ExecuteCondition(private val fn: Function) {
	fun block(pos: Argument.Coordinate, block: Argument.Block) = listOf(fn.literal("block"), pos, block)
	
	fun blocks(start: Argument.Coordinate, end: Argument.Coordinate, destination: Argument.Coordinate, mode: BlocksTestMode) = listOf(
		fn.literal("blocks"),
		start,
		end,
		destination,
		fn.literal(mode.asArg())
	)
	
	fun data(target: Argument.Data, path: String) = listOf(
		fn.literal("data"),
		fn.literal(target.literalName),
		target,
		fn.literal(path)
	)
	
	fun entity(target: Argument.Entity) = listOf(fn.literal("entity"), target)
	
	fun predicate(predicate: String) = listOf(fn.literal("predicate"), fn.literal(predicate))
	
	fun score(target: Argument.ScoreHolder, objective: String, source: Argument.ScoreHolder, sourceObjective: String, relation: RelationBlock.(Number, Number) -> Relation) = listOf(
		fn.literal("score"),
		target,
		fn.literal(objective),
		fn.literal(relation(RelationBlock(), 0.0, 0.0).asArg()),
		source,
		fn.literal(sourceObjective)
	)
	
	fun score(target: Argument.ScoreHolder, objective: String, range: RangeOrInt) = listOf(
		fn.literal("score"),
		target,
		fn.literal(objective),
		fn.literal(range.asArg())
	)
}

class ExecuteStore(private val fn: Function) {
	fun block(pos: Argument.Coordinate, path: String, type: DataType, scale: Double) =
		listOf(fn.literal("store"), fn.literal("block"), pos, fn.literal(path), fn.literal(type.asArg()), fn.float(scale))
	
	fun bossbarMax(id: String) = listOf(fn.literal("bossbar"), fn.literal(id), fn.literal("max"))
	fun bossbarValue(id: String) = listOf(fn.literal("bossbar"), fn.literal(id), fn.literal("value"))
	
	fun entity(target: Argument.Entity, path: String, type: DataType, scale: Double) = listOf(fn.literal("entity"), target, fn.literal(path), fn.literal(type.asArg()), fn.float(scale))
	
	fun score(target: Argument.ScoreHolder, objective: String) = listOf(fn.literal("score"), target, fn.literal(objective))
	
	fun storage(target: Argument.Storage, path: String, type: DataType, scale: Double) = listOf(fn.literal("storage"), target, fn.literal(path), fn.literal(type.asArg()), fn.float(scale))
}

class Execute(private val fn: Function) {
	private val array = mutableListOf<Argument>()
	private var command: Command? = null
	
	private fun <T> MutableList<T>.addAll(vararg args: T?) = addAll(args.filterNotNull())
	
	fun getFinalCommand() = (array + fn.literal("run") + fn.literal(command?.toString())).toTypedArray()
	
	fun align(axis: Axes, offset: Int? = null) = array.addAll(fn.literal("align"), fn.literal(axis.asArg()), fn.int(offset))
	fun anchored(anchor: Anchor) = array.addAll(fn.literal("anchored"), fn.literal(anchor.asArg()))
	fun asTarget(target: Argument.Entity) = array.addAll(fn.literal("as"), target)
	fun at(target: Argument.Entity) = array.addAll(fn.literal("at"), target)
	fun facing(target: Argument.Entity, anchor: Anchor? = null) = array.addAll(fn.literal("facing"), target, fn.literal(anchor?.asArg()))
	fun facingBlock(pos: Argument.Coordinate, anchor: Anchor? = null) = array.addAll(fn.literal("facing"), fn.literal("block"), pos, fn.literal(anchor?.asArg()))
	fun facingEntity(target: Argument.Entity, anchor: Anchor? = null) =
		array.addAll(fn.literal("facing"), target, fn.literal("entity"), target, fn.literal(anchor?.asArg()))
	
	fun facingPos(pos: Argument.Coordinate, anchor: Anchor? = null) = array.addAll(fn.literal("facing"), fn.literal("position"), pos, fn.literal(anchor?.asArg()))
	fun inDimension(dimension: Dimension) = array.addAll(fn.literal("in"), fn.dimension(dimension))
	fun inDimension(customDimension: String, namespace: String? = null) = array.addAll(fn.literal("in"), fn.dimension(customDimension, namespace))
	fun positioned(pos: Argument.Coordinate) = array.addAll(fn.literal("positioned"), pos)
	fun positionedAs(target: Argument.Entity) = array.addAll(fn.literal("positioned"), target, fn.literal("as"), target)
	fun rotated(rotation: Argument.Rotation) = array.addAll(fn.literal("rotated"), rotation)
	fun rotatedAs(target: Argument.Entity) = array.addAll(fn.literal("rotated"), target, fn.literal("as"), target)
	
	fun ifCondition(block: ExecuteCondition.() -> List<Argument>) = array.addAll(fn.literal("if"), *ExecuteCondition(fn).block().toTypedArray())
	fun unlessCondition(block: ExecuteCondition.() -> List<Argument>) = array.addAll(fn.literal("unless"), *ExecuteCondition(fn).block().toTypedArray())
	
	fun storeResult(block: ExecuteStore.() -> List<Argument>) = array.addAll(fn.literal("store"), fn.literal("result"), *ExecuteStore(fn).block().toTypedArray())
	fun storeValue(block: ExecuteStore.() -> List<Argument>) = array.addAll(fn.literal("store"), fn.literal("value"), *ExecuteStore(fn).block().toTypedArray())
	
	fun run(block: Function.() -> Command) {
		val function = Function.EMPTY
		command = function.block()
		function.clear()
	}
}

fun Function.execute(block: Execute.() -> Unit) = addLine(command("execute", *Execute(this).apply(block).getFinalCommand()))
