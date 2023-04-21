package data.item

import arguments.Argument
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class Enchantment(
	val id: Argument.Enchantment,
	@SerialName("lvl")
	@EncodeDefault
	val level: Int = 1,
)

fun enchantment(id: Argument.Enchantment, level: Int = 1) = Enchantment(id, level)
fun enchantment(id: String, level: Int = 1) = Enchantment(Argument.Enchantment(id), level)
