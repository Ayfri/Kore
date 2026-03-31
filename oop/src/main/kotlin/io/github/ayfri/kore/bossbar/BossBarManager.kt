package io.github.ayfri.kore.bossbar

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.BossBarColor
import io.github.ayfri.kore.arguments.types.resources.BossBarArgument
import io.github.ayfri.kore.commands.BossBarStyle
import io.github.ayfri.kore.commands.bossBar
import io.github.ayfri.kore.entities.Entity
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.load
import io.github.ayfri.kore.teams.Team
import io.github.ayfri.kore.teams.members

/** Configures a boss bar before it is registered into the datapack. */
data class BossBarConfig(
	val id: String,
	var color: BossBarColor = BossBarColor.WHITE,
	var displayName: ChatComponents = textComponent(id),
	var max: Int = 100,
	val namespace: String = "minecraft",
	var style: BossBarStyle = BossBarStyle.PROGRESS,
	var value: Int = 0,
	var visible: Boolean = true,
)

/** Wraps a registered boss bar and exposes convenient mutation helpers. */
data class BossBarHandle(val config: BossBarConfig) {
	/** Returns the resource argument pointing to this boss bar. */
	val argument get() = BossBarArgument(config.id, config.namespace)

	/** Hides the boss bar from players without deleting it. */
	context(fn: Function)
	fun hide() = fn.bossBar(argument) { setVisible(false) }

	/** Removes the boss bar entirely. */
	context(fn: Function)
	fun remove() = fn.bossBar(argument) { remove() }

	/** Updates the boss bar color. */
	context(fn: Function)
	fun setColor(color: BossBarColor) = fn.bossBar(argument) { setColor(color) }

	/** Updates the maximum value used to render progress. */
	context(fn: Function)
	fun setMax(max: Int) = fn.bossBar(argument) { setMax(max) }

	/** Sets the displayed name from a raw string. */
	context(fn: Function)
	fun setName(name: String) = fn.bossBar(argument) { setName(name) }

	/** Sets the players that should currently see this boss bar. */
	context(fn: Function)
	fun setPlayers(entity: Entity) = fn.bossBar(argument) { setPlayers(entity.asSelector()) }

	/** Sets the players that should currently see this boss bar from all members of [team]. */
	context(fn: Function)
	fun setPlayers(team: Team) = setPlayers(team.members())

	/** Changes the vanilla boss bar rendering style. */
	context(fn: Function)
	fun setStyle(style: BossBarStyle) = fn.bossBar(argument) { setStyle(style) }

	/** Updates the current progress value. */
	context(fn: Function)
	fun setValue(value: Int) = fn.bossBar(argument) { setValue(value) }

	/** Makes the boss bar visible again. */
	context(fn: Function)
	fun show() = fn.bossBar(argument) { setVisible(true) }
}

/** Registers a boss bar and emits its initialization function. */
fun DataPack.registerBossBar(config: BossBarConfig): BossBarHandle {
	load("bossbar_${config.id}_init") {
		bossBar(BossBarArgument(config.id, config.namespace)) {
			add(config.displayName)
			setColor(config.color)
			setMax(config.max)
			setStyle(config.style)
			setValue(config.value)
			setVisible(config.visible)
		}
	}
	return BossBarHandle(config)
}

/** Creates and registers a boss bar from its identifier and inline configuration. */
fun DataPack.registerBossBar(
	id: String,
	namespace: String = "minecraft",
	block: BossBarConfig.() -> Unit = {},
) = registerBossBar(BossBarConfig(id = id, namespace = namespace).apply(block))
