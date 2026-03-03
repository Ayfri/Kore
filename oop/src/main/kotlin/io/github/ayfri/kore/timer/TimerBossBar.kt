package io.github.ayfri.kore.timer

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.numbers.TimeNumber
import io.github.ayfri.kore.bossbar.BossBarConfig
import io.github.ayfri.kore.bossbar.BossBarHandle
import io.github.ayfri.kore.bossbar.registerBossBar
import io.github.ayfri.kore.entities.Entity
import io.github.ayfri.kore.functions.Function

data class TimerWithBossBar(
    val timer: TimerHandle,
    val bossBar: BossBarHandle,
)

context(fn: Function)
fun TimerWithBossBar.start(entity: Entity) {
    timer.start(entity)
    bossBar.show()
    bossBar.setPlayers(entity)
}

context(fn: Function)
fun TimerWithBossBar.stop(entity: Entity) {
    timer.stop(entity)
    bossBar.hide()
}

context(fn: Function)
fun TimerWithBossBar.onComplete(entity: Entity, block: Function.() -> Unit) {
    timer.onComplete(entity) {
        block()
        bossBar.hide()
    }
}

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
