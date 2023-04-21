package data.item.builders

import arguments.Argument
import data.item.Enchantment

class EnchantmentsBuilder {
	val enchantments = mutableSetOf<Enchantment>()

	fun enchantment(id: Argument.Enchantment, level: Int = 1) {
		enchantments += Enchantment(id, level)
	}

	fun enchantment(id: String, level: Int = 1) {
		enchantment(Argument.Enchantment(id), level)
	}

	operator fun Argument.Enchantment.invoke(level: Int = 1) = enchantment(this, level)
	operator fun Argument.Enchantment.unaryPlus() = enchantment(this)

	infix fun Argument.Enchantment.at(level: Int) = enchantment(this, level)
}
