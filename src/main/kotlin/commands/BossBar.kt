package commands

import arguments.Argument
import arguments.BossBarColor
import arguments.bool
import arguments.int
import arguments.literal
import functions.Function
import kotlinx.serialization.Serializable
import serializers.LowercaseSerializer

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
