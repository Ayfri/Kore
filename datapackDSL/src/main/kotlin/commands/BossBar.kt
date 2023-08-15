package commands

import arguments.chatcomponents.ChatComponents
import arguments.chatcomponents.PlainTextComponent
import arguments.chatcomponents.textComponent
import arguments.colors.BossBarColor
import arguments.colors.Color
import arguments.types.EntityArgument
import arguments.types.literals.bool
import arguments.types.literals.int
import arguments.types.literals.literal
import arguments.types.resources.BossBarArgument
import functions.Function
import serializers.LowercaseSerializer
import utils.asArg
import kotlinx.serialization.Serializable

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

class BossBar(private val fn: Function, val id: BossBarArgument) {
	fun add(displayName: ChatComponents) = fn.addLine(command("bossbar", literal("add"), id, displayName.asJsonArg()))
	fun add(displayName: String, color: Color? = null, block: PlainTextComponent.() -> Unit = {}) =
		fn.addLine(command("bossbar", literal("add"), id, textComponent(displayName) {
			if (color != null) this.color = color
			block()
		}))

	fun getMax() = fn.addLine(command("bossbar", literal("get"), id, literal("max")))
	fun getPlayers() = fn.addLine(command("bossbar", literal("get"), id, literal("players")))
	fun getValue() = fn.addLine(command("bossbar", literal("get"), id, literal("value")))
	fun getVisible() = fn.addLine(command("bossbar", literal("get"), id, literal("visible")))

	fun remove() = fn.addLine(command("bossbar", literal("remove"), id))
	fun setColor(color: BossBarColor) = fn.addLine(command("bossbar", literal("set"), id, literal("color"), color))
	fun setMax(max: Int) = fn.addLine(command("bossbar", literal("set"), id, literal("max"), int(max)))
	fun setName(name: String) = fn.addLine(command("bossbar", literal("set"), id, literal("name"), literal(name)))
	fun setPlayers(targets: EntityArgument) = fn.addLine(command("bossbar", literal("set"), id, literal("players"), targets))
	fun setStyle(style: BossBarStyle) = fn.addLine(command("bossbar", literal("set"), id, literal("style"), literal(style.asArg())))
	fun setValue(value: Int) = fn.addLine(command("bossbar", literal("set"), id, literal("value"), int(value)))
	fun setVisible(visible: Boolean) = fn.addLine(command("bossbar", literal("set"), id, literal("visible"), bool(visible)))
}

class BossBars(private val fn: Function) {
	fun add(id: String, namespace: String = "minecraft", displayName: ChatComponents) =
		fn.addLine(command("bossbar", literal("add"), BossBarArgument(id, namespace), displayName.asJsonArg()))

	fun add(
		id: String,
		namespace: String = "minecraft",
		displayName: String,
		color: Color? = null,
		block: PlainTextComponent.() -> Unit = {},
	) = fn.addLine(command("bossbar", literal("add"), BossBarArgument(id, namespace), textComponent(displayName) {
		if (color != null) this.color = color
		block()
	}))

	fun list() = fn.addLine(command("bossbar", literal("list")))
	fun get(id: String, namespace: String = "minecraft") = BossBar(fn, BossBarArgument(id, namespace))
	fun get(argument: BossBarArgument) = BossBar(fn, argument)
}

val Function.bossBars get() = BossBars(this)
fun Function.bossBar(name: String, namespace: String = "minecraft", block: BossBar.() -> Command) =
	BossBar(this, BossBarArgument(name, namespace)).block()

fun Function.bossBar(argument: BossBarArgument, block: BossBar.() -> Command) =
	BossBar(this, argument).block()
