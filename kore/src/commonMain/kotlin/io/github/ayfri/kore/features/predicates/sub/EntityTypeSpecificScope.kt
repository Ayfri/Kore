package io.github.ayfri.kore.features.predicates.sub

/** Receiver for [EntityPredicate.typeSpecific] - groups the type-specific sub-predicate builders (`fishingHook`, `player`, ...). */
class EntityTypeSpecificScope(internal val entity: EntityPredicate)

/**
 * Adds the entity's type-specific sub-predicate, matched only against entities of the relevant type
 * (e.g. [fishingHook] only matches a fishing hook).
 *
 * Example:
 * ```kotlin
 * entityPredicate {
 *     typeSpecific {
 *         player {
 *             gamemodes(Gamemode.SURVIVAL)
 *         }
 *     }
 * }
 * ```
 */
fun EntityPredicate.typeSpecific(block: EntityTypeSpecificScope.() -> Unit) {
	EntityTypeSpecificScope(this).apply(block)
}
