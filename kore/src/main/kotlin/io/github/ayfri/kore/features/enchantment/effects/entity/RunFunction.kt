package io.github.ayfri.kore.features.enchantment.effects.entity

import io.github.ayfri.kore.arguments.types.resources.FunctionArgument
import kotlinx.serialization.Serializable

@Serializable
data class RunFunction(
	var function: FunctionArgument,
) : EntityEffect()
