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

/**
 * OOP wrapper around a Minecraft boss bar.
 *
 * Create via [bossBar] DSL, then call [BossBarHandle.show] / [BossBarHandle.hide] /
 * [BossBarHandle.setValue] etc. from any function context.
 *
 * @property id the boss bar resource location (e.g. `"my_namespace:my_bar"`).
 * @property displayName initial display name shown above the bar.
 * @property color initial bar color.
 * @property max initial maximum value.
 * @property style initial bar style (notched or progress).
 * @property value initial value.
 * @property visible whether the bar is initially visible.
 */
data class BossBarConfig(
	val id: String,
	val namespace: String = "minecraft",
	var color: BossBarColor = BossBarColor.WHITE,
	var displayName: ChatComponents = textComponent(id),
	var max: Int = 100,
	var style: BossBarStyle = BossBarStyle.PROGRESS,
	var value: Int = 0,
	var visible: Boolean = true,
)

/** DSL builder for [BossBarConfig]. */
fun bossBarConfig(id: String, namespace: String = "minecraft", block: BossBarConfig.() -> Unit = {}) =
	BossBarConfig(id, namespace).apply(block)

/**
 * Handle returned by [DataPack.registerBossBar] to manipulate the boss bar from any function context.
 */
class BossBarHandle(val config: BossBarConfig) {
	val argument get() = BossBarArgument(config.id, config.namespace)

	context(fn: Function)
	fun setColor(color: BossBarColor) = fn.bossBar(argument) { setColor(color) }

	context(fn: Function)
	fun setMax(max: Int) = fn.bossBar(argument) { setMax(max) }

	context(fn: Function)
	fun setName(name: String) = fn.bossBar(argument) { setName(name) }

	context(fn: Function)
	fun setPlayers(entity: Entity) = fn.bossBar(argument) { setPlayers(entity.asSelector()) }

	context(fn: Function)
	fun setStyle(style: BossBarStyle) = fn.bossBar(argument) { setStyle(style) }

	context(fn: Function)
	fun setValue(value: Int) = fn.bossBar(argument) { setValue(value) }

	context(fn: Function)
	fun show() = fn.bossBar(argument) { setVisible(true) }

	context(fn: Function)
	fun hide() = fn.bossBar(argument) { setVisible(false) }

	context(fn: Function)
	fun remove() = fn.bossBar(argument) { remove() }
}

/**
 * Registers a boss bar in the datapack.
 *
 * A load function is generated that creates the bar and applies the initial configuration.
 *
 * @return a [BossBarHandle] to manipulate the bar from any function context.
 */
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

/** Shorthand - creates and registers a boss bar in one call. */
fun DataPack.registerBossBar(
	id: String,
	namespace: String = "minecraft",
	block: BossBarConfig.() -> Unit = {},
) = registerBossBar(BossBarConfig(id, namespace).apply(block))
