package data.item

import arguments.types.resources.EnchantmentArgument
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class Enchantment(
	val id: EnchantmentArgument,
	@SerialName("lvl")
	@EncodeDefault
	val level: Int = 1,
)

fun enchantment(id: EnchantmentArgument, level: Int = 1) = Enchantment(id, level)
fun enchantment(id: String, level: Int = 1) = Enchantment(EnchantmentArgument(id), level)
