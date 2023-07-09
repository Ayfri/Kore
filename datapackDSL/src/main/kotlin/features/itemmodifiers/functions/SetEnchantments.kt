package features.itemmodifiers.functions

import arguments.Argument
import features.predicates.providers.NumberProvider
import kotlinx.serialization.Serializable

@Serializable
data class SetEnchantments(
	var enchantments: Map<Argument.Enchantment, NumberProvider>,
	var add: Boolean? = null,
) : ItemFunctionSurrogate
