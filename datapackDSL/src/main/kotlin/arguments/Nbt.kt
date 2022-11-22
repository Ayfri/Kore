package arguments

import kotlinx.serialization.encodeToString
import net.benwoodworth.knbt.NbtCompound
import net.benwoodworth.knbt.NbtCompoundBuilder
import net.benwoodworth.knbt.NbtListBuilder
import net.benwoodworth.knbt.NbtTag
import net.benwoodworth.knbt.StringifiedNbt
import net.benwoodworth.knbt.buildNbtCompound
import net.benwoodworth.knbt.buildNbtList
import net.benwoodworth.knbt.put
import net.benwoodworth.knbt.putNbtCompound

fun nbt(block: NbtCompoundBuilder.() -> Unit) = buildNbtCompound(block)
fun <T : NbtTag> nbtList(block: NbtListBuilder<T>.() -> Unit) = buildNbtList(block)

@JvmName("nbtListCompound")
fun nbtList(block: NbtListBuilder<NbtCompound>.() -> Unit) = buildNbtList(block)

fun NbtCompoundBuilder.nbt(name: String, block: NbtCompoundBuilder.() -> Unit) = putNbtCompound(name, block)
fun NbtCompoundBuilder.json(name: String, block: NbtCompoundBuilder.() -> Unit) = put(name, StringifiedNbt.encodeToString(buildNbtCompound(block)))

fun stringifiedNbt(block: NbtCompoundBuilder.() -> Unit) = StringifiedNbt.encodeToString(buildNbtCompound(block))
fun stringifiedNbtList(block: NbtListBuilder<NbtCompound>.() -> Unit) = StringifiedNbt.encodeToString(buildNbtList(block))

fun nbt(nbt: NbtTag) = literal(StringifiedNbt.encodeToString(nbt))

@JvmName("nbtNullable")
internal fun nbt(nbt: NbtTag? = null) = nbt?.let { literal(StringifiedNbt.encodeToString(it)) }
