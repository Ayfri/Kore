package commands

import arguments.Selector
import arguments.SelectorNbtData
import arguments.SelectorType
import functions.Function
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonPrimitive

sealed interface Argument {
	fun asString(): String
	
	sealed interface Possessor : Argument
	
	sealed interface BlockOrTag : Argument
	
	sealed interface ItemOrTag : Argument
	
	sealed interface Data : Argument {
		val literalName
			get() = when (this) {
				is Block -> "block"
				is Selector, is UUID -> "entity"
				is Storage -> "storage"
			}
	}
	
	sealed interface Entity : Data
	
	object All : Argument, Possessor {
		override fun asString() = "*"
	}
	
	data class Advancement(val advancement: String, val namespace: String = "minecraft") : Argument {
		override fun asString() = "$namespace:$advancement"
	}
	
	data class Attribute(val attribute: arguments.Attribute, val namespace: String = "minecraft") : Argument {
		override fun asString() = "$namespace:${json.encodeToJsonElement(attribute).jsonPrimitive.content}"
	}
	
	data class Block(val block: String, val namespace: String = "minecraft", val states: MutableMap<String, String> = mutableMapOf(), val nbtData: nbt.NbtData? = null) : BlockOrTag,
		Data {
		override fun asString() = "$namespace:$block${states.map { "[$it]" }.joinToString("")}${nbtData?.toString() ?: ""}"
	}
	
	data class BlockTag(val tag: String, val namespace: String = "minecraft") : BlockOrTag {
		override fun asString() = "#$namespace:$tag"
	}
	
	data class Coordinate(val x: Double, val y: Double, val z: Double, val type: Type = Type.World) : Argument {
		enum class Type {
			Relative,
			Local,
			World
		}
		
		private val Double.str
			get() = when {
				this == toFloat().toDouble() -> this.toInt().toString()
				else -> toString()
			}
		
		override fun asString() = when (type) {
			Type.Relative -> "~${x.str} ~${y.str} ~${z.str}"
			Type.Local -> "^${x.str} ^${y.str} ^${z.str}"
			Type.World -> "${x.str} ${y.str} ${z.str}"
		}
	}
	
	data class Float(val value: Double) : Argument {
		override fun asString() = value.toString()
	}
	
	data class Int(val value: Long) : Argument {
		override fun asString() = value.toString()
	}
	
	data class Item(val item: String, val namespace: String = "minecraft", val nbtData: nbt.NbtData? = null) : ItemOrTag {
		override fun asString() = "$namespace:$item${nbtData?.toString() ?: ""}"
	}
	
	data class ItemTag(val tag: String, val namespace: String = "minecraft") : ItemOrTag {
		override fun asString() = "#$namespace:$tag"
	}
	
	data class Literal(val name: String) : Argument, Possessor {
		override fun asString() = name
	}
	
	data class NbtData(val nbt: nbt.NbtData) : Argument {
		override fun asString() = "\"$nbt\""
	}
	
	data class Selector(val selector: arguments.Selector) : Entity, Data, Possessor {
		override fun asString() = selector.toString()
	}
	
	data class Storage(val storage: String, val namespace: String = "minecraft") : Data {
		override fun asString() = "$namespace:$storage"
	}
	
	data class Time(val value: Double, val type: Type = Type.Tick) : Argument {
		enum class Type {
			Tick,
			Second,
			Day
		}
		
		override fun asString() = when (type) {
			Type.Tick -> "${value}t"
			Type.Second -> "${value}s"
			Type.Day -> "${value}d"
		}
	}
	
	data class UUID(val uuid: java.util.UUID) : Entity {
		override fun asString() = uuid.toString()
	}
}

fun Function.advancement(name: String, namespace: String = "minecraft") = Argument.Advancement(name, namespace)
fun Function.attribute(attribute: arguments.Attribute, namespace: String = "minecraft") = Argument.Attribute(attribute, namespace)
fun Function.block(
	block: String,
	namespace: String = "minecraft",
	states: Map<String, String> = mutableMapOf(),
	nbtData: nbt.NbtData? = null,
) = Argument.Block(block, namespace, states.toMutableMap(), nbtData)

fun Function.blockTag(tag: String, namespace: String = "minecraft") = Argument.BlockTag(tag, namespace)
fun Function.bool(value: Boolean) = Argument.Literal(value.toString())
internal fun Function.bool(value: Boolean?) = value?.let { Argument.Literal(it.toString()) }
fun Function.coordinate(x: Double, y: Double, z: Double, type: Argument.Coordinate.Type = Argument.Coordinate.Type.World) = Argument.Coordinate(x, y, z, type)
fun Function.coordinate(x: Int, y: Int, z: Int, type: Argument.Coordinate.Type = Argument.Coordinate.Type.World) = Argument.Coordinate(x.toDouble(), y.toDouble(), z.toDouble(), type)
fun Function.float(value: Double) = Argument.Float(value)
fun Function.float(value: Float) = Argument.Float(value.toDouble())
internal fun Function.float(value: Double?) = value?.let { Argument.Float(it) }
internal fun Function.float(value: Float?) = value?.let { Argument.Float(it.toDouble()) }
fun Function.int(value: Long) = Argument.Int(value)
fun Function.int(value: Int) = Argument.Int(value.toLong())
internal fun Function.int(value: Int?) = value?.let { Argument.Int(it.toLong()) }
internal fun Function.int(value: Long?) = value?.let { Argument.Int(it) }
fun Function.item(item: String, namespace: String = "minecraft", nbtData: nbt.NbtData? = null) = Argument.Item(item, namespace, nbtData)
fun Function.itemTag(tag: String, namespace: String = "minecraft") = Argument.ItemTag(tag, namespace)
fun Function.nbtData(nbt: nbt.NbtData) = Argument.NbtData(nbt)
fun Function.selector(base: SelectorType, limitToOne: Boolean = false, data: SelectorNbtData.() -> Unit = {}) = Argument.Selector(Selector(base).apply {
	nbtData.data()
	if (limitToOne) nbtData.limit = 1
})

fun Function.literal(name: String) = Argument.Literal(name)

@JvmName("literalNullable")
internal fun Function.literal(name: String?) = name?.let { Argument.Literal(it) }
fun Function.time(value: Double, type: Argument.Time.Type = Argument.Time.Type.Tick) = Argument.Time(value, type)
fun Function.time(value: Int, type: Argument.Time.Type = Argument.Time.Type.Tick) = Argument.Time(value.toDouble(), type)
fun Function.uuid(uuid: java.util.UUID) = Argument.UUID(uuid)
