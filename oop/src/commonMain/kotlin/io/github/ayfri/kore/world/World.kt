package io.github.ayfri.kore.world

import io.github.ayfri.kore.generated.arguments.types.DimensionArgument

/**
 * Handle representing the world (or a single [dimension] of it) used to register world-level events.
 *
 * A `null` [dimension] means the event applies globally; setting one scopes the handler so it runs
 * with the execution context set to that dimension (`execute in <dimension> run ...`).
 *
 * Mirror of the entity event handles, but for events that originate from the world itself
 * (ticking, weather, day/night cycle, intervals…) instead of from an entity.
 */
data class World(val dimension: DimensionArgument? = null)

/** Creates a global [World] handle, or one bound to [dimension] when provided. */
fun world(dimension: DimensionArgument? = null) = World(dimension)
