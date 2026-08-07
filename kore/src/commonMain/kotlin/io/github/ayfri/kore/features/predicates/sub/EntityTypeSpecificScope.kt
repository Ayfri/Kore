package io.github.ayfri.kore.features.predicates.sub

/** Receiver for [Entity.typeSpecific] - groups the type-specific sub-predicate builders (`fishingHook`, `player`, ...). */
class EntityTypeSpecificScope(internal val entity: Entity)

/**
 * Adds the entity's type-specific sub-predicate, matched only against entities of the relevant type
 * (e.g. [fishingHook] only matches a fishing hook).
 *
 * Example:
 * ```kotlin
 * entity {
 *     typeSpecific {
 *         player {
 *             gamemodes(Gamemode.SURVIVAL)
 *         }
 *     }
 * }
 * ```
 */
fun Entity.typeSpecific(block: EntityTypeSpecificScope.() -> Unit) {
	EntityTypeSpecificScope(this).apply(block)
}
