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

data class BossBarHandle(val config: BossBarConfig) {
	val argument get() = BossBarArgument(config.id, config.namespace)

    context(fn: Function)
    fun hide() = fn.bossBar(argument) { setVisible(false) }

    context(fn: Function)
    fun remove() = fn.bossBar(argument) { remove() }

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
}

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

fun DataPack.registerBossBar(
	id: String,
	namespace: String = "minecraft",
	block: BossBarConfig.() -> Unit = {},
) = registerBossBar(BossBarConfig(id = id, namespace = namespace).apply(block))
