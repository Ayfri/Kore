package io.github.ayfri.kore.features.enchantments.providers.types

import io.github.ayfri.kore.features.enchantments.providers.EnchantmentProvidersScope
import io.github.ayfri.kore.features.worldgen.intproviders.ConstantIntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProviderScope
import io.github.ayfri.kore.generated.arguments.EnchantmentOrTagArgument
import kotlinx.serialization.Serializable

/**
 * Hands out [enchantment] at a level rolled from [level], clamped to what the enchantment allows.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_provider#single
 *
 * @property enchantment The enchantment handed out, one being picked at random from a tag.
 * @property level The level the enchantment is applied at.
 */
@Serializable
data class Single(
	var enchantment: EnchantmentOrTagArgument,
	var level: IntProvider = ConstantIntProvider(1),
) : EnchantmentProviderType(), IntProviderScope

/**
 * Registers a `single` provider named [fileName], handing out [enchantment] at a level rolled from [level].
 *
 * ```kotlin
 * enchantmentProviders {
 *     single("pillager_spawn_crossbow", Enchantments.PIERCING, uniform(1, 3))
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_provider#single
 */
fun EnchantmentProvidersScope.single(
	fileName: String,
	enchantment: EnchantmentOrTagArgument,
	level: IntProvider = ConstantIntProvider(1),
	block: Single.() -> Unit = {},
) = register(fileName, Single(enchantment, level).apply(block))
