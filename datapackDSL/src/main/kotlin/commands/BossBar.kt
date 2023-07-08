package commands

import arguments.*
import arguments.colors.BossBarColor
import functions.Function
import kotlinx.serialization.Serializable
import serializers.LowercaseSerializer
import utils.asArg

@Serializable(BossBarGetResult.Companion.BossBarActionSerializer::class)
enum class BossBarGetResult {
	MAX,
	PLAYERS,
	VALUE,
	VISIBLE;

	companion object {
		data object BossBarActionSerializer : LowercaseSerializer<BossBarGetResult>(entries)
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
		data object BossBarStyleSerializer : LowercaseSerializer<BossBarStyle>(entries)
	}
}

class BossBar(private val fn: Function, val id: Argument.BossBar) {
	fun add(name: ChatComponents) = fn.addLine(command("bossbar", literal("add"), id, name.asJsonArg()))
	fun get(result: BossBarGetResult) = fn.addLine(command("bossbar", literal("get"), id, literal(result.asArg())))
	fun remove() = fn.addLine(command("bossbar", literal("remove"), id))
	fun setColor(color: BossBarColor) = fn.addLine(command("bossbar", literal("set"), id, literal("color"), color))
	fun setMax(max: Int) = fn.addLine(command("bossbar", literal("set"), id, literal("max"), int(max)))
	fun setName(name: String) = fn.addLine(command("bossbar", literal("set"), id, literal("name"), literal(name)))
	fun setPlayers(targets: Argument.Entity) = fn.addLine(command("bossbar", literal("set"), id, literal("players"), targets))
	fun setStyle(style: BossBarStyle) = fn.addLine(command("bossbar", literal("set"), id, literal("style"), literal(style.asArg())))
	fun setValue(value: Int) = fn.addLine(command("bossbar", literal("set"), id, literal("value"), int(value)))
	fun setVisible(visible: Boolean) = fn.addLine(command("bossbar", literal("set"), id, literal("visible"), bool(visible)))
}

class BossBars(private val fn: Function) {
	fun list() = fn.addLine(command("bossbar", literal("list")))
	fun get(id: String, namespace: String = "minecraft") = BossBar(fn, bossBar(id, namespace))
}

val Function.bossBars get() = BossBars(this)
fun Function.bossBar(name: String, namespace: String = "minecraft", block: BossBar.() -> Command) =
	BossBar(this, bossBar(name, namespace)).block()
