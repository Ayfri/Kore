package arguments

import arguments.numbers.*
import arguments.selector.Selector
import arguments.selector.SelectorNbtData
import arguments.selector.SelectorType
import kotlinx.serialization.Serializable
import kotlinx.serialization.encoding.Encoder
import net.benwoodworth.knbt.NbtCompound
import net.benwoodworth.knbt.NbtCompoundBuilder
import serializers.ToStringSerializer
import java.util.*

@Serializable(Argument.ArgumentSerializer::class)
sealed interface Argument {
	fun asString(): String

	object ArgumentSerializer : ToStringSerializer<Argument>() {
		override fun serialize(encoder: Encoder, value: Argument) {
			encoder.encodeString(value.asString())
		}
	}

	sealed interface BlockOrTag : Argument

	sealed interface Data : Argument {
		val literalName
			get() = when (this) {
				is Vec3 -> "block"
				is Selector, is UUID -> "entity"
				is Storage -> "storage"
			}
	}

	sealed interface ItemOrTag : Argument

	sealed interface Entity : Data

	sealed interface Possessor : Argument

	sealed interface ScoreHolder : Argument

	sealed interface ResourceLocation : Argument {
		val name: String
		val namespace: String

		override fun asString() = "$namespace:$name"
	}

	@Serializable(with = ArgumentSerializer::class)
	object All : Argument, Possessor, ScoreHolder {
		override fun asString() = "*"
	}

	@Serializable(with = ArgumentSerializer::class)
	interface Advancement : ResourceLocation {
		companion object {
			operator fun invoke(name: String, namespace: String = "minecraft") = object : Advancement {
				override val name = name
				override val namespace = namespace
			}
		}
	}

	@Serializable(with = ArgumentSerializer::class)
	interface Attribute : ResourceLocation {
		companion object {
			operator fun invoke(name: String, namespace: String = "minecraft") = object : Attribute {
				override val name = name
				override val namespace = namespace
			}
		}
	}

	@Serializable(with = ArgumentSerializer::class)
	interface Block : ResourceLocation, BlockOrTag {
		var states: MutableMap<String, String>
		var nbtData: NbtCompound?

		override fun asString(): String {
			return "$namespace:$name${states.map { "[$it]" }.joinToString("")}${nbtData?.toString() ?: ""}"
		}

		operator fun invoke(states: Map<String, String> = mutableMapOf(), nbtData: (NbtCompoundBuilder.() -> Unit)? = null) = apply {
			this.states = states.toMutableMap()
			nbtData?.let { this.nbtData = nbt(it) }
		}

		companion object {
			operator fun invoke(
				name: String,
				namespace: String,
				states: Map<String, String> = mutableMapOf(),
				nbtData: NbtCompound? = null
			) = object : Block {
				override val name = name
				override val namespace = namespace
				override var states = states.toMutableMap()
				override var nbtData = nbtData
			}
		}
	}

	@Serializable(with = ArgumentSerializer::class)
	interface BlockTag : ResourceLocation, BlockOrTag {
		override fun asString() = "#$namespace:$name"

		companion object {
			operator fun invoke(blockOrTag: String, namespace: String = "minecraft") = object : BlockTag {
				override val name = blockOrTag
				override val namespace = namespace
			}
		}
	}

	@Serializable(with = ArgumentSerializer::class)
	interface Biome : ResourceLocation {
		companion object {
			operator fun invoke(biome: String, namespace: String = "minecraft") = object : Biome {
				override val name = biome
				override val namespace = namespace
			}
		}
	}

	@Serializable(with = ArgumentSerializer::class)
	data class BossBar(override val name: String, override val namespace: String = "minecraft") : ResourceLocation

	@Serializable(with = ArgumentSerializer::class)
	interface CatVariant : ResourceLocation {
		companion object {
			operator fun invoke(catVariant: String, namespace: String = "minecraft") = object : CatVariant {
				override val name = catVariant
				override val namespace = namespace
			}
		}
	}

	@Serializable(with = ArgumentSerializer::class)
	interface Dimension : ResourceLocation {
		companion object {
			operator fun invoke(dimension: String, namespace: String = "minecraft") = object : Dimension {
				override val name = dimension
				override val namespace = namespace
			}
		}
	}

	@Serializable(with = ArgumentSerializer::class)
	interface Enchantment : ResourceLocation {
		companion object {
			operator fun invoke(enchantment: String, namespace: String = "minecraft") = object : Enchantment {
				override val name = enchantment
				override val namespace = namespace
			}
		}
	}

	@Serializable(with = ArgumentSerializer::class)
	data class Float(val value: Double) : Argument {
		override fun asString() = value.toString()
	}

	@Serializable(with = ArgumentSerializer::class)
	interface Fluid : ResourceLocation {
		companion object {
			operator fun invoke(fluid: String, namespace: String = "minecraft") = object : Fluid {
				override val name = fluid
				override val namespace = namespace
			}
		}
	}

	@Serializable(with = ArgumentSerializer::class)
	interface FrogVariant : ResourceLocation {
		companion object {
			operator fun invoke(frogVariant: String, namespace: String = "minecraft") = object : FrogVariant {
				override val name = frogVariant
				override val namespace = namespace
			}
		}
	}

	@Serializable(with = ArgumentSerializer::class)
	interface EntitySummon : ResourceLocation {
		companion object {
			operator fun invoke(name: String, namespace: String = "minecraft") = object : EntitySummon {
				override val name = name
				override val namespace = namespace
			}
		}
	}

	@Serializable(with = ArgumentSerializer::class)
	data class Int(val value: Long) : Argument {
		override fun asString() = value.toString()
	}

	@Serializable(with = ArgumentSerializer::class)
	interface Item : ResourceLocation, ItemOrTag {
		var nbtData: NbtCompound?

		override fun asString() = "$namespace:$name${nbtData?.toString() ?: ""}"

		operator fun invoke(nbtData: (NbtCompoundBuilder.() -> Unit)? = null) = apply { nbtData?.let { this.nbtData = nbt(it) } }

		companion object {
			operator fun invoke(
				name: String,
				namespace: String,
				nbtData: NbtCompound? = null
			) = object : Item {
				override val name = name
				override val namespace = namespace
				override var nbtData = nbtData
			}
		}
	}

	@Serializable(with = ArgumentSerializer::class)
	interface ItemTag : ResourceLocation, BlockOrTag {
		override fun asString() = "#$namespace:$name"

		companion object {
			operator fun invoke(blockOrTag: String, namespace: String = "minecraft") = object : ItemTag {
				override val name = blockOrTag
				override val namespace = namespace
			}
		}
	}

	@Serializable(with = ArgumentSerializer::class)
	data class Literal(val text: String) : Argument, Possessor, ScoreHolder {
		override fun asString() = text
	}

	@Serializable(with = ArgumentSerializer::class)
	interface LootTable : ResourceLocation {
		companion object {
			operator fun invoke(name: String, namespace: String = "minecraft") = object : LootTable {
				override val name = name
				override val namespace = namespace
			}
		}
	}

	@Serializable(with = ArgumentSerializer::class)
	interface MobEffect : ResourceLocation {
		companion object {
			operator fun invoke(name: String, namespace: String = "minecraft") = object : MobEffect {
				override val name = name
				override val namespace = namespace
			}
		}
	}

	@Serializable(with = ArgumentSerializer::class)
	interface Particle : ResourceLocation {
		companion object {
			operator fun invoke(name: String, namespace: String = "minecraft") = object : Particle {
				override val name = name
				override val namespace = namespace
			}
		}
	}

	@Serializable(with = ArgumentSerializer::class)
	interface Potion : ResourceLocation {
		companion object {
			operator fun invoke(name: String, namespace: String = "minecraft") = object : Potion {
				override val name = name
				override val namespace = namespace
			}
		}
	}

	@Serializable(with = ArgumentSerializer::class)
	interface Predicate : ResourceLocation {
		companion object {
			operator fun invoke(name: String, namespace: String = "minecraft") = object : Predicate {
				override val name = name
				override val namespace = namespace
			}
		}
	}

	@Serializable(with = ArgumentSerializer::class)
	interface Recipe : ResourceLocation {
		companion object {
			operator fun invoke(name: String, namespace: String = "minecraft") = object : Recipe {
				override val name = name
				override val namespace = namespace
			}
		}
	}

	@Serializable(with = ArgumentSerializer::class)
	data class Rotation(val yaw: RotNumber, val pitch: RotNumber) : Argument {
		override fun asString() = "$yaw $pitch"
	}

	@Serializable(with = ArgumentSerializer::class)
	data class Selector(val selector: arguments.selector.Selector) : Entity, Data, Possessor, ScoreHolder {
		override fun asString() = selector.toString()
	}

	@Serializable(with = ArgumentSerializer::class)
	interface StatisticType : ResourceLocation {
		companion object {
			operator fun invoke(name: String, namespace: String = "minecraft") = object : StatisticType {
				override val name = name
				override val namespace = namespace
			}
		}
	}

	@Serializable(with = ArgumentSerializer::class)
	interface Statistic : ResourceLocation {
		companion object {
			operator fun invoke(name: String, namespace: String = "minecraft") = object : Statistic {
				override val name = name
				override val namespace = namespace
			}
		}
	}

	@Serializable(with = ArgumentSerializer::class)
	data class Storage(override val name: String, override val namespace: String = "minecraft") : Data, ResourceLocation

	@Serializable(with = ArgumentSerializer::class)
	interface Structure : ResourceLocation {
		companion object {
			operator fun invoke(name: String, namespace: String = "minecraft") = object : Structure {
				override val name = name
				override val namespace = namespace
			}
		}
	}

	@Serializable(with = ArgumentSerializer::class)
	interface Sound : ResourceLocation {
		companion object {
			operator fun invoke(name: String, namespace: String = "minecraft") = object : Sound {
				override val name = name
				override val namespace = namespace
			}
		}
	}

	@Serializable(with = ArgumentSerializer::class)
	data class Time(val value: TimeNumber) : Argument {
		override fun asString() = value.toString()
	}

	@Serializable(with = ArgumentSerializer::class)
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
	nbtData: NbtCompoundBuilder.() -> Unit = {},
) = Argument.Block(block, namespace, states.toMutableMap(), nbt(nbtData))

fun blockTag(tag: String, namespace: String = "minecraft") = Argument.BlockTag(tag, namespace)

fun bool(value: Boolean) = Argument.Literal(value.toString())
internal fun bool(value: Boolean?) = value?.let { Argument.Literal(it.toString()) }

fun bossBar(id: String, namespace: String = "minecraft") = Argument.BossBar(id, namespace)

fun coordinate(x: Number = 0, y: Number = 0, z: Number = 0) = Vec3(x.pos, y.pos, z.pos)
fun coordinate(x: PosNumber, y: PosNumber, z: PosNumber) = Vec3(x, y, z)
fun coordinate(x: PosNumber.Type, y: PosNumber.Type, z: PosNumber.Type) = Vec3(pos(type = x), pos(type = y), pos(type = z))
fun coordinate(type: PosNumber.Type) = Vec3(pos(type = type), pos(type = type), pos(type = type))

fun dimension(customDimension: String, namespace: String = "minecraft") = Argument.Dimension(customDimension, namespace)

fun enchantment(name: String, namespace: String = "minecraft") = Argument.Enchantment(name, namespace)

fun float(value: Double) = Argument.Float(value)
fun float(value: Float) = Argument.Float(value.toDouble())
internal fun float(value: Double?) = value?.let { Argument.Float(it) }
internal fun float(value: Float?) = value?.let { Argument.Float(it.toDouble()) }

fun int(value: Long) = Argument.Int(value)
fun int(value: Int) = Argument.Int(value.toLong())
internal fun int(value: Int?) = value?.let { Argument.Int(it.toLong()) }
internal fun int(value: Long?) = value?.let { Argument.Int(it) }

fun item(
	item: String,
	namespace: String = "minecraft",
	nbtData: (NbtCompoundBuilder.() -> Unit)? = null
) = Argument.Item(item, namespace, nbtData?.let { nbt(it) })

fun itemTag(tag: String, namespace: String = "minecraft") = Argument.ItemTag(tag, namespace)

fun literal(name: String) = Argument.Literal(name)

@JvmName("literalNullable")
internal fun literal(name: String?) = name?.let { Argument.Literal(it) }

fun rotation(yaw: Number = 0, pitch: Number = 0) = Argument.Rotation(yaw.rot, pitch.rot)
fun rotation(hor: RotNumber, ver: RotNumber) = Argument.Rotation(hor, ver)

fun selector(base: SelectorType, limitToOne: Boolean = false, data: SelectorNbtData.() -> Unit = {}) = Argument.Selector(Selector(base).apply {
	nbtData.data()
	if (limitToOne) nbtData.limit = 1
})

fun allPlayers(limitToOne: Boolean = false, data: SelectorNbtData.() -> Unit = {}) = selector(SelectorType.ALL_PLAYERS, limitToOne, data)
fun allEntities(limitToOne: Boolean = false, data: SelectorNbtData.() -> Unit = {}) = selector(SelectorType.ALL_ENTITIES, limitToOne, data)
fun entity(name: String) = literal(name)
fun nearestPlayer(data: SelectorNbtData.() -> Unit = {}) = selector(SelectorType.NEAREST_PLAYER, data = data)
fun player(name: String, limitToOne: Boolean = true, data: SelectorNbtData.() -> Unit = {}) =
	allPlayers(limitToOne) { this.name = name; data() }

fun randomPlayer(data: SelectorNbtData.() -> Unit = {}) = selector(SelectorType.RANDOM_PLAYER, data = data)
fun self(limitToOne: Boolean = false, data: SelectorNbtData.() -> Unit = {}) = selector(SelectorType.SELF, limitToOne, data)

fun storage(storage: String, namespace: String = "minecraft") = Argument.Storage(storage, namespace)

fun tag(name: String, group: Boolean = true) = Argument.Literal(if (group) "#$name" else name)
fun tag(name: String, namespace: String, group: Boolean = true) = Argument.Literal(if (group) "#$namespace:$name" else "$namespace:$name")

fun time(value: TimeNumber) = Argument.Time(value)

fun uuid(uuid: UUID) = Argument.UUID(uuid)

fun vec2(x: Number = 0, y: Number = 0) = Vec2(x, y)
fun vec2(x: PosNumber, y: PosNumber) = Vec2(x, y)
fun vec2(x: PosNumber.Type, y: PosNumber.Type) = Vec2(pos(type = x), pos(type = y))
fun vec2(type: PosNumber.Type) = Vec2(pos(type = type), pos(type = type))

fun vec3(x: Number = 0, y: Number = 0, z: Number = 0) = Vec3(x, y, z)
fun vec3(x: PosNumber, y: PosNumber, z: PosNumber) = Vec3(x, y, z)
fun vec3(x: PosNumber.Type, y: PosNumber.Type, z: PosNumber.Type) = Vec3(pos(type = x), pos(type = y), pos(type = z))
fun vec3(type: PosNumber.Type) = Vec3(pos(type = type), pos(type = type), pos(type = type))
