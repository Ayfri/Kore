package commands

import arguments.BossBarColor
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

@Serializable(DataType.Companion.DataTypeSerializer::class)
enum class DataType {
	ENTITY,
	BLOCK,
	STORAGE;
	
	companion object {
		val values = values()
		
		object DataTypeSerializer : LowercaseSerializer<DataType>(values)
	}
}

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
	
	fun merge(from: Argument.Data, path: String) = fn.addLine(command(
		"data",
		fn.literal("merge"),
		fn.literal(target.literalName),
		target,
		fn.literal("from"),
		fn.literal(from.literalName),
		from,
		fn.literal(path)
	))
	
	fun remove(path: String) = fn.addLine(command(
		"data",
		fn.literal("remove"),
		fn.literal(target.literalName),
		target,
		fn.literal(path)
	))
}

fun Function.data(target: Argument.Data, block: Data.() -> Unit) = Data(this, target).apply(block)
