package io.github.ayfri.kore.spawner

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.OopConstants
import io.github.ayfri.kore.arguments.maths.Vec3
import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.commands.summon
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.generatedFunction
import io.github.ayfri.kore.generated.arguments.types.EntityTypeArgument
import net.benwoodworth.knbt.NbtCompound

data class SpawnerConfig(
    var entityType: EntityTypeArgument,
    var name: String,
    var nbt: NbtCompound? = null,
    var position: Vec3 = vec3(0, 0, 0),
)

data class SpawnerHandle(val config: SpawnerConfig) {
    context(fn: Function)
    fun spawn() = fn.summon(config.entityType, config.position, config.nbt)

    context(fn: Function)
    fun spawnAt(position: Vec3) = fn.summon(config.entityType, position, config.nbt)

    context(fn: Function)
    fun spawnMultiple(count: Int) = repeat(count) {
        fn.summon(config.entityType, config.position, config.nbt)
    }
}

fun DataPack.registerSpawner(config: SpawnerConfig): SpawnerHandle {
    generatedFunction(OopConstants.spawnerSpawnFunctionName(config.name)) {
        summon(config.entityType, config.position, config.nbt)
    }
    return SpawnerHandle(config)
}

fun DataPack.registerSpawner(
    name: String,
    entityType: EntityTypeArgument,
    block: SpawnerConfig.() -> Unit = {},
) = registerSpawner(SpawnerConfig(entityType = entityType, name = name).apply(block))
