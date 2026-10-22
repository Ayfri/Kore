package io.github.ayfri.kore.helpers.npcs

/**
 * Marks the [Npc] builder, the mannequin and interaction it nests and the receiver of the click callbacks of a plain interaction.
 *
 * Inside `npc("bob") { mannequin { } }`, a call such as `objective("visits")` that belongs to the NPC fails to compile
 * instead of silently binding to the enclosing scope, and inside `interaction("lever") { onRightClick { } }`, so does `width = 2f`.
 * The click callbacks of an [Npc] aren't marked, they declare objectives.
 */
@DslMarker
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
annotation class NpcDsl
