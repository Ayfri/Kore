package arguments.selector

enum class SelectorType(val value: String) {
	NEAREST_PLAYER("p"),
	RANDOM_PLAYER("r"),
	ALL_PLAYERS("a"),
	ALL_ENTITIES("e"),
	SELF("s");

	val isPlayer get() = this == NEAREST_PLAYER || this == RANDOM_PLAYER || this == ALL_PLAYERS
}
