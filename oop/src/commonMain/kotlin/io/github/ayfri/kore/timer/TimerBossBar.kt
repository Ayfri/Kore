package io.github.ayfri.kore.timer

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.numbers.TimeNumber
import io.github.ayfri.kore.bossbar.BossBarConfig
import io.github.ayfri.kore.bossbar.BossBarHandle
import io.github.ayfri.kore.bossbar.registerBossBar
import io.github.ayfri.kore.entities.Entity
import io.github.ayfri.kore.functions.Function

/** Couples a [TimerHandle] with a matching [BossBarHandle] for UI feedback. */
data class TimerWithBossBar(
	/** The timer driving the progression. */
    val timer: TimerHandle,
	/** The boss bar mirroring the timer state to players. */
    val bossBar: BossBarHandle,
)

/** Starts the timer and shows its boss bar to the provided entity. */
context(fn: Function)
fun TimerWithBossBar.start(entity: Entity) {
    timer.start(entity)
    bossBar.show()
    bossBar.setPlayers(entity)
}

/** Stops the timer and hides its boss bar. */
context(fn: Function)
fun TimerWithBossBar.stop(entity: Entity) {
    timer.stop(entity)
    bossBar.hide()
}

/** Runs [block] when the timer completes, then hides the boss bar. */
context(fn: Function)
fun TimerWithBossBar.onComplete(entity: Entity, block: Function.() -> Unit) {
    timer.onComplete(entity) {
        block()
        bossBar.hide()
    }
}

/** Registers a timer and a synchronized boss bar in one call. */
fun DataPack.registerTimerWithBossBar(
    name: String,
    duration: TimeNumber,
    bossBarConfig: BossBarConfig.() -> Unit = {},
): TimerWithBossBar {
    val timer = registerTimer(name, duration)
    val bar = registerBossBar("${name}_bar", this.name) {
        max = duration.value.toInt()
        value = 0
        bossBarConfig()
    }
    return TimerWithBossBar(timer, bar)
}
