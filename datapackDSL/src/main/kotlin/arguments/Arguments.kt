package arguments

import arguments.enums.Dimension
import arguments.numbers.*
import arguments.selector.Selector
import arguments.selector.SelectorNbtData
import arguments.selector.SelectorType
import arguments.selector.json
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonPrimitive
import net.benwoodworth.knbt.NbtTag
import java.util.*

sealed interface Argument {
	fun asString(): String

	sealed interface BlockOrTag : Argument

	sealed interface Data : Argument {
		val literalName
			get() = when (this) {
				is Coordinate -> "block"
				is Selector, is UUID -> "entity"
				is Storage -> "storage"
			}
	}

	sealed interface ItemOrTag : Argument

	sealed interface Entity : Data

	sealed interface Possessor : Argument

	sealed interface ScoreHolder : Argument

	sealed interface Namespaced : Argument {
		val name: String
		val namespace: String

		override fun asString() = "$namespace:$name"
	}

	object All : Argument, Possessor, ScoreHolder {
		override fun asString() = "*"
	}

	interface Advancement : Namespaced {
		companion object {
			operator fun invoke(name: String, namespace: String = "minecraft") = object : Advancement {
				override val name = name
				override val namespace = namespace
			}
		}
	}

	interface Attribute : Namespaced {
		companion object {
			operator fun invoke(name: String, namespace: String = "minecraft") = object : Attribute {
				override val name = name
				override val namespace = namespace
			}
		}
	}

	data class Block(
		val block: String,
		val namespace: String = "minecraft",
		val states: MutableMap<String, String> = mutableMapOf(),
		var nbtData: NbtTag? = null,
	) : BlockOrTag {
		override fun asString() = "$namespace:$block${states.map { "[$it]" }.joinToString("")}${nbtData?.toString() ?: ""}"
	}

	data class BlockTag(val name: String, val namespace: String = "minecraft") : BlockOrTag {
		override fun asString() = "#$namespace:$name"
	}

	interface Biome : Namespaced {
		companion object {
			operator fun invoke(biome: String, namespace: String = "minecraft") = object : Biome {
				override val name = biome
				override val namespace = namespace
			}
		}
	}

	data class BossBar(override val name: String, override val namespace: String = "minecraft") : Namespaced

	data class Dimension(
		val namespace: String? = null,
		val dimension: arguments.enums.Dimension? = null,
		val customDimension: String? = null
	) : Argument {
		override fun asString() = when {
			dimension != null -> "${namespace?.let { "$it:" } ?: ""}${json.encodeToJsonElement(dimension).jsonPrimitive.content}"
			customDimension != null -> "${namespace?.let { "$it:" } ?: ""}:$customDimension"
			else -> ""
		}
	}

	data class Float(val value: Double) : Argument {
		override fun asString() = value.toString()
	}

	interface EntitySummon : Namespaced {
		companion object {
			operator fun invoke(name: String, namespace: String = "minecraft") = object : EntitySummon {
				override val name = name
				override val namespace = namespace
			}
		}
	}

	data class Int(val value: Long) : Argument {
		override fun asString() = value.toString()
	}

	data class Item(val item: String, val namespace: String = "minecraft", val nbtData: NbtTag? = null) : ItemOrTag {
		override fun asString() = "$namespace:$item${nbtData?.toString() ?: ""}"
	}

	data class ItemTag(val tag: String, val namespace: String = "minecraft") : ItemOrTag {
		override fun asString() = "#$namespace:$tag"
	}

	data class Literal(val text: String) : Argument, Possessor, ScoreHolder {
		override fun asString() = text
	}

	interface MobEffect : Namespaced {
		companion object {
			operator fun invoke(name: String, namespace: String = "minecraft") = object : MobEffect {
				override val name = name
				override val namespace = namespace
			}
		}
	}

	data class Rotation(val yaw: RotNumber, val pitch: RotNumber) : Argument {
		override fun asString() = "$yaw $pitch"
	}

	data class Selector(val selector: arguments.selector.Selector) : Entity, Data, Possessor, ScoreHolder {
		override fun asString() = selector.toString()
	}

	data class Storage(override val name: String, override val namespace: String = "minecraft") : Data, Namespaced

	data class Time(val value: TimeNumber) : Argument {
		override fun asString() = value.toString()
	}

	data class UUID(val uuid: java.util.UUID) : Entity, ScoreHolder {
		override fun asString() = uuid.toString()
	}
}

fun advancement(name: String, namespace: String = "minecraft") = Argument.Advancement(name, namespace)
fun all() = Argument.All
fun block(
	block: String,
	namespace: String = "minecraft",
	states: Map<String, String> = mutableMapOf(),
	nbtData: NbtTag? = null,
) = Argument.Block(block, namespace, states.toMutableMap(), nbtData)

fun blockTag(tag: String, namespace: String = "minecraft") = Argument.BlockTag(tag, namespace)

fun bool(value: Boolean) = Argument.Literal(value.toString())
internal fun bool(value: Boolean?) = value?.let { Argument.Literal(it.toString()) }

fun bossBar(id: String, namespace: String = "minecraft") = Argument.BossBar(id, namespace)

fun coordinate(x: Number = 0, y: Number = 0, z: Number = 0) = Coordinate(x.pos, y.pos, z.pos)
fun coordinate(x: PosNumber, y: PosNumber, z: PosNumber) = Coordinate(x, y, z)
fun coordinate(x: PosNumber.Type, y: PosNumber.Type, z: PosNumber.Type) = Coordinate(pos(type = x), pos(type = y), pos(type = z))
fun coordinate(type: PosNumber.Type) = Coordinate(pos(type = type), pos(type = type), pos(type = type))

fun dimension(dimension: Dimension? = null) = Argument.Dimension("minecraft", dimension)
fun dimension(customDimension: String, namespace: String? = null) = Argument.Dimension(namespace, customDimension = customDimension)

fun float(value: Double) = Argument.Float(value)
fun float(value: Float) = Argument.Float(value.toDouble())
internal fun float(value: Double?) = value?.let { Argument.Float(it) }
internal fun float(value: Float?) = value?.let { Argument.Float(it.toDouble()) }

fun int(value: Long) = Argument.Int(value)
fun int(value: Int) = Argument.Int(value.toLong())
internal fun int(value: Int?) = value?.let { Argument.Int(it.toLong()) }
internal fun int(value: Long?) = value?.let { Argument.Int(it) }

fun item(item: String, namespace: String = "minecraft", nbtData: NbtTag? = null) = Argument.Item(item, namespace, nbtData)

fun itemTag(tag: String, namespace: String = "minecraft") = Argument.ItemTag(tag, namespace)

fun literal(name: String) = Argument.Literal(name)

@JvmName("literalNullable")
internal fun literal(name: String?) = name?.let { Argument.Literal(it) }

fun rotation(yaw: Number, pitch: Number) = Argument.Rotation(yaw.rot, pitch.rot)
fun rotation(hor: RotNumber, ver: RotNumber) = Argument.Rotation(hor, ver)

fun selector(base: SelectorType, limitToOne: Boolean = false, data: SelectorNbtData.() -> Unit = {}) = Argument.Selector(Selector(base).apply {
	nbtData.data()
	if (limitToOne) nbtData.limit = 1
})

fun allPlayers(limitToOne: Boolean = false, data: SelectorNbtData.() -> Unit = {}) = selector(SelectorType.ALL_PLAYERS, limitToOne, data)
fun allEntities(limitToOne: Boolean = false, data: SelectorNbtData.() -> Unit = {}) = selector(SelectorType.ALL_ENTITIES, limitToOne, data)
fun nearestPlayer(data: SelectorNbtData.() -> Unit = {}) = selector(SelectorType.NEAREST_PLAYER, data = data)
fun randomPlayer(data: SelectorNbtData.() -> Unit = {}) = selector(SelectorType.RANDOM_PLAYER, data = data)
fun self(limitToOne: Boolean = false, data: SelectorNbtData.() -> Unit = {}) = selector(SelectorType.SELF, limitToOne, data)

fun storage(storage: String, namespace: String = "minecraft") = Argument.Storage(storage, namespace)

fun tag(name: String, group: Boolean = true) = Argument.Literal(if (group) "#$name" else name)
fun tag(name: String, namespace: String, group: Boolean = true) = Argument.Literal(if (group) "#$namespace:$name" else "$namespace:$name")

fun time(value: TimeNumber) = Argument.Time(value)

fun uuid(uuid: UUID) = Argument.UUID(uuid)
