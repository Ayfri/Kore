package arguments

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.benwoodworth.knbt.*
import serializers.NbtAsJsonTextComponentSerializer

fun nbt(block: NbtCompoundBuilder.() -> Unit) = buildNbtCompound(block)
fun <T : NbtTag> nbtList(block: NbtListBuilder<T>.() -> Unit) = buildNbtList(block)

@JvmName("nbtListCompound")
fun nbtList(block: NbtListBuilder<NbtCompound>.() -> Unit) = buildNbtList(block)

fun NbtCompoundBuilder.nbt(name: String, block: NbtCompoundBuilder.() -> Unit) = putNbtCompound(name, block)
fun NbtCompoundBuilder.json(name: String, block: NbtCompoundBuilder.() -> Unit) = put(name, StringifiedNbt.encodeToString(buildNbtCompound(block)))

fun stringifiedNbt(block: NbtCompoundBuilder.() -> Unit) = StringifiedNbt.encodeToString(buildNbtCompound(block))
fun stringifiedNbtList(block: NbtListBuilder<NbtCompound>.() -> Unit) = StringifiedNbt.encodeToString(buildNbtList(block))

fun nbt(nbt: NbtTag) = literal(StringifiedNbt.encodeToString(nbt))
fun nbtText(nbt: NbtTag) = literal(Json.encodeToString(NbtAsJsonTextComponentSerializer, nbt))

@JvmName("nbtNullable")
internal fun nbt(nbt: NbtTag? = null) = nbt?.let { nbt(it) }
