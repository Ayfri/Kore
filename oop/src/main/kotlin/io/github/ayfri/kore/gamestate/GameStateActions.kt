package io.github.ayfri.kore.gamestate

import io.github.ayfri.kore.cooldown.CooldownHandle
import io.github.ayfri.kore.entities.Entity
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.spawner.SpawnerHandle
import io.github.ayfri.kore.timer.TimerHandle

context(fn: Function)
fun GameStateManager.transitionWithCooldown(stateName: String, cooldown: CooldownHandle, entity: Entity) {
    transitionTo(stateName)
    cooldown.start(entity)
}

context(fn: Function)
fun GameStateManager.whenStateSpawn(stateName: String, spawner: SpawnerHandle, count: Int = 1) {
    whenState(stateName) {
        spawner.spawnMultiple(count)
    }
}

context(fn: Function)
fun GameStateManager.whenStateStartTimer(stateName: String, timer: TimerHandle, entity: Entity) {
    whenState(stateName) {
        timer.start(entity)
    }
}
