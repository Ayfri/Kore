package io.github.ayfri.kore.arguments.types.resources.tagged

import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.arguments.types.EnchantmentOrTagArgument
import io.github.ayfri.kore.arguments.types.TaggedResourceLocationArgument
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
interface EnchantmentTagArgument : TaggedResourceLocationArgument, EnchantmentOrTagArgument {
	companion object {
		operator fun invoke(name: String, namespace: String = "minecraft") = object : EnchantmentTagArgument {
			override val name = name
			override val namespace = namespace
		}
	}
}
