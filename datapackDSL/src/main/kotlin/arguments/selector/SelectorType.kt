package arguments.selector

enum class SelectorType(val value: String) {
	NEAREST_PLAYER("p"),
	RANDOM_PLAYER("r"),
	ALL_PLAYERS("a"),
	ALL_ENTITIES("e"),
	SELF("s");
}
