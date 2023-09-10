package io.github.ayfri.kore.data.item.builders

import io.github.ayfri.kore.arguments.types.resources.EnchantmentArgument
import io.github.ayfri.kore.data.item.Enchantment

class EnchantmentsBuilder {
	val enchantments = mutableSetOf<Enchantment>()

	fun enchantment(id: EnchantmentArgument, level: Int = 1) {
		enchantments += Enchantment(id, level)
	}

	fun enchantment(id: String, level: Int = 1) {
		enchantment(EnchantmentArgument(id), level)
	}

	operator fun EnchantmentArgument.invoke(level: Int = 1) = enchantment(this, level)
	operator fun EnchantmentArgument.unaryPlus() = enchantment(this)

	infix fun EnchantmentArgument.at(level: Int) = enchantment(this, level)
}
