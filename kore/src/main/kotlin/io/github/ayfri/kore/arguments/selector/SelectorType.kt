package io.github.ayfri.kore.arguments.selector

enum class SelectorType(val value: String) {
	ALL_ENTITIES("e"),
	ALL_PLAYERS("a"),
	NEAREST_ENTITY("n"),
	NEAREST_PLAYER("p"),
	RANDOM_PLAYER("r"),
	SELF("s");

	val isPlayer get() = this == NEAREST_PLAYER || this == RANDOM_PLAYER || this == ALL_PLAYERS
}
