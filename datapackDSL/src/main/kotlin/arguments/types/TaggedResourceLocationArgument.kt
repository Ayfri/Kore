package arguments.types

interface TaggedResourceLocationArgument : ResourceLocationArgument {
	override fun asId() = "#$namespace:$name"
}
