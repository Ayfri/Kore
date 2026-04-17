package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.PlainTextComponent
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.BossBarColor
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.types.EntityArgument
import io.github.ayfri.kore.arguments.types.literals.bool
import io.github.ayfri.kore.arguments.types.literals.int
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.arguments.types.resources.BossBarArgument
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.serializers.LowercaseSerializer
import io.github.ayfri.kore.utils.asArg
import kotlinx.serialization.Serializable

/** Result slot queried by `bossbar get`. */
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

/** Visual style used by `bossbar set <id> style`. */
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

/**
 * DSL scope for manipulating an existing boss bar referenced by [id].
 *
 * See the [Minecraft wiki](https://minecraft.wiki/w/Commands/bossbar) for the full grammar.
 */
class BossBar(private val fn: Function, val id: BossBarArgument) {
	/** Creates the boss bar with [displayName] as its title component. */
	fun add(displayName: ChatComponents) = fn.addLine(command("bossbar", literal("add"), id, displayName.asJsonArg()))

	/** Creates the boss bar with a plain text [displayName]; apply extra styling in [block]. */
	fun add(displayName: String, color: Color? = null, block: PlainTextComponent.() -> Unit = {}) =
		fn.addLine(command("bossbar", literal("add"), id, textComponent(displayName) {
			if (color != null) this.color = color
			block()
		}))

	/** Queries the maximum value of the boss bar. */
	fun getMax() = fn.addLine(command("bossbar", literal("get"), id, literal("max")))

	/** Queries the players currently seeing the boss bar. */
	fun getPlayers() = fn.addLine(command("bossbar", literal("get"), id, literal("players")))

	/** Queries the current value of the boss bar. */
	fun getValue() = fn.addLine(command("bossbar", literal("get"), id, literal("value")))

	/** Queries whether the boss bar is visible. */
	fun getVisible() = fn.addLine(command("bossbar", literal("get"), id, literal("visible")))

	/** Deletes the boss bar. */
	fun remove() = fn.addLine(command("bossbar", literal("remove"), id))

	/** Sets the color of the boss bar. */
	fun setColor(color: BossBarColor) = fn.addLine(command("bossbar", literal("set"), id, literal("color"), color))

	/** Sets the maximum value (range upper bound). */
	fun setMax(max: Int) = fn.addLine(command("bossbar", literal("set"), id, literal("max"), int(max)))

	/** Sets the display name as a chat component. */
	fun setName(name: ChatComponents) =
		fn.addLine(command("bossbar", literal("set"), id, literal("name"), name.asJsonArg()))

	/** Sets the display name as plain text; apply extra styling in [block]. */
	fun setName(name: String, color: Color? = null, block: PlainTextComponent.() -> Unit = {}) =
		fn.addLine(command("bossbar", literal("set"), id, literal("name"), textComponent(name) {
			if (color != null) this.color = color
			block()
		}.asJsonArg()))

	/** Removes every player from the boss bar (hides it). */
	fun setPlayers() = fn.addLine(command("bossbar", literal("set"), id, literal("players")))

	/** Replaces the audience with the matched [targets]. */
	fun setPlayers(targets: EntityArgument) = fn.addLine(command("bossbar", literal("set"), id, literal("players"), targets))

	/** Sets the visual [style]. */
	fun setStyle(style: BossBarStyle) = fn.addLine(command("bossbar", literal("set"), id, literal("style"), literal(style.asArg())))

	/** Sets the current value. */
	fun setValue(value: Int) = fn.addLine(command("bossbar", literal("set"), id, literal("value"), int(value)))

	/** Shows or hides the boss bar. */
	fun setVisible(visible: Boolean) = fn.addLine(command("bossbar", literal("set"), id, literal("visible"), bool(visible)))
}

/** Entry point for top-level `bossbar` subcommands that do not target an existing bar. */
class BossBars(private val fn: Function) {
	/** Creates a new boss bar identified by [namespace]:[id] with a component [displayName]. */
	fun add(id: String, namespace: String = "minecraft", displayName: ChatComponents) =
		fn.addLine(command("bossbar", literal("add"), BossBarArgument(id, namespace), displayName.asJsonArg()))

	/** Creates a new boss bar identified by [namespace]:[id] with a plain text [displayName]. */
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

	/** Lists every boss bar. */
	fun list() = fn.addLine(command("bossbar", literal("list")))

	/** Obtains a [BossBar] scope for the bar identified by [namespace]:[id]. */
	fun get(id: String, namespace: String = "minecraft") = BossBar(fn, BossBarArgument(id, namespace))

	/** Obtains a [BossBar] scope for the given [argument]. */
	fun get(argument: BossBarArgument) = BossBar(fn, argument)
}

val Function.bossBars get() = BossBars(this)

/** Opens the [BossBar] DSL for the boss bar identified by [namespace]:[name]. */
fun Function.bossBar(name: String, namespace: String = "minecraft", block: BossBar.() -> Command) =
	BossBar(this, BossBarArgument(name, namespace)).block()

/** Opens the [BossBar] DSL for the boss bar identified by [argument]. */
fun Function.bossBar(argument: BossBarArgument, block: BossBar.() -> Command) =
	BossBar(this, argument).block()
