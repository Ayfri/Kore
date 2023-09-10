package io.github.ayfri.kore.data.item

enum class HideFlags {
	ENCHANTMENTS,
	ATTRIBUTES,
	UNBREAKABLE,
	CAN_DESTROY,
	CAN_PLACE_ON,
	DYE,
	MODIFIERS,
	CUSTOM_MODEL_DATA;

	fun toBitFlag() = 1 shl ordinal

	infix fun or(other: HideFlags) = listOf(this, other)
	infix fun or(other: List<HideFlags>) = listOf(this) + other
}
