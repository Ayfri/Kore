package io.github.ayfri.kore.features.enchantments.effects.entity

import io.github.ayfri.kore.arguments.types.resources.FunctionArgument
import kotlinx.serialization.Serializable

/**
 * Runs [function] at the position of the affected entity, with that entity as the executor.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#run_function
 *
 * @property function The function run by the effect.
 */
@Serializable
data class RunFunction(
	var function: FunctionArgument,
) : EntityEffect()
