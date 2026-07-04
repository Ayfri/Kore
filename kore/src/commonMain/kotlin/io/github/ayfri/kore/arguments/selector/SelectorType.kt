package io.github.ayfri.kore.arguments.selector

/** Selector variable used in Minecraft commands (e.g. `@p`, `@e`). */
enum class SelectorType(val value: String) {
    /** `@e` - all entities. */
    ALL_ENTITIES("e"),
    /** `@a` - all players. */
    ALL_PLAYERS("a"),
    /** `@n` - nearest entity. */
    NEAREST_ENTITY("n"),
    /** `@p` - nearest player. */
    NEAREST_PLAYER("p"),
    /** `@r` - random player. */
    RANDOM_PLAYER("r"),
    /** `@s` - the executing entity (self). */
    SELF("s");

    /** Whether this selector refers to player targets. */
    val isPlayer get() = this == NEAREST_PLAYER || this == RANDOM_PLAYER || this == ALL_PLAYERS
}
