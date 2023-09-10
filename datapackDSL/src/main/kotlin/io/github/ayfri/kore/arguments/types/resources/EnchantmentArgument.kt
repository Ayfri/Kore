package io.github.ayfri.kore.arguments.types.resources

import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.arguments.types.ResourceLocationArgument
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
interface EnchantmentArgument : ResourceLocationArgument {
	companion object {
		operator fun invoke(enchantment: String, namespace: String = "minecraft") = object : EnchantmentArgument {
			override val name = enchantment
			override val namespace = namespace
		}
	}
}
